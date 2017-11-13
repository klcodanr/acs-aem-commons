<%@ page session="false" contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<cq:setContentBundle />
<div class="coral-Well">
	<form action="${resource.path}" method="post" class="coral-Form--aligned" id="fn-acsCommons-table_${resource.name}" ng-submit="postValues('fn-acsCommons-table_${resource.name}')">
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Label *
			</label>
			<input type="text" class="coral-Textfield" name="./label" value="${properties.label}" required="required" />
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Field *
			</label>
			<input type="text" class="coral-Textfield" name="./field" value="${properties.field}" required="required" />
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Format *
			</label>
			<coral-select name="./format" >
				<coral-select-item <c:if test="${properties.method == 'String'}">selected</c:if>>
					String
				</coral-select-item>
				<coral-select-item <c:if test="${properties.method == 'Integer'}">selected</c:if>>
					Integer
				</coral-select-item>
				<coral-select-item <c:if test="${properties.method == 'Double'}">selected</c:if>>
					Double
				</coral-select-item>
				<coral-select-item <c:if test="${properties.method == 'Date'}">selected</c:if>>
					Date
				</coral-select-item>
				<coral-select-item <c:if test="${properties.method == 'Button'}">selected</c:if>>
					Button
				</coral-select-item>
			</coral-select>
		</div>
		
		<div class="coral-Form-fieldwrapper">
			<label class="coral-Form-fieldlabel">
				Pattern
			</label>
			<input type="text" class="coral-Textfield" name="./pattern" value="${properties.pattern}" />
			<p>
				Depending on the Format selected the pattern will be one of:
			</p>
			<ul>
				<li>
					<strong>String</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-" target="_blank">String format expression</a> with a single parameter of the value of the field.
				</li>
				<li>
					<strong>Double, Integer</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html" target="_blank">Decimal format expression</a>
				</li>
				<li>
					<strong>Date</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html" target="_blank">Simple date format expression</a>
				</li>
				<li>
					<strong>Button</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-" target="_blank">String format expression</a> with the parameters of the value of the field and the path of the item.
				</li>
			</ul>
		</div>
		
		<div class="coral-Form-fieldwrapper" >
			<button class="coral-Button coral-Button--primary">Save</button>
		</div>
	</form>
	<form action="${resource.path}" method="post" class="coral-Form--aligned" id="fn-acsCommons-remove_${resource.name}" ng-submit="postValues('fn-acsCommons-remove_${resource.name}')">
		<input type="hidden"  name=":operation" value="delete" />
		<div class="coral-Form-fieldwrapper" >
			<button class="coral-Button coral-Button--warning">Remove</button>
		</div>
	</form>
</div>
<br/>