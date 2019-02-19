<#escape x as x?xml>
<#-- table -->
 <fo:table margin-top="5mm">
    <fo:table-column column-number="1" column-width="proportional-column-width(20)"/>
    <fo:table-column column-number="2" column-width="proportional-column-width(20)"/>
    <fo:table-column column-number="3" column-width="proportional-column-width(20)"/>
    <fo:table-column column-number="4" column-width="proportional-column-width(25)"/>
    <fo:table-column column-number="5" column-width="proportional-column-width(5)"/>
    <fo:table-column column-number="6" column-width="proportional-column-width(5)"/>
    <fo:table-column column-number="7" column-width="proportional-column-width(5)"/>
    <fo:table-body>
	    <#-- header row -->
	    <fo:table-row background-color="#DFDFDF" padding-top="10pt">
	      <fo:table-cell padding="1mm" border="solid th	in black" text-align="center" >
	         <fo:block font-size="6pt">N° ACHATS PO #</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">AGENT</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">EXPÉDIÉ SHIP DATE</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">TERMES / TERMS</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" number-columns-spanned="3" >
	         <fo:block font-size="6pt">LIVRAISON SHIP VIA</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <#-- /header row -->
	    <#-- header info row -->
	    <fo:table-row>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt"></fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">${createdByAgentName?if_exists}</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">2012 03 29</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt" text-align="center" >
               <#if orderTerms?exists?has_content && orderTerms.size() gt 0>
				    <#list orderTerms as orderTerm>
				        <fo:block text-indent="0.2in">
				            ${orderTerm.getRelatedOne("TermType").get("description",locale)} ${orderTerm.termDays?default("")}
				        </fo:block>
				    </#list>
				</#if>
	         </fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" number-columns-spanned="3" >
	         <fo:block font-size="6pt">${shippingMethodName?if_exists}</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <#-- /header info row -->
	    <#-- items header row -->
	    <fo:table-row background-color="#DFDFDF" padding-top="10pt">
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">N° ITEM</fo:block>
	         <fo:block font-size="6pt">ITEM N°</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center"  number-columns-spanned="3" >
	         <fo:block font-size="6pt">DESCRIPTION</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">QTE COM</fo:block>
	         <fo:block font-size="6pt">QT ORD</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
	         <fo:block font-size="6pt">QTE  EXP</fo:block>
	         <fo:block font-size="6pt">QTY SHIP</fo:block>
	      </fo:table-cell>
	      <fo:table-cell padding="1mm" border="solid thin black" text-align="center">
	         <fo:block font-size="6pt">QTE BO</fo:block>
	         <fo:block font-size="6pt">QTY BO</fo:block>
	      </fo:table-cell>
	    </fo:table-row>
	    <#-- /items header row -->
	    <#-- items row -->
        <#list orderItems as orderItem>
            <#assign productId = orderItem.productId?if_exists>
            <#assign remainingQuantity = (orderItem.quantity?default(0) - orderItem.cancelQuantity?default(0))>
		    <fo:table-row >
		      <fo:table-cell padding="1mm" border="solid thin black" text-align="left" >
		         <fo:block font-size="6pt">${orderItem.productId?default("N/A")}</fo:block>
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black" text-align="left"  number-columns-spanned="3" >
		         <fo:block font-size="6pt">${orderItem.itemDescription?if_exists}</fo:block>
				 <#if orderItem.comments?has_content><fo:block font-size="6pt">Comments:${orderItem.comments!}</fo:block></#if>
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black" text-align="right" >
		         <fo:block font-size="6pt">${orderItem.quantity}</fo:block>
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black" text-align="right" >
		         <fo:block font-size="6pt">${orderItem.quantityShipped?default('0')}</fo:block>
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black" text-align="right">
		         <fo:block font-size="6pt">${orderItem.quantityBackOrdered?if_exists}</fo:block>
		      </fo:table-cell>
		    </fo:table-row>
		</#list>
		<#-- to draw blank lines to fill the page -->
		<#if (orderItemList?size < 60)>
			<#assign numberOfItems = orderItemList?size />
			<#assign blankLinesToDraw = (60-numberOfItems) />
	    </#if>
	    <#if blankLinesToDraw?exists>
		    <#list 1..blankLinesToDraw as index>
		    <fo:table-row >
		      <fo:table-cell padding="1mm" border="solid thin black" border-bottom-width="0" border-top-width="0">
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black"  border-bottom-width="0" border-top-width="0" number-columns-spanned="3" >
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black"  border-bottom-width="0" border-top-width="0" >
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black"  border-bottom-width="0" border-top-width="0" >
		      </fo:table-cell>
		      <fo:table-cell padding="1mm" border="solid thin black"  border-bottom-width="0" border-top-width="0" >
		      </fo:table-cell>
		    </fo:table-row>
		    </#list>
	    </#if>
	    <#-- footer row -->
	    <fo:table-row >
	      <fo:table-cell text-align="center" number-columns-spanned="7" >
	         <fo:table >
			    <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
			    <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
			    <fo:table-body>
				    <fo:table-row>
				      <fo:table-cell background-color="#6baf10" padding="1mm" border="solid thin black" text-align="center"  color="#FFFFFF">
				         <fo:block font-weight="bold" font-size="8pt">CONDITIONS DE VENTE</fo:block>
				      </fo:table-cell>
				      <fo:table-cell background-color="#6baf10" padding="1mm" border="solid thin black" text-align="center" color="#FFFFFF">
				         <fo:block font-weight="bold" font-size="8pt">CONDITIONS OF SALE</fo:block>
				      </fo:table-cell>
				    </fo:table-row>
				    <fo:table-row>
				      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
				         <fo:block font-size="7pt">LA MARCHANDISE DEMEURE LA PROPRIÉTÉ DE ALP MICROSYSTEMES JUSQU'À PAIEMENT INTÉGRAL. 
							TOUT SOLDE DÛ EN VERTU DES PRÉSENTES PORTERA INTÉRÊT AU TAUX DE 2% PAR MOIS (24% PAR ANNÉE). 
							DES FRAIS D'ADMINISTRATION DE 20% SERONT CHARGÉS 
							SUR TOUT COMPTE DONNÉ EN COLLECTION.
						</fo:block>
				      </fo:table-cell>
				      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
				         <fo:block font-size="7pt">MERCHANDISE REMAINS THE PROPERTY OF ALP MICROSYSTEMES UNTIL FULL PAYMENT IS RECEIVED. INTEREST CHARGE OF 24% PER ANNUM COMPUTED 2% MONTHLY ON ALL OVERDUE ACCOUNTS.  ADMINISTRATION FEES OF 20% WILL BE CHARGED ON ALL ACCOUNTS GIVEN FOR COLLECTION.</fo:block>
				      </fo:table-cell>
				    </fo:table-row>
				    <fo:table-row>
				      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
				         <fo:block font-weight="bold" font-size="7pt">Nº TPS/GST R100337021</fo:block>
				      </fo:table-cell>
				      <fo:table-cell padding="1mm" border="solid thin black" text-align="center" >
				         <fo:block font-weight="bold" font-size="7pt">Nº TVQ/QST 10019147030001</fo:block>
				      </fo:table-cell>
				    </fo:table-row>
			    </fo:table-body>
		    </fo:table>
	      </fo:table-cell>
	    </fo:table-row>
	    <#-- /footer row -->
    </fo:table-body>
  </fo:table>
<#-- /table -->
</#escape>
