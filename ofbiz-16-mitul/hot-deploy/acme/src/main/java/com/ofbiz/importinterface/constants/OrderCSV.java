package com.ofbiz.importinterface.constants;
public enum OrderCSV {
	
	ORDER_ID(0,"orderId"),Booking_ID(1,"bookingId"),Customer_ID(2,"customerId"),First_Name(3,"firstName"),Last_Name(4,"lastName"),
	Vehicle_Make_Id(5,"vehicleMakeId"),	Vehicle_Make(6,"vehicleMake"),Vehicle_Model_ID(7,"vehicleModelId"),Vehicle_Model(8,"vehicleModel"),
	Vehicle_Regstn_No(9,"vehicleRegistrationNo"),Service_Provider_ID(10,"serviceProviderId"),Service_Provider(11,"serviceProvider"),Service_ID(12,"serviceId"),
	Service_Availed(13,"serviceAvailed"),Oil_type(14,"oilType"),Order_Amount(15,"grandTotal"),Discount_Amount(16,"discountAmount"),Offer_Code(17,"offerCode"),
	Offer_Details(18,"offerDetails"),Payment_Mode(19,"paymentMode"),Payable_Amount(20,"payableAmount"),Invoice_Amount(21,"invoiceAmount"),
	Service_Date(22,"serviceDate"),Service_Time(23,"serviceTime"),Booking_Date(24,"bookingDate"),Status(25,"status"),Pick_up(26,"pickup"),
	Address_1(27,"address1"),Address_2(28,"address2"),City(29,"city"),Postal_Code(30,"postalCode"),State_Code(31,"stateCode"),
	Pick_up_Address1(32,"pickupAddress1"),Pick_up_Address2(33,"pickupAddress2"),Remarks(34,"remarks"),CUST_EMAIL(35,"emailId"),CUST_PHONE(36,"phoneNumber"),
	CUST_GTSN(37,"customerGtsn"),Gaadizo_Credit(38,"gaadizoCredit");
	
	private int code;
	private String ofbizColName;
	
	
	OrderCSV(int code,String ofbizColName){
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
