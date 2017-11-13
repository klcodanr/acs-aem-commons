<%@ page session="false" contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<cq:setContentBundle />
<c:set var="report" value="${sling2:adaptTo(slingRequest,'com.adobe.acs.commons.reports.models.ReportPageModel').report}" scope="request" />
<h2 class="coral-Heading coral-Heading--2">${report.title}</h2>
<p>
	${report.description}
</p>
<dl>

</dl>