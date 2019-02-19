<script src="https://code.jquery.com/ui/1.12.1/jquery-ui.js"></script>
<div class="title2"><span>ITEM ADDITION TO JOB CARD #${parameters.jobCardId!}</span></div>

<div class="row form-group">
	<div class="col-md-3 col-sm-4">
	<form action="<@ofbizUrl>selectItemForCard</@ofbizUrl>" name="selectItemForCardForm" id="selectItemForCardForm" method="post">
                                    	<input type="hidden" name="jobCardId" value="${parameters.jobCardId!}" />
                                    	<input type="hidden" name="orderId" value="${parameters.orderId!}" />
        <label>Vehicle Model</label>
		<select class="form-control" name="vehicleId" id="selectedVehicleId">
			<option>Select Service Name</option>
			<#list vehicleList as vehicle>
				<option value="${vehicle.modelId!}" <#if (vehicle.modelId == parameters.vehicleId!)>selected=selected</#if>>${vehicle.searchName}</option>
			</#list>
		</select>
                                    </form>
		
	</div>
</div>

<div class="row paddingbott20">
<div class="col-md-9 col-sm-8">
<h3>ADD SERVICE</h3>
<ul class="topmenu">
<#if fastServiceProductList?exists && fastServiceProductList?has_content>
    <#list fastServiceProductList as fastService>
	    <li><a href="javascript:document.selectItemForCardForm${fastService.productId!}.submit();">${fastService.productName!fastService.productId!}</a>
	    	<form action="<@ofbizUrl>addMultiItemForCard</@ofbizUrl>" name="selectItemForCardForm${fastService.productId!}" id="selectItemForCardForm${fastService.productId!}" method="post">
            	<input type="hidden" name="jobCardId" value="${parameters.jobCardId!parameters.selectedJobCardId!}" />
            	<input type="hidden" name="orderId" value="${parameters.selectedOrderId!parameters.orderId!}" />
            	<input type="hidden" name="vehicleId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
            	<input type="hidden" name="productId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
            	<input type="hidden" name="add_product_id" size="20" maxlength="20" id="add_product_id" value="${fastService.productId!}">
        	</form>
	    </li>
	</#list>
</#if>
<#-- 
<li><a href="#">Standard Service</a></li>
<li><a href="#">Extensive Service</a></li>
<li><a href="#">Dent-Paint Work</a></li>
<li><a href="#">Car Wash</a></li>
<li><a href="#">Dry Clean</a></li> -->
</ul>
</div>

<div class="col-md-3 col-sm-4">
<h3>OTHER SERVICE</h3>
<select class="form-control" name="selectedOtherService" id="selectedOtherService">
	<option>Select Service Name</option>
	<#list otherServiceList as otherService>
		<option value="${otherService.productId!}" <#if (otherService.productId == parameters.selectedOtherService!)>selected=selected</#if>>${otherService.productId!}</option>
	</#list>
</select>
</div>
</div>

<h3 class="paddingbott20">ADD SPARE PARTS</h3>
<form action="<@ofbizUrl>selectItemForCard</@ofbizUrl>" name="selectItemForCardForm" id="selectItemForCardForm" method="post">
<input type="hidden" name="jobCardId" value="${parameters.selectedJobCardId!parameters.jobCardId!}" />
                                    	<input type="hidden" name="orderId" value="${parameters.selectedOrderId!parameters.orderId!}" />
                                    	<input type="hidden" name="vehicleId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
<div class="row form-group">
<div class="col-md-3 col-sm-4">
<label>Part No.</label>
<input type="text" name="productId" id="autocompleteProductId" class="form-control" placeholder="Enter Part No." value="${parameters.productId!}">
</div>
<script language="JavaScript" type="text/javascript">
var articleIdList = [
   <#if productVehiclePriceList?exists && productVehiclePriceList?has_content>
   	<#list productVehiclePriceList as productVehiclePrice>
   		'${productVehiclePrice.productId!}'<#if  (productVehiclePriceList.size() > 1) && (productVehiclePrice_index != productVehiclePriceList.size()-1)>,</#if>
   	</#list>
   </#if>
];

