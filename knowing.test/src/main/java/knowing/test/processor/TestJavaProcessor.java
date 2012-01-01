package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Properties;

import weka.core.Instance;
import weka.core.Instances;
import akka.actor.ActorRef;
import de.lmu.ifi.dbs.knowing.core.japi.AbstractProcessor;
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor;

public class TestJavaProcessor extends AbstractProcessor {

	private double randomNumber = 0.0;
	private String randomString = "";

	public TestJavaProcessor(JProcessor wrapper) {
		super(wrapper);
	}

	@Override
	public void start() {
		debug("[Start] Opening inputstream..." );
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
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

	}

	@Override
	public void stop() {
		randomNumber = Math.random();
		randomString = new Date().toString();
		OutputStream out = getOutputStream();
		debug("[Stop] Opening outputstream..." );
		if(out == null)
			return;
		debug("[Stop] Saving state: " + String.valueOf(randomNumber));
		debug("[Stop] Saving state: " + randomString);
        PrintWriter writer = new PrintWriter(out);
        writer.println(String.valueOf(randomNumber));
        writer.println(randomString);
        writer.flush();
        writer.close();
	}

	@Override
	public void build(Instances instances) {
		debug("[Build] " + instances);
	}

	@Override
	public Instances query(Instance query, ActorRef ref) {
		debug("[Query] " + query);
		if (ref.getSender().isDefined()) {
			ActorRef sender = ref.getSender().get();
			sender.sendOneWay("Answer");
		}
		return null;
	}

	@Override
	public void result(Instances result, Instance query) {
		debug("[Result] " + result + " == [Query] " + query);
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
