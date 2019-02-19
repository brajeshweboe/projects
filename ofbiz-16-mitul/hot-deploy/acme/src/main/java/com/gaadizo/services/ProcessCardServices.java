package com.gaadizo.services;


import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URL;
import java.util.Locale;

import org.apache.ofbiz.base.util.UtilFormatOut;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.service.ServiceUtil;

import org.apache.ofbiz.service.GenericServiceException;
import org.apache.ofbiz.order.order.OrderReadHelper;
import org.apache.ofbiz.order.shoppingcart.ShoppingCart;
import org.apache.ofbiz.base.util.GeneralException;

import org.apache.ofbiz.service.ServiceUtil;

import org.apache.ofbiz.entity.util.*;
import org.apache.ofbiz.base.util.Debug;
import org.apache.ofbiz.base.util.UtilDateTime;
import org.apache.ofbiz.base.util.UtilMisc;
import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.entity.GenericEntityException;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.service.DispatchContext;
import org.apache.ofbiz.service.LocalDispatcher;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityExpr;
import org.apache.ofbiz.entity.condition.EntityOperator;
import java.sql.Timestamp;

import org.apache.catalina.util.CustomObjectInputStream;

public class ProcessCardServices {
	
	public static final String module = ProcessCardServices.class.getName();
	private static Map<String, String> orderMap;
	private static List<Map<String, String>> orderItemMapList;
	private static Map<String, String> failedOrderMap;
	private static Delegator delegator;
	public static final String entityName = "JOBCARDShoppingCartStore";
	
