/*******************************************************************************
 * Copyright (c) 2007-2015, D. Lutz and Elexis.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     D. Lutz - initial API and implementation
 *     Gerry Weirich - adapted for 2.1
 *     Niklaus Giger - small improvements, split into 20 classes
 *
 * Sponsors:
 *     Dr. Peter Schönbucher, Luzern
 ******************************************************************************/
package org.iatrix.widgets;

import static ch.elexis.core.data.events.ElexisEvent.EVENT_DESELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_UPDATE;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.Iatrix;
import org.iatrix.actions.IatrixEventHelper;
import org.iatrix.data.Problem;
import org.iatrix.util.Constants;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Heartbeat.IatrixHeartListener;
import org.iatrix.views.JournalView;
import org.iatrix.views.ProblemView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.data.interfaces.IDiagnose;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.CodeSelectorHandler;
import ch.elexis.core.ui.actions.ICodeSelectorTarget;
import ch.elexis.core.ui.dialogs.MediDetailDialog;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.views.codesystems.LeistungenView;
import ch.elexis.data.Artikel;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.data.PersistentObject;
import ch.elexis.data.Prescription;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.ExHandler;
import ch.rgw.tools.StringTool;
import ch.rgw.tools.TimeTool;
import de.kupzog.ktable.KTableCellDoubleClickListener;
import de.kupzog.ktable.KTableCellEditor;
import de.kupzog.ktable.KTableCellSelectionListener;
import de.kupzog.ktable.SWTX;

public class ProblemArea implements IJournalArea {

	private ProblemsTableModel problemsTableModel;
	private MyKTable problemsKTable;
	private static Logger log = LoggerFactory.getLogger(ProblemArea.class);
	private ICodeSelectorTarget problemFixmedikationCodeSelectorTarget;
	private String journalViewPartName;
	public Action addProblemAction;
	public Action delProblemAction;
	public Action unassignProblemAction;
	public Action addFixmedikationAction;
	public Action deleteFixmedikationAction;
	public Action editFixmedikationAction;
	private FormToolkit tk;
	private ICodeSelectorTarget problemDiagnosesCodeSelectorTarget;

	public ProblemArea(Composite topArea, String partName){
		tk = UiDesk.getToolkit();
		journalViewPartName = partName;
		problemsKTable = new MyKTable(topArea, SWTX.MARK_FOCUS_HEADERS | SWTX.AUTO_SCROLL
			| SWTX.FILL_WITH_DUMMYCOL | SWTX.EDIT_ON_KEY);
		tk.adapt(problemsKTable);

		problemsTableModel = new ProblemsTableModel();
		problemsTableModel.setProblemsKTable(problemsKTable);
		problemsKTable.setModel(problemsTableModel);
		makeActions();

		log.debug("addProblemAction is : " + addProblemAction);
		// selections
		problemsKTable.addCellSelectionListener(new KTableCellSelectionListener() {
			@Override
			public void cellSelected(int col, int row, int statemask){
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);
				if (problem != null) {
					IatrixEventHelper.fireSelectionEventProblem(problem);
				} else {
					IatrixEventHelper.clearSelectionProblem();
				}
			}

			@Override
			public void fixedCellSelected(int col, int row, int statemask){
				problemsTableModel.setComparator(col, row);

				problemsTableModel.reload();
				// TODO: ng problemsKTable.refresh();
			}
		});
		registerUpdateHeartbeat();

