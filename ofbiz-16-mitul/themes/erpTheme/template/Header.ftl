<#assign externalKeyParam = "&amp;externalLoginKey=" + requestAttributes.externalLoginKey!>

<#if (requestAttributes.person)??><#assign person = requestAttributes.person></#if>
<#if (requestAttributes.partyGroup)??><#assign partyGroup = requestAttributes.partyGroup></#if>
<#assign docLangAttr = locale.toString()?replace("_", "-")>
<#assign langDir = "ltr">
<#if "ar.iw"?contains(docLangAttr?substring(0, 2))>
    <#assign langDir = "rtl">
</#if>
<html lang="${docLangAttr}" dir="${langDir}" xmlns="http://www.w3.org/1999/xhtml">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
	<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" />
    <title>Acme ERP: <#if (titleProperty)?has_content>${uiLabelMap[titleProperty!]}<#else>${title!}</#if></title>
    <#if layoutSettings.shortcutIcon?has_content>
      <#assign shortcutIcon = layoutSettings.shortcutIcon/>
    <#elseif layoutSettings.VT_SHORTCUT_ICON?has_content>
      <#assign shortcutIcon = layoutSettings.VT_SHORTCUT_ICON.get(0)/>
    </#if>
    <#if shortcutIcon?has_content>
      <link rel="shortcut icon" href="<@ofbizContentUrl>${StringUtil.wrapString(shortcutIcon!)}</@ofbizContentUrl>" />
    </#if>
    
    <#if layoutSettings.VT_HDR_JAVASCRIPT?has_content>
        <#list layoutSettings.VT_HDR_JAVASCRIPT as javaScript>
            <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#list>
    </#if>
    <#if layoutSettings.javaScripts?has_content>
      <#--layoutSettings.javaScripts is a list of java scripts. -->
      <#-- use a Set to make sure each javascript is declared only once, but iterate the list to maintain the correct order -->
      <#assign javaScriptsSet = Static["org.apache.ofbiz.base.util.UtilMisc"].toSet(layoutSettings.javaScripts)/>
      <#list layoutSettings.javaScripts as javaScript>
        <#if javaScriptsSet.contains(javaScript)>
          <#assign nothing = javaScriptsSet.remove(javaScript)/>
          <script src="<@ofbizContentUrl>${StringUtil.wrapString(javaScript)}</@ofbizContentUrl>" type="text/javascript"></script>
        </#if>
      </#list>
    </#if>

    <#if layoutSettings.styleSheets?has_content>
        <#--layoutSettings.styleSheets is a list of style sheets. So, you can have a user-specified "main" style sheet, AND a component style sheet.-->
        <#list layoutSettings.styleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_STYLESHEET?has_content>
        <#list layoutSettings.VT_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.rtlStyleSheets?has_content && langDir == "rtl">
        <#--layoutSettings.rtlStyleSheets is a list of rtl style sheets.-->
        <#list layoutSettings.rtlStyleSheets as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_RTL_STYLESHEET?has_content && langDir == "rtl">
        <#list layoutSettings.VT_RTL_STYLESHEET as styleSheet>
            <link rel="stylesheet" href="<@ofbizContentUrl>${StringUtil.wrapString(styleSheet)}</@ofbizContentUrl>" type="text/css"/>
        </#list>
    </#if>
    <#if layoutSettings.VT_EXTRA_HEAD?has_content>
        <#list layoutSettings.VT_EXTRA_HEAD as extraHead>
            ${extraHead}
        </#list>
    </#if>
    <#if layoutSettings.WEB_ANALYTICS?has_content>
      <script language="JavaScript" type="text/javascript">
        <#list layoutSettings.WEB_ANALYTICS as webAnalyticsConfig>
          ${StringUtil.wrapString(webAnalyticsConfig.webAnalyticsCode!)}
        </#list>
      </script>
    </#if>
</head>
<#if layoutSettings.headerImageLinkUrl??>
  <#assign logoLinkURL = "${layoutSettings.headerImageLinkUrl}">
<#else>
  <#assign logoLinkURL = "${layoutSettings.commonHeaderImageLinkUrl}">
