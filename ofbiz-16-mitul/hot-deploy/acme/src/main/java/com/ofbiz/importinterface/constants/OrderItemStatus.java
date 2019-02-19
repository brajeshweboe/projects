package com.ofbiz.importinterface.constants;

public enum OrderItemStatus {
	ITEM_APPROVED("Placed");

	private String wcsStatus;

	OrderItemStatus(String wcsStatus) {
		this.wcsStatus = wcsStatus;
	}

	public static String getOfbizOrderItemStatus(String wcsStatus) {
		for (OrderItemStatus orderItemStatus : OrderItemStatus.values()) {
			if (orderItemStatus.wcsStatus.equals(wcsStatus)) {
				return orderItemStatus.name();
			}
		}
		return null;
	}

}
