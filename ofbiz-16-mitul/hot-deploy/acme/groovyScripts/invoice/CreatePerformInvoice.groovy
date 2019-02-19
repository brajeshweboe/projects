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
shipGroupSeqId = "00001";
// validate order information
if (orderId && !shipGroupSeqId && orderId.indexOf("/") > -1) {
    // split the orderID/shipGroupSeqID
    idSplit = orderId.split("\\/")
    orderId = idSplit[0]
    shipGroupSeqId = idSplit[1]
} else if (orderId && !shipGroupSeqId) {
    shipGroupSeqId = "00001"
}

context.orderId = orderId
context.shipGroupSeqId = shipGroupSeqId
//context.picklistBinId = picklistBinId

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
        context.personGV = personGV;
        context.vehicleGV = vehicleGV;
        context.orderReadHelper = orh
        productMap = new HashMap();
        productTypeMap = new HashMap();
        println("==138===orderHeader.statusId============"+orderHeader.statusId+"===========");
        if ("ORDER_APPROVED".equals(orderHeader.statusId)) {
        	orderItemList = from("OrderItem").where("orderId", orderId).queryList()
			println("==143===orderHeader.statusId============"+orderItemList.size()+"===========");
			productIdList = EntityUtil.getFieldListFromEntityList(orderItemList, "productId", true)
            //partyCond = EntityCondition.makeCondition([EntityCondition.makeCondition("partyIdTo", EntityOperator.IN, finAccountPartyIds),
            //	                                               EntityCondition.makeCondition("partyIdFrom", EntityOperator.IN, finAccountPartyIds)], EntityOperator.OR)
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
                /*if("SERVICE_PRODUCT".equals(productTypeMap.get(orderItemGV.productId)) || "SERVICE_ITEM".equals(productTypeMap.get(orderItemGV.productId))){
                	serviceMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
                }
                if("SPARE_PART".equals(productTypeMap.get(orderItemGV.productId)) || "LUBRICANTS".equals(productTypeMap.get(orderItemGV.productId))){
                	sparePartMap.put(orderItemGV.productId+"_"+orderItemGV.orderItemSeqId, orderItemGV);
                }*/
            }
            
            /*println("=717171717171==========="+orderItemMap+"===========");
            for(GenericValue productGV : productList) {
            	println("=717171717171==========="+productGV.productTypeId+"===========");
            	
                if("SERVICE".equals(productGV.productTypeId)){
                	serviceMap.put(productGV.productId, productGV);
            		println("==11111111    73============"+productGV.productId+"===========");
                }
                if("SPARE_PART".equals(productGV.productTypeId) || "CONSUMABLES".equals(productGV.productTypeId)){
                	sparePartMap.put(productGV.productId, productGV);
            		println("==222222222222222 77 ============"+productGV.productId+"===========");
                }
            }*/
        	context.productTypeMap = productTypeMap
        	context.productMap = productMap
        	context.serviceMap = serviceMap
        	context.sparePartMap = sparePartMap
        	context.orderItemMap = orderItemMap
        			
            /*if (shipGroupSeqId) {
            	//println("=====shipment============"+shipment+"===========");
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
            }*/
        } else {
            request.setAttribute("errorMessageList", ["Order #" + orderId + " is not approved for packing."])
        }
    } else {
        request.setAttribute("errorMessageList", ["Order #" + orderId + " cannot be found."])
    }
}

// Try to get the defaultWeightUomId first from the facility, then from the shipment properties, and finally defaulting to kilos
/*defaultWeightUomId = null
if (facility) {
    defaultWeightUomId = facility.defaultWeightUomId
}
if (!defaultWeightUomId) {
    defaultWeightUomId = EntityUtilProperties.getPropertyValue("shipment", "shipment.default.weight.uom", "WT_kg", delegator)
}
context.defaultWeightUomId = defaultWeightUomId
*/