</#if>
<#assign organizationLogoLinkURL = "${layoutSettings.organizationLogoLinkUrl!}">

<body>
  <div id="wait-spinner" style="display:none">
    <div id="wait-spinner-image"></div>
  </div>
  <div class="page-container">
    <div class="hidden">
      <a href="#column-container" title="${uiLabelMap.CommonSkipNavigation}" accesskey="2">
        ${uiLabelMap.CommonSkipNavigation}
      </a>
    </div>
    <#-- if (userPreferences.COMPACT_HEADER)?default("N") == "N">
      <div id="masthead">
        <ul>
          <#if layoutSettings.headerImageUrl??>
            <#assign headerImageUrl = layoutSettings.headerImageUrl>
          <#elseif layoutSettings.commonHeaderImageUrl??>
            <#assign headerImageUrl = layoutSettings.commonHeaderImageUrl>
          <#elseif layoutSettings.VT_HDR_IMAGE_URL??>
            <#assign headerImageUrl = layoutSettings.VT_HDR_IMAGE_URL.get(0)>
          </#if>
          <#if headerImageUrl??>
            <#if organizationLogoLinkURL?has_content>
                <li id="org-logo-area"><a href="<@ofbizUrl>${logoLinkURL}</@ofbizUrl>"><img alt="${layoutSettings.companyName}" src="<@ofbizContentUrl>${StringUtil.wrapString(organizationLogoLinkURL)}</@ofbizContentUrl>"></a></li>
                <#else>
                <li id="logo-area"><a href="<@ofbizUrl>${logoLinkURL}</@ofbizUrl>" title="${layoutSettings.companyName!}"></a></li>
            </#if>
          </#if>
          <#if layoutSettings.middleTopMessage1?has_content && layoutSettings.middleTopMessage1 != " ">
            <li class="last-system-msg">
                <a href="${StringUtil.wrapString(layoutSettings.middleTopLink1!)}">${layoutSettings.middleTopMessage1!}</a><br/>
                <a href="${StringUtil.wrapString(layoutSettings.middleTopLink2!)}">${layoutSettings.middleTopMessage2!}</a><br/>
                <a href="${StringUtil.wrapString(layoutSettings.middleTopLink3!)}">${layoutSettings.middleTopMessage3!}</a>
            </li>
          </#if>
        </ul>
        <br class="clear" />
      </div>
    </#if -->
<#-- ==============================================================================================  -->
<#if userLogin?exists && userLogin?has_content>
<header>
<section class="container-fluid">
<div class="row">
<div class="col-sm-3"><a href="<@ofbizUrl>main</@ofbizUrl>"><img src="/erpTheme/images/erp-logo.png" alt=""></a></div>
<div class="col-sm-4 col-sm-offset-5">
<#-- div class="col-sm-10">
<form action="" method="">
<button type="submit" name=""><i class="fa fa-search"></i></button>
<input type="text" name="" class="form-control" placeholder="Search">
</form>
</div -->

<div class="col-sm-2"><a href="#"><i class="fa fa-bars"></i></a></div>

</div>
</div>
<#-- div class="col-md-4 col-sm-5"><ul class="navbar-nav">                        
                        <li class="nav-item dropdown u-pro">
                            <a class="nav-link dropdown-toggle waves-effect waves-dark profile-pic" href="http://gc-technology.com/design/erp/dashboard.html#" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><img src="./Welcome_files/user.jpg" alt="user" class=""> <span class="hidden-md-down">Mark &nbsp;<i class="fa fa-angle-down"></i></span> </a>
                            <div class="dropdown-menu dropdown-menu-right animated flipInY">
                                <a href="http://gc-technology.com/design/erp/dashboard.html#" class="dropdown-item"><i class="ti-user"></i> My Profile</a>
                                <a href="http://gc-technology.com/design/erp/dashboard.html#" class="dropdown-item"><i class="ti-email"></i> Inbox</a>
                                <a href="http://gc-technology.com/design/erp/dashboard.html#" class="dropdown-item"><i class="ti-settings"></i> Account Setting</a>
                                <a href="http://gc-technology.com/design/erp/login.html" class="dropdown-item"><i class="fa fa-power-off"></i> Logout</a>                            </div>
                        </li>
                    </ul></div -->
