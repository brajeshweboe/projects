<#escape x as x?xml>
	<#-- if orderContactMechValueMaps?exists && orderContactMechValueMaps?has_content>
	    <#list orderContactMechValueMaps as orderContactMechValueMap>
	        <#assign contactMech = orderContactMechValueMap.contactMech>
	        <#assign contactMechPurpose = orderContactMechValueMap.contactMechPurposeType>
	        <#if (contactMech.contactMechTypeId == "POSTAL_ADDRESS") && (contactMechPurpose.contactMechPurposeTypeId == "SHIPPING_LOCATION")>
	            <#assign shippingPostalAddress = orderContactMechValueMap.postalAddress>
	        </#if>
	    </#list>
	</#if -->
    
    

<fo:table table-layout="fixed" width="100%">
    <fo:table-column column-number="1" column-width="proportional-column-width(90)"/>
    <fo:table-column column-number="2" column-width="proportional-column-width(10)"/>
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell>
                <fo:block font-size="10pt">
                    <fo:block>${companyName!}</fo:block>
                    <#if postalAddress?exists>
                        <#if postalAddress?has_content>
                            ${setContextField("postalAddress", postalAddress)}
                            ${screens.render("component://party/widget/partymgr/PartyScreens.xml#postalAddressPdfFormatter")}
                        </#if>
                    <#else>
                        <fo:block>${uiLabelMap.CommonNoPostalAddress}</fo:block>
                        <fo:block>${uiLabelMap.CommonFor}: ${companyName}</fo:block>
                    </#if>
                     <fo:block>${(phone.contactNumber)!}</fo:block>
                
                </fo:block>
            </fo:table-cell>
        </fo:table-row>
            
        
         <fo:table-row>
                      <fo:table-cell>
                         <fo:block font-size="8pt"> </fo:block>
                      </fo:table-cell>
        </fo:table-row>
		</fo:table-body>
</fo:table>
</#escape>