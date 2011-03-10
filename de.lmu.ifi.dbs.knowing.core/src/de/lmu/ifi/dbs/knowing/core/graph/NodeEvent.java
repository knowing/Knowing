package de.lmu.ifi.dbs.knowing.core.graph;

import java.sql.Date;
import java.text.DateFormat;

public class NodeEvent {

	public static final int LOADER_READY = 0;
	public static final int LOADER_WAITING = 1;
	public static final int LOADER_ERROR = 2;
	
	public static final int PROCESSOR_READY = 10;
	public static final int PROCESSOR_RUNNING = 11;
	public static final int PROCESSOR_ERROR = 12;
	
	private final int type;
	private final INode source;
	private final Object serviceObject;
	
	private final long timestamp;
	
	public NodeEvent(int type, INode source, Object serviceObject) {
		this.type = type;
		this.source = source;
		this.serviceObject = serviceObject;
		this.timestamp = System.currentTimeMillis();
	}

	public int getType() {
		return type;
	}

	public INode getSource() {
		return source;
	}

	public Object getServiceObject() {
		return serviceObject;
	}
	
	public long getTimestamp() {
		return timestamp;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("NodeEvent [type=");
		builder.append(typeToString(type));
		builder.append(", source=");
		builder.append(source);
		builder.append(", serviceObject=");
		builder.append(serviceObject);
		builder.append(", timestamp=");
		builder.append(timestamp);
		builder.append("]");
		return builder.toString();
	}
	
	public static String typeToString(int type) {
		switch (type) {
		case LOADER_READY:
			return "Loader ready";
		case LOADER_WAITING:
			return "Loader waiting";
		case LOADER_ERROR:
			return "!!Loader error!!";
		case PROCESSOR_READY:
			return "Processor ready";
		case PROCESSOR_RUNNING:
			return "Processor running";
		case PROCESSOR_ERROR:
			return "!!Processor error!!";
		default:
			return "UNKOWN";
		}
	}
	
}
