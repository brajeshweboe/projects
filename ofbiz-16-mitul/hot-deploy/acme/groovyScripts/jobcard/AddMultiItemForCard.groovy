import org.apache.ofbiz.entity.*;
import org.apache.ofbiz.order.shoppingcart.*;
import java.util.Map;
import org.apache.ofbiz.base.util.*;
import org.apache.ofbiz.product.catalog.CatalogWorker;
System.out.println("===11=Add multiItem for card.groovy=========");

System.out.println("====jobCardId======="+parameters.jobCardId+"=========");
System.out.println("====orderId======="+parameters.orderId+"===parameters.selectedorderId======");
System.out.println("====vehicleId======="+parameters.vehicleId+"=========");
parameters.vehicleId=parameters.vehicleId;
if(UtilValidate.isNotEmpty(parameters.orderId)){
	parameters.orderId=parameters.orderId;
    //if order not available then we have to set jobcard and vehicle in the card
	orderItemGVList = from("OrderItem").where("orderId",parameters.orderId).queryList()
			context.orderItemGVList = orderItemGVList;

}
parameters.jobCardId=parameters.jobCardId;
System.out.println("====add_product_id======="+parameters.add_product_id+"====userLogin.ownerCompanyId==="+userLogin.ownerCompanyId+"==");
//get all associated product for this standard product
productAssocConfList = from("ProductAssoc").where("productId", parameters.add_product_id,"productAssocTypeId", "PRODUCT_CONF", "vehicleModelId", parameters.vehicleId).queryList()
System.out.println("===11111111111111111111111111111=productAssocConfList======="+productAssocConfList+"=========");

if(UtilValidate.isNotEmpty(productAssocConfList)){
//TODO:get all product price and prepare map
    /* for(GenericValue productAssocConf : productAssocConfList){
        
    } */
    
        ShoppingCart cart = ShoppingCartEvents.getCartObject(request);
        cart.setProductStoreId(userLogin.ownerCompanyId);
        ShoppingCartHelper cartHelper = new ShoppingCartHelper(delegator, dispatcher, cart);
        for(GenericValue productAssocConf : productAssocConfList){
            Map<String, Object> paramMap = new HashMap<String, Object>();
        	//cartHelper.addToCart(null,null,null,productAssocConf.productIdTo,null,null,null,null,null,BigDecimal.ONE,null,null,null,null,null,null,null,paramMap,null);
        	resultMap = runService('appendOrderItem', [shipGroupSeqId : "00001", productId : productAssocConf.productIdTo,quantity:productAssocConf.quantity,orderId:parameters.orderId ,userLogin : userLogin])
        }
        //String controlDirective;
      // Map<String, Object> result;
        //Convert the params to a map to pass in
      //  Map<String, Object> paramMap = UtilHttp.getParameterMap(request);
       // System.out.println("======2727272========paramMap======="+paramMap+"=========");
      //String catalogId = CatalogWorker.getCurrentCatalogId(request);
      // result = cartHelper.addToCartBulk(catalogId, null, paramMap);
}
return "success";