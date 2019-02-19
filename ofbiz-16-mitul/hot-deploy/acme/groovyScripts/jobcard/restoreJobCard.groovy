import com.gaadizo.services.ProcessCardServices.JOBCARDShoppingCartStore;
import org.apache.ofbiz.order.shoppingcart.ShoppingCart;

println("=============start of Restore job card ================================");

try {
shoppingCart = session.getAttribute("shoppingCart")
println("==888888888888888888888============"+shoppingCart.getJobCardId()+"==========");
if(UtilValidate.isEmpty(shoppingCart)){
    shoppingCart = ShoppingCartEvents.getCartObject(request);
    println("==43=====productStore============"+shoppingCart.getCurrency()+"==========");
}
    JOBCARDShoppingCartStore jcardStore = new JOBCARDShoppingCartStore(delegator);
	ShoppingCart persistedCart = jcardStore.save(token,shoppingCart, userLogin);

	JOBCARDShoppingCartStore jcardRStore = new JOBCARDShoppingCartStore(delegator);
	ShoppingCart persistedCart = jcardRStore.load(token, userLogin);
    if (UtilValidate.isNotEmpty(persistedCart)) {
    	cart = persistedCart;
        session.setAttribute("shoppingCart", cart);
        ebsCartStore.remove(token, userLogin);
    }
} catch (Exception e) {
    Debug.logError(e, module);
}