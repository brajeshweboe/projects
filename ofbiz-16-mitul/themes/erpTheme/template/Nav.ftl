<div class="title"><span>Main</span></div>
<div class="collapse navbar-collapse navbar-ex1-collapse">
<ul class="nav navbar-nav side-nav panel-group" id="accordionMenu" role="tablist" aria-multiselectable="true">
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingOne">
    <a class="" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseThree" aria-expanded="true" aria-controls="collapseOne"><img src="/erpTheme/images/icon1.png" alt=""> Catalog Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseThree" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
                                            <li>
                                                <a href="<@ofbizUrl>EditProduct</@ofbizUrl>">Create Product</a>
                                            </li>
											<li>
                                                <a href="<@ofbizUrl>EditCategory</@ofbizUrl>">Create Product Category</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>FindProduct</@ofbizUrl>">Product List</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>EditShipment</@ofbizUrl>">Create Shipment</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>FindShipment</@ofbizUrl>">Shipment</a>
                                            </li>
                                            
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingOne">
    <a class="" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseWarehouse" aria-expanded="true" aria-controls="collapseOne"><img src="/erpTheme/images/icon1.png" alt=""> Warehouse Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseWarehouse" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
        <li>
                                                <a href="<@ofbizUrl>EditFacility</@ofbizUrl>">Create Warehouse</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>warehouseList</@ofbizUrl>">Warehouse List</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>EditShipment</@ofbizUrl>">Create Shipment</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>FindShipment</@ofbizUrl>">Shipment</a>
                                            </li>
                                            
                                       <#--     <li>
                                                <a href="<@ofbizUrl>expireWarehouse</@ofbizUrl>">Expire Warehouse</a>
                                            </li>
                                             <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Organisational GL Setting</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Tax Authorities</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>main</@ofbizUrl>">Billing Account</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Invoices</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Payments</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Payment Groups</a>
                                            </li -->
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingFour">
    <a class="" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseFour" aria-expanded="false" aria-controls="collapseFour"><img src="/erpTheme/images/icon4.png" alt="">  Order Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseFour" class="panel-collapse collapse in" role="tabpanel" aria-labelledby="headingFour">
        <li>
                            <a href="<@ofbizUrl>orderentry</@ofbizUrl>">create Order</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">orderlist</@ofbizUrl>">Order List</a>
                        </li>
                        <li>
                            <a class="active" href="<@ofbizUrl secure="${request.isSecure()?string}">jobcardlist</@ofbizUrl>">Job Card List</a>
                        </li>
                        <#-- li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">jobcarddetails</@ofbizUrl>">Job Card Details</a>
                        </li -->
                        <#-- li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">sparePartIssue</@ofbizUrl>">Spare Part Issue</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">jobCardListForPerformInvoice</@ofbizUrl>">Create Perform Invoice</a>
                        </li>
						<li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">createInvoiceForJobCard</@ofbizUrl>">create Invoice for Job Card</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">main</@ofbizUrl>">Job Card Close</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl secure="${request.isSecure()?string}">main</@ofbizUrl>">Vehicle Repair Invoice</a>
                        </li -->
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingOne">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseOne" aria-expanded="true" aria-controls="collapseOne"><img src="/erpTheme/images/icon1.png" alt=""> Finance Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseOne" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingOne">
        <li>
                                                <a href="<@ofbizUrl>ListGlAccountOrganization?organizationPartyId=${userLogin.userLoginId!}</@ofbizUrl>">Char of Accounts</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>TrialBalance</@ofbizUrl>">Trail Balance</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>globalGLSettings</@ofbizUrl>">Global GL Setting</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Organisational GL Setting</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Tax Authorities</a>
                                            </li>
                                            <li>
                                                <a  href="<@ofbizUrl>main</@ofbizUrl>">Billing Account</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Invoices</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Payments</a>
                                            </li>
                                            <li>
                                                <a href="<@ofbizUrl>main</@ofbizUrl>">Payment Groups</a>
                                            </li>
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingTwo">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseTwo" aria-expanded="false" aria-controls="collapseTwo"><img src="/erpTheme/images/icon2.png" alt="">  Account Receivables <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseTwo" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingTwo">
        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Sales Orders</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Customer Master</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Customer Groups</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Invoices</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Payments</a>
                        </li>
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingAccount">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseAccount" aria-expanded="false" aria-controls="collapseThree"><img src="/erpTheme/images/icon3.png" alt="">  Account Payables <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseAccount" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingAccount">
        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Purchase Order</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">GRN</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Purchase Return Order</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Vendor List</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Vendor Groups</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Invoices</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Payments</a>
                        </li>
    </ul>
</li>

<div class="title margintop20"><span>Fixed Assets</span></div>


<li class="panel panel-default">
    <div class="heading" role="tab" id="headingFive">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseFive" aria-expanded="false" aria-controls="collapseFive"><img src="/erpTheme/images/icon5.png" alt="">  Invoice Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseFive" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingFive">
        <li>
                            <a href="<@ofbizUrl>invoicelist</@ofbizUrl>">Invoice List</a>
                        </li>
        <li><a href="#">Invoice Management 2</a></li>
        <li><a href="#">Invoice Management 3</a></li>
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingSix">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseSix" aria-expanded="false" aria-controls="collapseSix"><img src="/erpTheme/images/icon6.png" alt="">   Inventory Management <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseSix" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingSix">
        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Spare Part Issue</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Spare Part Return</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Stock On Hand</a>
                        </li>
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingSeven">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseSeven" aria-expanded="false" aria-controls="collapseSeven"><img src="/erpTheme/images/icon7.png" alt="">   Reports <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseSeven" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingSeven">
        li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Gaadizo Commission Report</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Pending PO Report</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Job Card Details</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Pending Job Card details</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">On Hand Stock Report</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">MRN Tracker Report</a>
                        </li>
                        <li>
                            <a href="<@ofbizUrl>main</@ofbizUrl>">Pending MRN Report</a>
                        </li>
    </ul>
</li>
<li class="panel panel-default">
    <div class="heading" role="tab" id="headingEight">
    <a class="collapsed" role="button" data-toggle="collapse" data-parent="#accordionMenu" href="#collapseEight" aria-expanded="false" aria-controls="collapseEight"><img src="/erpTheme/images/icon8.png" alt="">  Admin <i class="fa fa-fw fa-angle-down pull-right"></i></a>
    </div>
    <ul id="collapseEight" class="panel-collapse collapse" role="tabpanel" aria-labelledby="headingEight">
        <li><a href="#">Profile Details</a></li>
        <li><a href="#">Change Password</a></li>
        <li><a href="#">Logout</a></li>
         <li>
                            <a href="<@ofbizUrl>createSparePart</@ofbizUrl>">Create Spare Part</a>
                        </li>
                         <li>
                            <a href="<@ofbizUrl>updateSparePart</@ofbizUrl>">Update Spare Part</a>
                        </li>
    </ul>
</li>
 
</ul>
</div>
