<div class="title2"><span>Warehouse List</span></div>

<div class="table-responsive margintop20">
<table width="100%" border="0" cellspacing="0" cellpadding="0" class="tabledetails" id="sortableData">
<thead>
  <tr>
    <th>Warehouse</th>
    <th>Name</th>
    <th>Warehouse Type</th>
    <th>Parent Warehouse</th>
  </tr>
  </thead>
  <tbody>
	<#list warehouseList as warehouse>
	<tr>
		<td>
		  <a class="buttontext" href="<@ofbizUrl>EditFacility?facilityId=${warehouse.facilityId!}&isUpdate=Y</@ofbizUrl>" title="Code"><span class="bookid">${warehouse.facilityId!}</span> </a>
		</td>
		<td>${warehouse.facilityName!}</td>
		<td>${warehouse.facilityTypeId!}</td>
		<td>${warehouse.parentFacilityId!}</td>
	</tr>
	</#list>
													
												</tbody>
</table>
</div>
