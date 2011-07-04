package de.lmu.ifi.dbs.knowing.core.japi;

import de.lmu.ifi.dbs.knowing.core.events.Event;

public abstract class AbstractProcessor implements IProcessor {

	private final JProcessor wrapper;

	public AbstractProcessor(JProcessor wrapper) {
		this.wrapper = wrapper;
	}
	
	@Override
	public void sendEvent(Event event, String port) {
		wrapper.sendEvent(event, port);
	}
	
}
