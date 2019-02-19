package com.ofbiz.importinterface.services.order;

import java.util.List;

public class BookingOrderPojo {

	private String msg;
    private Integer status;
    private Data data;

    public String getMsg() {
		return msg;
	}
	public Integer getStatus() {
		return status;
	}
	public Data getData() {
		return data;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public void setData(Data data) {
		this.data = data;
	}

}

class Data {

	private List<String> booking_id_list;
    private List<Booking> booking_list;
	
    public List<String> getBooking_id_list() {
		return booking_id_list;
	}
	public List<Booking> getBooking_list() {
		return booking_list;
	}
	public void setBooking_id_list(List<String> booking_id_list) {
		this.booking_id_list = booking_id_list;
	}
	public void setBooking_list(List<Booking> booking_list) {
		this.booking_list = booking_list;
	}
    
}

	
class Booking {
	
    private BookingDetail booking_detail;
    private CustomerDetail customer_detail;
    private VehicleDetail vehicle_detail;
    private ServiceDetail service_detail;
    private miscellaneous miscellaneous;
	
    public BookingDetail getBooking_detail() {
		return booking_detail;
	}
	public CustomerDetail getCustomer_detail() {
		return customer_detail;
	}
	public VehicleDetail getVehicle_detail() {
		return vehicle_detail;
	}
	public ServiceDetail getService_detail() {
		return service_detail;
	}
	public miscellaneous getMiscellaneous() {
		return miscellaneous;
	}
	public void setBooking_detail(BookingDetail booking_detail) {
		this.booking_detail = booking_detail;
	}
	public void setCustomer_detail(CustomerDetail customer_detail) {
		this.customer_detail = customer_detail;
	}
	public void setVehicle_detail(VehicleDetail vehicle_detail) {
		this.vehicle_detail = vehicle_detail;
	}
	public void setService_detail(ServiceDetail service_detail) {
		this.service_detail = service_detail;
	}
	public void setMiscellaneous(miscellaneous miscellaneous) {
		this.miscellaneous = miscellaneous;
	}
}

class BookingDetail {

	private String Order_ID;
    private String Booking_ID;
    private String Booking_Date;
    private String Service_Date;
    private String Service_Time;
    private String Payable_Amount;
    private String Invoice_Amount;
    private String Order_Amount;
    private String Discount_Amount;
    private String Offer_Code;
    private String Offer_Amount;
    private String Offer_Detail;
    private String Oil_Type;
    private String Status;
    private String Gaadizo_Creadit;
    private String Prepaid_Amount;
    private String Payment_Mode;
	
    public String getOrder_ID() {
		return Order_ID;
	}
	public String getBooking_ID() {
		return Booking_ID;
	}
	public String getBooking_Date() {
		return Booking_Date;
	}
	public String getService_Date() {
		return Service_Date;
	}
	public String getService_Time() {
		return Service_Time;
	}
	public String getPayable_Amount() {
		return Payable_Amount;
	}
	public String getInvoice_Amount() {
		return Invoice_Amount;
	}
	public String getOrder_Amount() {
		return Order_Amount;
	}
	public String getDiscount_Amount() {
		return Discount_Amount;
	}
	public String getOffer_Code() {
		return Offer_Code;
	}
	public String getOffer_Amount() {
		return Offer_Amount;
	}
	public String getOffer_Detail() {
		return Offer_Detail;
	}
	public String getOil_Type() {
		return Oil_Type;
	}
	public String getStatus() {
		return Status;
	}
	public String getGaadizo_Creadit() {
		return Gaadizo_Creadit;
	}
	public String getPrepaid_Amount() {
		return Prepaid_Amount;
	}
	public String getPayment_Mode() {
		return Payment_Mode;
	}
	public void setOrder_ID(String order_ID) {
		Order_ID = order_ID;
	}
	public void setBooking_ID(String booking_ID) {
		Booking_ID = booking_ID;
	}
	public void setBooking_Date(String booking_Date) {
		Booking_Date = booking_Date;
	}
	public void setService_Date(String service_Date) {
		Service_Date = service_Date;
	}
	public void setService_Time(String service_Time) {
		Service_Time = service_Time;
	}
	public void setPayable_Amount(String payable_Amount) {
		Payable_Amount = payable_Amount;
	}
	public void setInvoice_Amount(String invoice_Amount) {
		Invoice_Amount = invoice_Amount;
	}
	public void setOrder_Amount(String order_Amount) {
		Order_Amount = order_Amount;
	}
	public void setDiscount_Amount(String discount_Amount) {
		Discount_Amount = discount_Amount;
	}
	public void setOffer_Code(String offer_Code) {
		Offer_Code = offer_Code;
	}
	public void setOffer_Amount(String offer_Amount) {
		Offer_Amount = offer_Amount;
	}
	public void setOffer_Detail(String offer_Detail) {
		Offer_Detail = offer_Detail;
	}
	public void setOil_Type(String oil_Type) {
		Oil_Type = oil_Type;
	}
	public void setStatus(String status) {
		Status = status;
	}
	public void setGaadizo_Creadit(String gaadizo_Creadit) {
		Gaadizo_Creadit = gaadizo_Creadit;
	}
	public void setPrepaid_Amount(String prepaid_Amount) {
		Prepaid_Amount = prepaid_Amount;
	}
	public void setPayment_Mode(String payment_Mode) {
		Payment_Mode = payment_Mode;
	}
    
}

class CustomerDetail {

