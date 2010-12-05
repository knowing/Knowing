package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.io.IOException;
import java.util.Date;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.Assert;

import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.util.TimeUtil;

/**
 * Placeholder for a Block in a SensorFile
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 */
public class Block {

	public static final int FILE_BLOCK = 0;
	public static final int DB_BLOCK = 1;

	private final long begin;
	private final long end;
	private final int type;

	private BlockDescriptor descriptor;

	/**
	 * Constructor to use Block as a placeholder for a file
	 * @param file
	 * @param begin
	 * @param end
	 * @param calendarConstant
	 * @param firstTimestamp
	 * @param lastTimestamp
	 */
	public Block(String file, long begin, long end, int calendarConstant, Date firstTimestamp, Date lastTimestamp) {
		this.begin = begin;
		this.end = end;
		this.type = FILE_BLOCK;
		descriptor = new BlockDescriptor(file, firstTimestamp,lastTimestamp);
		descriptor.setAttribute(BlockDescriptor.CALENDAR, calendarConstant);
	}

	public Block(String file, long begin, long end, Date firstTimestamp, Date lastTimestamp) {
		this(file, begin, end, TimeUtil.getCalendarConstant(firstTimestamp, lastTimestamp), firstTimestamp, lastTimestamp);
	}

	public Block(String file, long begin, long end, int calendarConstant) {
		this(file, begin, end, null, null);
	}

	/**
	 * Constructor to use Block as a placeholder for DB Query
	 * @param em
	 * @param begin
	 * @param end
	 * @param firstTimestamp
	 * @param lastTimestamp
	 */
	public Block(EntityManager em, long begin, long end, Date firstTimestamp, Date lastTimestamp) {
		this.begin = begin;
		this.end = end;
		this.type= DB_BLOCK;
		descriptor = new BlockDescriptor(em, firstTimestamp, lastTimestamp);
	}
	
	public Block(EntityManager em, long begin, long end) {
		this.begin = begin;
		this.end = end;
		this.type= DB_BLOCK;
		descriptor = new BlockDescriptor(em, new Date(begin), new Date(end));
	}
	
	public long getBegin() {
		return begin;
	}

	public long getEnd() {
		return end;
	}
	
	public int getType() {
		return type;
	}

	public Object[] importData(IConverter converter) throws IOException {
		return converter.readData(this);
	}
	
	public long size() {
		return (end - begin);
	}

	public BlockDescriptor getDescriptor() {
		return descriptor;
	}
	
	/**
	 * The block must refere to the same File / Database. If not
	 * the result is unpredictable.
	 * @param block - a new, merged Block
	 * @return
	 */
	public Block merge(Block block) {
		System.out.println("----------------BLOCK MERGING--------------------");
		System.out.println(this + " and " + block);
		if(block == null)
			return this;
		
		Assert.isTrue(type == block.type, "Blocks don't have the same type: " + type + " != " + block.type);
		
		//Merge FILE_BLOCK
		if(type == FILE_BLOCK) {
			Assert.isTrue(descriptor.getAttribute(BlockDescriptor.FILE) == block.descriptor.getAttribute(BlockDescriptor.FILE), "Blocks dont' have the same file");
			long begin = 0;
			long end = 0;
			Date firstTimestamp, lastTimestamp;
			//Get the minimum block
			if(this.begin <= block.begin) {
				begin = this.begin;
				firstTimestamp = (Date) descriptor.getAttribute(BlockDescriptor.STARTDATE);
			} else {
				begin = block.begin;
				firstTimestamp = (Date) block.descriptor.getAttribute(BlockDescriptor.STARTDATE);
			}
			
			if(this.end >= block.end) {
				end = this.end;
				lastTimestamp = (Date) descriptor.getAttribute(BlockDescriptor.ENDDATE);
			} else {
				begin = block.begin;
				lastTimestamp = (Date) block.descriptor.getAttribute(BlockDescriptor.ENDDATE);
			}
			System.out.println("Merged block: " + begin + "-" + end);
			
			return new Block((String) descriptor.getAttribute(BlockDescriptor.FILE), begin, end, firstTimestamp, lastTimestamp);
		}
		
		return null;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Block [begin=");
		builder.append(begin);
		builder.append(", end=");
		builder.append(end);
		builder.append(", type=");
		builder.append(type);
		builder.append("]");
		return builder.toString();
	}
	
	

}
