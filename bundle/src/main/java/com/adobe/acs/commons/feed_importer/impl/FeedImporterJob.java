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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.script.ScriptException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.commons.json.JSONArray;
import org.apache.sling.commons.json.JSONObject;
import org.apache.sling.jcr.contentloader.ContentImportListener;
import org.apache.sling.jcr.contentloader.ContentImporter;
import org.apache.sling.jcr.contentloader.ImportOptions;
import org.apache.sling.servlets.post.Modification;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventAdmin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndCategory;
import com.sun.syndication.feed.synd.SyndContent;
import com.sun.syndication.feed.synd.SyndEnclosure;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.feed.synd.SyndImage;
import com.sun.syndication.feed.synd.SyndPerson;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Job for importing feed items using ROME.
 */
public class FeedImporterJob implements Runnable {

	public enum TYPE {
		DATE, LIST, STRING;
	}

	private static final Map<String, TYPE> entryMethodMappings = new HashMap<String, TYPE>() {
		private static final long serialVersionUID = 1L;
		{
			put("getAuthor", TYPE.STRING);
			put("getAuthors", TYPE.LIST);
			put("getCategories", TYPE.LIST);
			put("getContents", TYPE.STRING);
			put("getContributors", TYPE.LIST);
			put("getCopyright", TYPE.STRING);
			put("getDescription", TYPE.STRING);
			put("getEnclosures", TYPE.STRING);
			put("getLink", TYPE.STRING);
			put("getLinks", TYPE.LIST);
			put("getPublishedDate", TYPE.DATE);
			put("getTitle", TYPE.STRING);
			put("getTitleEx", TYPE.STRING);
			put("getUpdatedDate", TYPE.DATE);
			put("getUri", TYPE.STRING);
		}
	};

	private static final Map<String, TYPE> feedMethodMappings = new HashMap<String, TYPE>() {
		private static final long serialVersionUID = 1L;
		{
			put("getAuthor", TYPE.STRING);
			put("getAuthors", TYPE.LIST);
			put("getCategories", TYPE.LIST);
			put("getContributors", TYPE.LIST);
			put("getCopyright", TYPE.STRING);
			put("getDescription", TYPE.STRING);
			put("getEncoding", TYPE.STRING);
			put("getImage", TYPE.STRING);
			put("getLanguage", TYPE.STRING);
			put("getLink", TYPE.STRING);
			put("getLinks", TYPE.LIST);
			put("getPublishedDate", TYPE.DATE);
			put("getTitle", TYPE.STRING);
			put("getTitleEx", TYPE.STRING);
			put("getUri", TYPE.STRING);
		}
	};

	private static final Logger log = LoggerFactory.getLogger(FeedImporterJob.class);
	public static final String OSGI_EVENT_CONFIG_PATH_PARAM = "configPath";
	public static final String OSGI_EVENT_ENTRY_CREATED_TOPIC = "com/adobe/acs/commons/feed_importer/ENTRY_CREATED_IMPORTED";
	public static final String OSGI_EVENT_ENTRY_PATH_PARAM = "entryPath";
	public static final String OSGI_EVENT_FEED_FAILED_TOPIC = "com/adobe/acs/commons/feed_importer/FEED_FAILED";
	public static final String OSGI_EVENT_FEED_IMPORTED_TOPIC = "com/adobe/acs/commons/feed_importer/FEED_IMPORTED";

	public static final String OSGI_EVENT_FEED_PATH_PARAM = "feedPath";

	private final String configurationPath;

	private final ContentImporter contentImporter;

	private final EventAdmin eventAdmin;

	private final ResourceResolverFactory resolverFactory;

	public FeedImporterJob(ResourceResolverFactory resolverFactory, ContentImporter contentImporter,
			EventAdmin eventAdmin, String configurationPath) {
		this.resolverFactory = resolverFactory;
		this.configurationPath = configurationPath;
		this.contentImporter = contentImporter;
		this.eventAdmin = eventAdmin;
	}

	private SyndFeed downloadFeed(ResourceResolver resolver, FeedImportModel model)
			throws IllegalArgumentException, MalformedURLException, FeedException, IOException {
		SyndFeedInput input = new SyndFeedInput();
		SyndFeed feed = input.build(new XmlReader(model.getFeedURL()));
		return feed;
	}

