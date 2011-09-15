package de.lmu.ifi.dbs.knowing.core.japi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.events.Event;
import de.lmu.ifi.dbs.knowing.core.events.Status;

/**
 * <p>This class helps wrapping Java processors into the Knowing
 * system.</p>
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 */
public abstract class AbstractProcessor implements IProcessor {

	private final JProcessor wrapper;

	public void start() {}
	
	public void stop() {}

	public AbstractProcessor(JProcessor wrapper) {
		this.wrapper = wrapper;
	}

	@Override
	public void sendEvent(Event event, String output) {
		wrapper.sendEvent(event, output);
	}

	public void sendEvent(Event event) {
		sendEvent(event, null);
	}

	@Override
	public void setStatus(Status status) {
		wrapper.status_$eq(status);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrapper.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return wrapper.getOutputStream();
	}

	protected int guessAndSetClassLabel(Instances dataset) {
		return wrapper.guessAndSetClassLabel(dataset, -1);
	}

	protected int guessAndSetClassLabel(Instances dataset, int defaultClassIndex) {
		return wrapper.guessAndSetClassLabel(dataset, defaultClassIndex);
	}
	
	protected void debug(String msg) { wrapper.debug(msg); }
	protected void info(String msg) { wrapper.info(msg); }
	protected void warning(String msg) { wrapper.warning(msg); }
	protected void error(String msg) { wrapper.error(msg); }

}
