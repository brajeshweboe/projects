package com.ofbiz.importinterface.constants;
public enum ProductItemAssocXML {
	
	Service_Product_Id(0, "productId"),
	Vehicle_Model_Id(1, "vehicleModelId"),
	Service_External_Id(2,"productExternalId"),
	Service_Item_Id(3,"productIdTo"),
	Service_Item_Type(4,"productItemType"),
	Oil_Type(5,"oilType"),
	From_Date(6,"fromDate"),
	Thru_Date(7,"thruDate"),
	Quantity(8, "quantity"),
	Service_Provider_Id(9, "ownerId");
	
	private int code;
	private String ofbizColName;
	
	ProductItemAssocXML(int code,String ofbizColName){
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
