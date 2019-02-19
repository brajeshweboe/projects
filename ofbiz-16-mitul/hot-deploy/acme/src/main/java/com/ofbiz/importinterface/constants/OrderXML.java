package com.ofbiz.importinterface.constants;
public enum OrderXML {
	
	TYPE(0,"productId"),ORDER_ID(1,"orderId"),ORDER_ITEM_ID(2,"orderItemId"),RELEASE_NUMBER(3,"releaseNumber"),ORDER_STATUS(4,"statusId"),ORDER_ITEM_STATUS(5,"statusId"),
	ORDER_PLACED_DATE(6,"orderDate"),CUSTOMER_NAME(7,"customerName"),CUSTOMER_LOGON_ID(8,"customerLogonId"),SHIPPING_ADDRESS(9,"shipping_address"),
	BILLING_ADDRESS(10,"billingAddress"),CUST_EMAIL(11,"userLogonId"),
	CUST_GTSN(12,"customerGtsn"),SHIP_MODE(13,"shipmentMethodTypeId"),SHIP_PROVIDER(14,"partyId"),WEIGHT(15,"weight"),PAYMENT_METHOD(16,"paymentMethodId"),CURRENCY(17,"currencyUom"),
	QUANTITY(18,"quantity"),ITEM_PRICE(19,"unitPrice"),ORDER_ITEM_TOTAL(20,"orderItemTotal"),DISCOUNT(21,"orderAdjustmentId"),SHIPPING_CHARGES(22,"shippingCostEstimate"),
	TAXES_DUTIES(23,"taxesDuties"),ORDER_TOTAL(24,"grandTotal"),PRODUCT_NAME(25,"productName"),	PRODUCT_SKU(26,"productId"),CATEGORY_NAME(27,"categoryName")
	,PRODUCT_IMAGE(28,"prodcutImage"),SIZE_TYPE(29,"SIZE_TYPE"),SIZE(30,"SIZE");//,SHIPPING_CHARGES(23,"Shipping_Charges");
	
	private int code;
	private String ofbizColName;
	
	
	OrderXML(int code,String ofbizColName){
		this.code=code;
		this.ofbizColName=ofbizColName;
	}
	
	public String getOfbizColName() {
		return ofbizColName;
	}
	public void setOfbizColName(String ofbizColName) {
		this.ofbizColName = ofbizColName;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	
	
	
	
}