		// clear selection when ESC is pressed
		problemsKTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e){
				if (e.keyCode == SWT.ESC) {
					problemsKTable.clearSelection();
					// work-around: KTable doesn't redraw in single selection mode
					problemsKTable.redraw();

					IatrixEventHelper.clearSelectionProblem();
				} else if ((e.character == ' ') || (e.character == '\r')) {
					// Work-around for opening the diagnosis selector on ENTER
					// or changing the status.
					// KTable supports only cell editors based on a Control.
					// So we just catch this event ourselves and assume that KTable
					// hasn't processed it in KTable.onKeyDown().

					if ((e.stateMask & SWT.CTRL) == 0) {
						// plain SPACE or ENTER

						// This is actually the same code as in the double click listener

						Point[] selection = problemsKTable.getCellSelection();
						if (selection.length == 1) {
							int col = selection[0].x;
							int row = selection[0].y;

							int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();
							int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
							Problem problem = problemsTableModel.getProblem(rowIndex);

							switch (colIndex) {
							case Constants.DIAGNOSEN:
								// open diagnosis selector

								if (problem != null) {
									try {
										log.debug("skip problemDiagnosesCodeSelectorTarget");
										// TODO: ngng getViewSite().getPage().showView(DiagnosenView.ID);
										// register as ICodeSelectorTarget
										// TODO: ngng CodeSelectorHandler.getInstance().setCodeSelectorTarget(
										// TODO: ngng problemDiagnosesCodeSelectorTarget);
									} catch (Exception ex) {
										ExHandler.handle(ex);
										log.error("Fehler beim Starten des Diagnosencodes "
											+ ex.getMessage());
									}
								}
								break;

							case Constants.STATUS:
								// change status when status field has been double clicked

								if (problem != null) {
									if (problem.getStatus() == Episode.ACTIVE) {
										problem.setStatus(Episode.INACTIVE);
									} else {
										problem.setStatus(Episode.ACTIVE);
									}
								}
								break;
							}
						}
					} else {
						// SPACE or ENTER with CTRL

						Point[] selection = problemsKTable.getCellSelection();
						if (selection.length == 1) {
							int col = selection[0].x;
							int row = selection[0].y;

							int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();

							switch (colIndex) {
							case Constants.DIAGNOSEN:
								KTableCellEditor editor =
									problemsTableModel.getCellEditor(col, row);
								if (editor != null
									&& (editor.getActivationSignals()
										& KTableCellEditor.KEY_RETURN_AND_SPACE) != 0
									&& editor.isApplicable(KTableCellEditor.KEY_RETURN_AND_SPACE,
										problemsKTable, col, row, null, e.character + "",
										e.stateMask)) {

									problemsKTable.openEditorInFocus();
								}
								break;
							}
						}
					}
				}
			}
		});

		problemsKTable.addCellDoubleClickListener(new KTableCellDoubleClickListener() {
			@Override
			public void cellDoubleClicked(int col, int row, int statemask){
				int colIndex = col - problemsTableModel.getFixedHeaderColumnCount();
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);

				switch (colIndex) {
				case Constants.DIAGNOSEN:
					// open diagnosis selector

					if (problem != null) {
						try {
							log.debug("skip problemDiagnosesCodeSelectorTarget");
							// TODO: ngng getViewSite().getPage().showView(DiagnosenView.ID);
							// register as ICodeSelectorTarget
							// TODO: ngng CodeSelectorHandler.getInstance()
							// TODO: ngng 								.setCodeSelectorTarget(problemDiagnosesCodeSelectorTarget);
						} catch (Exception ex) {
							ExHandler.handle(ex);
							log.error("Fehler beim Starten des Diagnosencodes " + ex.getMessage());
						}
					}
					break;

				case Constants.STATUS:
					// change status when status field has been double clicked

					if (problem != null) {
						if (problem.getStatus() == Episode.ACTIVE) {
							problem.setStatus(Episode.INACTIVE);
						} else {
							problem.setStatus(Episode.ACTIVE);
						}
					}
					break;
				}
			}

			@Override
			public void fixedCellDoubleClicked(int col, int row, int statemask){
				// nothing to do
			}
		});

		// Drag'n'Drop support
		// Quelle
		DragSource ds = new DragSource(problemsKTable, DND.DROP_COPY);
		ds.setTransfer(new Transfer[] {
			TextTransfer.getInstance()
		});
		ds.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragStart(DragSourceEvent event){
				Point cell = problemsKTable.getCellForCoordinates(event.x, event.y);
				int col = cell.x;
				int row = cell.y;
				// only handle normal columns/rows, no header columns/rows
				if (col >= problemsTableModel.getFixedHeaderColumnCount()
					&& row >= problemsTableModel.getFixedHeaderRowCount()) {
					Problem problem = getSelectedProblem();
					if (problem != null) {
						event.doit = problem.isDragOK();
					} else {
						event.doit = false;
					}
				} else {
					event.doit = false;
				}
			}

			@Override
			public void dragSetData(DragSourceEvent event){
				// only add single selection
				Problem problem = getSelectedProblem();
				StringBuilder sb = new StringBuilder();
				if (problem != null) {
					sb.append(problem.storeToString()).append(",");
				}
				event.data = sb.toString().replace(",$", "");
			}
		});

		// Ziel
		DropTarget dt = new DropTarget(problemsKTable, DND.DROP_COPY);
		dt.setTransfer(new Transfer[] {
			TextTransfer.getInstance()
		});
		dt.addDropListener(new DropTargetListener() {
			@Override
			public void dragEnter(DropTargetEvent event){
				// Wir machen nur Copy-Operationen
				event.detail = DND.DROP_COPY;
			}

			// Mausbewegungen mit gedrückter Taste sind uns egal
			@Override
			public void dragLeave(DropTargetEvent event){}

			@Override
			public void dragOperationChanged(DropTargetEvent event){}

			@Override
			public void dragOver(DropTargetEvent event){}

			// Erst das Loslassen interessiert uns wieder
			@Override
			public void drop(DropTargetEvent event){
				String drp = (String) event.data;
				String[] dl = drp.split(",");
				for (String obj : dl) {
					PersistentObject dropped = CoreHub.poFactory.createFromString(obj);

					// we don't yet support dropping to the problemsKTable
				}
			}

			@Override
			public void dropAccept(DropTargetEvent event){}
		});

		problemDiagnosesCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return journalViewPartName;
			}

			@Override
			public void codeSelected(PersistentObject po){
				if (po instanceof IDiagnose) {
					IDiagnose diagnose = (IDiagnose) po;

					Problem problem = getSelectedProblem();
					if (problem != null) {
						problem.addDiagnose(diagnose);
						IatrixEventHelper.updateProblem(problem);

						if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
							Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
							// re-activate this view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(JournalView.ID);
							} catch (Exception ex) {
								ExHandler.handle(ex);
								log.error("Fehler beim Öffnen von JournalView: " + ex.getMessage());
							}
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				// TODO: ngng highlightProblemsTable(registered);
			}
		};

		// ICodeSelectorTarget for fixmedikation in problems list
		problemFixmedikationCodeSelectorTarget = new ICodeSelectorTarget() {
			@Override
			public String getName(){
				return journalViewPartName;
			}

			@Override
			public void codeSelected(PersistentObject po){
				Problem problem = getSelectedProblem();
				if (problem != null) {
					if (po instanceof Artikel) {
						Artikel artikel = (Artikel) po;

						Prescription prescription =
							new Prescription(artikel, problem.getPatient(), "", "");
						problem.addPrescription(prescription);

						// Let the user set the Prescription properties

						MediDetailDialog dlg = new MediDetailDialog(
							PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							prescription);
						dlg.open();

						// tell other viewers that something has changed
						IatrixEventHelper.updateProblem(problem);

						if (CoreHub.userCfg.get(Iatrix.CFG_CODE_SELECTION_AUTOCLOSE,
							Iatrix.CFG_CODE_SELECTION_AUTOCLOSE_DEFAULT)) {
							// re-activate this view
							try {
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
									.showView(JournalView.ID);
							} catch (Exception ex) {
								ExHandler.handle(ex);
								log.error("Fehler beim Öffnen von JournalView: " + ex.getMessage());
							}
						}
					}
				}
			}

			@Override
			public void registered(boolean registered){
				if (registered) {

					// TODO: ngng highlightProblemsTable(true, true);
				} else {
					// TODO: ngng highlightProblemsTable(false);
				}
			}
		};

	}

	public Problem getSelectedProblem(){
		Point[] selection = problemsKTable.getCellSelection();
		if (selection == null || selection.length == 0) {
			return null;
		} else {
			int rowIndex = selection[0].y - problemsTableModel.getFixedHeaderRowCount();
			Problem problem = problemsTableModel.getProblem(rowIndex);
			return problem;
		}

	}

	static class DummyProblem {}

	public void reloadAndRefresh(){
		problemsTableModel.reload();
		problemsKTable.refresh();
	}

	private void makeActions(){
		delProblemAction = new Action("Problem löschen") {
			@Override
			public void run(){
				log.warn(" TODO Problem löschen"); /// TODO ngng
				Problem problem = getSelectedProblem();
				if (problem != null) {
					String label = problem.getLabel();
					if (StringTool.isNothing(label)) {
						label = Constants.UNKNOWN;
					}
					if (!CoreHub.acl.request(AccessControlDefaults.DELETE_FORCED)) {
						log.error("Das Problem konnte nicht gelöscht werden: " + problem
							+ " missing AccessControlDefaults.DELETE_FORCED");
						SWTHelper.alert("Konnte Problem nicht löschen",
							"Sie haben keine Berechtigung das Problem mit den verknüpften Daten zu löschen. (aka AccessControlDefaults.DELETE_FORCED)");
						return;
					}
					if (MessageDialog.openConfirm(
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						"Wirklich löschen?", label) == true) {
						if (problem.remove(true) == false) {
							log.error("Das Problem konnte nicht gelöscht werden: " + problem);
							SWTHelper.alert("Konnte Problem nicht löschen",
								"Das Problem konnte nicht gelöscht werden.");
						} else {
							reloadAndRefresh();
							;

							// TODO: NGNGN updateProblemAssignmentViewer();
						}
					}
				}
			}

		};
		addProblemAction = new Action("Neues Problem") {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix",
					"rsc/new_problem.ico"));
				setToolTipText("Neues Problem für diesen Patienten erstellen");
			}

			@Override
			public void run(){
				Problem problem = new Problem(ElexisEventDispatcher.getSelectedPatient(), "");
				String currentDate = new TimeTool().toString(TimeTool.DATE_ISO);
				problem.setStartDate(currentDate);
				IatrixEventHelper.fireSelectionEventProblem(problem);

				// neues Problem der aktuellen Konsulation hinzufuegen
				/*
				 * if (actKons != null) { MessageBox mb = new MessageBox(getViewSite().getShell(),
				 * SWT.ICON_QUESTION | SWT.YES | SWT.NO); mb.setText("Neues Problem"); mb
				 * .setMessage("Neues Problem der aktuellen Konsulation zurdnen?"); if (mb.open() ==
				 * SWT.YES) { problem.addToKonsultation(actKons); } }
				 */

				reloadAndRefresh();
				problemsKTable.refresh();

				// select the new object
				int rowIndex = problemsTableModel.getIndexOf(problem);
				if (rowIndex > -1) {
					int col = problemsTableModel.getFixedHeaderColumnCount();
					int row = rowIndex + problemsTableModel.getFixedHeaderRowCount();
					problemsKTable.setSelection(col, row, true);
				}
				ElexisEventDispatcher.fireSelectionEvents(actKons);
			}
		};

		addFixmedikationAction = new Action("Fixmedikation hinzufügen") {
			{
				setToolTipText("Fixmedikation hinzufügen");
			}

			@Override
			public void run(){
				Point[] selection = problemsKTable.getCellSelection();
				if (selection.length != 1) {
					// no problem selected
					SWTHelper.alert("Fixmedikation hinzufügen",
						"Sie können eine Fixmedikation nur dann hinzufügen,"
							+ "wenn Sie in der entsprechenden Spalte der Patientenübersicht stehen.");
					return;
				}

				int row = selection[0].y;
				int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
				Problem problem = problemsTableModel.getProblem(rowIndex);
				if (problem != null) {
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
							.showView(LeistungenView.ID);
						// register as ICodeSelectorTarget
						CodeSelectorHandler.getInstance()
							.setCodeSelectorTarget(problemFixmedikationCodeSelectorTarget);
					} catch (Exception ex) {
						ExHandler.handle(ex);
						log.error("Fehler beim Anzeigen der Artikel " + ex.getMessage());
					}
				}
			}
		};

		editFixmedikationAction = new Action("Fixmedikation ändern...") {
			{
				setToolTipText("Fixmedikation ändern...");
			}

			@Override
			public void run(){
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView(ProblemView.ID);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.error("Fehler beim Öffnen von ProblemView: " + ex.getMessage());
				}
			}
		};

		deleteFixmedikationAction = new Action("Fixmedikation entfernen...") {
			{
				setToolTipText("Fixmedikation entfernen...");
			}

			@Override
			public void run(){
				try {
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
						.showView(ProblemView.ID);
				} catch (Exception ex) {
					ExHandler.handle(ex);
					log.error("Fehler beim Öffnen von ProblemView: " + ex.getMessage());
				}
			}
		};

	}

	public IAction getAddProblemAction(){
		return addProblemAction;
	}

	public IAction getDelProblemAction(){
		return delProblemAction;
	}

	public IAction getAddFixmedikationAction(){
		return addFixmedikationAction;
	}

	public IAction getEditFixmedikationAction(){
		return editFixmedikationAction;
	}

	public IAction getDeleteFixmedikationAction(){
		return deleteFixmedikationAction;
	}

	private final ElexisUiEventListenerImpl eeli_problem =
		new ElexisUiEventListenerImpl(Episode.class, EVENT_UPDATE | EVENT_DESELECTED) {

			@Override
			public void runInUi(ElexisEvent ev){
				switch (ev.getType()) {
				case EVENT_UPDATE:
					break;
				case EVENT_DESELECTED:
					problemsKTable.clearSelection();
					break;
				}

			}
		};
	private Konsultation actKons;
	private boolean heartbeatProblemEnabled;

	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			sb.append(actKons.getId());
			sb.append(" kons vom " + actKons.getDatum());
			sb.append(" " + actKons.getFall().getPatient().getPersonalia());
		}
		// TODO: probleme ?? sb.append(" sum: " + hVerrechnung.getText());
		log.debug(sb.toString());
	}

	public void registerUpdateHeartbeat(){
		Heartbeat heat = Heartbeat.getInstance();
		heat.addListener(new IatrixHeartListener() {
			@Override
			public void heartbeat(){
				logEvent("heartbeatProblem enabled " + heartbeatProblemEnabled);
				if (heartbeatProblemEnabled) {
					// backup selection

					boolean isRowSelectMode = problemsKTable.isRowSelectMode();

					Problem selectedProblem = null;
					int currentColumn = -1;

					if (isRowSelectMode) {
						// full row selection
						// not supported
					} else {
						// single cell selection

						Point[] cells = problemsKTable.getCellSelection();
						if (cells != null && cells.length > 0) {
							int row = cells[0].y;
							int rowIndex = row - problemsTableModel.getFixedHeaderRowCount();
							selectedProblem = problemsTableModel.getProblem(rowIndex);
							currentColumn = cells[0].x;
						}
					}

					// reload data
					setPatient(actKons.getFall().getPatient());

					// restore selection

					if (selectedProblem != null) {
						if (isRowSelectMode) {
							// full row selection
							// not supported
						} else {
							// single cell selection
							int rowIndex = problemsTableModel.getIndexOf(selectedProblem);
							if (rowIndex >= 0) {
								// problem found, i. e. still in list

								int row = rowIndex + problemsTableModel.getFixedHeaderRowCount();
								if (currentColumn == -1) {
									currentColumn = problemsTableModel.getFixedHeaderColumnCount();
								}
								problemsKTable.setSelection(currentColumn, row, true);
							}
						}
					}
				}
			}
		});
	}

	/*
	 * Aktuellen Patienten setzen
	 */
	@Override
	public void setKons(Konsultation newKons, boolean putCaretToEnd){
		if (actKons != newKons) {
			log.trace("setKons " + (newKons == null ? "null" : newKons.getId()));
			actKons = newKons;
		} else {
			log.trace("setKons newKons matches actKons");
		}
	}

	@Override
	public void setPatient(Patient newPatient){
		// problemsKTable.
		problemsTableModel.setPatient(newPatient);
		reloadAndRefresh();
		problemsKTable.refresh();

	}

	public MyKTable getProblemKTable(){
		// TODO Auto-generated method stub
		return problemsKTable;
	}

	public ProblemsTableModel getProblemsTableModel(){
		return problemsTableModel;
	}

	@Override
	public void visible(boolean mode){
		// TODO Auto-generated method stub

	}

	@Override
	public void activation(boolean mode){
		if (mode == true) {
			log.debug("activation " + mode);
			setKons((Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class), false);
		} else {
			setKons(null, false);
		}
	}
}
