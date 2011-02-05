package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.Map;


public class ProcessEvent {

	public static final int ERROR = -1;
	public static final int RUNNING = 0;
	public static final int WAITING = 1;
	public static final int FINISHED = 2;
	
	private IDataProcessor processor;
	private int status;
	
	private Map<String, IAnalyzedData> result;
		
	public ProcessEvent(IDataProcessor processor, int status, Map<String, IAnalyzedData> result) {
		this.processor = processor;
		this.status = status;
		this.result = result;
	}
	
	public ProcessEvent(IDataProcessor processor, int status) {
		this(processor, status, null);
	}

	public IDataProcessor getProcessor() {
		return processor;
	}
	
	public int getStatus() {
		return status;
	}
	
	public Map<String, IAnalyzedData> getResult() {
		return result;
	}
}
