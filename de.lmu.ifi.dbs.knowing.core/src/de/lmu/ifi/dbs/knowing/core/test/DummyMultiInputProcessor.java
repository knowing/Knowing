package de.lmu.ifi.dbs.knowing.core.test;

import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

public class DummyMultiInputProcessor extends DummyProcessor {

	private final int count;
	private int current = 0;
		
	public DummyMultiInputProcessor(int count) {
		this.count = count;
	}

	@Override
	public synchronized void buildModel(ILoader loader) {
		super.buildModel(loader);
		current++;
	}
	
	@Override
	public synchronized void buildModel(IProcessor processor) {
		super.buildModel(processor);
		current++;
	}
	
	@Override
	public boolean isReady() {
		return count <= current;
	}
	
	@Override
	public String toString() {
		return "DummyMultiInputProcessor";
	}
}