	public static Map addItemToOrderFromJobCard(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        System.out.println("===============TestInOsafe========================"+context);
        String productId = (String)context.get("productId");
        String orderId = (String)context.get("orderId");
        String jobCardId = (String)context.get("jobCardId");
        
        successResult.put("orderId", orderId);
        successResult.put("jobCardId", jobCardId);
        String shipGroupSeqId = (String)context.get("shipGroupSeqId");
        BigDecimal amount = (BigDecimal)context.get("amount");
        BigDecimal basePrice = (BigDecimal)context.get("basePrice");
        String overridePrice = (String)context.get("overridePrice");
        BigDecimal quantity = (BigDecimal)context.get("quantity");
        Map info = new HashMap();
        Map result = new HashMap();
        List<GenericValue> tobeStore = new ArrayList<GenericValue>();
        GenericValue orderHeaderGV = null;
        GenericValue productGV = null;
        
        try {
          // create the fulfillment record
        
        
        List<GenericValue> orderItemShipGroups = null;
        List<GenericValue> orderRoleList = null;
        try {
        	orderHeaderGV = EntityQuery.use(delegator).from("OrderHeader").where("orderId", context.get("orderId").toString()).cache().queryOne();
        	//orderRoleList = EntityQuery.use(delegator).from("OrderRole").where("orderId", context.get("orderId").toString(),"roleTypeId","BILL_TO_CUSTOMER").cache().queryOne();
        	productGV = EntityQuery.use(delegator).from("Product").where("productId", productId).cache().queryOne();
        	orderItemShipGroups = EntityQuery.use(delegator).from("OrderItemShipGroup").where("orderId", (String) context.get("orderId").toString()).queryList();
			System.out.println("===85========================"+orderItemShipGroups+"====================");
        } catch (GenericEntityException e) {
            Debug.logError(e, module);
            
        }
        GenericValue orderRole = null;
        
        if(UtilValidate.isNotEmpty(orderRoleList)){
        	orderRole = EntityUtil.getFirst(orderRoleList);
        	if(UtilValidate.isNotEmpty(orderRole)){
        		String partyId = orderRole.getString("partyId");
        		if(UtilValidate.isNotEmpty(partyId)){
        			
        		}
        	}
        	
        }
      //===================================================================================
    	Map<String, Object> priceContext = new HashMap<String, Object>();
    	
        priceContext.put("currencyUomId", orderHeaderGV.get("currencyUom"));
        
        OrderReadHelper orh = new OrderReadHelper(orderHeaderGV);
        GenericValue placingParty = orh.getPlacingParty();
        String placingPartyId = null;
        if (placingParty != null) {
            placingPartyId = placingParty.getString("partyId");
        }
        if (placingPartyId != null) {
            priceContext.put("partyId", placingPartyId);
        }
		
        priceContext.put("quantity", quantity);
        priceContext.put("product", productGV);
        priceContext.put("webSiteId", orderHeaderGV.get("webSiteId"));
        priceContext.put("productStoreId", orderHeaderGV.get("productStoreId"));
        // TODO: prodCatalogId, agreementId
        priceContext.put("productPricePurposeId", "PURCHASE");
        priceContext.put("checkIncludeVat", "Y");
        priceContext.put("userLogin", userLogin);
        Map<String, Object> priceResult = null;
        try {
            priceResult = dispatcher.runSync("calculateProductPrice", priceContext);
        } catch (GenericServiceException gse) {
            Debug.logError(gse, module);
        }
        
        BigDecimal amountValue = BigDecimal.ZERO;
        if(UtilValidate.isEmpty(overridePrice)){
        	amountValue = (BigDecimal) priceResult.get("price");
        } else {
        	if("Y".equals(overridePrice)){
        		amountValue = basePrice; 
        	}
        }
    //===================================================================================
    GenericValue orderItemNewGV = delegator.makeValue("OrderItem");
    String orderItemSeqId = delegator.getNextSeqId("OrderItem");
    System.out.println("=====140========================="+orderItemSeqId+"=================iiii====22222=========");
    orderItemNewGV.put("orderItemSeqId",orderItemSeqId);
    orderItemNewGV.put("productId",productId);
    orderItemNewGV.put("orderItemTypeId","PRODUCT_ORDER_ITEM");
    orderItemNewGV.put("orderId",orderId);
    orderItemNewGV.put("quantity",quantity);
    orderItemNewGV.put("unitPrice", amountValue);
    orderItemNewGV.put("statusId", "ITEM_APPROVED");
    tobeStore.add(orderItemNewGV);
        
		
		List<String> orderShipGroups = EntityUtil.getFieldListFromEntityList(orderItemShipGroups,"shipGroupSeqId",true);
		
		//String heightSeqNum = (String) orderShipGroups.get(orderShipGroups.size() -1);
		//BigDecimal seqN = BigDecimal.ONE.add(new BigDecimal(heightSeqNum));
		//String finalheightSeqNum = UtilFormatOut.formatPaddedNumber(seqN.longValue(), 5);
		//String newShipGroupSeqId = finalheightSeqNum;
		//System.out.println("======11111========================="+newShipGroupSeqId+"=================iiii====22222=========");
		
		//Map orderItemShipGroupCtx = new HashMap();
    //    orderItemShipGroupCtx.put("orderId", (String) context.get("orderId"));
//		List<GenericValue> orderItemShipGroupAssoces = delegator.findByAnd("OrderItemShipGroupAssoc", UtilMisc.toMap("orderId", (String) context.get("orderId")));
        
        
		GenericValue orderItemShipGroup = EntityUtil.getFirst(orderItemShipGroups);
		
		//GenericValue orderItemShipGroupAssoc = EntityUtil.getFirst(orderItemShipGroupAssoces);
		GenericValue orderItemSGNewGV = delegator.makeValue("OrderItemShipGroup");
		if(!UtilValidate.isNotEmpty(orderItemShipGroups)){
			System.out.println("=============orderItemShipGroup====================="+orderItemShipGroup);
			
			if(UtilValidate.isNotEmpty(orderItemShipGroup.get("shipmentMethodTypeId"))){
				orderItemSGNewGV.put("shipmentMethodTypeId",orderItemShipGroup.get("shipmentMethodTypeId").toString());
			}
			orderItemSGNewGV.put("vendorPartyId",(String) orderItemShipGroup.get("vendorPartyId"));
			if(UtilValidate.isNotEmpty(orderItemShipGroup.get("carrierPartyId"))){
				orderItemSGNewGV.put("carrierPartyId",orderItemShipGroup.get("carrierPartyId").toString());
			}
			if(UtilValidate.isNotEmpty(orderItemShipGroup.get("carrierRoleTypeId"))){
				orderItemSGNewGV.put("carrierRoleTypeId",orderItemShipGroup.get("carrierRoleTypeId").toString());
			}
			if(UtilValidate.isNotEmpty(orderItemShipGroup.get("contactMechId"))){
				orderItemSGNewGV.put("contactMechId",orderItemShipGroup.get("contactMechId").toString());
			}
			orderItemSGNewGV.put("shipGroupSeqId","00001");
			orderItemSGNewGV.put("orderId",orderId);
			orderItemSGNewGV.put("facilityId",orderHeaderGV.get("originFacilityId"));
			tobeStore.add(orderItemSGNewGV);
		}
       // 
        
        GenericValue orderItemShipGroupAssocGV = delegator.makeValue("OrderItemShipGroupAssoc");
        
        orderItemShipGroupAssocGV.put("quantity",quantity);
        orderItemShipGroupAssocGV.put("shipGroupSeqId","00001");
        orderItemShipGroupAssocGV.put("orderId",orderId);
        orderItemShipGroupAssocGV.put("orderItemSeqId",orderItemSeqId);
        tobeStore.add(orderItemShipGroupAssocGV);
        
        GenericValue inventoryItemGV = delegator.makeValue("InventoryItem");
        
        String inventoryItemId = delegator.getNextSeqId("InventoryItem"); //we have to get sequence based on service centerid
        //TODO we have to add code and get inventory from entity first if it is not then return error.
        inventoryItemGV.put("inventoryItemId", inventoryItemId);
        inventoryItemGV.put("inventoryItemTypeId","NON_SERIAL_INV_ITEM");
        inventoryItemGV.put("productId",productId);
        inventoryItemGV.put("facilityId","GAADIZO_FAC");
        inventoryItemGV.put("quantityOnHandTotal",BigDecimal.ONE);
        inventoryItemGV.put("availableToPromiseTotal",BigDecimal.ONE);
        tobeStore.add(inventoryItemGV);
        //TODO: Need to do changes as per above inventory changes.
        GenericValue orderItemShipGrpInvResGV = delegator.makeValue("OrderItemShipGrpInvRes");
        
        orderItemShipGrpInvResGV.put("reservedDatetime",UtilDateTime.nowTimestamp());
        orderItemShipGrpInvResGV.put("quantity",quantity);
        orderItemShipGrpInvResGV.put("reserveOrderEnumId", "INVRO_FIFO_REC");
        orderItemShipGrpInvResGV.put("inventoryItemId",inventoryItemId);
        orderItemShipGrpInvResGV.put("orderItemSeqId",orderItemSeqId);
        orderItemShipGrpInvResGV.put("shipGroupSeqId","00001");
        orderItemShipGrpInvResGV.put("orderId",orderId);
        tobeStore.add(orderItemShipGrpInvResGV);
			if(tobeStore.size() > 0){
				for(GenericValue tobeStoreS : tobeStore){
					delegator.create(tobeStoreS);
					//if(UtilValidate.isNotEmpty(orderItemSGNewGV)){
					//	delegator.create(orderItemSGNewGV);
					//}
					//delegator.create(orderItemShipGroupAssocGV);
					//delegator.create(inventoryItemGV);
					//delegator.create(orderItemShipGrpInvResGV);
				}
            }
        } catch (GenericEntityException e) {
            return ServiceUtil.returnError(e.getMessage());
        }
        
		return successResult;
    }
	
