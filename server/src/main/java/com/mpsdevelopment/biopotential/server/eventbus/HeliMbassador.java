package com.mpsdevelopment.biopotential.server.eventbus;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.config.IBusConfiguration;
import net.engio.mbassy.subscription.Subscription;

import java.util.Collection;

public class HeliMbassador extends MBassador<Event> {

	public HeliMbassador() {
		super();
	}

	public HeliMbassador(IBusConfiguration configuration) {
		super(configuration);
	}

	public Collection<Subscription> getSubscriptions(Class messageType) {
		return getSubscriptionsByMessageType(messageType);
	}

	public int getSubscriptionsCount(Class messageType) {
		return getSubscriptionsByMessageType(messageType).size();
	}

}
