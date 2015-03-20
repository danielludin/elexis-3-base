//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.0-b52-fcs 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.13 at 12:17:21 PM MEZ 
//

package ch.fd.invoice400.response;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for bankCompanyType complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="bankCompanyType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="companyname" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35"/>
 *           &lt;element name="department" type="{http://www.xmlData.ch/xmlInvoice/XSD}stringType1_35" maxOccurs="3" minOccurs="0"/>
 *         &lt;/sequence>
 *         &lt;sequence>
 *           &lt;element name="postal" type="{http://www.xmlData.ch/xmlInvoice/XSD}postalAddressType"/>
 *           &lt;element name="telecom" type="{http://www.xmlData.ch/xmlInvoice/XSD}telecomAddressType" minOccurs="0"/>
 *           &lt;element name="online" type="{http://www.xmlData.ch/xmlInvoice/XSD}onlineAddressType" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "bankCompanyType", propOrder = {
	"companyname", "department", "postal", "telecom", "online"
})
public class BankCompanyType {
	
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected String companyname;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected List<String> department;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD", required = true)
	protected PostalAddressType postal;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD")
	protected TelecomAddressType telecom;
	@XmlElement(namespace = "http://www.xmlData.ch/xmlInvoice/XSD")
	protected OnlineAddressType online;
	
	/**
	 * Gets the value of the companyname property.
	 * 
	 * @return possible object is {@link String }
	 * 
	 */
	public String getCompanyname(){
		return companyname;
	}
	
	/**
	 * Sets the value of the companyname property.
	 * 
	 * @param value
	 *            allowed object is {@link String }
	 * 
	 */
	public void setCompanyname(String value){
		this.companyname = value;
	}
	
	/**
	 * Gets the value of the department property.
	 * 
	 * <p>
	 * This accessor method returns a reference to the live list, not a snapshot. Therefore any
	 * modification you make to the returned list will be present inside the JAXB object. This is
	 * why there is not a <CODE>set</CODE> method for the department property.
	 * 
	 * <p>
	 * For example, to add a new item, do as follows:
	 * 
	 * <pre>
	 * getDepartment().add(newItem);
	 * </pre>
	 * 
	 * 
	 * <p>
	 * Objects of the following type(s) are allowed in the list {@link String }
	 * 
	 * 
	 */
	public List<String> getDepartment(){
		if (department == null) {
			department = new ArrayList<String>();
		}
		return this.department;
	}
	
	/**
	 * Gets the value of the postal property.
	 * 
	 * @return possible object is {@link PostalAddressType }
	 * 
	 */
	public PostalAddressType getPostal(){
		return postal;
	}
	
	/**
	 * Sets the value of the postal property.
	 * 
	 * @param value
	 *            allowed object is {@link PostalAddressType }
	 * 
	 */
	public void setPostal(PostalAddressType value){
		this.postal = value;
	}
	
	/**
	 * Gets the value of the telecom property.
	 * 
	 * @return possible object is {@link TelecomAddressType }
	 * 
	 */
	public TelecomAddressType getTelecom(){
		return telecom;
	}
	
	/**
	 * Sets the value of the telecom property.
	 * 
	 * @param value
	 *            allowed object is {@link TelecomAddressType }
	 * 
	 */
	public void setTelecom(TelecomAddressType value){
		this.telecom = value;
	}
	
	/**
	 * Gets the value of the online property.
	 * 
	 * @return possible object is {@link OnlineAddressType }
	 * 
	 */
	public OnlineAddressType getOnline(){
		return online;
	}
	
	/**
	 * Sets the value of the online property.
	 * 
	 * @param value
	 *            allowed object is {@link OnlineAddressType }
	 * 
	 */
	public void setOnline(OnlineAddressType value){
		this.online = value;
	}
	
}
