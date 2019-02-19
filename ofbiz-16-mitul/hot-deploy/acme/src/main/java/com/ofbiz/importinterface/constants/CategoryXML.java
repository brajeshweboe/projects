package com.ofbiz.importinterface.constants;
public enum CategoryXML {
	
	Category_Id(0,"productCategoryId"),Category_Type(1,"productCategoryTypeId"),
	Parent_Category_ID(2,"primaryParentCategoryId"),Category_Name(3,"categoryName"),Description(4,"description"),
	Long_Description(4,"longDescription"),Category_Image_URL(5,"categoryImageUrl"),
	Parent_Product_Category_Id(6,"parentProductCategoryId"), From_Date(7,"fromDate");

	private int code;
	private String ofbizColName;
	
	
	CategoryXML(int code,String ofbizColName){
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
