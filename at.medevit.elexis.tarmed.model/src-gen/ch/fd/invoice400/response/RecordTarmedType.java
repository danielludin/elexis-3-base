//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import java.math.BigInteger;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>
 * Java class for recordTarmedType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="recordTarmedType">
 *   &lt;simpleContent>
 *     &lt;extension base="&lt;http://www.xmlData.ch/xmlInvoice/XSD>stringType1_350">
 *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="amount.mt" type="{http://www.w3.org/2001/XMLSchema}double" default="0.0" />
 *       &lt;attribute name="amount.tt" type="{http://www.w3.org/2001/XMLSchema}double" default="0.0" />
 *       &lt;attribute name="billing_role" default="both">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="mt"/>
 *             &lt;enumeration value="tt"/>
 *             &lt;enumeration value="both"/>
 *             &lt;enumeration value="none"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="body_location" default="none">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="none"/>
 *             &lt;enumeration value="left"/>
 *             &lt;enumeration value="right"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="code" use="required" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_20" />
 *       &lt;attribute name="comment" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_350" />
 *       &lt;attribute name="date_begin" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="date_end" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="ean_provider" use="required" type="{http://www.xmlData.ch/xmlInvoice/XSD}eanPartyType" />
 *       &lt;attribute name="ean_responsible" use="required" type="{http://www.xmlData.ch/xmlInvoice/XSD}eanPartyType" />
 *       &lt;attribute name="external_factor.mt" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="external_factor.tt" default="1.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="medical_role" default="self_employed">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="self_employed"/>
 *             &lt;enumeration value="employee"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="number" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" default="1" />
 *       &lt;attribute name="obligation" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="quantity" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="record_id" use="required" type="{http://www.w3.org/2001/XMLSchema}positiveInteger" />
 *       &lt;attribute name="ref_code" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_20" />
 *       &lt;attribute name="remark" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_350" />
 *       &lt;attribute name="scale_factor.mt" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="scale_factor.tt" type="{http://www.w3.org/2001/XMLSchema}double" default="1.0" />
 *       &lt;attribute name="status" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="added"/>
 *             &lt;enumeration value="corrected"/>
 *             &lt;enumeration value="rejected"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="tariff_type" type="{http://www.w3.org/2001/XMLSchema}string" fixed="001" />
 *       &lt;attribute name="treatment" fixed="ambulatory">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="ambulatory"/>
 *             &lt;enumeration value="semi_stationary"/>
 *             &lt;enumeration value="stationary"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unit.mt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unit.tt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unit_factor.mt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minExclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="unit_factor.tt" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minExclusive value="0.0"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="validate" type="{http://www.w3.org/2001/XMLSchema}boolean" default="true" />
 *       &lt;attribute name="vat_rate" default="0.0">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}double">
 *             &lt;minInclusive value="0"/>
 *             &lt;maxInclusive value="100"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/extension>
 *   &lt;/simpleContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "recordTarmedType", propOrder = {
	"value"
})
public class RecordTarmedType {
	
	@XmlValue
	protected String value;
	@XmlAttribute(required = true)
	protected double amount;
	@XmlAttribute(name = "amount.mt")
	protected Double amountMt;
	@XmlAttribute(name = "amount.tt")
	protected Double amountTt;
	@XmlAttribute(name = "billing_role")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String billingRole;
	@XmlAttribute(name = "body_location")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String bodyLocation;
	@XmlAttribute(required = true)
	protected String code;
	@XmlAttribute
	protected String comment;
	@XmlAttribute(name = "date_begin", required = true)
	protected XMLGregorianCalendar dateBegin;
	@XmlAttribute(name = "date_end")
	protected XMLGregorianCalendar dateEnd;
	@XmlAttribute(name = "ean_provider", required = true)
	protected String eanProvider;
	@XmlAttribute(name = "ean_responsible", required = true)
	protected String eanResponsible;
	@XmlAttribute(name = "external_factor.mt")
	protected Double externalFactorMt;
	@XmlAttribute(name = "external_factor.tt")
	protected Double externalFactorTt;
	@XmlAttribute(name = "medical_role")
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String medicalRole;
	@XmlAttribute
	protected BigInteger number;
	@XmlAttribute
	protected Boolean obligation;
	@XmlAttribute(required = true)
	protected double quantity;
	@XmlAttribute(name = "record_id", required = true)
	protected BigInteger recordId;
	@XmlAttribute(name = "ref_code")
	protected String refCode;
	@XmlAttribute
	protected String remark;
	@XmlAttribute(name = "scale_factor.mt")
	protected Double scaleFactorMt;
	@XmlAttribute(name = "scale_factor.tt")
	protected Double scaleFactorTt;
	@XmlAttribute(required = true)
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String status;
	@XmlAttribute(name = "tariff_type")
	protected String tariffType;
	@XmlAttribute
	@XmlJavaTypeAdapter(CollapsedStringAdapter.class)
	protected String treatment;
	@XmlAttribute(name = "unit.mt", required = true)
	protected double unitMt;
	@XmlAttribute(name = "unit.tt", required = true)
	protected double unitTt;
	@XmlAttribute(name = "unit_factor.mt", required = true)
	protected double unitFactorMt;
	@XmlAttribute(name = "unit_factor.tt", required = true)
	protected double unitFactorTt;
	@XmlAttribute
	protected Boolean validate;
	@XmlAttribute(name = "vat_rate")
	protected Double vatRate;
	
