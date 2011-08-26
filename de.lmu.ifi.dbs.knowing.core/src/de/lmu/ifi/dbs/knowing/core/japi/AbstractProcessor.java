package de.lmu.ifi.dbs.knowing.core.japi;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.events.Event;
import de.lmu.ifi.dbs.knowing.core.events.Status;

public abstract class AbstractProcessor implements IProcessor {

	private final JProcessor wrapper;

	public AbstractProcessor(JProcessor wrapper) {
		this.wrapper = wrapper;
	}
	
	@Override
	public void sendEvent(Event event, String port) {
		wrapper.sendEvent(event, port);
	}
	
	@Override
	public void setStatus(Status status) {
		wrapper.status_$eq(status);		
	}
	
	protected int guessAndSetClassLabel(Instances dataset) {
		return wrapper.guessAndSetClassLabel(dataset, -1);
	}
	
	protected int guessAndSetClassLabel(Instances dataset, int defaultClassIndex) {
		return wrapper.guessAndSetClassLabel(dataset, defaultClassIndex);
	}
	
}
