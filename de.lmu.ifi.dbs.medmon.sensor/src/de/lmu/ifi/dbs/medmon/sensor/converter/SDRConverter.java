/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.lmu.ifi.dbs.medmon.sensor.converter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.sql.Timestamp;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.DataPK;
import de.lmu.ifi.dbs.medmon.database.sample.SampleDataFactory;
import de.lmu.ifi.dbs.medmon.sensor.data.AbstractSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.data.DaySensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.data.RootSensorDataContainer;

/**
 * Class for converting SDR Files to CSV File
 * 
 * @author Alexander Stautner
 */
public class SDRConverter {

	private final static int BLOCKSIZE = 512;
	private final static int CONTENT_BLOCK = 504;
	private final static int MINUTEINBLOCKS = 9;

	private final static long TIME_CORRECTION_BEFORE = 7000;
	private final static long TIME_CORRECTION_AFTER = 44;

	/**
	 * 
	 * @param file
	 * @param minuteBegin
	 *            (minutes?) 1 <= begin <= end
	 * @param minuteEnd
	 *            (minutes?)
	 * @return
	 * @throws IOException
	 */
	public static ISensorDataContainer convertSDRtoData(String file, int begin,
			int end) throws IOException {

		// Initialize position handling
		begin = begin * MINUTEINBLOCKS;
		end = end * MINUTEINBLOCKS;
		byte[] daten = new byte[BLOCKSIZE];

		// Initialize time handling
		GregorianCalendar date = new GregorianCalendar();
		GregorianCalendar timestamp = new GregorianCalendar();

		// Initialize data handling
		RandomAccessFile in = new RandomAccessFile(file, "r");
		List<Data> datalist = new LinkedList<Data>();

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
			date.set(year, month, day, hour, minute, second);
			long time = date.getTimeInMillis() - TIME_CORRECTION_BEFORE;

			for (int j = 0; j < CONTENT_BLOCK; j += 3) {
				timestamp.setTimeInMillis(time);
				int x = daten[j];
				int y = daten[j + 1];
				int z = daten[j + 2];
				
				//Avoiding Call-by-Reference effect
				GregorianCalendar ts = (GregorianCalendar) timestamp.clone();
				DataPK id = new DataPK(0, ts);
	
				datalist.add(new Data(id, x, y, z));
				time += TIME_CORRECTION_AFTER;
			}
		}
		return AbstractSensorDataContainer.parse(datalist.toArray(new Data[datalist.size()]), 0, 0);
	}

	
	public static String importSDRFileDialog(Shell parent) {
		FileDialog dialog = new FileDialog(parent);
		dialog.setFilterExtensions(new String[] { "*.sdr", "*.csv" });
		dialog.setFilterNames(new String[] { "SensorFile(*.sdr)","CSV Table(*.csv)" });
		return dialog.open();
	}

	public static ISensorDataContainer importDialog(Shell parent) {
		ISensorDataContainer returns = new RootSensorDataContainer();

		String selected = importSDRFileDialog(parent);
		try {
			returns = SDRConverter.convertSDRtoData(selected, 1, 20);
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(parent,	"Fehler beim Daten lesen", e.getMessage());
		}
		return returns;
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

}
