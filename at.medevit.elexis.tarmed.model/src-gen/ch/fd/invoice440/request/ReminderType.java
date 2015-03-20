//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2015.03.18 at 03:48:09 PM CET 
//


package ch.fd.invoice440.request;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for reminderType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="reminderType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="request_timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *       &lt;attribute name="request_date" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="request_id" use="required" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_35" />
 *       &lt;attribute name="reminder_level" default="1">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}NMTOKEN">
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="2"/>
 *             &lt;enumeration value="3"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="reminder_text" type="{http://www.forum-datenaustausch.ch/invoice}stringType1_700" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "reminderType")
public class ReminderType {

    @XmlAttribute(name = "request_timestamp", required = true)
    @XmlSchemaType(name = "unsignedLong")
    protected BigInteger requestTimestamp;
    @XmlAttribute(name = "request_date", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar requestDate;
    @XmlAttribute(name = "request_id", required = true)
    protected String requestId;
    @XmlAttribute(name = "reminder_level")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String reminderLevel;
    @XmlAttribute(name = "reminder_text")
    protected String reminderText;

    /**
     * Gets the value of the requestTimestamp property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getRequestTimestamp() {
        return requestTimestamp;
    }

    /**
     * Sets the value of the requestTimestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setRequestTimestamp(BigInteger value) {
        this.requestTimestamp = value;
    }

    /**
     * Gets the value of the requestDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRequestDate() {
        return requestDate;
    }

    /**
     * Sets the value of the requestDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRequestDate(XMLGregorianCalendar value) {
        this.requestDate = value;
    }

    /**
     * Gets the value of the requestId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequestId() {
        return requestId;
    }

    /**
     * Sets the value of the requestId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequestId(String value) {
        this.requestId = value;
    }

    /**
     * Gets the value of the reminderLevel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReminderLevel() {
        if (reminderLevel == null) {
            return "1";
        } else {
            return reminderLevel;
        }
    }

    /**
     * Sets the value of the reminderLevel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReminderLevel(String value) {
        this.reminderLevel = value;
    }

    /**
     * Gets the value of the reminderText property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReminderText() {
        return reminderText;
    }

    /**
     * Sets the value of the reminderText property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReminderText(String value) {
        this.reminderText = value;
    }

}
