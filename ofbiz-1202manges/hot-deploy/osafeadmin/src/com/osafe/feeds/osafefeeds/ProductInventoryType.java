//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.17 at 03:16:54 PM IST 
//


package com.osafe.feeds.osafefeeds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductInventoryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductInventoryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="BigfishInventoryTotal" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="BigfishInventoryWarehouse" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductInventoryType", propOrder = {

})
public class ProductInventoryType {

    @XmlElement(name = "BigfishInventoryTotal", required = true)
    protected String bigfishInventoryTotal;
    @XmlElement(name = "BigfishInventoryWarehouse")
    protected String bigfishInventoryWarehouse;

    /**
     * Gets the value of the bigfishInventoryTotal property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBigfishInventoryTotal() {
        return bigfishInventoryTotal;
    }

    /**
     * Sets the value of the bigfishInventoryTotal property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBigfishInventoryTotal(String value) {
        this.bigfishInventoryTotal = value;
    }

    /**
     * Gets the value of the bigfishInventoryWarehouse property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBigfishInventoryWarehouse() {
        return bigfishInventoryWarehouse;
    }

    /**
     * Sets the value of the bigfishInventoryWarehouse property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBigfishInventoryWarehouse(String value) {
        this.bigfishInventoryWarehouse = value;
    }

}
