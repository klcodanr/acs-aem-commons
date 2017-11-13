<%@ page session="false" contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<cq:setContentBundle />
<div ng-controller="MainCtrl" ng-init="init();">
	<section>
		<form action="${resource.path}" method="post" class="coral-Form--aligned" id="fn-acsCommons-cr-form" ng-submit="postValues('fn-acsCommons-cr-form')">
			<br/><hr/><br/>
			
			<div class="coral-Form-fieldwrapper">
				<label class="coral-Form-fieldlabel">
					Report Title *
				</label>
				<input type="text" class="coral-Textfield" name="./jcr:title" value="${properties['jcr:title']}"  required="required"/>
			</div>
			
			<div class="coral-Form-fieldwrapper">
				<label class="coral-Form-fieldlabel">
					Report Description
				</label>
				<textarea is="coral-textarea" class="coral-Textarea" name="./jcr:description">${properties['jcr:description']}</textarea>
			</div>
			
			<div class="coral-Form-fieldwrapper">
				<label class="coral-Form-fieldlabel">
					Query *
				</label>
				<textarea is="coral-textarea" class="coral-Textarea" name="./query" required="required">${properties.query}</textarea>
			</div>
			
			<div class="coral-Form-fieldwrapper">
				<label class="coral-Form-fieldlabel">
					Query Language *
				</label>
				<coral-select name="./queryLanguage" >
					<coral-select-item  <c:if test="${properties.method == 'JCR-SQL2'}">selected</c:if>>
						JCR-SQL2
					</coral-select-item>
					<coral-select-item  <c:if test="${properties.method == 'JCR-JQOM'}">selected</c:if>>
						JCR-JQOM
					</coral-select-item>
					<coral-select-item  <c:if test="${properties.method == 'sql'}">selected</c:if>>
						sql
					</coral-select-item>
					<coral-select-item  <c:if test="${properties.method == 'xpath'}">selected</c:if>>
						xpath
					</coral-select-item>
				</coral-select>
			</div>
			
			<div class="coral-Form-fieldwrapper" >
				<button class="coral-Button coral-Button--primary"><fmt:message key="Save" /></button>
			</div>
			
		</form>
	</section>
	<br/><hr/><br/>
	<section>
		<h2 class="coral-Heading coral-Heading--2">
			<fmt:message key="Configure Summary" />
		</h2>
		<c:set var="summaryParent" value="${sling2:getRelativeResource(resource, 'summary')}" />
		<c:forEach var="summaryItems" items="${sling2:listChildren(summaryParent)}">
	    	<cq:include path="${summaryItems.path}" resourceType="${summaryItems.resourceType}" />
		</c:forEach>
		<form action="${resource.path}/summary/*" method="post" class="coral-Form--aligned" id="fn-acsCommons-add-summaryitem" ng-submit="postValues('fn-acsCommons-add-summaryitem')">
			<input type="hidden"  name="sling:resourceType" value="acs-commons/components/utilities/reports/summaryitem" />
			<div class="coral-Form-fieldwrapper" >
				<button class="coral-Button coral-Button--primary">+ <fmt:message key="Summary Item" /></button>
			</div>
		</form>
	</section>
	<br/><hr/><br/>
	<section>
		<h2 class="coral-Heading coral-Heading--2">
			<fmt:message key="Configure Table" />
		</h2>
		<c:set var="tableParent" value="${sling2:getRelativeResource(resource, 'table')}" />
		<c:forEach var="tableItem" items="${sling2:listChildren(tableParent)}">
	    	<cq:include path="${tableItem.path}" resourceType="${tableItem.resourceType}" />
		</c:forEach>
		<form action="${resource.path}/table/*" method="post" class="coral-Form--aligned" id="fn-acsCommons-add-tableitem" ng-submit="postValues('fn-acsCommons-add-tableitem')">
			<input type="hidden"  name="sling:resourceType" value="acs-commons/components/utilities/reports/tableitem" />
			<div class="coral-Form-fieldwrapper" >
				<button class="coral-Button coral-Button--primary">+ <fmt:message key="Table Item" /></button>
			</div>
		</form>
	</section>
</div>
