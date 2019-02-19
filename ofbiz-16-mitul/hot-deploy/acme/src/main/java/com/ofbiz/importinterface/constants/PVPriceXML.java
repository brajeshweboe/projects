package com.ofbiz.importinterface.constants;
public enum PVPriceXML {
	
	Spare_Part_Number(0, "productId"),
	Vehicle_Id(1,"vehicleId"),
	Service_Center_Id(2,"productStoreId"),
	Facility_Id(3, "facilityId"),
	Product_Category_ID(4,"categoryId"),
	Brand_Name(5,"brandName"),
	Product_Type(6,"productType"),
	Product_Type_Id(7,"productTypeId"),
	Spare_Part_Name(8,"productName"),
	Description(9,"description"),
	Barcode(10,"barcode"),
	HSN_SAC_Code(11,"HSNSACCode"),
	GaadiZo_Price(12,"price"),
	Package_Discount(13,"discount"),
	WorkShop_Price(14,"workshopPrice"),
	MRP(15,"mrp"),
	Inventory_Unit_of_Measure(16,"inventoryUOM"),
	Sales_UOM(17,"salesUOM"),
	Purchase_UOM(18,"Purchase_UOM"),
	Packing_Type(19,"packingType"),
	Supplier_Id(20,"supplierId"),
	Tax_Rate_Percentage(21,"taxPercentage"),
	Product_Length(22,"productLength"),
	Product_Width(23,"productWidth"),
	Product_Height(24,"productHeight"),
	Oil_Capacity_In_Ltrs(25,"oilCapacity"),
	Pack_Size(26,"packSize");
	


	private int code;
	private String ofbizColName;
	
	PVPriceXML(int code,String ofbizColName){
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
