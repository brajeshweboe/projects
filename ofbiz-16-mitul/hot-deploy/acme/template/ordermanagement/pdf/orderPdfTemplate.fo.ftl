<#escape x as x?xml>
<fo:root xmlns:fo="http://www.w3.org/1999/XSL/Format"
    <#-- inheritance -->
    <#if defaultFontFamily?has_content>font-family="${defaultFontFamily}"</#if>
>
    <fo:layout-master-set>
        <fo:simple-page-master master-name="main-page"  font-size="8pt"
              page-width="8.5in" page-height="11in"
              margin-top="0.2in" margin-bottom="0.2in"
              margin-left="0.6in" margin-right="0.6in">
            <#-- main body -->
            <fo:region-body margin-top=".8in" margin-bottom="0.2in"/>
            <#-- the header -->
            <fo:region-before extent=".9in"/>
            <#-- the footer -->
            <fo:region-after extent="0.4in"/>
        </fo:simple-page-master>
        <fo:simple-page-master master-name="main-page-landscape"
              page-width="11in" page-height="8.5in"
              margin-top="0.2in" margin-bottom="0.2in"
              margin-left="0.6in" margin-right="0.4in">
            <#-- main body -->
            <fo:region-body margin-top="0in" margin-bottom="0.4in"/>
            <#-- the header -->
            <fo:region-before extent="1in"/>
            <#-- the footer -->
            <fo:region-after extent="0.4in"/>
        </fo:simple-page-master>
    </fo:layout-master-set>

    <fo:page-sequence master-reference="${pageLayoutName?default("main-page")}">

        <#-- Header -->
        <#-- The elements it it are positioned using a table composed by one row
             composed by two cells (each 50% of the total table that is 100% of the page):
             in the left side cell the "topLeft" template is included
             in the right side cell the "topRight" template is included
        -->
        <fo:static-content flow-name="xsl-region-before">
            <fo:table table-layout="fixed" width="100%" border-style="solid">
                <fo:table-column column-number="1" column-width="proportional-column-width(50)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(50)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell >
${sections.render("topLeft")}
                        </fo:table-cell>
                        <fo:table-cell padding-left="5mm" padding-right="5mm"  border-left-style="solid">
${sections.render("topRight")}
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:static-content>

        <#-- the footer 
        <fo:static-content flow-name="xsl-region-after">
            <fo:table table-layout="fixed" width="100%" border-style="solid">
                <fo:table-column column-number="1" column-width="proportional-column-width(70)"/>
                <fo:table-column column-number="2" column-width="proportional-column-width(30)"/>
                <fo:table-body>
                    <fo:table-row>
                        <fo:table-cell>
				            <fo:block font-size="8pt" text-align="left">
            					Signature : __________________________________________________________
        					</fo:block>
                        </fo:table-cell>
                        <fo:table-cell>
				            <fo:block font-size="8pt" text-align="right" space-before="10pt">
            					${uiLabelMap.CommonPage} <fo:page-number/> ${uiLabelMap.CommonOf} <fo:page-number-citation ref-id="theEnd"/>
        					</fo:block>
                        </fo:table-cell>
                    </fo:table-row>
                </fo:table-body>
            </fo:table>
        </fo:static-content>  -->

        <#-- the body -->
        <fo:flow flow-name="xsl-region-body">
${sections.render("body")}
            <fo:block id="theEnd"/>  <#-- marks the end of the pages and used to identify page-number at the end -->
        </fo:flow>
    </fo:page-sequence>
</fo:root>
</#escape>
