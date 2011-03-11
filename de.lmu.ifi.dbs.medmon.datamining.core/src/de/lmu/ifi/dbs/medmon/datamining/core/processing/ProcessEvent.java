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
	
	private Object source;
			
	public ProcessEvent(Object source, int status) {
		this.status = status;
		this.source = source;
	}

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
	
	public Object getSource() {
		if(source == null)
			return processor;
		return source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ProcessEvent [processor=");
		builder.append(processor);
		builder.append(", status=");
		builder.append(status);
		builder.append("]");
		return builder.toString();
	}
	
	
}
