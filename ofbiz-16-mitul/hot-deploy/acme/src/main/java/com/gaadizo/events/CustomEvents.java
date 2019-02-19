package com.gaadizo.events;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.ofbiz.base.util.UtilProperties;
import org.apache.ofbiz.base.util.UtilValidate;
import org.apache.ofbiz.entity.GenericValue;
import org.apache.ofbiz.entity.condition.EntityCondition;
import org.apache.ofbiz.entity.condition.EntityFunction;
import org.apache.ofbiz.entity.condition.EntityOperator;
import org.apache.ofbiz.base.util.UtilHttp;
import org.apache.ofbiz.entity.Delegator;
import org.apache.ofbiz.base.util.Debug;
@SuppressWarnings("deprecation")
public class CustomEvents {
    public static final String module = CustomEvents.class.getName();
    private static final ResourceBundle GAADIZO_UI_LABELS = UtilProperties.getResourceBundle("GaadizoUiLabels.xml", Locale.getDefault());

    public static String autoSuggestionList(HttpServletRequest request, HttpServletResponse response) 
    {
        Locale locale = UtilHttp.getLocale(request);
        Delegator delegator = (Delegator) request.getAttribute("delegator");
        GenericValue userLogin = (GenericValue) request.getSession().getAttribute("userLogin");
        List<String> autoSuggestionList = new ArrayList<String>();

        String searchText = request.getParameter("searchText");

        if(UtilValidate.isNotEmpty(searchText))
        {
            try 
            {
                List attrExprs = new ArrayList();
                attrExprs.add(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("ownerId"), EntityOperator.EQUALS, EntityFunction.UPPER(userLogin.get("ownerId").toString())));
                attrExprs.add(EntityCondition.makeCondition(EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+searchText+"%")), EntityOperator.OR, EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+searchText+"%"))));
                //attrExprs.add(EntityCondition.makeCondition([EntityFunction.UPPER_FIELD("firstName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+searchText+"%"),EntityCondition.makeCondition(EntityFunction.UPPER_FIELD("lastName"), EntityOperator.LIKE, EntityFunction.UPPER("%"+searchText+"%")),EntityOperator.OR]));
                //attrExprs.add();
                Debug.log("=====attrExprs===43========"+attrExprs+"=================");
                List<GenericValue> personList = delegator.findList("Person", EntityCondition.makeCondition(attrExprs, EntityOperator.AND), null, null, null, true);
                Debug.log("=====personList==========="+personList.size()+"=================");
                for(GenericValue person : personList)
                {
                    Debug.log("=====personList==========="+person+"=================");
                    autoSuggestionList.add(person.get("firstName")+" "+person.get("lastName"));
                }
                Debug.log("=====personList==========="+autoSuggestionList+"=================");
                request.setAttribute("autoSuggestionList", autoSuggestionList);
                if(autoSuggestionList.size() > 0)
                {
                    request.setAttribute("response", "success");    
                }
                else
                {
                    request.setAttribute("response", "error");
                }

            } 
            catch (Exception e) 
            {
                Debug.logError(e.getMessage(), module);
            }
        }
        return "success";
    }
}