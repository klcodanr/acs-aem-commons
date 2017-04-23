<%@ page session="false" contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<cq:setContentBundle />
<div ng-controller="MainCtrl" ng-init="init();">
	<form action="${resource.path}" method="post" class="coral-Form--aligned" id="fn-acsCommons-feed-importer-form" ng-submit="save()">
		<br/><hr/><br/>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Title
			</label>
			<input type="text" class="coral-Textfield" name="./jcr:title" value="${properties['jcr:title']}"/>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Cron Trigger
			</label>
			<input type="text" class="coral-Textfield" name="./cronTrigger" value="${properties.cronTrigger}"/>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Feed URL
			</label>
			<input type="text" class="coral-Textfield" name="./feedURL" value="${properties.feedURL}"/>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Base Path
			</label>
			<input type="text" class="coral-Textfield" name="./basePath" value="${properties.basePath}"/>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Entry Name Format
			</label>
			<input type="text" class="coral-Textfield" name="./nameFormat" value="${properties.nameFormat}"/>
		</div>
	
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel" for="./stripStopWords">
				Strip Stop Words
			</label>
			<label class="coral-Checkbox">
                    <input class="coral-Checkbox-input" name="./stripStopWords" type="checkbox" checked="${properties.stripStopWords == 'true'}">
                    <span class="coral-Checkbox-checkmark"></span>
                    
                </label>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Entry Resource JSON
			</label>
			<textarea class="coral-Textfield coral-Textfield--multiline" name="./resourceJSON">${properties.resourceJSON}</textarea>
		</div>
		
		<div class="coral-Form-fieldwrapper" >
			<button class="coral-Button coral-Button--primary" >Save</button>
		</div>
		
	</form>
	<br/>
	<br/>
	<h2 class="coral-Heading coral-Heading--2">Run Import</h2>
	<br/><hr/><br/>
	
	<button class="coral-Button coral-Button--primary" data-action="${resource.path}.importfeed.json" id="run-btn" ng-click="run()">Import</button>
	<pre style="max-width:100%;overflow=scroll"><code id="run-target"></code></pre>
</div>
