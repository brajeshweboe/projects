<div class="title2"><span>Warehouse Search</span></div>
	<form class="form-horizontal formCenter" method="post" id="findBookingForm" name="findBooking" onsubmit="javascript:submitFormDisableSubmits(this)" action="<@ofbizUrl>findBookingList</@ofbizUrl>">
  		<input type="hidden" name="noConditionFind" value="Y" id="FindInvoices_noConditionFind">
        <input type="hidden" name="hideSearch" value="Y" id="FindInvoices_hideSearch">
		<div class="row form-group">
			<div class="col-sm-3">
				<label>Warehouse Id</label>
				<input id="warehouseId" type="text" size="25" name="facilityId" class="form-control " value="${parameters.facilityId!}">
			</div>

			<div class="col-sm-3">
				<label>Warehouse Name</label>
				<input class="form-control" type="text" id="facilityName" name="facilityName" maxlength="40" value="${parameters.facilityName!""}"/>
			</div>
			<div class="col-sm-3">
				<label>Parent Warehouse Id</label>
				<input id="parentfacilityId" type="text" size="25" name="parentfacilityId" class="form-control " value="${parameters.parentfacilityId!}">
			</div>
 
			<div class="col-sm-3">
				<label>Warehouse Type</label>
				<#assign facilityTypes  = delegator.findByAnd("FacilityType",null, null, false)/>
				<select class="form-control" name="facilityTypeId">
        			<#list facilityTypes as nextFacilityType>
		          		<option value='${nextFacilityType.facilityTypeId!}'>${nextFacilityType.get("description",locale)!}</option>
        			</#list>
      			</select>
			</div>
		</div>
		<input type="submit" class="btn btn-danger" value="FIND">
	</form>
