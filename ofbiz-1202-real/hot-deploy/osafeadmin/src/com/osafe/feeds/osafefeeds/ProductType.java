//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2019.02.17 at 08:19:16 PM IST 
//


package com.osafe.feeds.osafefeeds;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ProductType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProductType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="MasterProductId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="ProductStoreId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="InternalName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SalesPitch" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="LongDescription" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="SpecialInstructions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DeliveryInfo" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Directions" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="TermsAndConds" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Ingredients" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Warnings" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PlpLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="PdpLabel" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductWidth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductDepth" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductHeight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductWeight" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Returnable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="Taxable" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ChargeShipping" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="IntroDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DiscoDate" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ManufacturerId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ProductPrice" type="{}ProductPriceType" minOccurs="0"/>
 *         &lt;element name="ProductCategoryMember" type="{}ProductCategoryMemberType" minOccurs="0"/>
 *         &lt;element name="ProductSelectableFeature" type="{}ProductSelectableFeatureType" minOccurs="0"/>
 *         &lt;element name="ProductDescriptiveFeature" type="{}ProductDescriptiveFeatureType" minOccurs="0"/>
 *         &lt;element name="ProductImage" type="{}ProductImageType" minOccurs="0"/>
 *         &lt;element name="ProductGoodIdentification" type="{}GoodIdentificationType" minOccurs="0"/>
 *         &lt;element name="ProductInventory" type="{}ProductInventoryType" minOccurs="0"/>
 *         &lt;element name="ProductAttachments" type="{}ProductAttachmentsType" minOccurs="0"/>
 *         &lt;element name="ProductAttribute" type="{}ProductAttributeType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProductType", propOrder = {

})
public class ProductType {

    @XmlElement(name = "MasterProductId", required = true, defaultValue = "")
    protected String masterProductId;
    @XmlElement(name = "ProductId", required = true, defaultValue = "")
    protected String productId;
    @XmlElement(name = "ProductStoreId", defaultValue = "")
    protected String productStoreId;
    @XmlElement(name = "InternalName", defaultValue = "")
    protected String internalName;
    @XmlElement(name = "ProductName", defaultValue = "")
    protected String productName;
    @XmlElement(name = "SalesPitch", defaultValue = "")
    protected String salesPitch;
    @XmlElement(name = "LongDescription", defaultValue = "")
    protected String longDescription;
    @XmlElement(name = "SpecialInstructions", defaultValue = "")
    protected String specialInstructions;
    @XmlElement(name = "DeliveryInfo", defaultValue = "")
    protected String deliveryInfo;
    @XmlElement(name = "Directions", defaultValue = "")
    protected String directions;
    @XmlElement(name = "TermsAndConds", defaultValue = "")
    protected String termsAndConds;
    @XmlElement(name = "Ingredients", defaultValue = "")
    protected String ingredients;
    @XmlElement(name = "Warnings", defaultValue = "")
    protected String warnings;
    @XmlElement(name = "PlpLabel", defaultValue = "")
    protected String plpLabel;
    @XmlElement(name = "PdpLabel", defaultValue = "")
    protected String pdpLabel;
    @XmlElement(name = "ProductWidth", defaultValue = "")
    protected String productWidth;
    @XmlElement(name = "ProductDepth", defaultValue = "")
    protected String productDepth;
    @XmlElement(name = "ProductHeight", defaultValue = "")
    protected String productHeight;
    @XmlElement(name = "ProductWeight", defaultValue = "")
    protected String productWeight;
    @XmlElement(name = "Returnable", defaultValue = "")
    protected String returnable;
    @XmlElement(name = "Taxable", defaultValue = "")
    protected String taxable;
    @XmlElement(name = "ChargeShipping", defaultValue = "")
    protected String chargeShipping;
    @XmlElement(name = "IntroDate", defaultValue = "")
    protected String introDate;
    @XmlElement(name = "DiscoDate", defaultValue = "")
    protected String discoDate;
    @XmlElement(name = "ManufacturerId", defaultValue = "")
    protected String manufacturerId;
    @XmlElement(name = "ProductPrice")
    protected ProductPriceType productPrice;
    @XmlElement(name = "ProductCategoryMember")
    protected ProductCategoryMemberType productCategoryMember;
    @XmlElement(name = "ProductSelectableFeature")
    protected ProductSelectableFeatureType productSelectableFeature;
    @XmlElement(name = "ProductDescriptiveFeature")
    protected ProductDescriptiveFeatureType productDescriptiveFeature;
    @XmlElement(name = "ProductImage")
    protected ProductImageType productImage;
    @XmlElement(name = "ProductGoodIdentification")
    protected GoodIdentificationType productGoodIdentification;
    @XmlElement(name = "ProductInventory")
    protected ProductInventoryType productInventory;
    @XmlElement(name = "ProductAttachments")
    protected ProductAttachmentsType productAttachments;
    @XmlElement(name = "ProductAttribute")
    protected ProductAttributeType productAttribute;

