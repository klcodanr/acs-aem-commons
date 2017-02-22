/*
 * #%L
 * ACS AEM Commons Bundle
 * %%
 * Copyright (C) 2017 - Adobe
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
package com.adobe.acs.commons.feed_importer.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;

//So interesting note, you can't seem to use Sling models in service
// activators...
/**
 * Model for retrieving Feed Import configurations
 */
public class FeedImportModel {

	private ValueMap properties;

	public FeedImportModel(Resource resource) {
		properties = resource.getValueMap();
	}

	public String getBasePath(){
		return properties.get("basePath", String.class);
	}

	public String getCronTrigger() {
		return properties.get("cronTrigger", String.class);
	}

	public URL getFeedURL() throws MalformedURLException{
		return new URL(properties.get("feedURL", String.class));
	}
	
	public String getNameFormat() {
		return properties.get("nameFormat", String.class);
	}

	public String getResourceJSON(){
		return properties.get("resourceJSON", String.class);
	}
	
	public String getTitle() {
		return properties.get("jcr:content", String.class);
	}

	@Override
	public String toString() {
		return "FeedImportModel [properties=" + properties + "]";
	}

}
