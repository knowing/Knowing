package knowing.test.processor;

import java.util.Properties;

import akka.actor.ActorRef;

import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.japi.AbstractProcessor;
import de.lmu.ifi.dbs.knowing.core.japi.JProcessor;

public class TestJavaProcessor extends AbstractProcessor {

	public TestJavaProcessor(JProcessor wrapper) {
		super(wrapper);
	}

	@Override
	public void build(Instances instances) {
		System.out.println("[Build] " + instances);
	}

	@Override
	public Instances query(Instance query, ActorRef ref) {
		System.out.println("[Query] " + query);
		if(ref.getSender().isDefined()) {
			ActorRef sender = ref.getSender().get();
			sender.sendOneWay("Answer");
		}
		return null;
	}

	@Override
	public void result(Instances result, Instance query) {
		System.out.println("[Result] " + result + " == [Query] " + query);
	}

	@Override
	public void configure(Properties properties) {
		System.out.println("[Configure] " + properties);
	}

	@Override
	public void messageException(Object message) {
		System.err.println("[MessageException] " + message);
	}


}