	private void fireEvent(final String topic, final String feedPath, final String entryPath) {
		final Event event = new Event(topic, new HashMap<String, String>() {
			private static final long serialVersionUID = 1L;
			{
				put(OSGI_EVENT_CONFIG_PATH_PARAM, configurationPath);
				put(OSGI_EVENT_FEED_PATH_PARAM, feedPath);
				if (entryPath != null) {
					put(OSGI_EVENT_ENTRY_PATH_PARAM, entryPath);
				}
			}
		});
		eventAdmin.postEvent(event);
	}

	@SuppressWarnings("unchecked")
	private void importFeed(ResourceResolver resolver, FeedImportModel model, SyndFeed feed)
			throws ScriptException, UnsupportedEncodingException, RepositoryException, IOException {
		log.trace("importFeed");

		Resource parent = resolver.getResource(model.getBasePath());
		if (parent == null) {
			throw new RepositoryException("Parent resource " + model.getBasePath() + " not found");
		} else {
			log.debug("Creating feed entries under {}", parent.getPath());
		}

		Map<String, String> feedData = toMap(feed);

		for (SyndEntry entry : (List<SyndEntry>) feed.getEntries()) {

			log.trace("Handing feed entry {}", entry);
			StringBuilder sb = new StringBuilder();
			Formatter formatter = null;
			InputStream is = null;
			try {
				formatter = new Formatter(sb);
				formatter.format(model.getNameFormat(), entry.getPublishedDate(), entry.getTitle());

				log.debug("Formatting name {} with date {} and title {}",
						new Object[] { model.getNameFormat(), entry.getPublishedDate(), entry.getTitle() });
				String name = sb.toString().toLowerCase().replaceAll("[^\\w\\d_]+", "-");

				if (parent.getChild(name) == null) {
					log.debug("Creating feed entry with name {}", name);

					Map<String, String> entryData = toMap(entry);
					entryData.putAll(feedData);
					log.trace("Using entries: {}", entryData);

					StrSubstitutor sub = new StrSubstitutor(entryData);
					String json = sub.replace(model.getResourceJSON());
					is = new ByteArrayInputStream(json.getBytes("UTF-8"));
					log.trace("Generated Content JSON: {}", json);

					final List<Modification> changes = new ArrayList<Modification>();
					contentImporter.importContent(parent.adaptTo(Node.class), name, is,  new ImportOptions() {

                        @Override
                        public boolean isCheckin() {
                            return false;
                        }

						@Override
						public boolean isAutoCheckout() {
							return false;
						}

						@Override
                        public boolean isIgnoredImportProvider(
                                String extension) {
                            return false;
                        }

                        @Override
                        public boolean isOverwrite() {
                            return false;
                        }

                        /* (non-Javadoc)
                         * @see org.apache.sling.jcr.contentloader.ImportOptions#isPropertyOverwrite()
                         */
                        @Override
                        public boolean isPropertyOverwrite() {
                            return false;
                        }
                    },
                    new ContentImportListener() {

                        public void onReorder(String orderedPath, String beforeSibbling) {
                            changes.add(Modification.onOrder(orderedPath, beforeSibbling));
                        }

                        public void onMove(String srcPath, String destPath) {
                            changes.add(Modification.onMoved(srcPath, destPath));
                        }

                        public void onModify(String srcPath) {
                            changes.add(Modification.onModified(srcPath));
                        }

                        public void onDelete(String srcPath) {
                            changes.add(Modification.onDeleted(srcPath));
                        }

                        public void onCreate(String srcPath) {
                            changes.add(Modification.onCreated(srcPath));
                        }

                        public void onCopy(String srcPath, String destPath) {
                            changes.add(Modification.onCopied(srcPath, destPath));
                        }

                        public void onCheckin(String srcPath) {
                            changes.add(Modification.onCheckin(srcPath));
                        }
                        public void onCheckout(String srcPath) {
                            changes.add(Modification.onCheckout(srcPath));
                        }
                    });
					
					log.trace("Changes performed: {}",changes);
					
					log.debug("Entry {}/{} created successfully", new Object[] { parent.getPath(), name });
					fireEvent(OSGI_EVENT_ENTRY_CREATED_TOPIC, model.getBasePath(), parent.getPath() + "/" + name);
				} else {
					log.debug("Feed entry with name {} already exists, skipping...", name);
				}
			} catch (Exception e) {
				log.warn("Excepting handling feed entry", e);
			} finally {
				IOUtils.closeQuietly(is);
				if (formatter != null) {
					formatter.close();
				}
			}

		}
	}

