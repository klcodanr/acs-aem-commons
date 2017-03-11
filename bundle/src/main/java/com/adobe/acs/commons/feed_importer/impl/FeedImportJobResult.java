package com.adobe.acs.commons.feed_importer.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.models.annotations.Model;

@Model(adaptables = Resource.class)
public class FeedImportJobResult {

	@Inject
	private Long created = 0L;

	@Inject
	private Long failedEntries = 0L;

	@Inject
	private List<String> messages = new ArrayList<String>();

	@Inject
	private Date startDate;

	@Inject
	private Boolean succeeded = true;

	public Long getCreated() {
		return created;
	}

	public Long getFailedEntries() {
		return failedEntries;
	}

	public List<String> getMessages() {
		return messages;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Boolean getSucceeded() {
		return succeeded;
	}

	public void save(Resource resource) throws PersistenceException {
		ModifiableValueMap mvm = resource.adaptTo(ModifiableValueMap.class);
		mvm.put("created", created);
		mvm.put("failedEntries", failedEntries);
		mvm.put("messages", messages);
		mvm.put("startDate", startDate);
		mvm.put("succeeded", succeeded);
		resource.getResourceResolver().commit();
	}

	public void setCreated(Long created) {
		this.created = created;
	}

	public void setFailedEntries(Long failedEntries) {
		this.failedEntries = failedEntries;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setSucceeded(Boolean succeeded) {
		this.succeeded = succeeded;
	}

	@Override
	public String toString() {
		return "FeedImportJobResult [created=" + created + ", failedEntries=" + failedEntries + ", messages=" + messages
				+ ", startDate=" + startDate + ", succeeded=" + succeeded + "]";
	}

}
