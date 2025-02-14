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
package org.iatrix.views;

import static ch.elexis.core.data.events.ElexisEvent.EVENT_DELETE;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_DESELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_SELECTED;
import static ch.elexis.core.data.events.ElexisEvent.EVENT_UPDATE;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart2;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.iatrix.Iatrix;
import org.iatrix.data.Problem;
import org.iatrix.util.Constants;
import org.iatrix.util.Heartbeat;
import org.iatrix.util.Helpers;
import org.iatrix.widgets.IJournalArea;
import org.iatrix.widgets.JournalHeader;
import org.iatrix.widgets.KonsDiagnosen;
import org.iatrix.widgets.KonsHeader;
import org.iatrix.widgets.KonsListDisplay;
import org.iatrix.widgets.KonsProblems;
import org.iatrix.widgets.KonsText;
import org.iatrix.widgets.KonsVerrechnung;
import org.iatrix.widgets.ProblemArea;
import org.iatrix.widgets.ProblemsTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.elexis.admin.AccessControlDefaults;
import ch.elexis.core.data.activator.CoreHub;
import ch.elexis.core.data.events.ElexisEvent;
import ch.elexis.core.data.events.ElexisEventDispatcher;
import ch.elexis.core.ui.UiDesk;
import ch.elexis.core.ui.actions.GlobalActions;
import ch.elexis.core.ui.actions.GlobalEventDispatcher;
import ch.elexis.core.ui.actions.IActivationListener;
import ch.elexis.core.ui.events.ElexisUiEventListenerImpl;
import ch.elexis.core.ui.icons.Images;
import ch.elexis.core.ui.util.SWTHelper;
import ch.elexis.core.ui.util.ViewMenus;
import ch.elexis.data.Anwender;
import ch.elexis.data.Fall;
import ch.elexis.data.Konsultation;
import ch.elexis.data.Patient;
import ch.elexis.extdoc.util.Email;
import ch.elexis.icpc.Episode;
import ch.rgw.tools.TimeTool;
import de.kupzog.ktable.KTable;

/**
 * KG-Ansicht nach Iatrix-Vorstellungen
 *
 * Oben wird die Problemliste dargestellt, unten die aktuelle Konsultation und die bisherigen
 * Konsultationen. Hinweis: Es wird sichergestellt, dass die Problemliste und die Konsultation(en)
 * zum gleichen Patienten gehoeren.
 *
 * TODO Definieren, wann welcher Patient und welche Konsultation gesetzt werden soll. Wie mit
 * Faellen umgehen? TODO adatpMenu as in KonsDetailView TODO check compatibility of assigned
 * problems if fall is changed
 *
 * @author Daniel Lutz <danlutz@watz.ch>
 */

public class JournalView extends ViewPart implements IActivationListener, ISaveablePart2 {

	public static final String ID = Constants.ID;

	private static Logger log = LoggerFactory.getLogger(JournalView.class);
	private Patient patient = null;
	private Konsultation actKons = null;

	private FormToolkit tk;
	private Form form;

	// container for hKonsultationDatum, hlMandant, cbFall

	// Parts (from top to bottom that make up our display
	private JournalHeader formHeader = null; // Patient name, sex, birthday, remarks, sticker, account balance, account overview
	private ProblemsTableModel problemsTableModel; // TODO: moved to external
	private KTable problemsKTable = null; // TODO: moved to external
	// date, mandant, Fall (drop-down list)
	private ProblemArea problemsArea = null; // KTable with Date, nr, diagnosis, therapy, code, activ/inactiv
	private KonsProblems konsProblems = null; // Checkbox of all problems for this consultation
	private KonsText konsTextComposite; // Konsultationtext (with lock over all stations), revision info
	private KonsVerrechnung konsVerrechnung = null; // Items to be billed for select consultation
	private KonsDiagnosen konsDiagnosen = null; // diagnosis line
	private KonsListDisplay konsListDisplay; // list of all consultations date, decreasing with date, mandant, case, text, billed items

