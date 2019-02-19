package com.ofbiz.importinterface.constants;

public enum OrderStatus {
	ORDER_APPROVED("Placed");
	
	private String wcsStatus;
	OrderStatus(String wcsStatus){
		this.wcsStatus=wcsStatus; 
	}
	
	public static String getOfbizOrderStatus(String wcsStatus){
		for(OrderStatus orderStatus:OrderStatus.values()){
			if(orderStatus.wcsStatus.equals(wcsStatus)){
				return orderStatus.name();
			}
		}
		return null;
	}
	 
}
