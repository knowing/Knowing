package de.lmu.ifi.dbs.knowing.core.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.Processor;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

public class DummyProcessor extends Processor {


	@Override
	public synchronized void buildModel(ILoader loader) {
		System.out.println("DummyProcessor.buildModel()");
		try {
			fireQuery("loader.model", loader, false);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		long sleep = (long) (Math.random() * 1600.0);
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	@Override
	public synchronized void buildModel(IProcessor processor) {
		System.out.println("DummyProcessor.buildModel()");
		long sleep = (long) (Math.random() * 600.0);
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	protected void result(BlockingQueue<QueryResult> results) {
		
	}
	
	@Override
	protected void query(BlockingQueue<QueryTicket> tickets) {
		
	}

	@Override
	public void resetModel() {
		
	}

	@Override
	public String[] validate() {
		return null;
	}

	@Override
	public boolean isAlive() {
		return true;
	}
	
	@Override
	public boolean isReady() {
		return true;
	}
	
	@Override
	public String toString() {
		return "DummyProcessor";
	}

	@Override
	public void persistModel(OutputStream out) {
		
	}

	@Override
	public void loadModel(InputStream in) {
		
	}

	@Override
	public Instances[] supportedQueries() {
		return null;
	}

	@Override
	public Instances[] resultFormat(Instances query) {
		return null;
	}


}
