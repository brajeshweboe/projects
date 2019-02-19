/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.*;
import org.ofbiz.entity.*;
import org.ofbiz.entity.util.EntityUtil;
import org.ofbiz.base.util.Debug;
import java.math.RoundingMode;
import org.ofbiz.base.util.*;
import org.ofbiz.base.util.collections.*;
import org.ofbiz.accounting.invoice.*;
import org.ofbiz.accounting.invoice.InvoiceWorker;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.math.BigDecimal;
import java.math.MathContext;
import org.ofbiz.base.util.UtilNumber;
import javolution.util.FastList;
import javolution.util.FastMap;
import org.ofbiz.order.order.OrderReadHelper;
import org.ofbiz.entity.condition.EntityCondition;



dateFormat = "dd-MMM-yyyy";
invoiceId = parameters.get("invoiceId");

invoice = delegator.findByPrimaryKey("Invoice", [invoiceId : invoiceId]);
context.invoice = invoice;
invoiceItemsPromotionMap = FastMap.newInstance();
currency = parameters.currency;        // allow the display of the invoice in the original currency, the default is to display the invoice in the default currency
BigDecimal conversionRate = new BigDecimal("1");
ZERO = BigDecimal.ZERO;
decimals = UtilNumber.getBigDecimalScale("invoice.decimals");
rounding = UtilNumber.getBigDecimalRoundingMode("invoice.rounding");
orderId = "";