</section>
</header>
<#-- nav class="navbar navbar-inverse navbar-fixed-top">
            <div class="mobile-only-brand pull-left">
                <div class="nav-header pull-left">
                    <div class="logo-wrap">
                        <a href="<@ofbizUrl>main</@ofbizUrl>">
                            <img class="brand-img" src="/erp/css/dist/img/logo.png" alt="brand"/>
                            <span class="brand-text">ERP</span>
                        </a>
                    </div>
                </div>  
                <a id="toggle_nav_btn" class="toggle-left-nav-btn inline-block ml-20 pull-left" href="javascript:void(0);"><i class="zmdi zmdi-menu"></i></a>
                <a id="toggle_mobile_search" data-toggle="collapse" data-target="#search_form" class="mobile-only-view" href="javascript:void(0);"><i class="zmdi zmdi-search"></i></a>
                <a id="toggle_mobile_nav" class="mobile-only-view" href="javascript:void(0);"><i class="zmdi zmdi-more"></i></a>
                <form id="search_form" role="search" class="top-nav-search collapse pull-left">
                    <div class="input-group">
                        <input type="text" name="example-input1-group2" class="form-control" placeholder="Search">
                        <span class="input-group-btn">
                        <button type="button" class="btn  btn-default"  data-target="#search_form" data-toggle="collapse" aria-label="Close" aria-expanded="true"><i class="zmdi zmdi-search"></i></button>
                        </span>
                    </div>
                </form>
            </div>
            <div id="mobile_only_nav" class="mobile-only-nav pull-right">
                <ul class="nav navbar-right top-nav pull-right">
                    <li>
                        <a id="open_right_sidebar" href="#"><i class="zmdi zmdi-settings top-nav-icon"></i></a>
                    </li>
                    
                    <li class="dropdown alert-drp">
                        <a href="#" class="dropdown-toggle" data-toggle="dropdown"><i class="zmdi zmdi-notifications top-nav-icon"></i><span class="top-nav-icon-badge">5</span></a>
                        <ul  class="dropdown-menu alert-dropdown" data-dropdown-in="bounceIn" data-dropdown-out="bounceOut">
                            <li>
                                <div class="notification-box-head-wrap">
                                    <span class="notification-box-head pull-left inline-block">notifications</span>
                                    <a class="txt-danger pull-right clear-notifications inline-block" href="javascript:void(0)"> clear all </a>
                                    <div class="clearfix"></div>
                                    <hr class="light-grey-hr ma-0"/>
                                </div>
                            </li>
                            <li>
                                <div class="streamline message-nicescroll-bar">
                                    <div class="sl-item">
                                        <a href="javascript:void(0)">
                                            <div class="icon bg-green">
                                                <i class="zmdi zmdi-flag"></i>
                                            </div>
                                            <div class="sl-content">
                                                <span class="inline-block capitalize-font  pull-left truncate head-notifications">
                                                New subscription created</span>
                                                <span class="inline-block font-11  pull-right notifications-time">2pm</span>
                                                <div class="clearfix"></div>
                                                <p class="truncate">Your customer subscribed for the basic plan. The customer will pay $25 per month.</p>
                                            </div>
                                        </a>    
                                    </div>
                                    <hr class="light-grey-hr ma-0"/>
                                    <div class="sl-item">
                                        <a href="javascript:void(0)">
                                            <div class="icon bg-yellow">
                                                <i class="zmdi zmdi-trending-down"></i>
                                            </div>
                                            <div class="sl-content">
                                                <span class="inline-block capitalize-font  pull-left truncate head-notifications txt-warning">Server #2 not responding</span>
                                                <span class="inline-block font-11 pull-right notifications-time">1pm</span>
                                                <div class="clearfix"></div>
                                                <p class="truncate">Some technical error occurred needs to be resolved.</p>
                                            </div>
                                        </a>    
                                    </div>
                                    <hr class="light-grey-hr ma-0"/>
                                    <div class="sl-item">
                                        <a href="javascript:void(0)">
                                            <div class="icon bg-blue">
                                                <i class="zmdi zmdi-email"></i>
                                            </div>
                                            <div class="sl-content">
                                                <span class="inline-block capitalize-font  pull-left truncate head-notifications">2 new messages</span>
                                                <span class="inline-block font-11  pull-right notifications-time">4pm</span>
                                                <div class="clearfix"></div>
                                                <p class="truncate"> The last payment for your G Suite Basic subscription failed.</p>
                                            </div>
                                        </a>    
                                    </div>
                                    <hr class="light-grey-hr ma-0"/>
                                    <div class="sl-item">
                                        <a href="javascript:void(0)">
                                            <div class="sl-avatar">
                                                <img class="img-responsive" src="/erp/css/dist/img/avatar.jpg" alt="avatar"/>
                                            </div>
                                            <div class="sl-content">
                                                <span class="inline-block capitalize-font  pull-left truncate head-notifications">Sandy Doe</span>
                                                <span class="inline-block font-11  pull-right notifications-time">1pm</span>
                                                <div class="clearfix"></div>
                                                <p class="truncate">Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit</p>
                                            </div>
                                        </a>    
                                    </div>
                                    <hr class="light-grey-hr ma-0"/>
                                    <div class="sl-item">
                                        <a href="javascript:void(0)">
                                            <div class="icon bg-red">
                                                <i class="zmdi zmdi-storage"></i>
                                            </div>
                                            <div class="sl-content">
                                                <span class="inline-block capitalize-font  pull-left truncate head-notifications txt-danger">99% server space occupied.</span>
                                                <span class="inline-block font-11  pull-right notifications-time">1pm</span>
                                                <div class="clearfix"></div>
                                                <p class="truncate">consectetur, adipisci velit.</p>
                                            </div>
                                        </a>    
                                    </div>
                                </div>
                            </li>
                            <li>
                                <div class="notification-box-bottom-wrap">
                                    <hr class="light-grey-hr ma-0"/>
                                    <a class="block text-center read-all" href="javascript:void(0)"> read all </a>
                                    <div class="clearfix"></div>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <li class="dropdown auth-drp">
                        <a href="#" class="dropdown-toggle pr-0" data-toggle="dropdown"><img src="/erp/css/dist/img/user1.png" alt="user_auth" class="user-auth-img img-circle"/><span class="user-online-status"></span></a>
                        <ul class="dropdown-menu user-auth-dropdown" data-dropdown-in="flipInX" data-dropdown-out="flipOutX">
                            <li>
                                <a href="profile.html"><i class="zmdi zmdi-account"></i><span>Profile</span></a>
                            </li>
                            <li>
                                <a href="#"><i class="zmdi zmdi-card"></i><span>my balance</span></a>
                            </li>
                            <li>
                                <a href="inbox.html"><i class="zmdi zmdi-email"></i><span>Inbox</span></a>
                            </li>
                            <li>
                                <a href="#"><i class="zmdi zmdi-settings"></i><span>Settings</span></a>
                            </li>
                            <li class="divider"></li>
                            <li class="sub-menu show-on-hover">
                                <a href="#" class="dropdown-toggle pr-0 level-2-drp"><i class="zmdi zmdi-check text-success"></i> available</a>
                                <ul class="dropdown-menu open-left-side">
                                    <li>
                                        <a href="#"><i class="zmdi zmdi-check text-success"></i><span>available</span></a>
                                    </li>
                                    <li>
                                        <a href="#"><i class="zmdi zmdi-circle-o text-warning"></i><span>busy</span></a>
                                    </li>
                                    <li>
                                        <a href="#"><i class="zmdi zmdi-minus-circle-outline text-danger"></i><span>offline</span></a>
                                    </li>
                                </ul>   
                            </li>
                            <li class="divider"></li>
                            <li>
                                <a href="<@ofbizUrl>logout</@ofbizUrl>"><i class="zmdi zmdi-power"></i><span>Log Out</span></a>
                            </li>
                        </ul>
                    </li>
                </ul>
            </div>  
        </nav -->
</#if>
