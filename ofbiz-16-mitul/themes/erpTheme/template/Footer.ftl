<#assign nowTimestamp = Static["org.apache.ofbiz.base.util.UtilDateTime"].nowTimestamp()>

<div id="footer">
  <ul>
    <li class="footer container-fluid pl-30 pr-30">
                <div class="row">
                    <div class="col-sm-12">
                        <p>2018 &copy; acme</p>
                    </div>
                </div>
            </li>
            <!-- /Footer -->
    <li>
      ${uiLabelMap.CommonCopyright} (c) 2015-${nowTimestamp?string("yyyy")} Acme - <a href="http://www.acme.com" target="_blank">www.acme.com</a><br/>
      ${uiLabelMap.CommonPoweredBy} <a href="http://acme.com" target="_blank">Acme</a> 
       <#include "ofbizhome://runtime/SvnInfo.ftl" ignore_missing=true/>
       <#include "ofbizhome://runtime/GitInfo.ftl" ignore_missing=true/>
    </li>
    <li class="opposed">${nowTimestamp?datetime?string.short} -
  <a href="<@ofbizUrl>ListTimezones</@ofbizUrl>">${timeZone.getDisplayName(timeZone.useDaylightTime(), Static["java.util.TimeZone"].LONG, locale)}</a>
    </li>
  </ul>
</div>

<#if layoutSettings.VT_FTR_JAVASCRIPT?has_content>
  <#list layoutSettings.VT_FTR_JAVASCRIPT as javaScript>
    <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
  </#list>
</#if>
<script type="text/javascript" src="/erpTheme/js/bootstrap-datetimepicker.js"></script>
</div>
<script>
$(document).ready(function(){
var date_input=$('input[name="date"]'); //our date input has the name "date"
var container=$('.bootstrap-iso form').length>0 ? $('.bootstrap-iso form').parent() : "body";
date_input.datepicker({
	format: 'mm/dd/yyyy',
	container: container,
	todayHighlight: true,
	autoclose: true,
})
})
</script>
</body>
</html>
