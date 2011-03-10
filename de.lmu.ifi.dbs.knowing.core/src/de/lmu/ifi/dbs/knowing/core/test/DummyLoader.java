package de.lmu.ifi.dbs.knowing.core.test;

import java.io.IOException;

import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractLoader;

public class DummyLoader extends AbstractLoader {

	@Override
	public String getRevision() {
		return null;
	}

	@Override
	public Instances getDataSet() throws IOException {
		System.out.println("DummyLoader.getDataSet()");
		long sleep = (long) (Math.random() * 1600.0);
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("DummyLoader.getDataSet() FINISHED");
		return null;
	}

	@Override
	public Instance getNextInstance(Instances instances) throws IOException {
		return null;
	}

	@Override
	public Instances getStructure() throws IOException {
		return null;
	}

	@Override
	public String toString() {
		return "DummyLoader";
	}

	
}
