package de.lmu.ifi.dbs.knowing.core.japi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import akka.actor.ActorRef;

import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.events.Event;
import de.lmu.ifi.dbs.knowing.core.events.Status;

/**
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 * @since 04.07.2011
 */
public interface IProcessor {

	/**
	 * <p>Is called, when the processor is started.
	 * You can load models in here via getInputStream and
	 * initialize your internal state</p>
	 */
	void start();
	
	/**
	 * <p>Is called, when the processor (the actor) is
	 * shutdown. Internal states can be saved via getOutputStrea,</p>
	 */
	void stop();
	
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
	 * @param output (port) - can be null
	 *            
	 */
	void sendEvent(Event event, String output);
	
	/**
	 * 
	 * @param status - Ready | Running | Progress(task, worked, work) | Finished
	 */
	void setStatus(Status status);
	
	/**
	 * <p>If property INodeProperties.DESERIALIZE is set,
	 * this methods creates an @InputStream </p>
	 */
	InputStream getInputStream() throws IOException;
	
	/**
	 * <p>If property INodeProperties.SERIALIZE is set,
	 * this methods creates an @OutputStream </p>
	 */
	OutputStream getOutputStream() throws IOException;
}
