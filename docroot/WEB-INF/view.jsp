<%@page contentType="text/html"%>
<%@page pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>


<liferay-ui:success key="not-configured" message="Portlet not configured. Edit portal-ext.properties." />

<span>
<portlet:actionURL name="getAccessToken" var="getAccessTokenUrl" />
<%=getAccessTokenUrl.toString() %>
</span>