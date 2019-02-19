package com.ofbiz.importinterface.constants;
public enum InventoryXML {
	
	Product_Id(0, "productId"),
	Service_Center_id(1, "serviceCenterId"),
	Inventroy_Quantity(2,"inventroyQuantity"),
	Cost(3,"unitCost");
	
	private int code;
	private String ofbizColName;
	
	InventoryXML(int code,String ofbizColName){
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
