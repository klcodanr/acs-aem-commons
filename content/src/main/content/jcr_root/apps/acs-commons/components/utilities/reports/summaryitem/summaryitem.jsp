<%@ page session="false" contentType="text/html" pageEncoding="utf-8" %>
<%@include file="/libs/foundation/global.jsp" %>
<%@taglib prefix="sling2" uri="http://sling.apache.org/taglibs/sling" %>
<cq:setContentBundle />
<div class="coral-Well">
	<form action="${resource.path}" method="post" class="coral-Form--aligned" id="fn-acsCommons-summary_${resource.name}" ng-submit="postValues('fn-acsCommons-summary_${resource.name}')">
		
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
				Aggregation Method *
			</label>
			<coral-select name="./method" >
				<coral-select-item  value="COUNT" <c:if test="${properties.method == 'COUNT'}">selected</c:if>>
					COUNT - Calculate the number of items in the return set containing a value in the specified field.
				</coral-select-item>
				<coral-select-item value="MAX" <c:if test="${properties.method == 'MAX'}">selected</c:if>>
					MAX - The largest value from the return set. The field must be numeric.
				</coral-select-item>
				<coral-select-item value="MEDIAN" <c:if test="${properties.method == 'MEDIAN'}">selected</c:if>>
					MEDIAN - The median value from the return set. The field must be numeric.
				</coral-select-item>
				<coral-select-item value="MIN" <c:if test="${properties.method == 'MIN'}">selected</c:if>>
					MIN - The smallest value from the return set. The field must be numeric.
				</coral-select-item>
				<coral-select-item value="MODE" <c:if test="${properties.method == 'MODE'}">selected</c:if>>
					MODE - The mode value from the return set. The field must be numeric.
				</coral-select-item>
				<coral-select-item value="SUM" <c:if test="${properties.method == 'SUM'}">selected</c:if>>
					SUM - The sum of the values for the field from the return set. The field must be numeric.
				</coral-select-item>
				<coral-select-item value="UNIQUE" <c:if test="${properties.method == 'UNQIUE'}">selected</c:if>>
					UNIQUE - The unique values for the field from the return set.
				</coral-select-item>
			</coral-select>
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
					<strong>String</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/lang/String.html#format-java.lang.String-java.lang.Object...-" target="_blank">String format expression</a> with a single parameter of the value of the summary.
				</li>
				<li>
					<strong>Double, Integer</strong> - A <a href="https://docs.oracle.com/javase/8/docs/api/java/text/DecimalFormat.html" target="_blank">Decimal format expression</a>
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