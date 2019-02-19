<#--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <fo:layout-master-set>
        <fo:simple-page-master master-name="main" page-height="8in" page-width="8in"
                               margin-top="0.5in" margin-bottom="0.25in" margin-left="0.25in" margin-right="0.25in">
            <fo:region-body margin-top="0in"/>
            <fo:region-before extent="0in"/>
            <fo:region-after extent="0in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="main">
        <fo:flow flow-name="xsl-region-body" font-family="Helvetica">
<fo:table width="100%" text-align="left">
    <fo:table-body>
        <fo:table-row>
            <fo:table-cell text-align="center">
                    <fo:block font-size="14pt" font-weight="bold" margin-bottom="0.1in" text-align="center">${companyName!}</fo:block>
                    
                    <#if postalAddress?exists && postalAddress?has_content>
                            <fo:block font-size="8pt" text-align="center">${postalAddress.address1!}
  							<#if postalAddress.address2?has_content>${postalAddress.address2}</#if>
  							${postalAddress.city!}
  							<#if postalAddress.stateProvinceGeoId?has_content>,
  								${postalAddress.stateProvinceGeoId}
  							</#if> 
  							${postalAddress.postalCode!}
  							${postalAddress.countryGeoId!}
  							
      						 </fo:block>
  				   </#if>
               
               
               
                
            </fo:table-cell>
        </fo:table-row>
	</fo:table-body>
</fo:table>
        
           
            <fo:block>From Address</fo:block>
            <#if originContactDetail?exists && originContactDetail?has_content>
                            <fo:block font-size="8pt" text-align="center">${originContactDetail.address1!}
  							<#if originContactDetail.address2?has_content>${originContactDetail.address2}</#if>
  							${originContactDetail.city!}
  							<#if originContactDetail.stateProvinceGeoId?has_content>,
  								${originContactDetail.stateProvinceGeoId}
  							</#if> 
  							${originContactDetail.postalCode!}
  							${originContactDetail.countryGeoId!}
  							
      						 </fo:block>
  				   </#if>
  				   
  				   <fo:block>Ship To Address</fo:block>
            <#if destinationContactDetail?exists && destinationContactDetail?has_content>
                            <fo:block font-size="8pt" text-align="center">${destinationContactDetail.address1!}
  							<#if destinationContactDetail.address2?has_content>${destinationContactDetail.address2}</#if>
  							${destinationContactDetail.city!}
  							<#if destinationContactDetail.stateProvinceGeoId?has_content>,
  								${destinationContactDetail.stateProvinceGeoId}
  							</#if> 
  							${destinationContactDetail.postalCode!}
  							${destinationContactDetail.countryGeoId!}
  							
      						 </fo:block>
  				   </#if>
  				   
  				    <fo:block text-align="center">
                <fo:instream-foreign-object>
                    <barcode:barcode xmlns:barcode="http://barcode4j.krysalis.org/ns"
                                     message="${shipmentId}">
                        <barcode:code39>
                            <barcode:height>3in</barcode:height>
                            <barcode:module-width>1.5mm</barcode:module-width>
                        </barcode:code39>
                        <barcode:human-readable>
                            <barcode:placement>bottom</barcode:placement>
                            <barcode:font-name>Helvetica</barcode:font-name>
                            <barcode:font-size>14pt</barcode:font-size>
                            <barcode:display-start-stop>false</barcode:display-start-stop>
                            <barcode:display-checksum>false</barcode:display-checksum>
                        </barcode:human-readable>
                    </barcode:barcode>
                </fo:instream-foreign-object>
            </fo:block>
            <fo:block><fo:leader/></fo:block>
        </fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>