if (invoice) {
    // each invoice of course has two billing addresses, but the one that is relevant for purchase invoices is the PAYMENT_LOCATION of the invoice
    // (ie Accounts Payable address for the supplier), while the right one for sales invoices is the BILLING_LOCATION (ie Accounts Receivable or
    // home of the customer.)
    if ("PURCHASE_INVOICE".equals(invoice.invoiceTypeId)) {
        billingAddress = InvoiceWorker.getSendFromAddress(invoice);
    } else {
        billingAddress = InvoiceWorker.getBillToAddress(invoice);
    }
    if (billingAddress) {
        context.billingAddress = billingAddress;
    }
    billingParty = InvoiceWorker.getBillToParty(invoice);
    context.billingParty = billingParty;
    sendingParty = InvoiceWorker.getSendFromParty(invoice);
    context.sendingParty = sendingParty;
    context.companyId=invoice.partyIdFrom;
    if (currency && !invoice.getString("currencyUomId").equals(currency)) {
        conversionRate = InvoiceWorker.getInvoiceCurrencyConversionRate(invoice);
        invoice.currencyUomId = currency;
        invoice.invoiceMessage = " converted from original with a rate of: " + conversionRate.setScale(8, rounding);
    }

    invoiceItems = invoice.getRelatedOrderBy("InvoiceItem", ["invoiceItemSeqId"]);
    invoiceItemsConv = FastList.newInstance();
    
    invoiceItemsPromotionMapForOrder = FastList.newInstance();
    vatTaxesByType = FastMap.newInstance();
    vatTaxesDescriptionMap = FastMap.newInstance();
    subtotalMap = FastMap.newInstance();
    subtotalMapWithItemSeqId = FastMap.newInstance();
    BigDecimal  totalPromoValue = new BigDecimal("0.0");
	
    totalItem=0;
    int i_=0;
    if(UtilValidate.isNotEmpty(invoiceItems)) {
    	
    	
    	
    	
    // This loop prepare invoiceItemsPromotionMap(itemSeqId,invoiceItem.amount) if Invoice Item has invoiceItem.parentInvoiceItemSeqId.
    // otherwise calculate total promp value.[totalPromoValue=totalPromoValue+invoiceItem.amount;]
    invoiceItems.each 
	{ invoiceItem ->
	    invoiceItemTypeMap = delegator.findByAnd("InvoiceItemTypeMap",UtilMisc.toMap("invoiceItemTypeId",invoiceItem.get("invoiceItemTypeId")));
	    BigDecimal value= new BigDecimal("0");
	if(UtilValidate.isNotEmpty(invoiceItemTypeMap)) {
	    println("====invoiceItemTypeMap============="+invoiceItemTypeMap+"=====================");
	    
	    if("PROMOTION_ADJUSTMENT".equals(invoiceItemTypeMap.getFirst().getString("invoiceItemMapKey")) )
	    {
	    	if(UtilValidate.isNotEmpty(invoiceItem.parentInvoiceItemSeqId) )
	    	{
	    		value=invoiceItem.amount;
	    		
	    		if(invoiceItemsPromotionMap.containsKey(invoiceItem.parentInvoiceItemSeqId))
	    		{
	    			value = value+invoiceItemsPromotionMap.get(invoiceItem.parentInvoiceItemSeqId);
	    		}
				Debug.log("======value============================"+value+"==================");
				
	    		//BP:07/06/2015: changes for invoice and order detail correction
	    		invoiceItemValue = delegator.findByPrimaryKey("InvoiceItem", [invoiceId : invoiceItem.invoiceId, invoiceItemSeqId:invoiceItem.parentInvoiceItemSeqId]);
				Debug.log("===1111=invoiceItemValue====11111========="+invoiceItemValue+"=====================");
	    		if(UtilValidate.isNotEmpty(invoiceItemValue) && invoiceItemValue.quantity != 0)
				Debug.log("===1111=invoiceItemValue.quantity===2222=========="+invoiceItemValue.quantity+"=====================");
				Debug.log("===1111=ivalue====3333========="+value+"==3333===================");
				Debug.log("===1111=invoiceItemValue.quantity====4444========="+invoiceItemValue.quantity+"=====================");
				Debug.log("===1111=value.divide(invoiceItemValue.quantity, 2, rounding)==5555====21212========"+value.divide(invoiceItemValue.quantity,2, RoundingMode.HALF_UP)+"==================");
	    			invoiceItemsPromotionMap.put(invoiceItem.parentInvoiceItemSeqId, value.divide(invoiceItemValue.quantity,2, RoundingMode.HALF_UP));
	    		totalPromoValue=totalPromoValue+value.divide(invoiceItemValue.quantity,2, RoundingMode.HALF_UP);
	    		
	    	}
	    	else
	    	{
	    		value=value.add(InvoiceWorker.getInvoiceItemTotal(invoiceItem));	
	    		totalPromoValue=totalPromoValue+value;
	    	}
	    	
	    }
	
	    invoiceItemsConv.add(invoiceItem);
	    
	    // get party tax id for VAT taxes: they are required in invoices by EU
	    // also create a map with tax grand total amount by VAT tax: it is also required in invoices by UE
	}
    }
	}
    orderItemBillings = delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId], ['orderId']);
	
    orders = new LinkedHashSet();
	releaseNumberForRef = null;
    orderItemBillings.each { orderIb ->
        orders.add(orderIb.orderId);
 }
    
	storeId = "";
    BigDecimal  totalTaxValue = new BigDecimal("0.0");
    BigDecimal  totalsubtotalValue = new BigDecimal("0.0");
    BigDecimal amtShippingCharge= new BigDecimal("0.0");
	BigDecimal amtSalesCharge= new BigDecimal("0.0");
    
    BigDecimal totalOrderDisc = BigDecimal.ZERO;
	BigDecimal totalSPValue = BigDecimal.ZERO;
	invoiceItemsConv.each 
	{ invoiceItem ->
	    if("INV_FPROD_ITEM".equals(invoiceItem.invoiceItemTypeId))
	    {
	    	totalSPValue = totalSPValue.add(invoiceItem.amount.multiply(invoiceItem.quantity));
	    }
	    if("ITM_PROMOTION_ADJ".equals(invoiceItem.invoiceItemTypeId) && UtilValidate.isEmpty(invoiceItem.parentInvoiceItemSeqId))
		{
	    	totalOrderDisc = totalOrderDisc.add(invoiceItem.amount);
		}
	}
    
    BigDecimal sgstBD = BigDecimal.ZERO;
    BigDecimal cgstBD = BigDecimal.ZERO;
    // This loop prepare list for Invoice.pdf where shows item, price, offer price, tax, discount, net total
    invoiceItemsConv.each 
    { invoiceItem ->
	    if(UtilValidate.isEmpty(releaseNumberForRef)){
	        orderItemBillingGV = EntityUtil.getFirst(orderItemBillings);
		    orderItemShipGroupAssocGV = EntityUtil.getFirst(delegator.findByAnd("OrderItemShipGroupAssoc",UtilMisc.toMap("orderId" , orderItemBillingGV.orderId, "orderItemSeqId" ,orderItemBillingGV.orderItemSeqId)));
		
			releaseNumberForRef = orderItemShipGroupAssocGV.shipGroupSeqId.substring(2,5);
		}
		
        // if Invoice item is a orderItem 
    	if("INV_FPROD_ITEM".equals(invoiceItem.invoiceItemTypeId))
    	{
    		
    		if(!invoiceItem.parentInvoiceItemSeqId )
	        {
	            orderId = EntityUtil.getFirst(orderItemBillings).getString("orderId");
	            storeId=delegator.findOne("OrderHeader",[orderId : orderId],false).getString("originFacilityId");
	            taxInfo=null;//delegator.findOne("ProductFacility",[productId : invoiceItem.productId,facilityId : storeId],false).getRelatedOne("TaxCodeInfo");
	            totalItem = totalItem + invoiceItem.quantity;
	            // if invoice itam has tax info
	            if(UtilValidate.isNotEmpty(taxInfo) && UtilValidate.isNotEmpty(taxInfo.taxPercentage) && taxInfo.taxPercentage.compareTo(BigDecimal.ZERO)>0 )
	            {
	            	
	    			
	            	 BigDecimal step_1=new BigDecimal("100");
	            	 BigDecimal totalAdjForItem = BigDecimal.ZERO;
	                 String invoiceItemSeqId =  invoiceItem.invoiceItemSeqId;
	                 orderItemBillingRow = EntityUtil.getFirst(delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId, invoiceItemSeqId:invoiceItem.invoiceItemSeqId]));
		        		orderItemRow = EntityUtil.getFirst(delegator.findByAnd("OrderItem", [orderId : orderItemBillingRow.orderId, orderItemSeqId:orderItemBillingRow.orderItemSeqId]));
					if(!"Y".equals(orderItemRow.isPromo))
		        		{
	                       List<GenericValue> invoiceItemChildList = delegator.findByAnd("InvoiceItem",UtilMisc.toMap("invoiceId",invoiceItem.invoiceId,"parentInvoiceItemSeqId",invoiceItem.invoiceItemSeqId))
	                       invoiceItemChildList.each {invoiceItemChild ->
	                          totalAdjForItem = totalAdjForItem.add(invoiceItemChild.amount);
	          	    		  //BP:07/06/2015: changes for invoice and order detail correction
	                          invoiceItemItemChild = delegator.findByPrimaryKey("InvoiceItem", [invoiceId : invoiceItemChild.invoiceId, invoiceItemSeqId:invoiceItemChild.parentInvoiceItemSeqId]);
		                       totalAdjForItem = totalAdjForItem.divide(invoiceItemItemChild.quantity,2, RoundingMode.HALF_UP);
		        		   }
	                      
	                       
	                 }
	                 //Apply prorata amount on order level discount 
		        		//Also remove free gift item.
		          //   BigDecimal orderLevelProRataItemDisc = (invoiceItem.amount.multiply(invoiceItem.quantity)).multiply(totalOrderDisc.divide(totalSPValue, 2, rounding));
		             //TODO:add order level 
	                 BigDecimal invoiceItemMRP =invoiceItem.amount;
	                 BigDecimal offerPrice =invoiceItemMRP.add(totalAdjForItem);
	                 BigDecimal step_2=offerPrice.multiply(step_1);
	                 
	                 BigDecimal step_3=new BigDecimal(taxInfo.taxPercentage);
	                 BigDecimal step_4=step_1.add(step_3);
	                 BigDecimal step_5=step_2.divide(step_4, 2, rounding);
	                 BigDecimal taxAmt=offerPrice.subtract(step_5).multiply(invoiceItem.quantity); 
		             totalTaxValue=totalTaxValue+taxAmt;
		             vatTaxesByType.put(invoiceItem.invoiceItemSeqId,taxAmt);
		             vatTaxesDescriptionMap.put(invoiceItem.invoiceItemSeqId,taxInfo.taxPercentage.setScale(decimals, rounding)+"%");
		             
		             BigDecimal subtotalAmt=InvoiceWorker.getInvoiceItemTotal(invoiceItem,offerPrice).subtract(taxAmt);  
		             totalsubtotalValue=totalsubtotalValue+subtotalAmt; 
		             subtotalMap.put(invoiceItem.productId,subtotalAmt);
	            }
	            else
	            {
	              //Apply prorata amount on order level discount 
		          //BigDecimal orderLevelProRataItemDisc = (invoiceItem.amount.multiply(invoiceItem.quantity)).multiply(totalOrderDisc.divide(totalSPValue, 2, rounding));
		         
                  vatTaxesByType.put(invoiceItem.invoiceItemSeqId,0.0);
                  vatTaxesDescriptionMap.put(invoiceItem.invoiceItemSeqId,"--");
	            //BigDecimal subtotalAmt=(invoiceItem.amount).multiply(invoiceItem.quantity);
	              BigDecimal subtotalAmt=BigDecimal.ZERO;//(invoiceItem.amount).multiply(invoiceItem.quantity);
	              if(UtilValidate.isNotEmpty(invoiceItemsPromotionMap.get(invoiceItem.invoiceItemSeqId)) && UtilValidate.isNotEmpty(invoiceItemsPromotionMap)) {
	                  subtotalAmt=(invoiceItem.amount.add(invoiceItemsPromotionMap.get(invoiceItem.invoiceItemSeqId))).multiply(invoiceItem.quantity);
	              }else {
	                  subtotalAmt=(invoiceItem.amount).multiply(invoiceItem.quantity);
	              }
	              //orderLevelProRataItemDisc.substract();
	              totalsubtotalValue=totalsubtotalValue+subtotalAmt;
	              subtotalMap.put(invoiceItem.productId,subtotalAmt);
	            }
	        }
    	}
    	else
    	{
    		if("ITM_SHIPPING_CHARGES".equals(invoiceItem.invoiceItemTypeId))
    		{
    		// Shipping charges from orderAdjustment instead of invoiceItem table
    			shippingChrgeInInvoice = EntityUtil.getFirst(delegator.findByAnd("OrderAdjustmentBilling",UtilMisc.toMap("invoiceId",invoiceItem.invoiceId,"invoiceItemSeqId",invoiceItem.invoiceItemSeqId)));
    			shippingChrgeInOrder = delegator.findByPrimaryKey("OrderAdjustment",UtilMisc.toMap("orderAdjustmentId",shippingChrgeInInvoice.getString("orderAdjustmentId")));
    			amtShippingCharge=amtShippingCharge.add(shippingChrgeInOrder.getBigDecimal("amount"));
    			
    		}
			if("ITM_SALES_TAX".equals(invoiceItem.invoiceItemTypeId))
    		{
    		// Shipping charges from orderAdjustment instead of invoiceItem table
    			shippingChrgeInInvoice = EntityUtil.getFirst(delegator.findByAnd("OrderAdjustmentBilling",UtilMisc.toMap("invoiceId",invoiceItem.invoiceId,"invoiceItemSeqId",invoiceItem.invoiceItemSeqId)));
    			salesTaxInOrder = delegator.findByPrimaryKey("OrderAdjustment",UtilMisc.toMap("orderAdjustmentId",shippingChrgeInInvoice.getString("orderAdjustmentId")));
    			amtSalesCharge=amtSalesCharge.add(salesTaxInOrder.getBigDecimal("amount"));
    			if(UtilValidate.isNotEmpty(invoice) && "INR".equals(invoice.currencyUomId)){
    		        if(UtilValidate.isNotEmpty(invoiceItem.description) && "SGST".equals(invoiceItem.description))
    		        {	    
    			        sgstBD = sgstBD.add(invoiceItem.amount);
    			        }
	    			if(UtilValidate.isNotEmpty(invoiceItem.description) && "CGST".equals(invoiceItem.description))
    		        {	    
    			        cgstBD = cgstBD.add(invoiceItem.amount);
    			        }
				}
    			
    		}
    		
    		
    		if("ITM_PROMOTION_ADJ".equals(invoiceItem.invoiceItemTypeId) && UtilValidate.isNotEmpty(invoiceItem.parentInvoiceItemSeqId))
    		{
    			
    			orderItemBillingRow = EntityUtil.getFirst(delegator.findByAnd("OrderItemBilling", [invoiceId : invoiceId, invoiceItemSeqId:invoiceItem.parentInvoiceItemSeqId]));
        		orderItemRow = EntityUtil.getFirst(delegator.findByAnd("OrderItem", [orderId : orderItemBillingRow.orderId, orderItemSeqId:orderItemBillingRow.orderItemSeqId]));
			    if("Y".equals(orderItemRow.isPromo))
        		{
	    			if(UtilValidate.isNotEmpty(vatTaxesByType.get(invoiceItem.parentInvoiceItemSeqId)))
	    			{
	    				BigDecimal subtotalAmt=subtotalMap.get(invoiceItem.productId).add(vatTaxesByType.get(invoiceItem.parentInvoiceItemSeqId));
	    				subtotalMap.put(invoiceItem.productId,subtotalAmt);
	    				totalsubtotalValue=totalsubtotalValue.add(vatTaxesByType.get(invoiceItem.parentInvoiceItemSeqId));
	    				totalTaxValue=totalTaxValue.subtract(vatTaxesByType.get(invoiceItem.parentInvoiceItemSeqId));
	    				vatTaxesByType.put(invoiceItem.parentInvoiceItemSeqId,0.0);
	    				vatTaxesDescriptionMap.put(invoiceItem.parentInvoiceItemSeqId,"--");
	    			}
	    			if(UtilValidate.isNotEmpty(invoiceItemsPromotionMap.get(invoiceItem.parentInvoiceItemSeqId))  )
	    			{
	    				
	    				invoiceItemBySeqId = delegator.findByPrimaryKey("InvoiceItem", [invoiceId : invoiceId, invoiceItemSeqId:invoiceItem.parentInvoiceItemSeqId]);
	    				invoiceItemsPromotionMap.put(invoiceItem.parentInvoiceItemSeqId,0.0);
	    				//may use in future
	    				BigDecimal subtotalAmt=subtotalMap.get(invoiceItem.productId).subtract(invoiceItemBySeqId.amount);
	    				//totalPromoValue=totalPromoValue.subtract(invoiceItem.amount);
	    				subtotalMap.put(invoiceItem.productId,subtotalAmt);
	    				totalsubtotalValue=totalsubtotalValue.subtract(invoiceItemBySeqId.amount);
	    				subtotalMapWithItemSeqId.put(invoiceItem.parentInvoiceItemSeqId,0.0);
	    			}
        		}
    			
    		}
    	}
    }
	context.releaseNumberForRef=releaseNumberForRef;
    context.subtotalMapWithItemSeqId=subtotalMapWithItemSeqId;
    context.vatTaxesByType = vatTaxesByType;
    context.invoiceItemsPromotionMap=invoiceItemsPromotionMap;
    context.vatTaxesDescriptionMap=vatTaxesDescriptionMap;
    context.subtotalMap=subtotalMap;
    context.sgstBD = sgstBD
    context.cgstBD = cgstBD
    context.invoiceItems = invoiceItemsConv;
    
    context.totalItem=totalItem;
    context.totalsubtotalValue=totalsubtotalValue;
    context.totalTaxValue=totalTaxValue;
    context.totalPromoValue=totalPromoValue;
    invoiceTotal = InvoiceWorker.getInvoiceTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    invoiceNoTaxTotal = InvoiceWorker.getInvoiceNoTaxTotal(invoice).multiply(conversionRate).setScale(decimals, rounding);
    context.invoiceTotal = invoiceTotal;
    context.invoiceNoTaxTotal = invoiceNoTaxTotal;
    context.amtShippingCharge=amtShippingCharge;
	context.amtSalesCharge=amtSalesCharge;
	
                //*________________this snippet was added for adding Tax ID in invoice header if needed _________________

               sendingTaxInfos = sendingParty.getRelated("PartyTaxAuthInfo");
               billingTaxInfos = billingParty.getRelated("PartyTaxAuthInfo");
               sendingPartyTaxId = null;
               billingPartyTaxId = null;

               if (billingAddress) {
                   sendingTaxInfos.eachWithIndex { sendingTaxInfo, i ->
                       if (sendingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            sendingPartyTaxId = sendingTaxInfos[i-1].partyTaxId;
                       }
                   }
                   billingTaxInfos.eachWithIndex { billingTaxInfo, i ->
                       if (billingTaxInfo.taxAuthGeoId.equals(billingAddress.countryGeoId)) {
                            billingPartyTaxId = billingTaxInfos[i-1].partyTaxId;
                       }
                   }
               }
               if (sendingPartyTaxId) {
                   context.sendingPartyTaxId = sendingPartyTaxId;
               }
               if (billingPartyTaxId && !context.billingPartyTaxId) {
                   context.billingPartyTaxId = billingPartyTaxId;
               }
               //________________this snippet was added for adding Tax ID in invoice header if needed _________________*/

    terms = invoice.getRelated("InvoiceTerm");
	context.personInvoice = delegator.findOne("Person",[partyId : invoice.partyId],false);
    context.terms = terms;

    paymentAppls = delegator.findByAnd("PaymentApplication", [invoiceId : invoiceId]);
    context.payments = paymentAppls;
    context.orders  = orders;
    context.storeId = storeId;
    invoiceStatus = invoice.getRelatedOne("StatusItem");
    context.invoiceStatus = invoiceStatus;

	orderDate = null;
	orderFacility = null;
	shippingAddress = null;
	orderAttrs = null;
	payment = null;
	paymentMethodType = null;
	pgr = null;
	deliveryDate = null;
	deliverySlot = null;
	if(UtilValidate.isNotEmpty(storeId))
	{
		orderFacility = delegator.findOne("Facility",[facilityId : storeId],false);
	}
	if(UtilValidate.isNotEmpty(orderId))
	{
		orderHeader = delegator.findOne("OrderHeader",[orderId : orderId],false);
		if(UtilValidate.isNotEmpty(orderHeader))
		{ 
            orderDate = UtilDateTime.toDateString(orderHeader.orderDate, dateFormat);
	        orh = new OrderReadHelper(orderHeader);
			/*orderAttrs = orderHeader.getRelated("OrderAttribute");
			deliveryDate = EntityUtil.getFirst(EntityUtil.filterByAnd(orderAttrs,  [attrName : "DELIVERY_SLOTDATE"]));
			deliverySlot = EntityUtil.getFirst(EntityUtil.filterByAnd(orderAttrs,  [attrName : "DELIVERY_SLOT"]));*/
		    shippingAddress = orh.getShippingAddress();
		    context.billingAddress = orh.getBillingAddress();
		}
	}
	transactionId = null;
	if(UtilValidate.isNotEmpty(paymentAppls))
	{
        paymentAppl = EntityUtil.getFirst(paymentAppls);
		payment = paymentAppl.getRelatedOne("Payment");
		paymentMethodType = payment.getRelatedOne("PaymentMethodType");
		pgr = payment.getRelatedOne("PaymentGatewayResponse");
		if("EXT_PAYU".equals(paymentMethodType.paymentMethodTypeId)){
			//paymentMethodCGV = payment.getRelatedOne("PayUPaymentMethod");
			paymentMethodCGV = delegator.findOne("PayuPaymentMethod",[paymentMethodId : payment.paymentMethodId],false);
			transactionId = paymentMethodCGV.transactionId;
		} else if("EXT_PAYPAL".equals(paymentMethodType.paymentMethodTypeId)){
			//paymentMethodCGV = payment.getRelatedOne("PayPalPaymentMethod");
			paymentMethodCGV = delegator.findOne("PayPalPaymentMethod",[paymentMethodId : payment.paymentMethodId],false);
			transactionId = paymentMethodCGV.transactionId;
		} else if("EXT_CITRUS".equals(paymentMethodType.paymentMethodTypeId)){
			//paymentMethodCGV = payment.getRelatedOne("CitrusPaymentMethod");	
			paymentMethodCGV = delegator.findOne("CitrusPaymentMethod",[paymentMethodId : payment.paymentMethodId],false);
			transactionId = paymentMethodCGV.responseMessage;
		}		
	}
	
	context.transactionId=transactionId;
	context.orderDate = orderDate;
	context.orderFacility = orderFacility;
	context.shippingAddress = shippingAddress;
	context.orderAttrs = orderAttrs;
	context.payment = payment;
	context.paymentMethodType = paymentMethodType;
	context.pgr = pgr;
	context.deliveryDate = deliveryDate;
	context.deliverySlot = deliverySlot;

    edit = parameters.editInvoice;
    if ("true".equalsIgnoreCase(edit)) {
        invoiceItemTypes = delegator.findList("InvoiceItemType", null, null, null, null, false);
        context.invoiceItemTypes = invoiceItemTypes;
        context.editInvoice = true;
    }

    // format the date
    if (invoice.invoiceDate) {
        invoiceDate = UtilDateTime.toDateString(invoice.invoiceDate, dateFormat);
        context.invoiceDate = invoiceDate;
    } else {
        context.invoiceDate = "N/A";
    }
    if (invoice.paidDate) {
        paidDate = UtilDateTime.toDateString(invoice.paidDate, dateFormat);
        context.paidDate = paidDate;
    } else {
        context.paidDate = "N/A";
    }
}
