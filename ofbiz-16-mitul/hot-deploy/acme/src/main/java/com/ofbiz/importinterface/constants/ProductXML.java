package com.ofbiz.importinterface.constants;
public enum ProductXML {
	
	Spare_Part_Number(0, "productId"),
	Product_Category_ID(1,"categoryId"),
	Brand_Name(2,"brandName"),
	Product_Type(3,"productType"),
	Product_Type_Id(4,"productTypeId"),
	Spare_Part_Name(5,"productName"),
	Description(6,"description"),
	Barcode(7,"barcode"),
	HSN_SAC_Code(8,"hsnScaCode"),
	GaadiZo_Price(9,"price"),
	Package_Discount(10,"discount"),
	WorkShop_Price(11,"workshopPrice"),
	MRP(12,"mrp"),
	Inventory_Unit_of_Measure(13,"inventoryUOM"),
	Sales_UOM(14,"salesUOM"),
	Purchase_UOM(15,"Purchase_UOM"),
	Packing_Type(16,"packingType"),
	Supplier_Id(17,"supplierId"),
	Tax_Rate_Percentage(18,"taxPercentage"),
	Product_Length(19,"productWeight"),
	Product_Width(20,"productWidth"),
	Product_Height(21,"productHeight"),
	Oil_Capacity_In_Ltrs(22,"oilCapacity"),
	Pack_Size(23,"packSize"),
	Is_Inventory(24,"isInventory");

	private int code;
	private String ofbizColName;
	
	ProductXML(int code,String ofbizColName){
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