	private ViewMenus menus;

	/* Actions */
	private IAction exportToClipboardAction;
	private IAction sendEmailAction;
	private IAction addKonsultationAction;
	private Action showAllChargesAction;
	private Action showAllConsultationsAction;

	private boolean heartbeatActive = false;
	private List<IJournalArea> allAreas;

	private KonsHeader konsHeader;

	private Heartbeat heartbeat;

	@Override
	public void createPartControl(Composite parent){
		parent.setLayout(new FillLayout());
		heartbeat = Heartbeat.getInstance();
		tk = UiDesk.getToolkit();
		form = tk.createForm(parent);
		Composite formBody = form.getBody();

		formBody.setLayout(new GridLayout(1, true));
		formHeader = new JournalHeader(formBody);

		SashForm mainSash = new SashForm(form.getBody(), SWT.VERTICAL);
		mainSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite topArea = tk.createComposite(mainSash, SWT.NONE);
		topArea.setLayout(new FillLayout(SWT.VERTICAL));
		topArea.setBackground(topArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));

		problemsArea = new ProblemArea(topArea, JournalView.this.getPartName());
		problemsKTable = problemsArea.getProblemKTable();
		problemsTableModel = problemsArea.getProblemsTableModel();
		Composite middleArea = tk.createComposite(mainSash, SWT.NONE);
		middleArea.setLayout(new FillLayout());
		Composite konsultationComposite = tk.createComposite(middleArea);
		konsultationComposite.setLayout(new GridLayout(1, true));

		konsHeader = new KonsHeader(konsultationComposite);

		SashForm konsultationSash = new SashForm(konsultationComposite, SWT.HORIZONTAL);
		konsultationSash.setLayoutData(SWTHelper.getFillGridData(1, true, 1, true));

		Composite assignmentComposite = tk.createComposite(konsultationSash);
		assignmentComposite.setLayout(new GridLayout(1, true));
		konsProblems = new KonsProblems(assignmentComposite);
		System.out.println("konsSash 1 has " + konsultationSash.getChildren().length + " children");
		Composite konsultationTextComposite = tk.createComposite(konsultationSash);
		System.out.println("konsSash 2 has " + konsultationSash.getChildren().length + " children");
		konsultationTextComposite.setLayout(new GridLayout(1, true));
		konsTextComposite = new KonsText(konsultationTextComposite);
		System.out.println("konsSash 3 has " + konsultationSash.getChildren().length + " children");
		konsDiagnosen = new KonsDiagnosen(konsultationComposite);
		Composite verrechnungComposite = tk.createComposite(konsultationSash);
		konsVerrechnung = new KonsVerrechnung(verrechnungComposite, form,
			JournalView.this.getPartName(), assignmentComposite);
		if (konsultationSash.getChildren().length == 3) {
			konsultationSash.setWeights(new int[] {
				15, 65, 20
			});
		} else {
			System.out.println("konsSash should have 3, but has "
				+ konsultationSash.getChildren().length + " children");
		}
		Composite bottomArea = tk.createComposite(mainSash, SWT.NONE);
		bottomArea.setLayout(new FillLayout());
		bottomArea.setBackground(bottomArea.getDisplay().getSystemColor(SWT.COLOR_WHITE));
		konsListDisplay = new KonsListDisplay(bottomArea);

