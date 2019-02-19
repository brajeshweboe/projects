<#escape x as x?xml>
 <fo:table table-layout="fixed" width="100%" font-size="8pt">
    <fo:table-column column-width="proportional-column-width(50)"/>
    <fo:table-column column-width="proportional-column-width(50)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            <#if partyAndPostalAddressGV?has_content>
	            	<#if orderHeaderAndJobCard?exists && orderHeaderAndJobCard?has_content><fo:block>Customer :${orderHeaderAndJobCard.firstName?if_exists}</fo:block></#if>
	            	<#if partyAndPostalAddressGV.toName?has_content><fo:block>${partyAndPostalAddressGV.toName?if_exists}</fo:block></#if>
	                
	                <fo:block>Address : ${partyAndPostalAddressGV.address1?if_exists}</fo:block>
	                <#if partyAndPostalAddressGV.address2?has_content><fo:block>${partyAndPostalAddressGV.address2?if_exists}</fo:block></#if>
	                <fo:block>
	                    <#assign stateGeo = (delegator.findOne("Geo", {"geoId", partyAndPostalAddressGV.stateProvinceGeoId?if_exists}, false))?if_exists />
	                    ${partyAndPostalAddressGV.city?if_exists}<#if stateGeo?has_content>, ${stateGeo.geoName?if_exists}</#if>
						<#if partyAndPostalAddressGV.postalCode?has_content>, ${partyAndPostalAddressGV.postalCode?if_exists}</#if>
	                </fo:block>
	                <fo:block>
	                    <#assign countryGeo = (delegator.findOne("Geo", {"geoId", partyAndPostalAddressGV.countryGeoId?if_exists}, false))?if_exists />
	                    <#if countryGeo?has_content>${countryGeo.geoName?if_exists}</#if>
	                </fo:block>
	                 <#if partyAndPostalAddressGV.contactNumber?has_content><fo:block>Mobile : ${partyAndPostalAddressGV.contactNumber?if_exists}</fo:block></#if>
	            </#if>
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			  <fo:table table-layout="fixed" width="100%" font-size="8pt">
			    <fo:table-column column-width="proportional-column-width(50)"/>
			    <fo:table-column column-width="proportional-column-width(50)"/>
			    <fo:table-body>
				    <fo:table-row>
				    <fo:table-cell padding="1mm" text-align="left">
					        	<fo:block font-size="10pt" font-weight="bold">${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >VinNo : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Eng.No : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Mileage : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Dt. of Sale :${orderHeaderAndJobCard.jobCardId!}</fo:block>
				        	</fo:table-cell>
				        	<fo:table-cell padding="1mm" text-align="left"  border-left-style="solid">
					        	<fo:block font-size="10pt" font-weight="bold">R.O. No.:${orderHeaderAndJobCard.orderId!}</fo:block>
					        	<fo:block >Date : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Time : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Model : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Variant : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
					        	<fo:block >Time : ${orderHeaderAndJobCard.jobCardId!}</fo:block>
				        	</fo:table-cell>
				        	 </fo:table-row>	
				        	 <fo:table-row width="100%">
				        	 <fo:table-cell padding="1mm" text-align="left"   number-columns-spanned="2"   border-top-style="solid" >
					        	<fo:block font-size="10pt">Colour : SLEEK SILVER</fo:block>
					        	</fo:table-cell>
				        	 </fo:table-row>
			    </fo:table-body>
			</fo:table>
	      </fo:table-cell>
	    </fo:table-row>
    </fo:table-body>
</fo:table>

<#-- ======================================================================================= -->
 <fo:table table-layout="fixed" width="100%" font-size="8pt">
    <fo:table-column column-width="proportional-column-width(50)"/>
    <fo:table-column column-width="proportional-column-width(50)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Customer's request
	            <#if orderHeaderAndItems?has_content>
	                <#list orderHeaderAndItems as orderHeaderAndItem>
	                    <fo:block font-size="10pt" font-weight="bold">${orderHeaderAndItem.productId!}   ${orderHeaderAndItem.unitPrice!} </fo:block>
	                </#list>
	                 <fo:block font-size="10pt" font-weight="bold">Total Amount :   ${orderHeaderAndJobCard.grandTotal!} </fo:block>
	            </#if> 
	            
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
				<fo:table table-layout="fixed" width="100%" font-size="8pt">
			    <fo:table-column column-width="proportional-column-width(100)"/>
			    <fo:table-body>
				    <fo:table-row>
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block font-size="9pt" font-weight="bold">Service Advisor Instruction to Shop Floor:</fo:block>
				         </fo:table-cell>
				     </fo:table-row>
				     <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Primary jobs:</fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	<fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	     <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				      
				       
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Secondary jobs:</fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	<fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	     <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       
				       
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Additional jobs:</fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	<fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       	     <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid"></fo:block>
				        </fo:table-cell>
				       </fo:table-row>	
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid" font-weight="bold">Customer Confirmation for additional jobs:</fo:block>
				        </fo:table-cell>
				       </fo:table-row>
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Name of customer/customer representative</fo:block>
				        	<fo:block border-bottom-style="solid">(to whom spoken to)</fo:block>

				        </fo:table-cell>
				       </fo:table-row>
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Date         Time</fo:block>

				        </fo:table-cell>
				       </fo:table-row>
				       <fo:table-row >
				    	<fo:table-cell padding="1mm" text-align="left">
				        	<fo:block border-bottom-style="solid">Tick(/) additional jobs approved by customer/cust. representative</fo:block>
				        	<fo:block border-bottom-style="solid">Cross(/)addtional jobs not approved by customer/ cust. representativ</fo:block>

				        </fo:table-cell>
				       </fo:table-row>
				       
			    </fo:table-body>
			</fo:table>
	      </fo:table-cell>
	    </fo:table-row>
    </fo:table-body>
</fo:table>
<#-- ============================================================================================= -->
<fo:table table-layout="fixed" width="100%" font-size="7pt">
    <fo:table-column column-width="proportional-column-width(100)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Terms of payment are Cash, Demand Draft or Pay Order only. Demand Draft/Pay Order should be made in favour of-----------------.payable at-------
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    	
    </fo:table-body>
</fo:table>
<#-- ============================================================================================= -->

<fo:table table-layout="fixed" width="100%" font-size="8pt">
    <fo:table-column column-width="proportional-column-width(65)"/>
	<fo:table-column column-width="proportional-column-width(35)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell>
	      <fo:block>				<fo:table table-layout="fixed" font-size="8pt">
    				<fo:table-column column-width="proportional-column-width(55)"/>
    				<fo:table-column column-width="proportional-column-width(45)"/>
    				<fo:table-body>
	    				<fo:table-row>
<fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	          <fo:table table-layout="fixed" width="100%" font-size="7pt">
    <fo:table-column  column-width="proportional-column-width(50)"/>
    <fo:table-column  column-width="proportional-column-width(50)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="center"  border-style="solid" number-columns-spanned="2">
	      	<fo:block>Estimated</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Promised Date:
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            
	        </fo:block>
	      </fo:table-cell>
	      
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Promised Time:
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            
	        </fo:block>
	      </fo:table-cell>
	      
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Estimated Amount:
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            
	        </fo:block>
	      </fo:table-cell>
	      
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Service Advisor:
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            
	        </fo:block>
	      </fo:table-cell>
	      
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Mobile No.:
	        </fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            
	        </fo:block>
	      </fo:table-cell>
	      
	    </fo:table-row>
        <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      	<fo:block> I hereby authorised for the above-repaires to be executed using necessaary materials, and I am affixing my signature below in evidence of agreeing to the terms and conditions given in the reverse side of this repair order absolutely and unconditionally.</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell  font-size="9pt" padding="1mm" text-align="right"  border-style="solid" number-columns-spanned="2">
	      	<fo:block> Customer's Signature</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    
    </fo:table-body>
</fo:table>
	          
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	     	 <fo:block>Body / paint damages</fo:block>
	     	 <fo:block text-align="center">
    			<fo:external-graphic src="<@ofbizContentUrl>http://localhost:8085/erp/images/logo/gaadiimage.png</@ofbizContentUrl>" height="100%" content-width="234px" content-height="scale-to-fit"/>
			 </fo:block>
 			 <fo:block text-align="center">C- CRACK, D- DENT / DAMAGE S-SCRATCH / SPOT, P-PEELING</fo:block>
	      </fo:table-cell>
</fo:table-row>
	    	<fo:table-row height="15px">
	      					<fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      						<fo:block>				
</fo:block>
				            </fo:table-cell>
				            </fo:table-row>
				            <fo:table-row height="15px">
	      					<fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      						<fo:block>				
</fo:block>
				            </fo:table-cell>
				            </fo:table-row>
				            <fo:table-row height="15px">
	      					<fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      						<fo:block>				
</fo:block>
				            </fo:table-cell>
				            </fo:table-row>
				            <fo:table-row height="15px">
	      					<fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      						<fo:block>				
</fo:block>
				            </fo:table-cell>
				            </fo:table-row>
				            <fo:table-row><fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="2">
	      						<fo:block>
				            <fo:table table-layout="fixed" width="100%" font-size="7pt">
    <fo:table-column  column-width="proportional-column-width(50)"/>
    <fo:table-column  column-width="proportional-column-width(50)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  >
	      <fo:block>
	           <fo:table table-layout="fixed" width="100%" font-size="7pt">
    <fo:table-column  column-width="proportional-column-width(100)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block>
	            Delivered by:
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Name
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>	           Date      <fo:block> Time</fo:block>     </fo:block>
	        	      
	        
	      </fo:table-cell>
		  
	    </fo:table-row>
	    	
    </fo:table-body>
</fo:table>
	        </fo:block>
	      </fo:table-cell>
	       <fo:table-cell padding="1mm" text-align="left">
	      <fo:block>
	            <fo:table table-layout="fixed" width="100%" font-size="7pt">
    <fo:table-column  column-width="proportional-column-width(100)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Final Inspection: OK/NOT OK 
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Name
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block>
	            Signature
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    	
    </fo:table-body>
</fo:table>
	        </fo:block>
	      </fo:table-cell>
		  
	    </fo:table-row>
	    	
    </fo:table-body>
</fo:table>
</fo:block>
				            </fo:table-cell>
				            </fo:table-row>
    </fo:table-body>
</fo:table></fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      	<fo:block>
	            <fo:table table-layout="fixed" font-size="8pt">
    				<fo:table-column  column-width="proportional-column-width(40)"/>
    				<fo:table-column  column-width="proportional-column-width(60)"/>
    				<fo:table-body>
	    				<fo:table-row>
	      					<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      						<fo:block>				Fuel gauge Needle
Position</fo:block>
				            </fo:table-cell>
				            <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      						<fo:block text-align="left">
    			<fo:external-graphic src="<@ofbizContentUrl>http://localhost:8085/erp/images/logo/spedometer.png</@ofbizContentUrl>" content-width="100px"  content-height="scale-to-fit"/>
			 </fo:block>
				            </fo:table-cell>
					    </fo:table-row>
					    <fo:table-row>
					        <fo:table-cell number-columns-spanned="2">
					        <fo:table table-layout="fixed" width="100%">
    <fo:table-column  column-width="proportional-column-width(40)"/>
    <fo:table-column  column-width="proportional-column-width(10)"/>
	<fo:table-column  column-width="proportional-column-width(7)"/>
	<fo:table-column  column-width="proportional-column-width(43)"/>
    <fo:table-body>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Service Book</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Idols (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Tool Kit</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Wheel cover (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Spare Wheel</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Wheel Cap. (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Jack</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Mud Flaps. (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Jack Handle</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Mats (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Car Perfume</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	           Dicky Mat (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Clock</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Cigarette lighter
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Stereo</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	           Speaker-RR (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">CD Player</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Speaker-FR (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">Mouth Player</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Tweeters (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid">
			<fo:block font-size="8pt">CD Changer</fo:block>      
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
			<fo:block font-size="8pt">Yes</fo:block>
	      </fo:table-cell>
		  <fo:table-cell padding="1mm" text-align="center"  border-style="solid">
	      <fo:block font-size="8pt">
	            No
	        </fo:block>
	      </fo:table-cell>
		<fo:table-cell padding="1mm" text-align="left"  border-style="solid">
	      <fo:block font-size="8pt">
	            Ext (Nos.)
	        </fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	     <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="4" >
			<fo:block font-size="8pt">Battery</fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	   <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="4" >
			<fo:block font-size="8pt"> </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	      <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="4" >
			<fo:block font-size="8pt">Tyres</fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	   <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="4" >
			<fo:block font-size="8pt"> </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	       <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left"  border-style="solid" number-columns-spanned="4" >
			<fo:block font-size="8pt"> </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	         <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left" number-columns-spanned="4" >
			<fo:block font-size="8pt">I hereby certify that the repaires have been carried out to my entire satsifaction. </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	      <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left" number-columns-spanned="4" >
			<fo:block font-size="8pt"> </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	      <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left" number-columns-spanned="4" >
			<fo:block font-size="8pt"> </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
	      
	      <fo:table-row>
	      <fo:table-cell padding="1mm" text-align="left" number-columns-spanned="4" >
			<fo:block font-size="8pt">Date:_______________     Customer's Signature </fo:block>      
	      </fo:table-cell>
	      </fo:table-row>
    </fo:table-body>
</fo:table>
					        </fo:table-cell>
					    </fo:table-row>
				    </fo:table-body>
				</fo:table>
	        </fo:block>
	      </fo:table-cell>
	      </fo:table-row>
    </fo:table-body>
</fo:table>

</#escape>