	public static Map cancelOrderItemCustom(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        
        String purposeFrom = (String)context.get("purposeFrom");
        
        try 
        {
	        System.out.println("===============cancelOrderItemCustom========================"+context);
	        
	        String jobCardId = (String)context.get("jobCardId");
	        successResult.put("jobCardId",jobCardId);
	        GenericValue jobCardGV = delegator.findOne("JobCard", UtilMisc.toMap("jobCardId", jobCardId), false);
        	

	        if(UtilValidate.isNotEmpty(jobCardGV)) {
	        	
	        	String orderItemSeqId = (String)context.get("orderItemSeqId");
	            String orderId = (String)context.get("orderId");
	            successResult.put("orderId",orderId);
	            BigDecimal cancelQuantity = (BigDecimal)context.get("cancelQuantity");
	            
	            
	        	 Map cancelOrderItemCtx = new HashMap(); 
	        	 cancelOrderItemCtx.put("jobCardId", jobCardId);
	        	 cancelOrderItemCtx.put("orderItemSeqId", orderItemSeqId);
	        	 cancelOrderItemCtx.put("orderId", orderId);
	        	 cancelOrderItemCtx.put("cancelQuantity", cancelQuantity);
	        	 cancelOrderItemCtx.put("userLogin", userLogin);
	             Map<String, Object> cancelOrderItemResult = null;
	             try {
	            	 cancelOrderItemResult = dispatcher.runSync("cancelOrderItem", cancelOrderItemCtx);
	             } catch (GenericServiceException gse) {
	                 Debug.logError(gse, module);
	             }
	             System.out.println("=======262=======in purpose========================");
	        	 if(!ServiceUtil.isError(cancelOrderItemResult) && "cancelItemFromPerformInvoice".equals(purposeFrom)){
	        		 GenericValue userLoginGV = delegator.findOne("UserLogin", UtilMisc.toMap("userLoginId", "system"), false);
	        		 GenericValue orderItemGV = delegator.findOne("OrderItem", UtilMisc.toMap("orderId", orderId,"orderItemSeqId", orderItemSeqId), false);
	        		 
	        		 List orderRoleList = EntityQuery.use(delegator).from("OrderRole").where("orderId", context.get("orderId").toString(),"roleTypeId","BILL_TO_CUSTOMER").cache().queryList();
	        		 if(UtilValidate.isNotEmpty(orderRoleList)){
	        			 GenericValue orderRole = EntityUtil.getFirst(orderRoleList);
	        			 if(UtilValidate.isNotEmpty(orderRole)){
	        				 List shoppingLists = EntityQuery.use(delegator).from("ShoppingList").where("partyId", orderRole.getString("partyId"),"shoppingListTypeId", "SLT_WISH_LIST").cache().queryList();
	        				 if(UtilValidate.isNotEmpty(shoppingLists)){
	        					 GenericValue shoppingList = EntityUtil.getFirst(shoppingLists);
	        					 //Update shopping list item
	        					 if(UtilValidate.isNotEmpty(shoppingList)){
        		            		 String shoppingListId = (String) shoppingList.get("shoppingListId");
        		            		 Map createShoppingListItemCtx = new HashMap();
        		            		 createShoppingListItemCtx.put("shoppingListId", shoppingListId);
        		            		 createShoppingListItemCtx.put("productId", orderItemGV.getString("productId"));
        		            		 createShoppingListItemCtx.put("userLogin", userLoginGV);
        		            		 //createShoppingListItemCtx.put("productStoreId", productStoreId);
        		            		 Map createShoppingListItemResult = dispatcher.runSync("createShoppingListItem", createShoppingListItemCtx);
        		            	 }
	        					 
	        				 } else {
	        					 try {
	        						 Map createShoppingListCtx = new HashMap();
	        						 createShoppingListCtx.put("partyId", orderRole.get("partyId"));
	        						 createShoppingListCtx.put("userLogin", userLoginGV);
	        						 createShoppingListCtx.put("shoppingListTypeId", "SLT_WISH_LIST");
	        						 //createShoppingListCtx.put("productStoreId", orderRole.get("partyId"));
	        						 
	        		            	 Map createShoppingListResult = dispatcher.runSync("createShoppingList", createShoppingListCtx);
	        		            	 
	        		            	 if(!ServiceUtil.isError(createShoppingListResult)){
	        		            		 String shoppingListId = (String) createShoppingListResult.get("shoppingListId");
	        		            		 Map createShoppingListItemCtx = new HashMap();
	        		            		 createShoppingListItemCtx.put("shoppingListId", shoppingListId);
	        		            		 createShoppingListItemCtx.put("userLogin", userLoginGV);
	        		            		 createShoppingListItemCtx.put("productId", orderItemGV.getString("productId"));
	        		            		 //createShoppingListItemCtx.put("productStoreId", productStoreId);
	        		            		 Map createShoppingListItemResult = dispatcher.runSync("createShoppingListItem", createShoppingListItemCtx);
	        		            	 }
	        		            	 //also create shopping list item
	        		             } catch (GenericServiceException gse) {
	        		                 Debug.logError(gse, module);
	        		             }
	        				 }
	        			 }
	        		 }
	        		 
	        		 Map createShoppingListCtx = new HashMap();
	        		// createShoppingListCtx
	        		 
	        		 System.out.println("=======264========add item in wish list========================");
	        	 }
	        	
	        }
		        
        }
        catch(Exception e)
        {
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    return successResult;
	}
	

	public static Map updateJobCard(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        try 
        {
	        System.out.println("===============updateJobCard========================"+context);
	        String jobCardId = (String)context.get("jobCardId");
	        successResult.put("jobCardId",jobCardId);
	        String partyId = (String)context.get("partyId");
	        List<GenericValue> jobCardGVList = EntityQuery.use(delegator).from("JobCard").where("jobCardId", jobCardId,"serviceProviderId",userLogin.getString("ownerCompanyId")).cache().queryList();
	        List<GenericValue> partyAndContactMechEmailList = EntityQuery.use(delegator).from("PartyAndContactMech").where("partyId", partyId, "ownerId", userLogin.getString("ownerCompanyId"),"contactMechTypeId","EMAIL_ADDRESS").cache().queryList();
	        List<GenericValue> partyAndContactMechAddressList = EntityQuery.use(delegator).from("PartyAndContactMech").where("partyId", partyId, "ownerId", userLogin.getString("ownerCompanyId"),"contactMechTypeId","POSTAL_ADDRESS").cache().queryList();
	        Map updateContactMechResult = null;
			Map updatePartyPostalAddressResult = null;
	        if(UtilValidate.isNotEmpty(partyAndContactMechEmailList)) {
	        	GenericValue partyAndContactMechEmail = EntityUtil.getFirst(partyAndContactMechEmailList);
	        	Map<String, Object> updateContactMechContext = new HashMap<String, Object>();
	        	String emailAddress = (String)context.get("emailAddress");
		        if(UtilValidate.isNotEmpty(emailAddress)){
		        	updateContactMechContext.put("infoString",emailAddress);
		        }
				updateContactMechContext.put("contactMechId",partyAndContactMechEmail.getString("contactMechId"));
				updateContactMechContext.put("contactMechTypeId","EMAIL_ADDRESS");
				updateContactMechContext.put("userLogin",userLogin);
		        
		        try {
		            updateContactMechResult = dispatcher.runSync("updateContactMech", updateContactMechContext);
		        } catch (GenericServiceException gse) {
		            Debug.logError(gse, module);
		        }
	        }
			if(UtilValidate.isNotEmpty(partyAndContactMechAddressList)) {
	        	GenericValue partyAndContactMechAddress = EntityUtil.getFirst(partyAndContactMechAddressList);
				String contactNumber = (String)context.get("contactNumber");
		        String address = (String)context.get("address");
		        String city = (String)context.get("city");
		        String postalCode = (String)context.get("postalCode");
		        Map<String, Object> updatePartyPostalAddressCtx = new HashMap<String, Object>();
	        	partyId = partyAndContactMechAddress.getString("partyId");
		        String contactMechId = partyAndContactMechAddress.getString("contactMechId");
				String  contactMechTypeId= partyAndContactMechAddress.getString("contactMechTypeId");
		        
				updatePartyPostalAddressCtx.put("partyId",partyId);
				updatePartyPostalAddressCtx.put("userLogin",userLogin);
				updatePartyPostalAddressCtx.put("contactMechId",contactMechId);
				updatePartyPostalAddressCtx.put("contactMechTypeId",contactMechTypeId);
		        if(UtilValidate.isNotEmpty(contactNumber)){
		        	updatePartyPostalAddressCtx.put("contactNumber",contactNumber);
		        }
		        if(UtilValidate.isNotEmpty(address)){
		        	updatePartyPostalAddressCtx.put("address1",address);
		        }
		        if(UtilValidate.isNotEmpty(city)){
		        	updatePartyPostalAddressCtx.put("city",city);
		        }
		        if(UtilValidate.isNotEmpty(postalCode)){
		        	updatePartyPostalAddressCtx.put("postalCode",postalCode);
		        }
		        try {
		        	updatePartyPostalAddressResult = dispatcher.runSync("updatePartyPostalAddress", updatePartyPostalAddressCtx);
		        } catch (GenericServiceException gse) {
		            Debug.logError(gse, module);
		        }
	        }
	        if(UtilValidate.isNotEmpty(jobCardGVList)) {
	        	GenericValue jobCardGV = EntityUtil.getFirst(jobCardGVList);
		        String customerName = (String)context.get("customerName");
		        String serviceDate = (String)context.get("serviceDate");
		        String pickup = (String)context.get("pickup");
		        String vehicleId = (String)context.get("vehicleId");
		        String vehicleModel = (String)context.get("vehicleModel");
		        String vehicleManufacturer = (String)context.get("vehicleManufacturer");
		        String fuelType = (String)context.get("fuelType");
		        String oilCapacity = (String)context.get("oilCapacity");
		        String vehicleVariant = (String)context.get("vehicleVariant");
		        String VehicleRegNo = (String)context.get("VehicleRegNo");
		        String VehicleRegYear = (String)context.get("VehicleRegYear");
		        String ServiceDate = (String)context.get("ServiceDate");
		        String LastServiceDate = (String)context.get("LastServiceDate");
		        String deliveryDate = (String)context.get("deliveryDate");
		        String vehicleInsuranceComp = (String)context.get("vehicleInsuranceComp");
		        String paymentMode = (String)context.get("paymentMode");
		        String InsuranceExpiryDate = (String)context.get("InsuranceExpiryDate");
		        String customerType = (String)context.get("customerType");
		        if(UtilValidate.isNotEmpty(customerName)){
		        	jobCardGV.put("customerName",customerName);
		        }
		        if(UtilValidate.isNotEmpty(serviceDate)){
		        	jobCardGV.put("serviceDate",serviceDate);
		        }
		        if(UtilValidate.isNotEmpty(pickup)){
		        	jobCardGV.put("pickup",pickup);
		        }
		        if(UtilValidate.isNotEmpty(vehicleId)){
		        	jobCardGV.put("vehicleId",vehicleId);
		        }
		        if(UtilValidate.isNotEmpty(vehicleModel)){
		        	jobCardGV.put("vehicleModel",vehicleModel);
		        }
		        if(UtilValidate.isNotEmpty(vehicleManufacturer)){
		        	jobCardGV.put("vehicleManufacturer",vehicleManufacturer);
		        }
		        if(UtilValidate.isNotEmpty(fuelType)){
		        	jobCardGV.put("fuelType",fuelType);
		        }
		        if(UtilValidate.isNotEmpty(oilCapacity)){
		        	jobCardGV.put("oilCapacity",oilCapacity);
		        }
		        if(UtilValidate.isNotEmpty(vehicleVariant)){
		        	jobCardGV.put("vehicleVariant",vehicleVariant);
		        }
		        if(UtilValidate.isNotEmpty(VehicleRegNo)){
		        	jobCardGV.put("VehicleRegNo",VehicleRegNo);
		        }
		        if(UtilValidate.isNotEmpty(VehicleRegYear)){
		        	jobCardGV.put("VehicleRegYear",VehicleRegYear);
		        }
		        if(UtilValidate.isNotEmpty(LastServiceDate)){
		        	jobCardGV.put("LastServiceDate",LastServiceDate);
		        }
		        if(UtilValidate.isNotEmpty(deliveryDate)){
		        	jobCardGV.put("deliveryDate",deliveryDate);
		        }
		        if(UtilValidate.isNotEmpty(vehicleInsuranceComp)){
		        	jobCardGV.put("vehicleInsuranceComp",vehicleInsuranceComp);
		        }
		        if(UtilValidate.isNotEmpty(paymentMode)){
		        	jobCardGV.put("paymentMode",paymentMode);
		        }
		        if(UtilValidate.isNotEmpty(InsuranceExpiryDate)){
		        	jobCardGV.put("InsuranceExpiryDate",InsuranceExpiryDate);
		        }
		        if(UtilValidate.isNotEmpty(customerType)){
		        	jobCardGV.put("customerType",customerType);
		        }
		        try {
	                delegator.store(jobCardGV);
	            } catch (GenericEntityException gee) {
	                return ServiceUtil.returnError(gee.getMessage());
	            }
				
	        } else {
	        	return ServiceUtil.returnError("Entered/Selected Job Card does not exists in the system.");
	        }
        } 
        catch(Exception e)
        {
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    return successResult;
	}

	public static Map generatePerFormInvoiceForJobCard(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        try 
        {
	        System.out.println("=====521==========updateJobCard========================"+context);
	        String jobCardId = (String)context.get("jobCardId");
	        String orderId = (String)context.get("orderId");
	        
	        successResult.put("jobCardId",jobCardId);
	        System.out.println("=====525==========updateJobCard========================"+context);
	        GenericValue jobCardGV = EntityQuery.use(delegator).from("JobCard").where("jobCardId", jobCardId).cache().queryOne();
	        if(UtilValidate.isEmpty(orderId) && UtilValidate.isNotEmpty(jobCardGV)){
	        	orderId = (String)jobCardGV.get("orderId");
	        }
        	//orderItemShipGroups = EntityQuery.use(delegator).from("OrderItemShipGroup").where("orderId", (String) context.get("orderId").toString()).queryList();
	        if(UtilValidate.isNotEmpty(jobCardId)) {
	        	System.out.println("=====529==========updateJobCard========================"+context);
	        	List orderIdList = (List)context.get("orderIdList");
		        String facilityId = (String)context.get("facilityId");
		        GenericValue userLoginGV = null;
		        if(UtilValidate.isNotEmpty(userLogin)){
		        	userLoginGV = userLogin;
		        }else{
		        	userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").cache().queryOne();
		        }
		        System.out.println("=====542=========before PicklistBin ========================"+context);
		        List picklistBinList = EntityQuery.use(delegator).from("PicklistBin").where("primaryOrderId", (String) context.get("orderId")).queryList();
		        System.out.println("=====544=========updateJobCard========================");
		        if(UtilValidate.isEmpty(picklistBinList)){
			        Map createPicklistFromOrdersCtx = new HashMap();
			        createPicklistFromOrdersCtx.put("orderIdList", orderIdList);
			        createPicklistFromOrdersCtx.put("facilityId", facilityId);
			        createPicklistFromOrdersCtx.put("userLogin", userLoginGV);
			        System.out.println("====542===========createPicklistFromOrdersCtx========================"+createPicklistFromOrdersCtx);
	       		 	Map createPicklistFromOrdersResult = dispatcher.runSync("createPicklistFromOrders", createPicklistFromOrdersCtx);
	       		 System.out.println("=====552====w2w2w2w2======updateJobCard========="+createPicklistFromOrdersResult+"===============");
	       		    if(!ServiceUtil.isError(createPicklistFromOrdersResult)){
	       		    	System.out.println("=====554==========pickListId=========="+createPicklistFromOrdersResult.get("picklistId")+"==============");
	       		    	successResult.put("picklistId",(String) createPicklistFromOrdersResult.get("picklistId"));
	       		    	
	       		    	System.out.println("=====556==========successResult=========="+successResult+"==============");
	       		    	//jobCardGV.put("status","PERFORM_INVOICE_GENE");
	       		    	System.out.println("=====558==========jobCardGV=========="+jobCardGV+"==============");
	       		    	/*try {
	       		    		System.out.println("=====560==========jobCardGV=========="+jobCardGV+"==============");
	       		    		delegator.store(jobCardGV);
	       		    		System.out.println("=====562==========updateJobCard========================");
	       		    	} catch (GenericEntityException gee) {
	       		    		return ServiceUtil.returnError(gee.getMessage());
	       		    	}*/
	       		    }
	       		    System.out.println("=====563==========updateJobCard========================");
	        	} else {
	        		GenericValue picklistBinGV = EntityUtil.getFirst(picklistBinList);
	        		System.out.println("=====566==========picklistBinGV============"+picklistBinGV+"============");
	        		List<GenericValue> picklistItemList = EntityQuery.use(delegator).from("PicklistItem").where("picklistBinId",picklistBinGV.getString("picklistBinId"),"orderId", (String) context.get("orderId").toString()).queryList();
	        		System.out.println("=====568==========picklistBinGV============"+picklistItemList.size()+"============");
	        		List<GenericValue> orderItemList = EntityQuery.use(delegator).from("OrderItem").where("orderId", (String) context.get("orderId").toString(), "statusId", "ITEM_APPROVED").queryList();
	        		System.out.println("=====570==========orderItemList============"+orderItemList.size()+"============");
	        		if(UtilValidate.isNotEmpty(picklistItemList) && UtilValidate.isNotEmpty(orderItemList)){
	        			List<String> orderItemSeqIdList = EntityUtil.getFieldListFromEntityList(orderItemList, "orderItemSeqId", true);
	        			List<String> picklistItemSeqIdList = EntityUtil.getFieldListFromEntityList(picklistItemList, "orderItemSeqId", true);
	        			List<String> orderItemSeqIdNotInPickList = new ArrayList<String>();
	        			System.out.println("===============564========================");
	        			for(String picklistItemSeqId : picklistItemSeqIdList){
	        				if(orderItemSeqIdList.contains(picklistItemSeqId)){
	        				    
	        				} else {
	        				    orderItemSeqIdNotInPickList.add(picklistItemSeqId);
	        				}
	        			}
	        			if(UtilValidate.isNotEmpty(orderItemSeqIdNotInPickList) && orderItemSeqIdNotInPickList.size() > 0){
	        				//List<GenericValue> picklistItemsList = EntityQuery.use(delegator).from("PicklistItem").where("picklistBinId",picklistBinGV.getString("picklistBinId"),"orderId", (String) context.get("orderId").toString()).queryList();
	        				List<EntityExpr> exprs = UtilMisc.toList(EntityCondition.makeCondition("picklistBinId", EntityOperator.EQUALS, picklistBinGV.getString("picklistBinId")),
                                    EntityCondition.makeCondition("orderId", EntityOperator.EQUALS, (String) context.get("orderId").toString()),
                                    EntityCondition.makeCondition("orderItemSeqId", EntityOperator.IN, orderItemSeqIdNotInPickList));
                            EntityCondition andCondition = EntityCondition.makeCondition(exprs, EntityOperator.AND);
                            List<GenericValue> picklistItems =  delegator.findList("PicklistItem", andCondition, null, null, null, false);
                            
                            System.out.println("==573=====picklistItems=========="+picklistItems.size()+"=======================");
                            
                            for(GenericValue picklistItem : picklistItems){
                            	Map<String,String> deletePicklistItemCtx = new HashMap<String,String>();
                            	deletePicklistItemCtx.put("picklistBinId", picklistItem.getString("picklistBinId"));
                            	deletePicklistItemCtx.put("orderId", picklistItem.getString("orderId"));
                            	deletePicklistItemCtx.put("orderItemSeqId", picklistItem.getString("orderItemSeqId"));
                            	deletePicklistItemCtx.put("shipGroupSeqId", picklistItem.getString("shipGroupSeqId"));
                            	deletePicklistItemCtx.put("inventoryItemId", picklistItem.getString("inventoryItemId"));
                            	Map createProductPromoResult = dispatcher.runSync("deletePicklistItem", deletePicklistItemCtx);
    	        			}
	        			}
	        		}
	        		
	        	}
	        } else {
	        	return ServiceUtil.returnError("Entered/Selected Job Card does not exists in the system.");
	        }
        } 
        catch(Exception e)
        {
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    return successResult;
	}
	
	public static Map addPromotionForPerFormInvoiceOfJobCard(DispatchContext ctx, Map context)
    {
        LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        try 
        {
            String jobCardId = (String)context.get("jobCardId");
            String orderId = (String)context.get("orderId");
            String productPromoId = (String)context.get("productPromoId");
            
            successResult.put("jobCardId",jobCardId);
            successResult.put("orderId",orderId);
            BigDecimal discountAmountBD = (BigDecimal)context.get("discountAmount");
            //BigDecimal discountAmountBD = new BigDecimal(discountAmount);
            
            System.out.println("===============updateJobCard========================"+context);
            String integerCondValue = EntityUtilProperties.getPropertyValue("gaadizo","performInvoiceMinValue", delegator);
            BigDecimal integerCondValueBD = new BigDecimal(integerCondValue);
            GenericValue orderHeader = EntityQuery.use(delegator).from("OrderHeader").where("orderId", orderId).queryOne();
            if(orderHeader.getBigDecimal("grandTotal").compareTo(integerCondValueBD) >= 0){
                Timestamp nowTimestamp = UtilDateTime.nowTimestamp();
                if(discountAmountBD.compareTo(BigDecimal.ZERO) > 0){
                    GenericValue userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").cache().queryOne();
                        if(UtilValidate.isEmpty(productPromoId)) 
                        {
                            Map createProductPromoCtx = new HashMap();
                            
                            GenericValue createProductPromoGV = delegator.makeValue("ProductPromo");
                            productPromoId = delegator.getNextSeqId("ProductPromo");
                            createProductPromoGV.put("productPromoId", productPromoId);
                            createProductPromoGV.put("createdDate", nowTimestamp);
                            createProductPromoGV.put("lastModifiedDate", nowTimestamp);
                            createProductPromoGV.put("lastModifiedByUserLogin", userLogin.get("userLoginId"));
                            createProductPromoGV.put("promoName", "Discount by User for Card:"+jobCardId+"_"+nowTimestamp+"_"+discountAmountBD);
                            createProductPromoGV.put("promoText", "Manual Entered Promo for:"+jobCardId+"by"+userLogin.get("userLoginId").toString()+"_"+nowTimestamp);
                            createProductPromoGV.put("createdByUserLogin", userLoginGV.get("userLoginId").toString());
                            //createProductPromoGV.put("userLogin", userLoginGV);
                            delegator.create(createProductPromoGV);
                           // Map createProductPromoResult = dispatcher.runSync("createProductPromo", createProductPromoCtx);
                        
                            //if(!ServiceUtil.isError(createProductPromoResult)){
                               // Map createProductStorePromoApplCtx = new HashMap();
                                GenericValue createProductStorePromoApplGV = delegator.makeValue("ProductStorePromoAppl");
                                createProductStorePromoApplGV.put("fromDate", nowTimestamp);
                                createProductStorePromoApplGV.put("productStoreId", userLogin.get("ownerId"));
                                createProductStorePromoApplGV.put("productPromoId", productPromoId);
                                delegator.create(createProductStorePromoApplGV);
                               // Map createProductStorePromoApplResult = dispatcher.runSync("createProductStorePromoAppl", createProductStorePromoApplCtx);
                            
                                Map createProductPromoRuleCtx = new HashMap();
                                GenericValue createProductPromoRuleGV = delegator.makeValue("ProductPromoRule");
                                createProductPromoRuleGV.put("productPromoId", productPromoId);
                                createProductPromoRuleGV.put("productPromoRuleId", "01");
                               // createProductPromoRuleGV.put("userLogin", userLoginGV);
                                createProductPromoRuleGV.put("ruleName", "User entered discount");
                                delegator.create(createProductPromoRuleGV);
                                
                                //Map createProductPromoRuleResult = dispatcher.runSync("createProductPromoRule", createProductPromoRuleGV);
                                
                            
                                Map createProductPromoCondCtx = new HashMap();
                                GenericValue createProductPromoCondGV = delegator.makeValue("ProductPromoCond");
                                createProductPromoCondGV.put("productPromoId", productPromoId);
                                createProductPromoCondGV.put("productPromoCondSeqId", "01");
                                createProductPromoCondGV.put("productPromoRuleId", "01");
                                createProductPromoCondGV.put("inputParamEnumId", "PPIP_ORDER_TOTAL");
                                createProductPromoCondGV.put("operatorEnumId", "PPC_GTE");
                                createProductPromoCondGV.put("condValue", integerCondValue);
                                //createProductPromoCondGV.put("userLogin", userLoginGV);
                                delegator.create(createProductPromoCondGV);
                                
                                //Map createProductPromoCondResult = dispatcher.runSync("createProductPromoCond", createProductPromoCondCtx);
                            
                                GenericValue createProductPromoActionGV = delegator.makeValue("ProductPromoAction");
                                createProductPromoActionGV.put("productPromoId", productPromoId);
                                createProductPromoActionGV.put("productPromoRuleId", "01");
                                createProductPromoActionGV.put("productPromoActionSeqId", "01");
                                createProductPromoActionGV.put("productPromoActionEnumId", "PROMO_ORDER_AMOUNT");
                                createProductPromoActionGV.put("orderAdjustmentTypeId", "PROMOTION_ADJUSTMENT");
                                createProductPromoActionGV.put("useCartQuantity", "N");
                                createProductPromoActionGV.put("amount", discountAmountBD);
                                delegator.create(createProductPromoActionGV);
                                //Map createProductPromoActionResult = dispatcher.runSync("createProductPromoAction", createProductPromoActionCtx);
                            //}
                        }else{
                            GenericValue productPromoActionGV = EntityQuery.use(delegator).from("ProductPromoAction").where("productPromoId", productPromoId,"productPromoRuleId", "01","productPromoActionSeqId", "01").cache().queryOne();
                            productPromoActionGV.put("amount", discountAmountBD);
                            delegator.store(productPromoActionGV);
                        }
                        
                        //===========================================================
                        GenericValue orderAdjustmentGV = EntityQuery.use(delegator).from("OrderAdjustment").where("orderAdjustmentId", jobCardId).queryOne();
                        if(UtilValidate.isEmpty(orderAdjustmentGV)){
                            GenericValue createOrderAdjustmentGV = delegator.makeValue("OrderAdjustment");
                            createOrderAdjustmentGV.put("productPromoId", productPromoId);
                            createOrderAdjustmentGV.put("productPromoRuleId", "01");
                            createOrderAdjustmentGV.put("orderItemSeqId", "_NA_");
                            createOrderAdjustmentGV.put("orderAdjustmentTypeId","PROMOTION_ADJUSTMENT");
                            createOrderAdjustmentGV.put("productPromoActionSeqId", "01");
                            createOrderAdjustmentGV.put("description", "Discount");
                            createOrderAdjustmentGV.put("comments", discountAmountBD+"_"+nowTimestamp+"_"+userLogin.get("userLoginId").toString());
                            
                            createOrderAdjustmentGV.put("createdByUserLogin", userLogin.get("userLoginId"));
                            createOrderAdjustmentGV.put("amount", discountAmountBD.negate());
                            createOrderAdjustmentGV.put("orderId", orderId);
                            createOrderAdjustmentGV.put("createdDate", nowTimestamp);
                            //String orderAdjustmentId = delegator.getNextSeqId("OrderAdjustment");
                            createOrderAdjustmentGV.put("orderAdjustmentId", jobCardId);
                            delegator.create(createOrderAdjustmentGV);
                        } else {
                            orderAdjustmentGV.put("amount", discountAmountBD.negate());
                            orderAdjustmentGV.put("comments", orderAdjustmentGV.get("comments")+"_"+discountAmountBD+"_"+nowTimestamp+"_"+userLogin.get("userLoginId").toString());
                            delegator.store(orderAdjustmentGV);
                        }
                } else {
                    return ServiceUtil.returnError("Entered/Selected Job Card does not exists in the system.");
                }
            } else {
                return ServiceUtil.returnError("JobCard Total is less for discount.");
            }
        } 
        catch(Exception e)
        {
            return ServiceUtil.returnError(e.getMessage());
        }
        return successResult;
    }
	
	public static Map updateCustomerFromJobCard(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        Locale locale = (Locale) context.get("locale");
        Delegator delegator = ctx.getDelegator();
        try 
        {
	        System.out.println("===============updateCustomerFromJobCard========================"+context);
	        
	        String jobCardId = (String)context.get("jobCardId");
	        String orderId = (String)context.get("orderId");
	        String partyId = (String)context.get("partyId");
	        successResult.put("jobCardId",jobCardId);
	        successResult.put("orderId",orderId);
	        
	        GenericValue jobCardGV = EntityQuery.use(delegator).from("JobCard").where("jobCardId", jobCardId).cache().queryOne();
	        
        	//orderItemShipGroups = EntityQuery.use(delegator).from("OrderItemShipGroup").where("orderId", (String) context.get("orderId").toString()).queryList();
	        if(UtilValidate.isNotEmpty(jobCardGV)) {
	        	
	        	List orderIdList = (List)context.get("orderIdList");
		        String facilityId = (String)context.get("facilityId");
		        
		        GenericValue userLoginGV = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").cache().queryOne();
		        
		        Map createPicklistFromOrdersCtx = new HashMap();
		        createPicklistFromOrdersCtx.put("orderIdList", orderIdList);
		        createPicklistFromOrdersCtx.put("facilityId", facilityId);
		        createPicklistFromOrdersCtx.put("userLogin", userLoginGV);
       		 	Map createPicklistFromOrdersResult = dispatcher.runSync("createPicklistFromOrders", createPicklistFromOrdersCtx);
						
	       		 if(!ServiceUtil.isError(createPicklistFromOrdersResult)){
	       			jobCardGV.put("status","PERFORM_INVOICE_GENE");
	       			
	       			try {
		                delegator.store(jobCardGV);
		            } catch (GenericEntityException gee) {
		                return ServiceUtil.returnError(gee.getMessage());
		            }
	       		 }
	        } else {
	        	return ServiceUtil.returnError("Entered/Selected Job Card does not exists in the system.");
	        }
        } 
        catch(Exception e)
        {
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    return successResult;
	}
	
	public static Map createCustomerAndJobCardFromRegistration(DispatchContext ctx, Map context)
	{
		LocalDispatcher dispatcher = ctx.getDispatcher();
        GenericValue userLogin = (GenericValue) context.get("userLogin");
        Map successResult = ServiceUtil.returnSuccess();
        try {
	        /*if(UtilValidate.isNotEmpty(userLogin)){
	        	userLogin = EntityQuery.use(delegator).from("UserLogin").where("userLoginId", "system").cache().queryOne();
	        }
	        */
	        Locale locale = (Locale) context.get("locale");
	        Delegator delegator = ctx.getDelegator();
	        System.out.println("===============updateCustomerFromJobCard========================"+context);
	        //get next sequence from SC based on given scid
	        //get next sequence of customer from sc
	        
	        String serviceCenterId = (String)context.get("serviceCenterId");
	        String registationNumber = (String)context.get("registrationNumber");
	        String customerId = (String)context.get("customerId");
	        String customerType = "INDEPENDENT";
	        String partyId = null;
	        String orderId = null;
	        
	        if(UtilValidate.isNotEmpty(customerId)){
	        	customerType = "GAADIZO";
	        	partyId = customerId;
	        }
	        
	        String vehicleImageUrl = (String)context.get("vehicleImageUrl");
	        
	        if(UtilValidate.isNotEmpty(registationNumber) && UtilValidate.isNotEmpty(serviceCenterId)){
	        	Map<String,Object> getNextPartyIdContext = new HashMap<String,Object>();
		        getNextPartyIdContext.put("ownerId", serviceCenterId);
	        	getNextPartyIdContext.put("userLogin", userLogin);
	        	
	        	Map<String, Object> getNextJobCardIdContext = new HashMap<String, Object>();
	        	getNextJobCardIdContext.put("ownerId", serviceCenterId);
	        	getNextJobCardIdContext.put("userLogin", userLogin);
	        	
	        	if(UtilValidate.isEmpty(partyId)){
	        		Map<String, Object> getNextPartyIdResult = dispatcher.runSync("getNextPartyId", getNextPartyIdContext);
	        		partyId = (String) getNextPartyIdResult.get("partyId");
	        		
	        		Map<String, Object> getNextOrderIdResult = dispatcher.runSync("getNextOrderId", UtilMisc.toMap("userLogin", userLogin,"ownerId",serviceCenterId));
	        		orderId = (String) getNextOrderIdResult.get("orderId");
	        	}
	            
	            Map<String, Object> getNextJobCardIdResult = dispatcher.runSync("getNextJobCardId", getNextJobCardIdContext);
	            if (ServiceUtil.isError(getNextJobCardIdResult)) {
	                String errMsg = "Party sequence in corret.";
	                return ServiceUtil.returnError(errMsg, null, null, getNextJobCardIdResult);
	            }
	            String jobCardId = (String) getNextJobCardIdResult.get("jobCardId");
	            
	            Map<String, Object> createJobCardResult = null;
	            
            	Map<String,Object> createPersonCtx = UtilMisc.toMap("partyId", partyId,"ownerId",serviceCenterId, "userLogin", userLogin);
            	Map<String,Object> createPersonResult = dispatcher.runSync("createPerson", createPersonCtx);
            	
            	Map<String,Object> createOrderHeaderCtx = UtilMisc.toMap("orderId", orderId,"jobCardId",jobCardId, "userLogin", userLogin,"orderTypeId","SALES_ORDER","productStoreId",serviceCenterId);
            	createOrderHeaderCtx.put("originFacilityId", serviceCenterId);
            	createOrderHeaderCtx.put("ownerId", serviceCenterId);
            	//createOrderHeaderCtx.put("statusId", "ORDER_APPROVED");
            	createOrderHeaderCtx.put("orderDate", UtilDateTime.nowTimestamp());
            	Map<String,Object> createOrderHeaderResult = dispatcher.runSync("createOrderHeader", createOrderHeaderCtx);
            	
                
                Map<String,Object> createPartyRoleCUSTOMERCtx = UtilMisc.toMap("partyId", partyId,"roleTypeId","CUSTOMER", "userLogin", userLogin);
                Map<String,Object> createPartyRoleCUSTOMERResult = dispatcher.runSync("createPartyRole", createPartyRoleCUSTOMERCtx);
                
                Map<String,Object> createPartyRoleBILL_TO_CUSTOMERCtx = UtilMisc.toMap("partyId", partyId,"roleTypeId","BILL_TO_CUSTOMER", "userLogin", userLogin);
                Map<String,Object> createPartyRoleBILL_TO_CUSTOMERResult = dispatcher.runSync("createPartyRole", createPartyRoleBILL_TO_CUSTOMERCtx);
                
                Map<String,Object> createPartyRoleEND_USER_CUSTOMERCtx = UtilMisc.toMap("partyId", partyId,"roleTypeId","END_USER_CUSTOMER", "userLogin", userLogin);
                Map<String,Object> createPartyRoleEND_USER_CUSTOMERResult = dispatcher.runSync("createPartyRole", createPartyRoleEND_USER_CUSTOMERCtx);
                
                Map<String,Object> createPartyRoleSHIP_TO_CUSTOMERCtx = UtilMisc.toMap("partyId", partyId,"roleTypeId","SHIP_TO_CUSTOMER", "userLogin", userLogin);
                Map<String,Object> createPartyRoleSHIP_TO_CUSTOMERResult = dispatcher.runSync("createPartyRole", createPartyRoleSHIP_TO_CUSTOMERCtx);
                
                Map<String,Object> createPartyRolePLACING_CUSTOMERCtx = UtilMisc.toMap("partyId", partyId,"roleTypeId","PLACING_CUSTOMER", "userLogin", userLogin);
                Map<String,Object> createPartyRolePLACING_CUSTOMERResult = dispatcher.runSync("createPartyRole", createPartyRolePLACING_CUSTOMERCtx);
                
                Map<String,Object> createJobCardCtx = UtilMisc.toMap("jobCardId", jobCardId, "customerId", partyId,"registationNumber",registationNumber, "serviceProviderId",serviceCenterId,"serviceDate",UtilDateTime.nowTimestamp().toString(), "userLogin", userLogin);
                createJobCardCtx.put("customerType", customerType);
                createJobCardCtx.put("orderId", orderId);
                createJobCardResult = dispatcher.runSync("createJobCard", createJobCardCtx);
	            System.out.println("===orderId======="+orderId+"=========");
	            System.out.println("===jobCardId======="+jobCardId+"=========");
	            System.out.println("===customerId======="+partyId+"=========");
	        }
        } 
        catch(Exception e)
        {
	    	return ServiceUtil.returnError(e.getMessage());
	    }
	    return successResult;
	}
	
	 /**
     * The Class JOBCARDShoppingCartStore.
     */
    public static class JOBCARDShoppingCartStore
    {
        protected Delegator delegator = null;

        public JOBCARDShoppingCartStore(Delegator delegator) {
            this.delegator = delegator;
        }

        public ShoppingCart load(String token, GenericValue userLogin) throws ClassNotFoundException, IOException {
            ShoppingCart _cart = null;
            GenericValue cartValue = null;
            try {
                cartValue = delegator.findOne(entityName, false, "token", token,"ownerId",userLogin.getString("ownerCompanyId"));
            } catch (GenericEntityException e) {
                throw new IOException(e.getMessage());
            }

            if (cartValue != null) {
                byte[] bytes = cartValue.getBytes("cartInfo");
                if (bytes != null) {
                    //_cart = (ShoppingCart) SerializationUtils.deserialize(bytes);

                	ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
                    BufferedInputStream bis = new BufferedInputStream(bais);

                    ClassLoader classLoader = JOBCARDShoppingCartStore.class.getClassLoader();

                    ObjectInputStream ois = null;
                    /*if (classLoader != null) {
                        ois = new CustomObjectInputStream(bis, classLoader);
                    } else {*/
                        ois = new ObjectInputStream(bis);
                    //}

                    _cart = (ShoppingCart) ois.readObject();
                    ois.close();
                    bis.close();
                    bais.close();
                }
            }
            return _cart;
        }

        public void remove(String token, GenericValue userLogin) throws IOException {
            try {
                delegator.removeByAnd(entityName, "token", token,"ownerId",userLogin.getString("ownerCompanyId"));
            } catch (GenericEntityException e) {
                throw new IOException(e.getMessage());
            }
        }

        public void clear() throws IOException {
            try {
                delegator.removeAll(entityName);
            } catch (GenericEntityException e) {
                throw new IOException(e.getMessage());
            }
        }

        public void save(String token, ShoppingCart cart, GenericValue userLogin) throws IOException {
            byte[] obs = SerializationUtils.serialize(cart);

            GenericValue sessionValue = delegator.makeValue(entityName);
            sessionValue.setBytes("cartInfo", obs);
            sessionValue.set("token", token);
            sessionValue.set("ownerId", userLogin.getString("ownerCompanyId"));

            try {
                delegator.createOrStore(sessionValue);
            } catch (GenericEntityException e) {
                throw new IOException(e.getMessage());
            }

            Debug.logError("Persisted Shopping Cart [" + token + "]", module);
        }
    }
}