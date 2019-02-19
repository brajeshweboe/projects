import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.order.shoppingcart.*;
import java.util.Map;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.product.catalog.CatalogWorker;
System.out.println("==============Add Item for card.groovy=========");
if(UtilValidate.isNotEmpty(userLogin)){

System.out.println("==1====parameters========="+parameters.jobCardId+"===============");
System.out.println("==2====parameters========="+parameters.orderId+"===============");
System.out.println("==3====parameters========="+parameters.vehicleId+"===============");
jobCardId = parameters.jobCardId
orderId = parameters.orderId
vehicleId = parameters.vehicleId

if(UtilValidate.isNotEmpty(parameters.jobCardId) && UtilValidate.isEmpty(jobCardId)){
	jobCardId=parameters.jobCardId;
}
if(UtilValidate.isNotEmpty(parameters.orderId) && UtilValidate.isEmpty(orderId)){
	orderId=parameters.orderId;
	    //if order not available then we have to set jobcard and vehicle in the card
	orderItemGVList = from("OrderItem").where("orderId",orderId).queryList()
			context.orderItemGVList = orderItemGVList;
}
if(UtilValidate.isNotEmpty(parameters.vehicleId) && UtilValidate.isEmpty(vehicleId)){
	vehicleId=parameters.vehicleId;
}
System.out.println("==1====parameters========="+parameters.jobCardId+"===============");
System.out.println("==2====parameters========="+parameters.orderId+"===============");
System.out.println("==3====parameters========="+parameters.vehicleId+"===============");
parameters.vehicleId=vehicleId;
parameters.orderId=orderId;
parameters.jobCardId=jobCardId;
/*
<attribute name="orderId" type="String" mode="INOUT" optional="false"/>
        <attribute name="shipGroupSeqId" type="String" mode="IN" optional="false"/>
        <attribute name="productId" type="String" mode="IN" optional="false"/>
        <attribute name="prodCatalogId" type="String" mode="IN" optional="true"/>
        <attribute name="basePrice" type="BigDecimal" mode="IN" optional="true"/>
        <attribute name="quantity" type="BigDecimal" mode="IN" optional="false"/>
        <attribute name="amount" type="BigDecimal" mode="IN" optional="true"/>
        <attribute name="overridePrice" type="String" mode="IN" optional="true"/>
        <attribute name="reasonEnumId" type="String" mode="IN" optional="true"/>
        <attribute name="orderItemTypeId" type="String" mode="IN" optional="true"/>
        <attribute name="changeComments" type="String" mode="IN" optional="true"/>
        <attribute name="itemDesiredDeliveryDate" type="Timestamp" mode="IN" optional="true"/>
        <attribute name="itemAttributesMap" type="Map" mode="IN" optional="true"/>
        <attribute name="calcTax" type="Boolean" mode="IN" optional="true" default-value="true"/>
*/
System.out.println("====add_product_id======="+parameters.add_product_id+"====userLogin.ownerCompanyId==="+userLogin.ownerCompanyId+"==");
//get all associated product for this standard product

if(UtilValidate.isNotEmpty(parameters.add_product_id)){
//TODO:get all product price and prepare map
   
       // ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        //cart.setProductStoreId("GAADIZO_STORE_ID");
        //ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        Map<String, Object> resultMap = new HashMap<String, Object>();
        
        	//cartHelper.addToCart(null,null,null,parameters.add_product_id,null,null,null,null,null,BigDecimal.ONE,null,null,null,null,null,null,null,paramMap,null);
        resultMap = runService('appendOrderItem', [shipGroupSeqId : "00001", productId : parameters.add_product_id,quantity:new BigDecimal(parameters.quantity),orderId:parameters.orderId ,userLogin : userLogin])
         System.out.println("======2727272========resultMap======="+resultMap+"=========");
        //==========================================================================================
        /*productVehiclePriceList = EntityQuery.use(delegator).from("ProductVehiclePrice").where("productId", parameters.add_product_id,"vehicleId",parameters.vehicleId,"facilityId",userLogin.ownerCompanyId).queryList();
        productVehiclePriceList = EntityUtil.filterByDate(productVehiclePriceList);
        productVehiclePrice = EntityUtil.getFirst(productVehiclePriceList);
        
        Map<String, Object> createOrderAdjContext = new HashMap<String, Object>();
        createOrderAdjContext.put("orderAdjustmentTypeId", "SALES_TAX");
        createOrderAdjContext.put("orderId", parameters.orderId);
        if (UtilValidate.isNotEmpty(orderItemSeqId)) {
            createOrderAdjContext.put("orderItemSeqId", orderItemSeqId);
        } else {
            createOrderAdjContext.put("orderItemSeqId", "_NA_");
        }
        createOrderAdjContext.put("shipGroupSeqId", "_NA_");
        createOrderAdjContext.put("description", "Tax adjustment due to order change");
        createOrderAdjContext.put("amount", orderTaxDifference);
        createOrderAdjContext.put("userLogin", userLogin);
        Map<String, Object> createOrderAdjResponse = null;
        try {
            createOrderAdjResponse = dispatcher.runSync("createOrderAdjustment", createOrderAdjContext);
        } catch (GenericServiceException e) {
            String createOrderAdjErrMsg = UtilProperties.getMessage(resource_error, 
                    "OrderErrorCallingCreateOrderAdjustmentService", locale);
            Debug.logError(createOrderAdjErrMsg, module);
            return ServiceUtil.returnError(createOrderAdjErrMsg);
        }
        if (ServiceUtil.isError(createOrderAdjResponse)) {
            Debug.logError(ServiceUtil.getErrorMessage(createOrderAdjResponse), module);
            return ServiceUtil.returnError(ServiceUtil.getErrorMessage(createOrderAdjResponse));
        }*/
      //==========================================================================================
        //invoices = resultMap.filteredInvoiceList
       // context.invoices = invoices
        //String controlDirective;
      // Map<String, Object> result;
        //Convert the params to a map to pass in
      //  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
       // System.out.println("======2727272========resultMap======="+resultMap+"=========");
      //String catalogId = CatalogWorker.getCurrentCatalogId(request);
      // result = cartHelper.addToCartBulk(catalogId, null, paramMap);
}
return "success";
}