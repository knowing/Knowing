package de.lmu.ifi.dbs.knowing.core.japi;

import java.util.Properties;

import akka.actor.ActorRef;

import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.events.Event;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 04.07.2011
 */
public interface IProcessor {

	/**
	 * <p>
	 * Input instances from source-nodes
	 * </p>
	 * 
	 * @param instances
	 */
	void build(Instances instances);

	/**
	 * <p>
	 * Query from target-node. Should be answered via
	 * </p>
	 * <pre>
	 * {@code	
	 * if(ref.getSender().isDefined()) { 
	 *    ActorRef sender = ref.getSender().get(); 
	 *    sender.sendOneWay("Answer"); //Or a message as you like	
	 * }
	 * </pre>
	 * 
	 * @param query
	 * @return
	 */
	Instances query(Instance query, ActorRef ref);

	void result(Instances result, Instance query);

	/**
	 * <p>
	 * Before started, the Processor gets configured
	 * </p>
	 * 
	 * @param properties
	 */
	void configure(Properties properties);

	/**
	 * <p>
	 * All unkown events will be catched by this method
	 * </p>
	 * 
	 * @param event
	 */
	void messageException(Object message);

	/**
	 * <p>
	 * If port is null, then it will be send to the separated list
	 * </p>
	 * 
	 * @param event
	 * @param port
	 *            - can be null
	 */
	void sendEvent(Event event, String port);
}
