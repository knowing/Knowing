package de.lmu.ifi.dbs.knowing.core.japi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.events.Event;
import de.lmu.ifi.dbs.knowing.core.events.Status;

/**
 * <p>This class helps wrapping Java processors into the Knowing
 * system. Usage is documented in plugin knowing.test </p>
 * 
 * @author Nepomuk Seiler
 * @version 1.2
 */
public abstract class AbstractProcessor implements IProcessor {

	/** Hook into the akka system */
	private final JProcessor wrapper;

	public AbstractProcessor(JProcessor wrapper) {
		this.wrapper = wrapper;
	}
	
	/**
	 * @see ActorRef.preStart
	 */
	public void start() {}
	
	/**
	 * @see ActorRef.stop
	 */
	public void stop() {}

	@Override
	public void sendEvent(Event event, String output) {
		wrapper.sendEvent(event, output);
	}

	public void sendEvent(Event event) {
		sendEvent(event, null);
	}

	@Override
	public void setStatus(Status status) {
		wrapper.statusChanged(status);
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return wrapper.getInputStream();
	}

	@Override
	public OutputStream getOutputStream() {
		return wrapper.getOutputStream();
	}
	
	@Override
	public void throwException(Throwable t, String details) {
		wrapper.throwException(t, details);
	}

	protected int guessAndSetClassLabel(Instances dataset) {
		return wrapper.guessAndSetClassLabel(dataset, -1);
	}

	protected int guessAndSetClassLabel(Instances dataset, int defaultClassIndex) {
		return wrapper.guessAndSetClassLabel(dataset, defaultClassIndex);
	}
	
	/* =================================== */
	/* ===Logging into the akka system === */
	/* =================================== */
	
	/**
	 * <p>Delegates to {@link akka.event.EventHandler.debug}</p>
	 * @param msg
	 */
	protected void debug(String msg) { wrapper.debug(msg); }
	
	/**
	 * <p>Delegates to {@link akka.event.EventHandler.info}</p>
	 * @param msg
	 */
	protected void info(String msg) { wrapper.info(msg); }
	
	/**
	 * <p>Delegates to {@link akka.event.EventHandler.warning}</p>
	 * @param msg
	 */
	protected void warning(String msg) { wrapper.warning(msg); }
	
	/**
	 * <p>Delegates to {@link akka.event.EventHandler.error}</p>
	 * @param msg
	 */
	protected void error(String msg) { wrapper.error(msg); }

}
