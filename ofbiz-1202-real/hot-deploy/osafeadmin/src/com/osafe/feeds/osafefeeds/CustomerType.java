//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.17 at 08:19:16 PM IST 
//


package com.osafe.feeds.osafefeeds;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for CustomerType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CustomerType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProductStoreId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CustomerId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FirstName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="LastName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="DateRegistered" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EmailAddress" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="EmailOptIn" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="HomePhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CellPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkPhone" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="WorkPhoneExt" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BillingAddress" type="{}BillingAddressType" maxOccurs="unbounded"/>
 *         &lt;element name="ShippingAddress" type="{}ShippingAddressType" maxOccurs="unbounded"/>
 *         &lt;element name="UserLogin" type="{}UserLoginType"/>
 *         &lt;element name="CustomerAttribute" type="{}CustomerAttributeType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CustomerType", propOrder = {
    "productStoreId",
    "customerId",
    "firstName",
    "lastName",
    "dateRegistered",
    "emailAddress",
    "emailOptIn",
    "homePhone",
    "cellPhone",
    "workPhone",
    "workPhoneExt",
    "billingAddress",
    "shippingAddress",
    "userLogin",
    "customerAttribute"
})
public class CustomerType {

    @XmlElement(name = "ProductStoreId", required = true, defaultValue = "")
    protected String productStoreId;
    @XmlElement(name = "CustomerId", required = true, defaultValue = "")
    protected String customerId;
    @XmlElement(name = "FirstName", required = true, defaultValue = "")
    protected String firstName;
    @XmlElement(name = "LastName", required = true, defaultValue = "")
    protected String lastName;
    @XmlElement(name = "DateRegistered", required = true, defaultValue = "")
    protected String dateRegistered;
    @XmlElement(name = "EmailAddress", required = true, defaultValue = "")
    protected String emailAddress;
    @XmlElement(name = "EmailOptIn", required = true, defaultValue = "")
    protected String emailOptIn;
    @XmlElement(name = "HomePhone", required = true, defaultValue = "")
    protected String homePhone;
    @XmlElement(name = "CellPhone", required = true, defaultValue = "")
    protected String cellPhone;
    @XmlElement(name = "WorkPhone", required = true, defaultValue = "")
    protected String workPhone;
    @XmlElement(name = "WorkPhoneExt", required = true, defaultValue = "")
    protected String workPhoneExt;
    @XmlElement(name = "BillingAddress", required = true)
    protected List<BillingAddressType> billingAddress;
    @XmlElement(name = "ShippingAddress", required = true)
    protected List<ShippingAddressType> shippingAddress;
    @XmlElement(name = "UserLogin", required = true)
    protected UserLoginType userLogin;
    @XmlElement(name = "CustomerAttribute", required = true)
    protected CustomerAttributeType customerAttribute;

    /**
     * Gets the value of the productStoreId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductStoreId() {
        return productStoreId;
    }

    /**
     * Sets the value of the productStoreId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductStoreId(String value) {
        this.productStoreId = value;
    }

    /**
     * Gets the value of the customerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * Sets the value of the customerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCustomerId(String value) {
        this.customerId = value;
    }

    /**
     * Gets the value of the firstName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the value of the firstName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFirstName(String value) {
        this.firstName = value;
    }

    /**
     * Gets the value of the lastName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the value of the lastName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLastName(String value) {
        this.lastName = value;
    }

    /**
     * Gets the value of the dateRegistered property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDateRegistered() {
        return dateRegistered;
    }

    /**
     * Sets the value of the dateRegistered property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDateRegistered(String value) {
        this.dateRegistered = value;
    }

    /**
     * Gets the value of the emailAddress property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Sets the value of the emailAddress property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailAddress(String value) {
        this.emailAddress = value;
    }

    /**
     * Gets the value of the emailOptIn property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getEmailOptIn() {
        return emailOptIn;
    }

    /**
     * Sets the value of the emailOptIn property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setEmailOptIn(String value) {
        this.emailOptIn = value;
    }

    /**
     * Gets the value of the homePhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHomePhone() {
        return homePhone;
    }

    /**
     * Sets the value of the homePhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHomePhone(String value) {
        this.homePhone = value;
    }

    /**
     * Gets the value of the cellPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCellPhone() {
        return cellPhone;
    }

    /**
     * Sets the value of the cellPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCellPhone(String value) {
        this.cellPhone = value;
    }

    /**
     * Gets the value of the workPhone property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkPhone() {
        return workPhone;
    }

    /**
     * Sets the value of the workPhone property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkPhone(String value) {
        this.workPhone = value;
    }

    /**
     * Gets the value of the workPhoneExt property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWorkPhoneExt() {
        return workPhoneExt;
    }

    /**
     * Sets the value of the workPhoneExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWorkPhoneExt(String value) {
        this.workPhoneExt = value;
    }

    /**
     * Gets the value of the billingAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the billingAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBillingAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BillingAddressType }
     * 
     * 
     */
    public List<BillingAddressType> getBillingAddress() {
        if (billingAddress == null) {
            billingAddress = new ArrayList<BillingAddressType>();
        }
        return this.billingAddress;
    }

    /**
     * Gets the value of the shippingAddress property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the shippingAddress property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getShippingAddress().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ShippingAddressType }
     * 
     * 
     */
    public List<ShippingAddressType> getShippingAddress() {
        if (shippingAddress == null) {
            shippingAddress = new ArrayList<ShippingAddressType>();
        }
        return this.shippingAddress;
    }

    /**
     * Gets the value of the userLogin property.
     * 
     * @return
     *     possible object is
     *     {@link UserLoginType }
     *     
     */
    public UserLoginType getUserLogin() {
        return userLogin;
    }

    /**
     * Sets the value of the userLogin property.
     * 
     * @param value
     *     allowed object is
     *     {@link UserLoginType }
     *     
     */
    public void setUserLogin(UserLoginType value) {
        this.userLogin = value;
    }

    /**
     * Gets the value of the customerAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerAttributeType }
     *     
     */
    public CustomerAttributeType getCustomerAttribute() {
        return customerAttribute;
    }

    /**
     * Sets the value of the customerAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerAttributeType }
     *     
     */
    public void setCustomerAttribute(CustomerAttributeType value) {
        this.customerAttribute = value;
    }

}