jQuery('#autocompleteProductId').autocomplete({
    source: articleIdList,
    select: function (event, e) {
    }
});

</script>
<#-- 
<script language="JavaScript" type="text/javascript">
jQuery(document).ready(function() {
	jQuery('#autocompleteProductId').change(function(){
		
		jQuery("#selectItemForCardForm").submit();
	});
});
</script>
-->
<div class="col-md-3 col-sm-4">
<label>Part Name</label>
<input type="text" name="productName"  id="autocompleteProductName" class="form-control" placeholder="Enter Part Name" value="${parameters.productName!}">
<script language="JavaScript" type="text/javascript">
var articleNameList = [
   <#if productVehiclePriceList?exists && productVehiclePriceList?has_content>
   	<#list productVehiclePriceList as productVehiclePrice>
   		'${productVehiclePrice.description!}'<#if  (productVehiclePriceList.size() > 1) && (productVehiclePrice_index != productVehiclePriceList.size()-1)>,</#if>
   	</#list>
   </#if>
];


jQuery('#autocompleteProductName').autocomplete({
    source: articleNameList,
    select: function (event, e) {
        alert('You selected'+e.item.value);
    }
});

</script>
</div>

<div class="col-md-3 col-sm-4">
<label>Manufacturer</label>
<input type="text" name="" class="form-control" placeholder="Enter Manufacturer">
</div>

<div class="col-md-3 col-sm-4">
<label>Category</label>
<select class="form-control"><option>Enter Category</option></select>
</div>
 
</div>
<input type="submit" class="btn btn-danger" value="SEARCH">
</form>



<div class="table-responsive margintop20">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabledetails" id="sortableData">
<thead>
  <tr>
    <th>PART NO.</th>
    <th>DESCRIPTION</th>
    <th>QTY AVAILABLE</th>
    <th>BRAND</th>
    <th>MRP</th>
    <th>ACTIONS</th>
  </tr>
  </thead>
  <tbody>
  <#if selectedProductList?exists && selectedProductList?has_content>
  <#list selectedProductList as selectedProduct>
  <tr>
    <td>${selectedProduct.productId!}</td>
    <td>${selectedProduct.description!}</td>
    <td><#if productFacilityInventoryItemMap?exists && productFacilityInventoryItemMap?has_content>${(productFacilityInventoryItemMap[selectedProduct.productId].quantityOnHandTotal)!"0"}</#if></td>
    <td>BRAND</td>
    <td><@ofbizCurrency amount=selectedProduct.price!"0" isoCode=INR/></td>
    <td>
    <form method="post" action="<@ofbizUrl>additemForJobCard</@ofbizUrl>" name="addForm${selectedProduct.productId?replace("-", "_")}_${selectedProduct_index}" id="addForm${selectedProduct.productId!}">
        <input type="hidden" class="inputBox" name="add_product_id" value="${selectedProduct.productId!requestParameters.add_product_id!}" />
        <input type="hidden" name="jobCardId" value="${parameters.selectedJobCardId!parameters.jobCardId!}" />
                                    	<input type="hidden" name="orderId" value="${parameters.selectedOrderId!parameters.orderId!}" />
                                    	<input type="hidden" name="vehicleId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
         <input type="hidden" class="inputBox" size="5" name="quantity" value="${requestParameters.quantity?default("1")}" />
    </form>
    <#--
     <form method="post" action="#" name="deleteForm${selectedProduct.productId!}_${selectedProduct_index}" id="deleteForm${selectedProduct.productId!}">
        <input type="hidden" class="inputBox" name="add_product_id" value="${selectedProduct.productId!requestParameters.add_product_id!}" />
        <input type="hidden" name="jobCardId" value="${parameters.selectedJobCardId!parameters.jobCardId!}" />
                                    	<input type="hidden" name="orderId" value="${parameters.selectedOrderId!parameters.orderId!}" />
                                    	<input type="hidden" name="vehicleId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
         <input type="hidden" class="inputBox" size="5" name="quantity" value="0" />
    </form>
    -->
    <a href="javascript:document.addForm${selectedProduct.productId?replace("-", "_")}_${selectedProduct_index}.submit();">
    
    <i class="fa fa-plus"></i></a>
    <#--
     <a href="javascript:document.deleteForm${selectedProduct.productId?replace("-", "_")}_${selectedProduct_index}.submit();"><i class="fa fa-minus"></i></a>
     -->
     </td>
  </tr>
  </#list>
  </#if>
 </tbody>
