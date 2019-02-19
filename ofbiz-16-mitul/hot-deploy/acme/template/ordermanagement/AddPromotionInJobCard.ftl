<#-- Row -->
<div class="row">
	<div class="col-md-12">
		<div class="panel panel-default card-view">
			<div class="panel-heading">
				<div class="pull-left">
				    <form name="addDiscountInJobCard" id="addDiscountInJobCard" method="post" action="<@ofbizUrl>addPromotionForPerFormInvoiceOfJobCard</@ofbizUrl>">
                        <input type="hidden" name="orderId" value="${orderHeaderAndjobCardGV.orderId!}"/>
                        <input type="hidden" name="jobCardId" value="${orderHeaderAndjobCardGV.jobCardId!}"/>
                        <input type="hidden" name="productPromoId" value="${parameters.productPromoId!}"/>
					    Add promotion on the card
					    <input type="text" name="discountAmount" id="discountAmount" class="form-control" placeholder="Enter discount amount" value="${parameters.discountAmount!}">
					    <a href="javascript:document.addDiscountInJobCard.submit();" class="btn btn-primary btn-icon-anim">Apply Discount</a>
					</form>
				</div>
			</div>
		</div>
	</div>
</div>