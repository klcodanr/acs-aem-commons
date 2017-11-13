<%--
  #%L
  ACS AEM Commons Package
  %%
  Copyright (C) 2013 Adobe
  %%
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
  #L%
  --%><%
%><%@page session="false" %><%
%><%@include file="/libs/granite/ui/global.jsp"%><%
    String suffix = slingRequest.getRequestPathInfo().getSuffix();
    String url = "/apps/acs-commons/content/projects/dashboard/default/reports/.html" + resource.getPath() + "?project=" + suffix;
    String reportsLink = xssAPI.getValidHref(url);

    request.setAttribute("reportsLink", reportsLink);
    request.setAttribute("projectLinkResource", resource);
%>
<sling:include path="/apps/acs-commons/content/projects/dashboard/default/reports/jcr:content"/><%
    request.setAttribute("projectLinkResource", null);
%>