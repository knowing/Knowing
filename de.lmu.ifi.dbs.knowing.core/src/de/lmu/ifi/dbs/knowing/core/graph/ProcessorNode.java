package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

public class ProcessorNode extends Node implements IProcessorListener {

	private IProcessor processor;

	public ProcessorNode(String factoryName, String factoryId, String nodeId) {
		super(factoryName, factoryId, nodeId);
	}
	
	public ProcessorNode(String factoryId, String nodeId) {
		super(FactoryUtil.getFactory(factoryId, IProcessorFactory.class).getName(), factoryId, nodeId);
	}

	@Override
	public void initialize() {
		processor = FactoryUtil.getProcessorService(getFactoryId(), properties);
		processor.setProcessorListener(this);
	}

	public IProcessor getProcessor() {
		return processor;
	}

	@Override
	protected boolean validate(Properties properties) {
		return true;
	}

	@Override
	public void nodeChanged(NodeEvent event) {
		System.out.println("!!! [NodeEvent]: " + event.getSource() + " -> " + this);
		Object so = event.getServiceObject();
		switch (event.getType()) {
		case NodeEvent.LOADER_READY:
			buildModel((ILoader) so);
			break;
		case NodeEvent.PROCESSOR_READY:
			buildModel((IProcessor) so);
			break;
		}
		//fireNodeEvent(NodeEvent.PROCESSOR_RUNNING, processor);
	}
	
	
	@Override
	public void processorChanged(IProcessor processor) {
		if(processor.isReady())
			fireNodeEvent(NodeEvent.PROCESSOR_READY, processor);
		else
			fireNodeEvent(NodeEvent.PROCESSOR_RUNNING, processor);
	}

	/**
	 * Runs a task, building the internal model
	 * @param loader
	 */
	private void buildModel(final ILoader loader) {
		System.out.println("=== [RUN/BUILD]: " + processor + " with " + loader);
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				processor.buildModel(loader);
			}
		};
		supervisor.execute(buildTask);
	}

	/**
	 * Runs a task, building the internal model
	 * @param inputProcessor
	 */
	private void buildModel(final IProcessor inputProcessor) {
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				processor.buildModel(inputProcessor);
			}
		};
		supervisor.execute(buildTask);
	}
	
	@Override
	public void setProperties(Properties properties) {
		if(validate(properties)) {
			this.properties = properties;
			for (String key : properties.stringPropertyNames()) 
				processor.setProperty(key, properties.getProperty(key));
			
		}
	}
	
	@Override
	public INode clone() {
		ProcessorNode clone = new ProcessorNode(getName(), getFactoryId(), getNodeId());
		clone.setProperties(properties);
		clone.setSupervisor(supervisor);
		return clone;
	}

	@Override
	public String toString() {
		return getNodeId() + "[" + processor + "]";
	}

}