    /**
     * Gets the value of the masterProductId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMasterProductId() {
        return masterProductId;
    }

    /**
     * Sets the value of the masterProductId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMasterProductId(String value) {
        this.masterProductId = value;
    }

    /**
     * Gets the value of the productId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductId() {
        return productId;
    }

    /**
     * Sets the value of the productId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductId(String value) {
        this.productId = value;
    }

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
     * Gets the value of the internalName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * Sets the value of the internalName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setInternalName(String value) {
        this.internalName = value;
    }

    /**
     * Gets the value of the productName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Sets the value of the productName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductName(String value) {
        this.productName = value;
    }

    /**
     * Gets the value of the salesPitch property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSalesPitch() {
        return salesPitch;
    }

    /**
     * Sets the value of the salesPitch property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSalesPitch(String value) {
        this.salesPitch = value;
    }

    /**
     * Gets the value of the longDescription property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLongDescription() {
        return longDescription;
    }

    /**
     * Sets the value of the longDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLongDescription(String value) {
        this.longDescription = value;
    }

    /**
     * Gets the value of the specialInstructions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecialInstructions() {
        return specialInstructions;
    }

    /**
     * Sets the value of the specialInstructions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecialInstructions(String value) {
        this.specialInstructions = value;
    }

    /**
     * Gets the value of the deliveryInfo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDeliveryInfo() {
        return deliveryInfo;
    }

    /**
     * Sets the value of the deliveryInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDeliveryInfo(String value) {
        this.deliveryInfo = value;
    }

    /**
     * Gets the value of the directions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirections() {
        return directions;
    }

    /**
     * Sets the value of the directions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirections(String value) {
        this.directions = value;
    }

    /**
     * Gets the value of the termsAndConds property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTermsAndConds() {
        return termsAndConds;
    }

    /**
     * Sets the value of the termsAndConds property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTermsAndConds(String value) {
        this.termsAndConds = value;
    }

    /**
     * Gets the value of the ingredients property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIngredients() {
        return ingredients;
    }

    /**
     * Sets the value of the ingredients property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIngredients(String value) {
        this.ingredients = value;
    }

    /**
     * Gets the value of the warnings property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWarnings() {
        return warnings;
    }

    /**
     * Sets the value of the warnings property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWarnings(String value) {
        this.warnings = value;
    }

    /**
     * Gets the value of the plpLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPlpLabel() {
        return plpLabel;
    }

    /**
     * Sets the value of the plpLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPlpLabel(String value) {
        this.plpLabel = value;
    }

    /**
     * Gets the value of the pdpLabel property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPdpLabel() {
        return pdpLabel;
    }

    /**
     * Sets the value of the pdpLabel property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPdpLabel(String value) {
        this.pdpLabel = value;
    }

    /**
     * Gets the value of the productWidth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductWidth() {
        return productWidth;
    }

    /**
     * Sets the value of the productWidth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductWidth(String value) {
        this.productWidth = value;
    }

    /**
     * Gets the value of the productDepth property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductDepth() {
        return productDepth;
    }

    /**
     * Sets the value of the productDepth property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductDepth(String value) {
        this.productDepth = value;
    }

    /**
     * Gets the value of the productHeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductHeight() {
        return productHeight;
    }

    /**
     * Sets the value of the productHeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductHeight(String value) {
        this.productHeight = value;
    }

    /**
     * Gets the value of the productWeight property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProductWeight() {
        return productWeight;
    }

    /**
     * Sets the value of the productWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProductWeight(String value) {
        this.productWeight = value;
    }

    /**
     * Gets the value of the returnable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReturnable() {
        return returnable;
    }

    /**
     * Sets the value of the returnable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReturnable(String value) {
        this.returnable = value;
    }

    /**
     * Gets the value of the taxable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTaxable() {
        return taxable;
    }

    /**
     * Sets the value of the taxable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTaxable(String value) {
        this.taxable = value;
    }

    /**
     * Gets the value of the chargeShipping property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getChargeShipping() {
        return chargeShipping;
    }

    /**
     * Sets the value of the chargeShipping property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setChargeShipping(String value) {
        this.chargeShipping = value;
    }

    /**
     * Gets the value of the introDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getIntroDate() {
        return introDate;
    }

    /**
     * Sets the value of the introDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setIntroDate(String value) {
        this.introDate = value;
    }

    /**
     * Gets the value of the discoDate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDiscoDate() {
        return discoDate;
    }

    /**
     * Sets the value of the discoDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDiscoDate(String value) {
        this.discoDate = value;
    }

    /**
     * Gets the value of the manufacturerId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getManufacturerId() {
        return manufacturerId;
    }

    /**
     * Sets the value of the manufacturerId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setManufacturerId(String value) {
        this.manufacturerId = value;
    }

    /**
     * Gets the value of the productPrice property.
     * 
     * @return
     *     possible object is
     *     {@link ProductPriceType }
     *     
     */
    public ProductPriceType getProductPrice() {
        return productPrice;
    }

