package com.adobe.acs.commons.feed_importer.impl;

import java.util.Hashtable;

import javax.management.NotCompliantMBeanException;

import org.apache.commons.lang.ObjectUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.SlingConstants;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.scheduler.Scheduler;
import org.apache.sling.jcr.contentloader.ContentImporter;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.event.EventAdmin;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.acs.commons.ResourceServiceManager;
import com.adobe.acs.commons.ResourceServiceManagerMBean;

/**
 * Listens to changes under /etc/acs-commons/feed-importer and manages the Feed
 * Importer jobs based on the updates.
 */
@Component(immediate = true)
@Service(value = { EventHandler.class, ResourceServiceManagerMBean.class })
@Properties({
		@Property(name = EventConstants.EVENT_TOPIC, value = { SlingConstants.TOPIC_RESOURCE_ADDED,
				SlingConstants.TOPIC_RESOURCE_CHANGED, SlingConstants.TOPIC_RESOURCE_REMOVED }),
		@Property(name = "jmx.objectname", value = "com.adobe.acs.commons:type=Feed Importer"),
		@Property(name = EventConstants.EVENT_FILTER, value = "(path=/etc/acs-commons/feed-importer/*/jcr:content)") })
public class FeedImporterManager extends ResourceServiceManager implements EventHandler, ResourceServiceManagerMBean {

	public FeedImporterManager() throws NotCompliantMBeanException {
		super(ResourceServiceManagerMBean.class);
	}

	protected FeedImporterManager(Class<?> mbeanInterface) throws NotCompliantMBeanException {
		super(mbeanInterface);
	}

	private static final Logger log = LoggerFactory.getLogger(FeedImporterManager.class);

	public static final String ROOT_PATH = "/etc/acs-commons/feed-importer";

	@Reference
	private ResourceResolverFactory resolverFactory;

	@Reference
	private ContentImporter contentImporter;
	
	@Reference
	private EventAdmin eventAdmin;

	/**
	 * Creating this as a separate method to make migrating to service users
	 * easier. Callers of this method must ensure the resource resolver is
	 * closed.
	 * 
	 * @param factory
	 *            the resource resolver factory to use for getting the resource
	 *            resolver
	 * @return the resource resolver or null if there is an exception allocating
	 *         the resource resolver
	 */
	@SuppressWarnings("deprecation")
	final static ResourceResolver getResourceResolver(ResourceResolverFactory factory) {
		ResourceResolver resolver = null;
		try {
			if(factory != null){
				resolver = factory.getAdministrativeResourceResolver(null);
			}
		} catch (LoginException e) {
			log.error("Exception allocating resource resolver", e);
		}
		return resolver;
	}

	@Override
	protected ResourceResolver getResourceResolver() {
		return getResourceResolver(resolverFactory);
	}

	@Override
	public String getRootPath() {
		return ROOT_PATH;
	}

	@Override
	protected boolean isServiceUpdated(Resource config, ServiceReference reference) {
		FeedImportModel model = new FeedImportModel(config);
		return ObjectUtils.equals(model.getCronTrigger(),
				reference.getProperty(Scheduler.PROPERTY_SCHEDULER_EXPRESSION));
	}

	@Override
	protected ServiceRegistration registerServiceObject(Resource config, Hashtable<String, Object> props) {
		FeedImporterJob feedImporterJob = new FeedImporterJob(resolverFactory, contentImporter,
				eventAdmin, config.getPath());
		FeedImportModel model = new FeedImportModel(config);
		props.put(Scheduler.PROPERTY_SCHEDULER_EXPRESSION, model.getCronTrigger());
		return getBundleContext().registerService(Runnable.class.getCanonicalName(), feedImporterJob, props);
	}

}