		mainSash.setWeights(new int[] {
			20, 40, 30
		});
		allAreas = new ArrayList<IJournalArea>();
		allAreas.add(formHeader);
		allAreas.add(problemsArea);
		allAreas.add(konsHeader);
		allAreas.add(konsProblems);
		allAreas.add(konsDiagnosen);
		allAreas.add(konsTextComposite);
		allAreas.add(konsVerrechnung);
		makeActions();
		menus = new ViewMenus(getViewSite());
		if (CoreHub.acl.request(AccessControlDefaults.AC_PURGE)) {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction,
				problemsArea.addProblemAction, GlobalActions.delKonsAction,
				problemsArea.delProblemAction, exportToClipboardAction, sendEmailAction,
				konsTextComposite.getVersionForwardAction(),
				konsTextComposite.getVersionBackAction(),
				konsTextComposite.getChooseVersionAction(), konsTextComposite.getPurgeAction(),
				konsTextComposite.getSaveAction(), showAllConsultationsAction, showAllChargesAction,
				problemsArea.addFixmedikationAction);
		} else {
			menus.createMenu(addKonsultationAction, GlobalActions.redateAction,
				problemsArea.addProblemAction, GlobalActions.delKonsAction,
				problemsArea.delProblemAction, exportToClipboardAction, sendEmailAction,
				konsTextComposite.getVersionForwardAction(),
				konsTextComposite.getVersionBackAction(),
				konsTextComposite.getChooseVersionAction(), konsTextComposite.getSaveAction(),
				showAllConsultationsAction, showAllChargesAction,
				problemsArea.addFixmedikationAction);
		}

		menus.createToolbar(sendEmailAction, exportToClipboardAction, addKonsultationAction,
			problemsArea.getAddProblemAction(), konsTextComposite.getSaveAction());
		menus.createViewerContextMenu(konsProblems.getProblemAssignmentViewer(),
			konsProblems.unassignProblemAction);
		menus.createViewerContextMenu(konsVerrechnung.getVerrechnungViewer(),
			konsVerrechnung.changeVerrechnetPreisAction, konsVerrechnung.changeVerrechnetZahlAction,
			konsVerrechnung.delVerrechnetAction);

		GlobalEventDispatcher.addActivationListener(this, this);
		activateContext();
	}

	private void updateAllPatientAreas(Patient newPatient){
		logEvent("updateAllPatientAreas: " + newPatient);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setPatient(newPatient);
			}
		}
	}

	static String savedKonsId = "-";
	static Konsultation savedKons;

	private void updateAllKonsAreas(Konsultation newKons, boolean putCaretToEnd){
		/* Not yet sure whether comparing only the id or the whole cons is better
		 */
		actKons = newKons;
		String newId = newKons == null ? "null" : newKons.getId();
		// It is a bad idea to skip updating the kons, when the Id matches
		if (savedKons == newKons && savedKons == null) {
			// Some changes, e.g. when date of actual kons are possible even when the compare matches.
			// Therefore we return only when we have nothing to update savedKonst == newKons?" + newId + " konsId match? " + savedKonsId.equals(newId));
			return;
		}
		savedKons = newKons;
		savedKonsId = newId;
		logEvent("updateAllKonsAreas: " + newId + " konsId match? " + savedKonsId.equals(newId));
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.setKons(newKons, putCaretToEnd);
			}
		}
	}

	private void activateAllKonsAreas(boolean mode){
		logEvent("activateAllKonsAreas: " + mode);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.activation(mode);
			}
		}
	}

	private void visibleAllKonsAreas(boolean mode){
		logEvent("visibleAllKonsAreas: " + mode);
		for (int i = 0; i < allAreas.size(); i++) {
			IJournalArea a = allAreas.get(i);
			if (a != null) {
				a.visible(mode);
			}
		}
	}

	private final ElexisUiEventListenerImpl eeli_problem =
		new ElexisUiEventListenerImpl(Episode.class, EVENT_UPDATE | EVENT_DESELECTED) {

			@Override
			public void runInUi(ElexisEvent ev){
				switch (ev.getType()) {
				case EVENT_UPDATE:
					// problem change may affect current problems list and consultation
					// TODO check if problem is part of current consultation
					// work-around: just update the current patient and consultation
					logEvent("eeli_problem EVENT_UPDATE");
					setPatient(patient);
					// TODO ngng: konskonsArea.updateKonsultation(!konsEditorHasFocus, false);
					break;
				case EVENT_DESELECTED:
					logEvent("eeli_problem EVENT_DESELECTED");
					problemsKTable.clearSelection();
					break;
				}

			}
		};

	private final ElexisUiEventListenerImpl eeli_kons = new ElexisUiEventListenerImpl(
		Konsultation.class, EVENT_DELETE | EVENT_UPDATE | EVENT_SELECTED | EVENT_DESELECTED) {

		@Override
		public void runInUi(ElexisEvent ev){
			Konsultation k = (Konsultation) ev.getObject();
			logEvent("eeli_kons " + (k != null ? k.getId(): "null") + " typ " + ev.getType());
			switch (ev.getType()) {
			case EVENT_UPDATE:
				updateAllKonsAreas(k, true);
				break;
			case EVENT_DELETE:
				updateAllKonsAreas(k, false);
				break;
			case EVENT_SELECTED:
				logEvent("eeli_kons EVENT_SELECTED");
				updateAllKonsAreas(k, true);
				break;
			case EVENT_DESELECTED:
				logEvent("eeli_kons EVENT_DESELECTED null");
				updateAllKonsAreas(null, true);
				break;
			}

		}

	};

	private final ElexisUiEventListenerImpl eeli_fall =
		new ElexisUiEventListenerImpl(Fall.class, ElexisEvent.EVENT_SELECTED) {
			@Override
			public void runInUi(ElexisEvent ev){
				Fall fall = (Fall) ev.getObject();
				Patient patient = fall.getPatient();
				logEvent("eeli_fall EVENT_SELECTED fall " + fall.getId());

				// falls aktuell ausgewaehlte Konsulation zu diesem Fall
				// gehoert,
				// diese setzen
				Konsultation konsulation =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (konsulation != null) {
					if (konsulation.getFall().getId().equals(fall.getId())) {
						// diese Konsulation gehoert zu diesem Patienten

						setPatient(patient);
						updateAllKonsAreas(konsulation, true);
						return;
					}
				}

				// sonst die aktuellste Konsulation des Falls setzen
				konsulation = Helpers.getTodaysLatestKons(fall);

				setPatient(patient);
				updateAllKonsAreas(null, true);

			}
		};
	private final ElexisUiEventListenerImpl eeli_pat =
		new ElexisUiEventListenerImpl(Patient.class) {

			@Override
			public void runInUi(ElexisEvent ev){
				if (ev.getType() == ElexisEvent.EVENT_SELECTED) {
					Patient selectedPatient = (Patient) ev.getObject();
					logEvent("eeli_pat EVENT_SELECTED " + selectedPatient.getId());

					showAllChargesAction.setChecked(false);
					showAllConsultationsAction.setChecked(false);

					Patient patient = null;
					Fall fall = null;
					Konsultation konsultation = null;

					konsultation =
						(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
					if (konsultation != null) {
						// diese Konsulation setzen, falls sie zum ausgewaehlten Patienten gehoert
						fall = konsultation.getFall();
						patient = fall.getPatient();
						logEvent("runInUi eeli_pat EVENT_SELECTED kons " + konsultation.getId());
						if (patient.getId().equals(selectedPatient.getId())) {
							setPatient(patient);
							updateAllKonsAreas(konsultation, true);
							return;
						}
					}

					// Konsulation gehoert nicht zu diesem Patienten, Fall
					// untersuchen
					fall = (Fall) ElexisEventDispatcher.getSelected(Fall.class);
					if (fall != null) {
						patient = fall.getPatient();
						if (patient.getId().equals(selectedPatient.getId())) {
							// aktuellste Konsultation dieses Falls waehlen
							konsultation = Helpers.getTodaysLatestKons(fall);
							logEvent(
								"runInUi eeli_pat EVENT_SELECTED kons Konsulation gehoert nicht zu diesem Patienten "
									+ patient.getPersonalia());

							setPatient(patient);
							updateAllKonsAreas(null, true);
							return;
						}
					}

					// weder aktuell ausgewaehlte Konsulation noch aktuell
					// ausgewaehlter Fall gehoeren zu diesem Patienten
					setPatient(selectedPatient);

					// lezte Kons setzen, falls heutiges Datum
					Konsultation letzteKons = Helpers.getTodaysLatestKons(selectedPatient);
					if (letzteKons != null) {
						TimeTool letzteKonsDate = new TimeTool(letzteKons.getDatum());
						TimeTool today = new TimeTool();
						if (!letzteKonsDate.isSameDay(today)) {
							letzteKons = null;
						}
						logEvent("runInUi eeli_pat EVENT_SELECTED letzte Kons setzen"
							+ selectedPatient.getPersonalia());
						updateAllKonsAreas(letzteKons, true);
					} else {
						// When no consultation is selected we must create one (maybe a case, too)
						// This allows one to start working, as soon as possible
						if (fall == null) {
							Fall[] faelle = selectedPatient.getFaelle();
							if (faelle.length == 0) {
								actKons = selectedPatient.createFallUndKons();
								logEvent(
									"runInUi eeli_pat EVENT_SELECTED create FallUndKons as none selected"
										+ selectedPatient.getPersonalia());
							} else {
								actKons = faelle[0].getLetzteBehandlung();
								if (actKons == null) {
									actKons = faelle[0].neueKonsultation();
									logEvent(
										"runInUi eeli_pat EVENT_SELECTED create kons for faelle[0]"
											+ selectedPatient.getPersonalia());
								} else {
									logEvent(
										"runInUi eeli_pat EVENT_SELECTED found kons for for faelle[0]"
											+ selectedPatient.getPersonalia());
								}
							}
						} else {
							logEvent("runInUi eeli_pat EVENT_SELECTED create kons for fall"
								+ selectedPatient.getPersonalia());
							actKons = fall.neueKonsultation();
						}
						updateAllKonsAreas(actKons, true);
					}

				} else if (ev.getType() == ElexisEvent.EVENT_DESELECTED) {
					logEvent("runInUi eeli_pat EVENT_DESELECTED");
					setPatient(null);
					updateAllKonsAreas(null, true);
				}
			}

		};

	private final ElexisUiEventListenerImpl eeli_user =
		new ElexisUiEventListenerImpl(Anwender.class, ElexisEvent.EVENT_USER_CHANGED) {
			@Override
			public void runInUi(ElexisEvent ev){
				logEvent("runInUi eeli_user adaptMenus");
				adaptMenus();
			}

		};

	/**
	 * Activate a context that this view uses. It will be tied to this view activation events and
	 * will be removed when the view is disposed. Copied from
	 * org.eclipse.ui.examples.contributions.InfoView.java
	 */
	private void activateContext(){
		IContextService contextService =
			(IContextService) getSite().getService(IContextService.class);
		contextService.activateContext(Constants.VIEW_CONTEXT_ID);
	}

	@Override
	public void dispose(){
		// konsTextComposite.removeKonsTextLock(); ngng not needed. is calle by setKons(null)
		GlobalEventDispatcher.removeActivationListener(this, this);
		super.dispose();
	}

	@Override
	public void setFocus(){
		// TODO Auto-generated method stub

	}

	public void adaptMenus(){
		konsVerrechnung.getVerrechnungViewer().getTable().getMenu()
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.LSTG_VERRECHNEN));

		// TODO this belongs to GlobalActions itself (action creator)
		GlobalActions.delKonsAction
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.KONS_DELETE));
		GlobalActions.neueKonsAction
			.setEnabled(CoreHub.acl.request(AccessControlDefaults.KONS_CREATE));
	}

	private void makeActions(){
		// Konsultation

		// Replacement for GlobalActions.neueKonsAction (other image)
		addKonsultationAction = new Action(GlobalActions.neueKonsAction.getText()) {
			{
				setImageDescriptor(AbstractUIPlugin.imageDescriptorFromPlugin("org.iatrix",
					"rsc/new_konsultation.ico"));
				setToolTipText(GlobalActions.neueKonsAction.getToolTipText());
			}

			@Override
			public void run(){
				GlobalActions.neueKonsAction.run();
			}
		};
		addKonsultationAction.setActionDefinitionId(Constants.NEWCONS_COMMAND);
		GlobalActions.registerActionHandler(this, addKonsultationAction);

		// Probleme
		if (problemsArea != null) {
			GlobalActions.registerActionHandler(this, problemsArea.addProblemAction);
			problemsArea.addProblemAction.setActionDefinitionId(Constants.NEWPROBLEM_COMMAND);
		}

		exportToClipboardAction = new Action("Export (Zwischenablage)") {
			{
				setImageDescriptor(Images.IMG_EXPORT.getImageDescriptor());
				setToolTipText("Zusammenfassung in Zwischenablage kopieren");
			}

			@Override
			public void run(){
				Helpers.exportToClipboard(patient, null); // TODO: selected problem
			}
		};
		exportToClipboardAction.setActionDefinitionId(Constants.EXPORT_CLIPBOARD_COMMAND);
		GlobalActions.registerActionHandler(this, exportToClipboardAction);

		sendEmailAction = new Action("E-Mail verschicken") {
			{
				setImageDescriptor(Images.IMG_MAIL.getImageDescriptor());
				setToolTipText("E-Mail Programm öffnent (mit Medikation und allen Konsultationen)");
			}

			@Override
			public void run(){
				Email.openMailApplication("", // No default to address
					null, Helpers.exportToClipboard(patient, null), // TODO: selected problem
					null);

			}
		};
		sendEmailAction.setActionDefinitionId(Constants.EXPORT_SEND_EMAIL_COMMAND);
		GlobalActions.registerActionHandler(this, sendEmailAction);

		// history display
		showAllChargesAction = new Action("Alle Leistungen anzeigen", Action.AS_CHECK_BOX) {
			{
				setToolTipText(
					"Leistungen aller Konsultationen anzeigen, nicht nur der ersten paar.");
			}

			@Override
			public void run(){
				konsListDisplay.setPatient(patient, showAllChargesAction.isChecked(),
					showAllConsultationsAction.isChecked());
			}
		};
		showAllChargesAction.setActionDefinitionId(Iatrix.SHOW_ALL_CHARGES_COMMAND);
		GlobalActions.registerActionHandler(this, showAllChargesAction);

		showAllConsultationsAction =
			new Action("Alle Konsultationen anzeigen", Action.AS_CHECK_BOX) {
				{
					setToolTipText("Alle Konsultationen anzeigen");
				}

				@Override
				public void run(){
					konsListDisplay.setPatient(patient, showAllChargesAction.isChecked(),
						showAllConsultationsAction.isChecked());
				}
			};
		showAllConsultationsAction.setActionDefinitionId(Iatrix.SHOW_ALL_CONSULTATIONS_COMMAND);
		GlobalActions.registerActionHandler(this, showAllConsultationsAction);
	}

	@Override
	public void activation(boolean mode){
		activateAllKonsAreas(mode);
	}

	@Override
	public void visible(boolean mode){
		if (mode == true) {
			showAllChargesAction.setChecked(false);
			showAllConsultationsAction.setChecked(false);

			ElexisEventDispatcher.getInstance().addListeners(eeli_kons, eeli_problem, eeli_fall,
				eeli_pat, eeli_user);

			Patient patient = ElexisEventDispatcher.getSelectedPatient();
			setPatient(patient);

			/*
			 * setPatient(Patient) setzt eine neue Konsultation, falls bereits eine gestzt ist und
			 * diese nicht zum neuen Patienten gehoert. Ansonsten sollten wir die letzte
			 * Konsultation des Paitenten setzten.
			 */
			if (actKons == null) {
				Konsultation kons =
					(Konsultation) ElexisEventDispatcher.getSelected(Konsultation.class);
				if (kons != null) {
					if (!kons.getFall().getPatient().equals(patient) && patient!=null) {
						kons = patient.getLetzteKons(false);
					}
				}
				updateAllKonsAreas(kons, false);
			}
			heartbeat.enableListener(true);
		} else {
			heartbeat.enableListener(false);
			ElexisEventDispatcher.getInstance().removeListeners(eeli_kons, eeli_problem, eeli_fall,
				eeli_pat, eeli_user);

			/*
			 * setPatient(null) ruft setKonsultation(null) auf.
			 */
			setPatient(null);
		}
		visibleAllKonsAreas(mode);
	};

	private void logEvent(String msg){
		StringBuilder sb = new StringBuilder(msg + ": ");
		if (actKons == null) {
			sb.append("actKons null");
		} else {
			Fall f = actKons.getFall();
			if (f != null) {
				Patient pat = f.getPatient();
				sb.append(actKons.getId());
				sb.append(" kons vom " + actKons.getDatum());
				sb.append(" " + pat.getId() + ": " + pat.getPersonalia());
			}
		}
		log.debug(sb.toString());
	}

	/*
	 * Aktuellen Patienten setzen
	 */
	public void setPatient(Patient newPatient){
		if (patient == newPatient
			|| (patient != null && newPatient != null && patient.getId() == newPatient.getId())) {
			logEvent("setPatient skipped als alread set "
				+ (newPatient == null ? "null" : newPatient.getId()));
			return;
		}
		logEvent("setPatient " + (newPatient == null ? "null" : newPatient.getId()));
		patient = newPatient;

		// widgets may be disposed when application is closed
		if (form.isDisposed()) {
			return;
		}
		updateAllPatientAreas(patient);

		if (patient != null) {
			// Pruefe, ob Patient Probleme hat, sonst Standardproblem erstellen
			List<Problem> problems = Problem.getProblemsOfPatient(patient);
			if (problems.size() == 0) {
				// TODO don't yet do this
				// Problem.createStandardProblem(actPatient);
			}
		} else {
			// Kein Patient ausgewaehlt, somit auch keine Konsultation anzeigen
			updateAllKonsAreas(null, true);
			// Konsistenz Patient/Konsultation ueberpruefen
			if (actKons != null) {
				if (!actKons.getFall().getPatient().getId().equals(patient.getId())) {
					// aktuelle Konsultation gehoert nicht zum aktuellen Patienten
					logEvent("aktuelle Konsultation gehoert nicht zum aktuellen Patienten");
					// setKonsultation(actPatient.getLetzteKons(false), false);
				}
			}
		}
		if (konsListDisplay != null) {
			konsListDisplay.setPatient(patient, showAllChargesAction.isChecked(),
				showAllConsultationsAction.isChecked());
		}
		logEvent("setPatient done");
	}

	/***********************************************************************************************
	 * Die folgenden 6 Methoden implementieren das Interface ISaveablePart2 Wir benötigen das
	 * Interface nur, um das Schliessen einer View zu verhindern, wenn die Perspektive fixiert ist.
	 * Gibt es da keine einfachere Methode?
	 */
	@Override
	public int promptToSaveOnClose(){
		return GlobalActions.fixLayoutAction.isChecked() ? ISaveablePart2.CANCEL
				: ISaveablePart2.NO;
	}

	@Override
	public void doSave(IProgressMonitor monitor){ /* leer */}

	@Override
	public void doSaveAs(){ /* leer */}

	@Override
	public boolean isDirty(){
		return true;
	}

	@Override
	public boolean isSaveAsAllowed(){
		return false;
	}

	@Override
	public boolean isSaveOnCloseNeeded(){
		return true;
	}
}
