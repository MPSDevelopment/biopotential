package com.mpsdevelopment.biopotential.server.eventbus;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;

public class Event {

	private static final Logger LOGGER = LoggerUtil.getLogger(Event.class);

	private String source;
	private String publisherId;

	public Event() {
		// LOGGER.info("Has been created an event '%s'", this);
	}

	public Event(String source, String publisherId) {
		this();
		this.source = source;
		this.publisherId = publisherId;
	}

	public String getSource() {
		return source;
	}

	public String getPublisherId() {
		return this.publisherId;
	}

}
