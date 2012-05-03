<%@ taglib uri="http://java.sun.com/portlet_2_0" prefix="portlet" %>
<%@ taglib uri="http://liferay.com/tld/ui" prefix="liferay-ui" %>
<%@ taglib uri="http://liferay.com/tld/portlet" prefix="liferay-portlet" %>

<portlet:defineObjects />

<liferay-ui:error message="message" key="key"/>

<%

String consumerKey = renderRequest.getAttribute("consumerKey").toString();

%>

<liferay-portlet:actionURL var="configurationURL" portletConfiguration="true"  />

<form action="<%=configurationURL %>" method="POST" name="<portlet:namespace />fm">

Consumer Key: <input type="text" name="consumerKey" value="<%=consumerKey %>"><br/>

<input type="submit" value="<liferay-ui:message key="save" />" />

</form>
