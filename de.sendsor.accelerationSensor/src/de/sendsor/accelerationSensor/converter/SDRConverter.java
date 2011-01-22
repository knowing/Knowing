/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.sendsor.accelerationSensor.converter;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.BlockDescriptor;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.AbstractConverter;
import de.lmu.ifi.dbs.medmon.sensor.core.util.TimeUtil;
import de.sendsor.accelerationSensor.model.Data;

/**
 * Class for converting SDR Files to {@link ISensorDataContainers}
 * 
 * @author Alexander Stautner, Nepomuk Seiler
 * @param <E>
 */
public class SDRConverter extends AbstractConverter<Data> {

	private static final Logger log = Logger.getLogger(SDRConverter.class.getName());
	
	public final static int BLOCKSIZE = 512;
	public final static int CONTENT_BLOCK = 504;
	private final static int MINUTEINBLOCKS = 9;

	private final static long TIME_CORRECTION_BEFORE = 7000; // Should be 7392 = 504 / 3 * 44																
	private final static long TIME_CORRECTION_AFTER = 44;

	@Override
	public Block[] convertToBlock(String file, ContainerType type) throws IOException {
		// Initialize time handling
		int calendarConstant = TimeUtil.getCalendarConstant(type);
		Calendar startDate = null;
		Calendar compareDate = null;
		Calendar endDate = new GregorianCalendar();

		// Initialize data handling
		RandomAccessFile in = new RandomAccessFile(file, "r");
		List<Block> blocklist = new LinkedList<Block>();

		// Initialize position handling
		int begin = 0;
		int end = ((int) in.length()) / BLOCKSIZE;
		byte[] daten = new byte[BLOCKSIZE];

		// Block
		int blockBegin = 0;
		int blockOffset = 0;

		// Convert each block
		for (int i = begin; i <= end; i++) {
			// Search position
			int position = i * BLOCKSIZE;
			in.seek(position);
			// Load Data into data-Buffer
			in.read(daten, 0, BLOCKSIZE);

			// Create timestamp
			int year = calcYear(daten[506]);
			int month = daten[507] - 1;
			int day = daten[508];
			int hour = daten[509];
			int minute = daten[510];
			int second = daten[511];

			compareDate = (Calendar) endDate.clone();
			endDate.set(year, month, day, hour, minute, second);

			// First loop
			if (startDate == null) {
				startDate = new GregorianCalendar();
				startDate.set(year, month, day, hour, minute, second);
				continue;
			}

			// Check Time
			if (startDate.get(calendarConstant) != endDate.get(calendarConstant)) {
				log.fine("-----------------------------");
				log.fine("Start: " + startDate.getTime() + " End: " + compareDate.getTime());
				Block block = new Block(file, blockBegin, blockOffset, startDate.getTime(), compareDate.getTime());
				blocklist.add(block);
				blockBegin = blockOffset + 1;
				startDate.setTime(endDate.getTime());

				log.fine(block.toString());
			}

			// Checks if the recorded data ended
			if (recordEnd(day, hour))
				break;

			blockOffset++;

		}
		// long time = startDate.getTimeInMillis() - TIME_CORRECTION_BEFORE;
		// endDate.setTimeInMillis(time);
		in.close();
		return blocklist.toArray(new Block[blocklist.size()]);
	}
	
	@Override
	public Data[] readData(Block block) throws IOException {	
		String file = (String) block.getDescriptor().getAttribute(BlockDescriptor.FILE);
		return convertSDRtoData(file, block.getBegin(), block.getEnd());
	}

	/**
	 * Should only be used by {@link Block} to lazily import the Data
	 * 
	 * @param file
	 * @param begin
	 * @param end
	 * @return Data[]
	 * @throws IOException
	 */
	public Data[] convertSDRtoData(String file, long begin, long end) throws IOException {
		// Initialize position handling
		byte[] daten = new byte[BLOCKSIZE];

		// Initialize time handling
		Calendar date = new GregorianCalendar();
		Calendar timestamp = new GregorianCalendar();

		// Initialize data handling
		RandomAccessFile in = new RandomAccessFile(file, "r");
		List<Data> datalist = new LinkedList<Data>();

		// Convert each block
		for (long i = begin; i <= end; i++) {
			// Search position
			long position = i * BLOCKSIZE;
			in.seek(position);
			// Load Data into data-Buffer
			in.read(daten, 0, BLOCKSIZE);

			// Create timestamp
			int year = calcYear(daten[506]);
			int month = daten[507] - 1;
			int day = daten[508];
			int hour = daten[509];
			int minute = daten[510];
			int second = daten[511];
			date.set(year, month, day, hour, minute, second);
			long time = date.getTimeInMillis() - TIME_CORRECTION_BEFORE;

			for (int j = 0; j < CONTENT_BLOCK; j += 3) {
				timestamp.setTimeInMillis(time);
				int x = daten[j];
				int y = daten[j + 1];
				int z = daten[j + 2];

				// Avoiding Call-by-Reference effect

				datalist.add(new Data(timestamp.getTimeInMillis(), x, y, z));
				time += TIME_CORRECTION_AFTER;
			}

		}
		log.info("Converted Data[]: " + datalist.size());
		in.close();
		return datalist.toArray(new Data[datalist.size()]);
	}

	@Override
	public String openChooseInputDialog(Shell shell) {
		DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
		//FileDialog dialog = new FileDialog(shell);
		//dialog.setFilterExtensions(new String[] { "*.sdr", "*.csv" });
		//dialog.setFilterNames(new String[] { "SensorFile(*.sdr)", "CSV Table(*.csv)" });
		return dialog.open();
	}

	/**
	 * Calculate the complete year from the incomplete data of file
	 * 
	 * @param yearIncomlpete
	 * @return the complete year
	 */
	private static int calcYear(int yearIncomlpete) {
		int year = 0;
		if (yearIncomlpete >= 0)
			year = 2000 + yearIncomlpete;
		else
			year = 2000 + 127 + Math.abs(yearIncomlpete);

		return year;
	}

	/**
	 * Currently a SDR file is a bunch of zeros. Those zeros are placeholders
	 * and will be overitten. This method checks if the end of the recorded data
	 * is reached, however not the end of the file.
	 * 
	 * @param day
	 * @param hour
	 * @return
	 */
	private boolean recordEnd(int day, int hour) {
		return (day == 48) && (hour == 48);
	}

}
