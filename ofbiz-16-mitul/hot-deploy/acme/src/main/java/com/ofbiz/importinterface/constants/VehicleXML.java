package com.ofbiz.importinterface.constants;
public enum VehicleXML {
	
	Vehicle_Id(0,"vehicleId"),Vehicle_Model_Id(1,"modelId"),Vehicle_Manufacturer_Id(2,"manufacturerId"), 
	Vehicle_Manufacturer_Code(3,"manufacturerCode"),Category_Id(4,"categoryId"),Vehicle_Model_Name(5,"modelName"),
	Vehicle_Manufacturer(6,"manufacturerName"),Search_Name(7,"searchName"),Year(8,"year"),Variant(9,"variant"),Power(10,"power"),
	Type(11,"type"),Fuel_Type(12,"fuelType"),Oil_Capacity(13,"oilCapacity");

	private int code;
	private String ofbizColName;
	
	
	VehicleXML(int code,String ofbizColName){
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