	private String Customer_ID;
	private String First_Name;
	private String Last_Name;
	private String Cust_Email;
	private String Address_1;
	private String Address_2;
	private String City;
	private String State_Code;
	public String getCustomer_ID() {
		return Customer_ID;
	}
	public String getFirst_Name() {
		return First_Name;
	}
	public String getLast_Name() {
		return Last_Name;
	}
	public String getCust_Email() {
		return Cust_Email;
	}
	public String getAddress_1() {
		return Address_1;
	}
	public String getAddress_2() {
		return Address_2;
	}
	public String getCity() {
		return City;
	}
	public String getState_Code() {
		return State_Code;
	}
	public void setCustomer_ID(String customer_ID) {
		Customer_ID = customer_ID;
	}
	public void setFirst_Name(String first_Name) {
		First_Name = first_Name;
	}
	public void setLast_Name(String last_Name) {
		Last_Name = last_Name;
	}
	public void setCust_Email(String cust_Email) {
		Cust_Email = cust_Email;
	}
	public void setAddress_1(String address_1) {
		Address_1 = address_1;
	}
	public void setAddress_2(String address_2) {
		Address_2 = address_2;
	}
	public void setCity(String city) {
		City = city;
	}
	public void setState_Code(String state_Code) {
		State_Code = state_Code;
	}
	
}

class VehicleDetail {

	private String Vehicle_Make;
	private String Vehicle_Make_ID;
	private String Vehicle_Model;
	private String Vehicle_Model_ID;
	private String Vehicle_Regstn_No;
	private String Year;
	public String getVehicle_Make() {
		return Vehicle_Make;
	}
	public String getVehicle_Make_ID() {
		return Vehicle_Make_ID;
	}
	public String getVehicle_Model() {
		return Vehicle_Model;
	}
	public String getVehicle_Model_ID() {
		return Vehicle_Model_ID;
	}
	public String getVehicle_Regstn_No() {
		return Vehicle_Regstn_No;
	}
	public String getYear() {
		return Year;
	}
	public void setVehicle_Make(String vehicle_Make) {
		Vehicle_Make = vehicle_Make;
	}
	public void setVehicle_Make_ID(String vehicle_Make_ID) {
		Vehicle_Make_ID = vehicle_Make_ID;
	}
	public void setVehicle_Model(String vehicle_Model) {
		Vehicle_Model = vehicle_Model;
	}
	public void setVehicle_Model_ID(String vehicle_Model_ID) {
		Vehicle_Model_ID = vehicle_Model_ID;
	}
	public void setVehicle_Regstn_No(String vehicle_Regstn_No) {
		Vehicle_Regstn_No = vehicle_Regstn_No;
	}
	public void setYear(String year) {
		Year = year;
	}

}

class ServiceDetail {

	private String Service_ID;
    private String Service_Provider_ID;
    private String Service_Provider;
    private List<String> Service_Availed;

    public String getService_ID() {
		return Service_ID;
	}
	public String getService_Provider_ID() {
		return Service_Provider_ID;
	}
	public String getService_Provider() {
		return Service_Provider;
	}
	public List<String> getService_Availed() {
		return Service_Availed;
	}
	public void setService_ID(String service_ID) {
		Service_ID = service_ID;
	}
	public void setService_Provider_ID(String service_Provider_ID) {
		Service_Provider_ID = service_Provider_ID;
	}
	public void setService_Provider(String service_Provider) {
		Service_Provider = service_Provider;
	}
	public void setService_Availed(List<String> service_Availed) {
		Service_Availed = service_Availed;
	}
	
}

class miscellaneous {
	
	private String Gaadizo_Comments;
	private String Pick_up;
	private PickUpAddress Pick_up_Address;
	public String getGaadizo_Comments() {
		return Gaadizo_Comments;
	}
	public String getPick_up() {
		return Pick_up;
	}
	public PickUpAddress getPick_up_Address() {
		return Pick_up_Address;
	}
	public void setGaadizo_Comments(String gaadizo_Comments) {
		Gaadizo_Comments = gaadizo_Comments;
	}
	public void setPick_up(String pick_up) {
		Pick_up = pick_up;
	}
	public void setPick_up_Address(PickUpAddress pick_up_Address) {
		Pick_up_Address = pick_up_Address;
	}

}

class PickUpAddress {
	
	private String Area;
	private String Locality;
	private String Land_Mark;
	
	public String getArea() {
		return Area;
	}
	public String getLocality() {
		return Locality;
	}
	public String getLand_Mark() {
		return Land_Mark;
	}
	public void setArea(String area) {
		Area = area;
	}
	public void setLocality(String locality) {
		Locality = locality;
	}
	public void setLand_Mark(String land_Mark) {
		Land_Mark = land_Mark;
	}
	
}