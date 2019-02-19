import org.apache.ofbiz.base.util.*
import org.apache.ofbiz.order.order.*
import org.apache.ofbiz.entity.util.EntityUtil
import org.apache.ofbiz.entity.util.EntityUtilProperties
import org.apache.ofbiz.entity.condition.EntityCondition
import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.entity.condition.*;

facilityId = parameters.facilityId
facilityId = userLogin.ownerCompanyId;
if (facilityId) {
    facility = from("Facility").where("facilityId", facilityId).queryOne()
    context.facilityId = facilityId
    context.facility = facility
}

// order based packing
orderId = parameters.orderId
shipGroupSeqId = parameters.shipGroupSeqId
shipmentId = parameters.shipmentId
if (!shipmentId) {
    shipmentId = request.getAttribute("shipmentId")
}
context.shipmentId = shipmentId

// If a shipment exists, provide the IDs of any related invoices
invoiceIds = null
if (shipmentId) {
    // Get the primaryOrderId from the shipment
    shipment = from("Shipment").where("shipmentId", shipmentId).queryOne()
    if (shipment && shipment.primaryOrderId) {
        orderItemBillingList = from("OrderItemBilling").where("orderId", shipment.primaryOrderId).orderBy("invoiceId").queryList()
        invoiceIds = EntityUtil.getFieldListFromEntityList(orderItemBillingList, "invoiceId", true)
        if (invoiceIds) {
            context.invoiceIds = invoiceIds
        }
    }
}

// validate order information
if (orderId && !shipGroupSeqId && orderId.indexOf("/") > -1) {
    // split the orderID/shipGroupSeqID
    idSplit = orderId.split("\\/")
    orderId = idSplit[0]
    shipGroupSeqId = idSplit[1]
} else if (orderId && !shipGroupSeqId) {
    shipGroupSeqId = "00001"
}

// setup the packing session
packSession = session.getAttribute("packingSession")
clear = parameters.clear
if (!packSession) {
    packSession = new org.apache.ofbiz.shipment.packing.PackingSession(dispatcher, userLogin)
    session.setAttribute("packingSession", packSession)
    Debug.log("Created NEW packing session!!")
} else {
    if (packSession.getStatus() == 0) {
        OrderReadHelper orh = new OrderReadHelper(delegator, orderId)
        shipGrp = orh.getOrderItemShipGroup(shipGroupSeqId)
        context.shippedShipGroupSeqId = shipGroupSeqId
        context.shippedOrderId = orderId
        context.shippedCarrier = shipGrp.carrierPartyId

        packSession.clear()
        shipGroupSeqId = null
        orderId = null
    } else if (clear) {
        packSession.clear()
    }
}
packSession.clearItemInfos()

// picklist based packing information
picklistBinId = parameters.picklistBinId
// see if the bin ID is already set
if (!picklistBinId) {
    picklistBinId = packSession.getPicklistBinId()
}
if (picklistBinId) {
    bin = from("PicklistBin").where("picklistBinId", picklistBinId).queryOne()
    if (bin) {
        orderId = bin.primaryOrderId
        shipGroupSeqId = bin.primaryShipGroupSeqId
        packSession.addItemInfo(bin.getRelated("PicklistItem", [itemStatusId : 'PICKITEM_PENDING'], null, false))
    }
} else {
    picklistBinId = null
}

// make sure we always re-set the infos
packSession.setPrimaryShipGroupSeqId(shipGroupSeqId)
packSession.setPrimaryOrderId(orderId)
packSession.setPicklistBinId(picklistBinId)
packSession.setFacilityId(facilityId)

if (invoiceIds) {
    orderId = null
}
shipment = from("Shipment").where("primaryOrderId", orderId, "statusId", "SHIPMENT_PICKED").queryFirst()
context.shipment = shipment

context.packingSession = packSession
context.orderId = orderId
context.shipGroupSeqId = shipGroupSeqId
context.picklistBinId = picklistBinId

