/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2013 Adobe
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.adobe.acs.commons.reports.models;

import java.util.HashMap;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.day.cq.commons.jcr.JcrConstants;

/**
 * A Sling Model for fetching the report from a request suffix.
 */
@Model(adaptables = SlingHttpServletRequest.class)
public class ReportPageModel {

	private static final Logger log = LoggerFactory.getLogger(ReportPageModel.class);

	@Self
	private SlingHttpServletRequest request;

	/**
	 * Gets the ReportModel from the suffix of the request.
	 * 
	 * @return the report model
	 */
	public ReportModel getReport() {
		log.trace("getReport");
		Resource suffixResource = request.getRequestPathInfo().getSuffixResource();
		if (suffixResource != null && suffixResource.getChild(JcrConstants.JCR_CONTENT) != null
				&& suffixResource.getChild(JcrConstants.JCR_CONTENT).adaptTo(ReportModel.class) != null) {
			log.debug("Fetching report report from {}", suffixResource);
			ReportModel report = suffixResource.getChild(JcrConstants.JCR_CONTENT).adaptTo(ReportModel.class);
			Map<String, String> requestParams = new HashMap<String, String>();
			for (String name : request.getParameterMap().keySet()) {
				requestParams.put(name, request.getParameter(name));
			}
			report.setRequestParams(requestParams);
			report.init();
			return report;
		} else {
			log.warn("No report or resource found for suffix: {}", request.getRequestPathInfo().getSuffix());
			return null;
		}
	}
}
