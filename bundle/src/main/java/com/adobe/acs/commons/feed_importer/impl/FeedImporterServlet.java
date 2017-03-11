package com.adobe.acs.commons.feed_importer.impl;

import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.sling.SlingServlet;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONException;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.contentloader.ContentImporter;
import org.osgi.service.event.EventAdmin;

@SlingServlet(methods = "POST", resourceTypes = "acs-commons/components/utilities/feed-importer", selectors = "importfeed", extensions="json")
public class FeedImporterServlet extends SlingAllMethodsServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3472442461363741364L;

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private ContentImporter contentImporter;

	@Reference
	private EventAdmin eventAdmin;

	protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response)
			throws ServletException, IOException {
		FeedImporterJob feedImporterJob = new FeedImporterJob(resolverFactory, contentImporter, eventAdmin,
				request.getResource().getPath());
		JSONObject res = new JSONObject();

		FeedImportJobResult result = feedImporterJob.runJob();

		try {
			res.put("created", result.getCreated());

			res.put("failedEntries", result.getFailedEntries());
			JSONArray messages = new JSONArray();
			for (String message : result.getMessages()) {
				messages.put(message);
			}
			res.put("messages", messages);
			res.put("startDate", result.getStartDate());
			res.put("succeeded", result.getSucceeded());
			
			response.setContentType("application/json");
			response.getWriter().write(res.toString(2));
		} catch (JSONException e) {
			throw new IOException("Failed to serialize to JSON", e);
		}
		
		
	}
}