</table>

<div class="title2"><span>Job Card Items Detail</span></div>

<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabledetails">
<thead>
												
  <tr>
    <th>Service Item Id</th>
    <th>Qty AVAILABLE</th>
    <th>Qty</th>
    <th>Item Description</th>
    <th>Service Cost</th>
    <th>Discount %</th>
    <th>Tax</th>
    <th>Total Amount</th>
    <th>ACTIONS</th>
  </tr>
  </thead>
  <tbody>
  <#list orderItemGVList as orderItemGV>
                                                <tr>
                                                  <td><#if orderItemGV.productId??>${orderItemGV.productId}</#if></td>
                                                  <td><#if productFacilityInventoryItemMap?exists && productFacilityInventoryItemMap?has_content>${(productFacilityInventoryItemMap[orderItemGV.productId].quantityOnHandTotal)!"0"}</#if></td>
                                                  <td><#if orderItemGV.quantity??>${orderItemGV.quantity}</#if></td>
                                                  <td>${orderItemGV.itemDescription!}</td>
                                                  <td><@ofbizCurrency amount=orderItemGV.unitPrice!"0" isoCode=INR/></td>
                                                  <td><input type="text" class="form-control input-sml" placeholder="Discount %">
                                                  <@ofbizCurrency amount=0!"0" isoCode=INR/>
                                                  </td>
                                                  <td>&#x20B9; 200</td>
                                                  <td><@ofbizCurrency amount=0!"0" isoCode=INR/></td>
                                                  <td>
                                                  <form method="post" action="<@ofbizUrl>additemForJobCard</@ofbizUrl>" name="deleteForm${orderItemGV.productId?replace("-", "_")}_${orderItemGV_index}" id="deleteForm${orderItemGV.productId!}">
        <input type="hidden" class="inputBox" name="add_product_id" value="${orderItemGV.productId!requestParameters.add_product_id!}" />
        <input type="hidden" name="jobCardId" value="${parameters.selectedJobCardId!parameters.jobCardId!}" />
                                    	<input type="hidden" name="orderId" value="${orderItemGV.orderId!}" />
                                    	<input type="hidden" name="vehicleId" value="${parameters.selectedVehicleId!parameters.vehicleId!}" />
         <input type="hidden" class="inputBox" size="5" name="quantity" value="0" />
    </form>
                                                   <a href="javascript:document.deleteForm${orderItemGV.productId?replace("-", "_")}_${orderItemGV_index}.submit();"><i class="fa fa-minus"></i></a>
                                                  </td>
                                                </tr>
                                                </#list>
                                                </tbody>
 
</table>
</div>


</div>
</div>

<div class="table-responsive margintop20">

</div>

</div>
</section>




<script language="JavaScript" type="text/javascript">
jQuery(document).ready(function() {
	jQuery('#selectedVehicleId').change(function(){
		
		jQuery("#selectItemForCardForm").submit();
	});
});
</script>
<#--
<script language="JavaScript" type="text/javascript">
jQuery('#autocomplete').autocomplete({
    lookup: countries,
    onSelect: function (suggestion) {
        alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
    }
});

</script>

jQuery('#autocomplete').autocomplete({
    serviceUrl: '/autocomplete/countries',
    onSelect: function (suggestion) {
        alert('You selected: ' + suggestion.value + ', ' + suggestion.data);
    }
});

-->