    /**
     * Sets the value of the productPrice property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductPriceType }
     *     
     */
    public void setProductPrice(ProductPriceType value) {
        this.productPrice = value;
    }

    /**
     * Gets the value of the productCategoryMember property.
     * 
     * @return
     *     possible object is
     *     {@link ProductCategoryMemberType }
     *     
     */
    public ProductCategoryMemberType getProductCategoryMember() {
        return productCategoryMember;
    }

    /**
     * Sets the value of the productCategoryMember property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductCategoryMemberType }
     *     
     */
    public void setProductCategoryMember(ProductCategoryMemberType value) {
        this.productCategoryMember = value;
    }

    /**
     * Gets the value of the productSelectableFeature property.
     * 
     * @return
     *     possible object is
     *     {@link ProductSelectableFeatureType }
     *     
     */
    public ProductSelectableFeatureType getProductSelectableFeature() {
        return productSelectableFeature;
    }

    /**
     * Sets the value of the productSelectableFeature property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductSelectableFeatureType }
     *     
     */
    public void setProductSelectableFeature(ProductSelectableFeatureType value) {
        this.productSelectableFeature = value;
    }

    /**
     * Gets the value of the productDescriptiveFeature property.
     * 
     * @return
     *     possible object is
     *     {@link ProductDescriptiveFeatureType }
     *     
     */
    public ProductDescriptiveFeatureType getProductDescriptiveFeature() {
        return productDescriptiveFeature;
    }

    /**
     * Sets the value of the productDescriptiveFeature property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductDescriptiveFeatureType }
     *     
     */
    public void setProductDescriptiveFeature(ProductDescriptiveFeatureType value) {
        this.productDescriptiveFeature = value;
    }

    /**
     * Gets the value of the productImage property.
     * 
     * @return
     *     possible object is
     *     {@link ProductImageType }
     *     
     */
    public ProductImageType getProductImage() {
        return productImage;
    }

    /**
     * Sets the value of the productImage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductImageType }
     *     
     */
    public void setProductImage(ProductImageType value) {
        this.productImage = value;
    }

    /**
     * Gets the value of the productGoodIdentification property.
     * 
     * @return
     *     possible object is
     *     {@link GoodIdentificationType }
     *     
     */
    public GoodIdentificationType getProductGoodIdentification() {
        return productGoodIdentification;
    }

    /**
     * Sets the value of the productGoodIdentification property.
     * 
     * @param value
     *     allowed object is
     *     {@link GoodIdentificationType }
     *     
     */
    public void setProductGoodIdentification(GoodIdentificationType value) {
        this.productGoodIdentification = value;
    }

    /**
     * Gets the value of the productInventory property.
     * 
     * @return
     *     possible object is
     *     {@link ProductInventoryType }
     *     
     */
    public ProductInventoryType getProductInventory() {
        return productInventory;
    }

    /**
     * Sets the value of the productInventory property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductInventoryType }
     *     
     */
    public void setProductInventory(ProductInventoryType value) {
        this.productInventory = value;
    }

    /**
     * Gets the value of the productAttachments property.
     * 
     * @return
     *     possible object is
     *     {@link ProductAttachmentsType }
     *     
     */
    public ProductAttachmentsType getProductAttachments() {
        return productAttachments;
    }

    /**
     * Sets the value of the productAttachments property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductAttachmentsType }
     *     
     */
    public void setProductAttachments(ProductAttachmentsType value) {
        this.productAttachments = value;
    }

    /**
     * Gets the value of the productAttribute property.
     * 
     * @return
     *     possible object is
     *     {@link ProductAttributeType }
     *     
     */
    public ProductAttributeType getProductAttribute() {
        return productAttribute;
    }

    /**
     * Sets the value of the productAttribute property.
     * 
     * @param value
     *     allowed object is
     *     {@link ProductAttributeType }
     *     
     */
    public void setProductAttribute(ProductAttributeType value) {
        this.productAttribute = value;
    }

}