// grab the order information
if (orderId) {
    orderHeader = from("OrderHeader").where("orderId", orderId).queryOne()
    orderHeaderAndJobCard = from("OrderHeaderAndJobCard").where("orderId", orderId,"ownerId",userLogin.ownerId).queryFirst();
    
    if (orderHeader) {
    	Map sparePartMap = new HashMap();
    	Map serviceMap = new HashMap();
    	Map orderItemMap = new HashMap();
    	
    	
        OrderReadHelper orh = new OrderReadHelper(orderHeader)
        context.orderId = orderId
        context.orderHeader = orderHeader
        personGV = from("Person").where("partyId", orderHeaderAndJobCard.customerId,"ownerId",userLogin.ownerId).queryFirst();
        partyAndPostalAddressGV = from("PartyAndPostalAddress").where("partyId", orderHeaderAndJobCard.customerId,"contactMechTypeId","POSTAL_ADDRESS").queryFirst();
        partyAndEmailAddressGV = from("PartyAndContactMech").where("partyId", orderHeaderAndJobCard.customerId,"contactMechTypeId","EMAIL_ADDRESS").queryFirst();
        
        vehicleGV = from("Vehicle").where("vehicleId", orderHeaderAndJobCard.vehicleId).queryOne()
        context.orderHeaderAndJobCard = orderHeaderAndJobCard;
        context.partyAndPostalAddressGV = partyAndPostalAddressGV;
        context.partyAndEmailAddressGV = partyAndEmailAddressGV;
        productMap = new HashMap();
        productTypeMap = new HashMap();
        context.personGV = personGV;
        context.vehicleGV = vehicleGV;
        context.orderReadHelper = orh
        orderItemShipGroup = orh.getOrderItemShipGroup(shipGroupSeqId)
        		println("==136===orderItemShipGroup============"+orderItemShipGroup+"===========");
        context.orderItemShipGroup = orderItemShipGroup
        carrierPartyId = orderItemShipGroup.carrierPartyId
            carrierShipmentBoxTypes = from("CarrierShipmentBoxType").where("partyId", carrierPartyId).queryList()
            if (carrierShipmentBoxTypes) {
            context.carrierShipmentBoxTypes = carrierShipmentBoxTypes
            }
        println("==138===orderHeader.statusId============"+orderHeader.statusId+"===========");
        if ("ORDER_APPROVED".equals(orderHeader.statusId)) {
        	orderItemList = from("OrderItem").where("orderId", orderId).queryList()
        			println("==143===orderHeader.statusId============"+orderItemList.size()+"===========");
        	productIdList = EntityUtil.getFieldListFromEntityList(orderItemList, "productId", true)
                    //partyCond = EntityCondition.makeCondition([EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, finAccountPartyIds),
                    //	                                               EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, finAccountPartyIds)], EntityOperator.OR)
                    productList = from("Product").where(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList)).queryList()
                    		println("==148===productList============"+productList.size()+"===========");
        	productList = from("Product").where(EntityCondition.makeCondition("productId", EntityOperator.IN, productIdList)).queryList()
            		println("==148===productList============"+productList.size()+"===========");
	        for(GenericValue productGenericValue : productList){
	        	productMap.put(productGenericValue.productId, productGenericValue);
	        	productTypeMap.put(productGenericValue.productId, productGenericValue.productType);
            }
	        
            for(GenericValue orderItemGV : orderItemList){
            	orderItemMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
            	if("SERVICE_PRODUCT".equals(productTypeMap.get(orderItemGV.productId)) || "SERVICE_ITEM".equals(productTypeMap.get(orderItemGV.productId))){
                	serviceMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
                }
                if("SPARE_PART".equals(productTypeMap.get(orderItemGV.productId)) || "LUBRICANTS".equals(productTypeMap.get(orderItemGV.productId))){
                	sparePartMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
                }
            }
        	/*
                    for(GenericValue orderItemGV : orderItemList){
                    	orderItemMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
                    }
                    for(GenericValue productGV : productList) {
	                    if("SERVICE".equals(productGV.productTypeId)){
	                    	serviceMap.put(productGV.productId, productGV);
	                    }
                        if("SPARE_PART".equals(productGV.productTypeId)){
                        	sparePartMap.put(productGV.productId, productGV);
                        }
                    }
                    */

                	context.productTypeMap = productTypeMap
                	context.productMap = productMap
                	context.serviceMap = serviceMap
                	context.sparePartMap = sparePartMap
                	context.orderItemMap = orderItemMap
        			
            if (shipGroupSeqId) {
            	println("=====shipment============"+shipment+"===========");
                if (!shipment) {
    
                    // Generate the shipment cost estimate for the ship group
                    productStoreId = orh.getProductStoreId()
                    shippableItemInfo = orh.getOrderItemAndShipGroupAssoc(shipGroupSeqId)
                    shippableItems = from("OrderItemAndShipGrpInvResAndItemSum").where("orderId", orderId, "shipGroupSeqId", shipGroupSeqId).queryList()
                    
                    
                    
                    
                    shippableTotal = new Double(orh.getShippableTotal(shipGroupSeqId).doubleValue())
                    shippableWeight = new Double(orh.getShippableWeight(shipGroupSeqId).doubleValue())
                    shippableQuantity = new Double(orh.getShippableQuantity(shipGroupSeqId).doubleValue())
                    if (orderItemShipGroup.contactMechId && orderItemShipGroup.shipmentMethodTypeId && orderItemShipGroup.carrierPartyId && orderItemShipGroup.carrierRoleTypeId) {
                        shipmentCostEstimate = packSession.getShipmentCostEstimate(orderItemShipGroup, productStoreId, shippableItemInfo, shippableTotal, shippableWeight, shippableQuantity)
                        context.shipmentCostEstimateForShipGroup = shipmentCostEstimate
                    }
                    context.productStoreId = productStoreId
    
                    if (!picklistBinId) {
                        packSession.addItemInfo(shippableItems)
                    }
                } else {
                    request.setAttribute("_ERROR_MESSAGE_", UtilProperties.getMessage("OrderErrorUiLabels", "OrderErrorOrderHasBeenAlreadyVerified", [orderId : orderId], locale))
                }
            } else {
                request.setAttribute("errorMessageList", ['No ship group sequence ID. Cannot process.'])
            }
        } else {
            request.setAttribute("errorMessageList", ["Order #" + orderId + " is not approved for packing."])
        }
    } else {
        request.setAttribute("errorMessageList", ["Order #" + orderId + " cannot be found."])
    }
}

// Try to get the defaultWeightUomId first from the facility, then from the shipment properties, and finally defaulting to kilos
defaultWeightUomId = null
if (facility) {
    defaultWeightUomId = facility.defaultWeightUomId
}
if (!defaultWeightUomId) {
    defaultWeightUomId = EntityUtilProperties.getPropertyValue("shipment", "shipment.default.weight.uom", "WT_kg", delegator)
}
context.defaultWeightUomId = defaultWeightUomId
