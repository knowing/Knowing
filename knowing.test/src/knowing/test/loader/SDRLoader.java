/**
 * 
 */
package knowing.test.loader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Properties;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.AbstractFileLoader;
import weka.core.converters.BatchConverter;
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 01.04.2011
 * 
 */
public class SDRLoader extends AbstractFileLoader implements BatchConverter {

	/* ==== Factory ==== */
	public static final String ID = "de.sendsor.accelerationSensor.converter.SDRLoader";
	public static final String URL = "input.url";
	public static final String FILE = "input.file";
	
	private Properties properties = new Properties();
	
	/* ==== Loader ==== */
	public static String FILE_EXTENSION = ".sdr";
	
	private final static int BLOCKSIZE = 512;
	private final static int CONTENT_BLOCK = 504;
	private final static int MINUTEINBLOCKS = 9;
	private final static long TIME_CORRECTION_BEFORE = 7000; // Should be 7392 =																// 504 / 3 * 44
	private final static long TIME_CORRECTION_AFTER = 44;

	private Instances dataset;

	private final Attribute timeAttribute;
	private Attribute xAttribute;
	private Attribute yAttribute;
	private Attribute zAttribute;

	public SDRLoader() {
		m_structure = ResultsUtil.dateAndValuesResult(Arrays.asList(new String[] { "x", "y", "z" }));
		dataset = new Instances(m_structure);
		
		timeAttribute = dataset.attribute(ResultsUtil.ATTRIBUTE_TIMESTAMP());
		//initialize value attributes
		List<Attribute> valueAttributes = ResultsUtil.findValueAttributes(dataset);
		for (Attribute attribute : valueAttributes) {
			String name = attribute.getMetadata().getProperty(ResultsUtil.META_ATTRIBUTE_NAME());
			if (name.equals("x"))
				xAttribute = attribute;
			else if (name.equals("y"))
				yAttribute = attribute;
			else if (name.equals("z"))
				zAttribute = attribute;
		}
	}
	
	@Override
	public void setSource(InputStream input) throws IOException {
		if(!(input instanceof FileInputStream))
			return;	
	}

	@Override
	public Instances getStructure() throws IOException {
		return m_structure;
	}

	@Override
	public Instances getDataSet() throws IOException {
		if(!dataset.isEmpty())
			return dataset;
		
		// Initialize position handling
		byte[] daten = new byte[BLOCKSIZE];

		// Initialize time handling
		Calendar date = new GregorianCalendar();
		Calendar timestamp = new GregorianCalendar();

		// Initialize data handling
		//TODO SDRLoader -> replace RandomAccessFile with FileInputStream? 
		RandomAccessFile in = new RandomAccessFile(m_sourceFile, "r");

		// Convert each block
		for (long i = 0; i <= 2/*in.length()*/; i++) {
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

//				datalist.add(new Data(timestamp.getTimeInMillis(), x, y, z));
				time += TIME_CORRECTION_AFTER;
				DenseInstance instance = new DenseInstance(4);
				instance.setValue(timeAttribute, timestamp.getTimeInMillis());
				instance.setValue(xAttribute, x);
				instance.setValue(yAttribute, y);
				instance.setValue(zAttribute, z);
				dataset.add(instance);
			}
			
			// Checks if the recorded data ended
			if (recordEnd(day, hour))
				break;

		}
//		log.info("Converted Data[]: " + datalist.size());
		in.close();
		dataset.sort(timeAttribute);
		return dataset;
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

	@Override
	public Instance getNextInstance(Instances structure) throws IOException {
		return null;
	}

	@Override
	public String getFileExtension() {
		return FILE_EXTENSION;
	}

	@Override
	public String[] getFileExtensions() {
		return new String[] { FILE_EXTENSION };
	}

	@Override
	public String getFileDescription() {
		return "Sendsor - Acceleration Input Format";
	}

	@Override
	public String getRevision() {
		return "";
	}
	

//	@Override
//	public ILoader getInstance(Properties properties) {
//		String pathname = properties.getProperty(FILE, "/home/muki/input.sdr");
//		SDRLoader loader = new SDRLoader();		
//		try {
//			loader.setFile(new File(pathname));
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return new SynchronizedLoader(loader);
//	}

}