	/**
	 * Gets the value of the value property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getValue(){
		return value;
	}
	
	/**
	 * Sets the value of the value property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setValue(String value){
		this.value = value;
	}
	
	/**
	 * Gets the value of the amount property.
	 * 
	 */
	public double getAmount(){
		return amount;
	}
	
	/**
	 * Sets the value of the amount property.
	 * 
	 */
	public void setAmount(double value){
		this.amount = value;
	}
	
	/**
	 * Gets the value of the amountMt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getAmountMt(){
		if (amountMt == null) {
			return 0.0D;
		} else {
			return amountMt;
		}
	}
	
	/**
	 * Sets the value of the amountMt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setAmountMt(Double value){
		this.amountMt = value;
	}
	
	/**
	 * Gets the value of the amountTt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getAmountTt(){
		if (amountTt == null) {
			return 0.0D;
		} else {
			return amountTt;
		}
	}
	
	/**
	 * Sets the value of the amountTt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setAmountTt(Double value){
		this.amountTt = value;
	}
	
	/**
	 * Gets the value of the billingRole property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBillingRole(){
		if (billingRole == null) {
			return "both";
		} else {
			return billingRole;
		}
	}
	
	/**
	 * Sets the value of the billingRole property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBillingRole(String value){
		this.billingRole = value;
	}
	
	/**
	 * Gets the value of the bodyLocation property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getBodyLocation(){
		if (bodyLocation == null) {
			return "none";
		} else {
			return bodyLocation;
		}
	}
	
	/**
	 * Sets the value of the bodyLocation property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setBodyLocation(String value){
		this.bodyLocation = value;
	}
	
	/**
	 * Gets the value of the code property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCode(){
		return code;
	}
	
	/**
	 * Sets the value of the code property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCode(String value){
		this.code = value;
	}
	
	/**
	 * Gets the value of the comment property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getComment(){
		return comment;
	}
	
	/**
	 * Sets the value of the comment property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setComment(String value){
		this.comment = value;
	}
	
	/**
	 * Gets the value of the dateBegin property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDateBegin(){
		return dateBegin;
	}
	
	/**
	 * Sets the value of the dateBegin property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDateBegin(XMLGregorianCalendar value){
		this.dateBegin = value;
	}
	
	/**
	 * Gets the value of the dateEnd property.
	 * 
	 * @return possible object is {@link XMLGregorianCalendar }
	 * 
	 */
	public XMLGregorianCalendar getDateEnd(){
		return dateEnd;
	}
	
	/**
	 * Sets the value of the dateEnd property.
	 * 
	 * @param value
	 *            allowed object is {@link XMLGregorianCalendar }
	 * 
	 */
	public void setDateEnd(XMLGregorianCalendar value){
		this.dateEnd = value;
	}
	
	/**
	 * Gets the value of the eanProvider property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEanProvider(){
		return eanProvider;
	}
	
	/**
	 * Sets the value of the eanProvider property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEanProvider(String value){
		this.eanProvider = value;
	}
	
	/**
	 * Gets the value of the eanResponsible property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getEanResponsible(){
		return eanResponsible;
	}
	
	/**
	 * Sets the value of the eanResponsible property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setEanResponsible(String value){
		this.eanResponsible = value;
	}
	
	/**
	 * Gets the value of the externalFactorMt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getExternalFactorMt(){
		if (externalFactorMt == null) {
			return 1.0D;
		} else {
			return externalFactorMt;
		}
	}
	
	/**
	 * Sets the value of the externalFactorMt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setExternalFactorMt(Double value){
		this.externalFactorMt = value;
	}
	
	/**
	 * Gets the value of the externalFactorTt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getExternalFactorTt(){
		if (externalFactorTt == null) {
			return 1.0D;
		} else {
			return externalFactorTt;
		}
	}
	
	/**
	 * Sets the value of the externalFactorTt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setExternalFactorTt(Double value){
		this.externalFactorTt = value;
	}
	
	/**
	 * Gets the value of the medicalRole property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getMedicalRole(){
		if (medicalRole == null) {
			return "self_employed";
		} else {
			return medicalRole;
		}
	}
	
	/**
	 * Sets the value of the medicalRole property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setMedicalRole(String value){
		this.medicalRole = value;
	}
	
	/**
	 * Gets the value of the number property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getNumber(){
		if (number == null) {
			return new BigInteger("1");
		} else {
			return number;
		}
	}
	
	/**
	 * Sets the value of the number property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setNumber(BigInteger value){
		this.number = value;
	}
	
	/**
	 * Gets the value of the obligation property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isObligation(){
		if (obligation == null) {
			return true;
		} else {
			return obligation;
		}
	}
	
	/**
	 * Sets the value of the obligation property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setObligation(Boolean value){
		this.obligation = value;
	}
	
	/**
	 * Gets the value of the quantity property.
	 * 
	 */
	public double getQuantity(){
		return quantity;
	}
	
