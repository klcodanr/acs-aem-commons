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

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.Self;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A model for representing a report.
 */
@Model(adaptables=Resource.class)
public class ReportModel {

	private static final Logger log = LoggerFactory.getLogger(ReportModel.class);
	
	@Inject
	@Named("jcr:description")
	private String description;

	@Inject
	@Named("query")
	private String query;

	@Inject
	@Named("queryLanguage")
	private String queryLanguage;

	private Map<String,String> requestParams;
	
	@Inject
	@Named("jcr:title")
	private String title;
	
	@Inject
	private List<SummaryItemModel> summary;
	
	@Self
	private Resource resource;

	public String getDescription() {
		return description;
	}

	public String getQuery() {
		return query;
	}

	public String getQueryLanguage() {
		return queryLanguage;
	}

	public Map<String, String> getRequestParams() {
		return requestParams;
	}

	public String getTitle() {
		return title;
	}

	public void setRequestParams(Map<String, String> requestParams) {
		this.requestParams = requestParams;
	}
	
	public void init(){
		
		log.trace("init");
		
		StrSubstitutor sub = new StrSubstitutor(requestParams);
		String queryStr = sub.replace(this.query);
		log.debug("Executing query {} with language {}",queryStr, queryLanguage);
		
		Iterator<Resource> result = resource.getResourceResolver().findResources(queryStr, queryLanguage);
		while(result.hasNext()){
			Resource item = result.next();
			for(SummaryItemModel si : summary){
				si.update(item);
			}
		}
		
	}
	
	public List<SummaryItemModel> getSummaryItems(){
		return summary;
	}
}
