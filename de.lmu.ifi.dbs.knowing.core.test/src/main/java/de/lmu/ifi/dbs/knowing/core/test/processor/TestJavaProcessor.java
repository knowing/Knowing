package de.lmu.ifi.dbs.knowing.core.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Properties;

import weka.core.Instances;
import akka.actor.ActorRef;
import de.lmu.ifi.dbs.knowing.core.japi.AbstractProcessor;
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor;

/**
 * Java Implementation of a Processor.
 * 
 * The Constructor is essential.
 * <pre>
 * public TestJavaProcessor(JProcessor wrapper) {
 *	super(wrapper);
 * }
 * </pre>
 * 
 * The other part of the Implementation is in TestJavaProcessorWrapper,<br>
 * which actually wraps the TProcessor trait.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class TestJavaProcessor extends AbstractProcessor {

	private double	randomNumber	= 0.0;
	private String	randomString	= "";

	public TestJavaProcessor(JProcessor wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		debug("[Start] Opening inputstream...");
		try {
			InputStream in = getInputStream();
			if (in == null)
				return;
			LineNumberReader reader = new LineNumberReader(new InputStreamReader(in));
			String line = reader.readLine();
			if (line != null) {
				randomNumber = Double.valueOf(line);
				randomString = reader.readLine();
			}
			debug("[Start] Loading state: " + String.valueOf(randomNumber));
			debug("[Start] Loading state: " + randomString);
			reader.close();
		} catch (MalformedURLException e) {
			throwException(e, "Model could not be resolved");
			return;
		} catch (IOException e) {
			throwException(e, "Error while reading model");
			return;
		}
	}

	@Override
	public void stop() {
		randomNumber = Math.random();
		randomString = new Date().toString();
		try (OutputStream out = getOutputStream()) {
			debug("[Stop] Opening outputstream...");
			if (out == null)
				return;
			debug("[Stop] Saving state: " + String.valueOf(randomNumber));
			debug("[Stop] Saving state: " + randomString);
			PrintWriter writer = new PrintWriter(out);
			writer.println(String.valueOf(randomNumber));
			writer.println(randomString);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			throwException(e, "Error while writing results");
		}

	}

	@Override
	public void process(Instances instances, String port, Instances query) {
		debug("[Build] " + instances);
	}

	@Override
	public Instances query(Instances query, ActorRef ref) {
		debug("[Query] " + query);
//		if (ref.getSender().isDefined()) {
//			ActorRef sender = ref.getSender().get();
//			sender.tell("Answer");
//		}
		return null;
	}

	@Override
	public void configure(Properties properties) {
		debug("[Configure] " + properties);
	}

	@Override
	public void messageException(Object message) {
		warning("[MessageException] " + message);
	}

}
