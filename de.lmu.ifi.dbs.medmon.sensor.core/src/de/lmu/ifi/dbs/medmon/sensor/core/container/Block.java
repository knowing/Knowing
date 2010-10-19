package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.util.Date;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.SDRConverter;

public class Block {

	private final String file;
	private final int begin;
	private final int end;

	private Date firstTimestamp;
	private Date lastTimestamp;
	

	public Block(String file, int begin, int end, Date firstTimestamp, Date lastTimestamp) {
		super();
		this.file = file;
		this.begin = begin;
		this.end = end;
		this.firstTimestamp = firstTimestamp;
		this.lastTimestamp = lastTimestamp;
	}
	
	public Block(String file, int begin, int end) {
		this(file, begin, end, null,null);
	}



	public String getFile() {
		return file;
	}

	public int getBegin() {
		return begin;
	}

	public int getEnd() {
		return end;
	}
	
	public Data[] importData() throws IOException {
		return SDRConverter.convertSDRtoData(file, begin, end);
	}
	
	public Date getFirstTimestamp() throws IOException {
		if(firstTimestamp == null) {
			Data[] data = SDRConverter.convertSDRtoData(file, begin, begin+1);
			firstTimestamp = data[0].getImported();
		}
			
		return firstTimestamp;
	}
	
	public Date getLastTimestamp() throws IOException {
		if(lastTimestamp == null) {
			Data[] data = SDRConverter.convertSDRtoData(file, end-1, end);
			lastTimestamp = data[0].getImported();
		}
		
		return lastTimestamp;
	}
	
	public int size() {
		return (end - begin) * SDRConverter.CONTENT_BLOCK;
	}

	@Override
	public String toString() {
		return "Block [file=" + file + ", begin=" + begin + ", end=" + end + ", size=" + size() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + begin;
		result = prime * result + end;
		result = prime * result + ((file == null) ? 0 : file.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Block other = (Block) obj;
		if (begin != other.begin)
			return false;
		if (end != other.end)
			return false;
		if (file == null) {
			if (other.file != null)
				return false;
		} else if (!file.equals(other.file))
			return false;
		return true;
	}
	
	
	
}
