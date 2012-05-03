<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>

<%@ page import="javax.portlet.*"%>

<portlet:defineObjects />
<portlet:actionURL name="getAccessToken" var="getAccessTokenUrl" />
<portlet:actionURL name="oauthLogin" var="oauthLoginUrl" />

<%
oauthLoginUrl = oauthLoginUrl.replace("&", "|");
Object userInfo = renderRequest.getAttribute(PortletRequest.USER_INFO);
%>

<a href="${getAccessTokenUrl}&oauthLoginUrl=<%=oauthLoginUrl %>">Login through Google OAuth</a>