	@Override
	public void run() {
		log.info("run");
		ResourceResolver resolver = null;
		try {
			resolver = FeedImporterManager.getResourceResolver(resolverFactory);

			log.debug("Loading configuration from {}", configurationPath);
			Resource configurationResource = resolver.getResource(configurationPath);
			FeedImportModel model = new FeedImportModel(configurationResource);
			log.debug("Loaded model {}", model);

			SyndFeed feed = null;
			try {
				feed = downloadFeed(resolver, model);
			} catch (Exception e) {
				log.error("Failed to download feed for " + configurationPath, e);
				fireEvent(OSGI_EVENT_FEED_FAILED_TOPIC, model.getBasePath(), null);
			}

			try {
				if (feed != null) {
					importFeed(resolver, model, feed);
					fireEvent(OSGI_EVENT_FEED_IMPORTED_TOPIC, model.getBasePath(), null);
				}
			} catch (Exception e) {
				log.error("Failed to import feed for " + configurationPath, e);
				fireEvent(OSGI_EVENT_FEED_FAILED_TOPIC, model.getBasePath(), null);
			}
		} catch (Throwable t) {
			log.error("Unexpected problem importing feed " + configurationPath, t);
		} finally {
			if (resolver != null) {
				resolver.close();
			}
		}
	}

	private Map<String, String> toMap(SyndEntry entry) {
		return toMap(entry, "entry", entryMethodMappings);
	}

	private Map<String, String> toMap(SyndFeed feed) {
		return toMap(feed, "feed", feedMethodMappings);
	}

	private Map<String, String> toMap(Object thing, String prefix, Map<String, TYPE> methodMappings) {
		Map<String, String> data = new HashMap<String, String>();
		for (Method m : thing.getClass().getDeclaredMethods()) {
			if (methodMappings.containsKey(m.getName())) {
				String key = prefix + "." + m.getName().replaceFirst("get", "");
				TYPE type = methodMappings.get(m.getName());
				try {
					Object val = m.invoke(thing);
					if (type == TYPE.DATE) {
						data.put(key, handleDate(val));
					} else if (type == TYPE.LIST) {
						data.put(key, handleList(val));
					} else {
						data.put(key, handleString(val));
					}
				} catch (Exception e) {
					log.error("Exception reading value from attribute " + m.getName() + " of " + thing, e);
				}

			}
		}
		return data;
	}

	private String handleString(Object val) {
		String value = null;
		if (val != null) {
			if (val instanceof String) {
				value = (String) val;
			} else if (val instanceof SyndImage) {
				value = ((SyndImage) val).getUrl();
			} else if (val instanceof SyndContent) {
				value = ((SyndContent) val).getValue();
			} else if (val instanceof List) {
				StringBuilder sb = new StringBuilder();
				for (SyndContent content : (List<SyndContent>) val) {
					sb.append(content.getValue());
				}
				value = sb.toString();
			} else {
				log.warn("Unexpected value {}", val);
			}
			value = JSONObject.quote(value);
		}
		return value;
	}

	private String handleList(Object val) {
		if (val != null) {
			JSONArray array = new JSONArray();
			for (Object item : (List) val) {
				if (item instanceof SyndCategory) {
					array.put(((SyndCategory) val).getName());
				} else if (item instanceof SyndPerson) {
					array.put(((SyndPerson) val).getName());
				} else if (item instanceof SyndEnclosure) {
					array.put(((SyndEnclosure) val).getUrl());
				}
			}
			return array.toString();
		} else {
			return null;
		}
	}

	private String handleDate(Object val) {
		if (val != null) {
			Date date = (Date) val;
			DateTime dt = new DateTime(date.getTime());
			DateTimeFormatter fmt = ISODateTimeFormat.dateTime();
			String str = fmt.print(dt);
			return JSONObject.quote(str);
		} else {
			return null;
		}
	}

}