	/**
	 * Sets the value of the quantity property.
	 * 
	 */
	public void setQuantity(double value){
		this.quantity = value;
	}
	
	/**
	 * Gets the value of the recordId property.
	 * 
	 * @return possible object is {@link BigInteger }
	 * 
	 */
	public BigInteger getRecordId(){
		return recordId;
	}
	
	/**
	 * Sets the value of the recordId property.
	 * 
	 * @param value
	 *            allowed object is {@link BigInteger }
	 * 
	 */
	public void setRecordId(BigInteger value){
		this.recordId = value;
	}
	
	/**
	 * Gets the value of the refCode property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRefCode(){
		return refCode;
	}
	
	/**
	 * Sets the value of the refCode property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRefCode(String value){
		this.refCode = value;
	}
	
	/**
	 * Gets the value of the remark property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getRemark(){
		return remark;
	}
	
	/**
	 * Sets the value of the remark property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setRemark(String value){
		this.remark = value;
	}
	
	/**
	 * Gets the value of the scaleFactorMt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getScaleFactorMt(){
		if (scaleFactorMt == null) {
			return 1.0D;
		} else {
			return scaleFactorMt;
		}
	}
	
	/**
	 * Sets the value of the scaleFactorMt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setScaleFactorMt(Double value){
		this.scaleFactorMt = value;
	}
	
	/**
	 * Gets the value of the scaleFactorTt property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getScaleFactorTt(){
		if (scaleFactorTt == null) {
			return 1.0D;
		} else {
			return scaleFactorTt;
		}
	}
	
	/**
	 * Sets the value of the scaleFactorTt property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setScaleFactorTt(Double value){
		this.scaleFactorTt = value;
	}
	
	/**
	 * Gets the value of the status property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getStatus(){
		return status;
	}
	
	/**
	 * Sets the value of the status property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setStatus(String value){
		this.status = value;
	}
	
	/**
	 * Gets the value of the tariffType property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTariffType(){
		if (tariffType == null) {
			return "001";
		} else {
			return tariffType;
		}
	}
	
	/**
	 * Sets the value of the tariffType property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTariffType(String value){
		this.tariffType = value;
	}
	
	/**
	 * Gets the value of the treatment property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getTreatment(){
		if (treatment == null) {
			return "ambulatory";
		} else {
			return treatment;
		}
	}
	
	/**
	 * Sets the value of the treatment property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setTreatment(String value){
		this.treatment = value;
	}
	
	/**
	 * Gets the value of the unitMt property.
	 * 
	 */
	public double getUnitMt(){
		return unitMt;
	}
	
	/**
	 * Sets the value of the unitMt property.
	 * 
	 */
	public void setUnitMt(double value){
		this.unitMt = value;
	}
	
	/**
	 * Gets the value of the unitTt property.
	 * 
	 */
	public double getUnitTt(){
		return unitTt;
	}
	
	/**
	 * Sets the value of the unitTt property.
	 * 
	 */
	public void setUnitTt(double value){
		this.unitTt = value;
	}
	
	/**
	 * Gets the value of the unitFactorMt property.
	 * 
	 */
	public double getUnitFactorMt(){
		return unitFactorMt;
	}
	
	/**
	 * Sets the value of the unitFactorMt property.
	 * 
	 */
	public void setUnitFactorMt(double value){
		this.unitFactorMt = value;
	}
	
	/**
	 * Gets the value of the unitFactorTt property.
	 * 
	 */
	public double getUnitFactorTt(){
		return unitFactorTt;
	}
	
	/**
	 * Sets the value of the unitFactorTt property.
	 * 
	 */
	public void setUnitFactorTt(double value){
		this.unitFactorTt = value;
	}
	
	/**
	 * Gets the value of the validate property.
	 * 
	 * @return possible object is {@link Boolean }
	 * 
	 */
	public boolean isValidate(){
		if (validate == null) {
			return true;
		} else {
			return validate;
		}
	}
	
	/**
	 * Sets the value of the validate property.
	 * 
	 * @param value
	 *            allowed object is {@link Boolean }
	 * 
	 */
	public void setValidate(Boolean value){
		this.validate = value;
	}
	
	/**
	 * Gets the value of the vatRate property.
	 * 
	 * @return possible object is {@link Double }
	 * 
	 */
	public double getVatRate(){
		if (vatRate == null) {
			return 0.0D;
		} else {
			return vatRate;
		}
	}
	
	/**
	 * Sets the value of the vatRate property.
	 * 
	 * @param value
	 *            allowed object is {@link Double }
	 * 
	 */
	public void setVatRate(Double value){
		this.vatRate = value;
	}
	
}
