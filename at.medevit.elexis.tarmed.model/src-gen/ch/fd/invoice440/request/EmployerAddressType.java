//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.18 at 03:48:09 PM CET 
//


package ch.fd.invoice440.request;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for employerAddressType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="employerAddressType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="company" type="{http://www.forum-datenaustausch.ch/invoice}companyType"/>
 *         &lt;element name="person" type="{http://www.forum-datenaustausch.ch/invoice}personType"/>
 *       &lt;/choice>
 *       &lt;attribute name="ean_party" type="{http://www.forum-datenaustausch.ch/invoice}eanPartyType" />
 *       &lt;attribute name="reg_number" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "employerAddressType", propOrder = {
    "company",
    "person"
})
public class EmployerAddressType {

    protected CompanyType company;
    protected PersonType person;
    @XmlAttribute(name = "ean_party")
    protected String eanParty;
    @XmlAttribute(name = "reg_number")
    protected String regNumber;

    /**
     * Gets the value of the company property.
     * 
     * @return
     *     possible object is
     *     {@link CompanyType }
     *     
     */
    public CompanyType getCompany() {
        return company;
    }

    /**
     * Sets the value of the company property.
     * 
     * @param value
     *     allowed object is
     *     {@link CompanyType }
     *     
     */
    public void setCompany(CompanyType value) {
        this.company = value;
    }

    /**
     * Gets the value of the person property.
     * 
     * @return
     *     possible object is
     *     {@link PersonType }
     *     
     */
    public PersonType getPerson() {
        return person;
    }

    /**
     * Sets the value of the person property.
     * 
     * @param value
     *     allowed object is
     *     {@link PersonType }
     *     
     */
    public void setPerson(PersonType value) {
        this.person = value;
    }

    /**
     * Gets the value of the eanParty property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEanParty() {
        return eanParty;
    }

    /**
     * Sets the value of the eanParty property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEanParty(String value) {
        this.eanParty = value;
    }

    /**
     * Gets the value of the regNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegNumber() {
        return regNumber;
    }

    /**
     * Sets the value of the regNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegNumber(String value) {
        this.regNumber = value;
    }

}
