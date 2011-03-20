package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

public class ProcessorNode extends Node {

	private IProcessor processor;

	public ProcessorNode(String factoryName, String factoryId, String nodeId) {
		super(factoryName, factoryId, nodeId);
	}
	
	public ProcessorNode(String factoryId, String nodeId) {
		super(FactoryUtil.getFactory(factoryId, IProcessorFactory.class).getName(), factoryId, nodeId);
	}

	@Override
	public void run() throws Exception {
		
	}

	@Override
	public void initialize() {
		processor = FactoryUtil.getProcessorService(getFactoryId(), properties);
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

	private void buildModel(final ILoader loader) {
		System.out.println("=== [RUN/BUILD]: " + processor + " with " + loader);
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				processor.buildModel(loader);
				ready = processor.isReady();
				if (processor.isReady()) {
					fireNodeEvent(NodeEvent.PROCESSOR_READY, processor);
				}

			}
		};
		supervisor.execute(buildTask);
	}

	private void buildModel(final IProcessor inputProcessor) {
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				processor.buildModel(inputProcessor);
				ready = inputProcessor.isReady();
				if (processor.isReady()) {
					fireNodeEvent(NodeEvent.PROCESSOR_READY, inputProcessor);
				}

			}
		};
		supervisor.execute(buildTask);
	}
	
	@Override
	public String toString() {
		return getNodeId() + "[" + processor + "]";
	}
	
	@Override
	public INode clone() {
		ProcessorNode clone = new ProcessorNode(getName(), getFactoryId(), getNodeId());
		clone.setProperties(properties);
		return clone;
	}

}
