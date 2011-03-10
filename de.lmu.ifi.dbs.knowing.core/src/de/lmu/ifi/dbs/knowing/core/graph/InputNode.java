package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

public class InputNode extends Node {

	private Properties properties;
		
	private ILoader loader;
	
	public InputNode(String factoryName, String factoryId, String nodeId) {
		super(factoryName, factoryId, nodeId);
	}
	
	public InputNode(String factoryId, String nodeId) {
		super(FactoryUtil.getFactory(factoryId, ILoaderFactory.class).getName(), factoryId, nodeId);
	}

	@Override
	public void initialize() {
		loader = FactoryUtil.getLoaderService(getFactoryId(), properties);
		ready = true;
	}
	
	@Override
	public void run() throws Exception {
		System.out.println("=== [RUN/BUILD]: " + this);
		fireNodeEvent(NodeEvent.LOADER_READY, loader);
	}

	@Override
	protected boolean validate(Properties properties) {
		return true;
	}
	
	@Override
	public void nodeChanged(NodeEvent event) {
		
	}
	
	@Override
	public String toString() {
		return getNodeId() + "[" + loader + "]";
	}
		
}
