package com.mpsdevelopment.biopotential.server.eventbus;

import com.mpsdevelopment.plasticine.commons.logging.Logger;
import com.mpsdevelopment.plasticine.commons.logging.LoggerUtil;
import net.engio.mbassy.bus.config.BusConfiguration;
import net.engio.mbassy.bus.config.Feature;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.subscription.Subscription;

import java.util.Collection;

public class EventBus {

	private static final Logger LOGGER = LoggerUtil.getLogger(EventBus.class);

	private static HeliMbassador eventBus = null;

	private EventBus() {

	}

	static {
		getEventBus();
	}

	private static HeliMbassador getEventBus() {
		if (eventBus == null) {
			IBusConfiguration busConfiguration = new BusConfiguration().addFeature(Feature.SyncPubSub.Default()).addFeature(Feature.AsynchronousHandlerInvocation.Default())
					.addFeature(Feature.AsynchronousMessageDispatch.Default()).addPublicationErrorHandler(new IPublicationErrorHandler() {
						@Override
						public void handleError(PublicationError error) {
							LOGGER.warn("Handled error while publishing event.");
							LOGGER.printStackTrace(error.getCause());
						}
					});
			eventBus = new HeliMbassador(busConfiguration);
		}
		return eventBus;
	}

	public static void publishEvent(Event event) {
		try {
			getEventBus().publish(event);

		} catch (Throwable e) {
			LOGGER.printStackTrace(e);
		}
	}

	public static void subscribe(Object subscriber) {
		getEventBus().subscribe(subscriber);
	}

	public static boolean unsubscribe(Object subscriber) {
		return getEventBus().unsubscribe(subscriber);
	}

	public static Collection<Subscription> getSubscribers(Class messageType) {
		return getEventBus().getSubscriptions(messageType);
	}
}
