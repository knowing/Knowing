package de.lmu.ifi.dbs.medmon.datamining.core.csv;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;

/**
 * CSVFileReader is a class derived from CSVFile used to parse an existing CSV
 * file.
 * <p>
 * Adapted from a C++ original that is Copyright (C) 1999 Lucent Technologies.<br>
 * Excerpted from 'The Practice of Programming' by Brian Kernighan and Rob Pike.
 * <p>
 * Included by permission of the <a
 * href="http://tpop.awl.com/">Addison-Wesley</a> web site, which says: <cite>
 * "You may use this code for any purpose, as long as you leave the copyright notice and book citation attached"
 * </cite>.
 * 
 * @author Brian Kernighan and Rob Pike (C++ original)
 * @author Ian F. Darwin (translation into Java and removal of I/O)
 * @author Ben Ballard (rewrote handleQuotedField to handle double quotes and
 *         for readability)
 * @author Fabrizio Fazzino (added integration with CSVFile, handling of
 *         variable textQualifier and Vector with explicit String type)
 * @author Franz Graf (added method to split strings, changed some Vector to
 *         List, added constructors)
 * @see {@link http://sourceforge.net/projects/csvfile/}
 * @version %I%, %G%
 */
public class CSVFileReader extends CSVFile {

	/**
	 * The buffered reader linked to the CSV file to be read.
	 */
	protected BufferedReader in;

	/**
	 * empty CSVFileReader constructor. For use of strings.
	 */
	public CSVFileReader() {
		super();
	}

	/**
	 * CSVFileReader constructor just need the name of the existing CSV file
	 * that will be read.
	 * 
	 * @param inputFileName
	 *            The name of the CSV file to be opened for reading
	 * @throws FileNotFoundException
	 *             If the file to be read does not exist
	 */
	public CSVFileReader(String inputFileName) throws FileNotFoundException {
		super();
		in = new BufferedReader(new FileReader(inputFileName));
	}

	/**
	 * CSVFileReader constructor with a given field separator.
	 * 
	 * @param inputFileName
	 *            The name of the CSV file to be opened for reading
	 * @param sep
	 *            The field separator to be used; overwrites the default one
	 * @throws FileNotFoundException
	 *             If the file to be read does not exist
	 */
	public CSVFileReader(String inputFileName, char sep) throws FileNotFoundException {
		super(sep);
		in = new BufferedReader(new FileReader(inputFileName));
	}

	/**
	 * CSVFileReader constructor with given field separator and text qualifier.
	 * 
	 * @param inputFileName
	 *            The name of the CSV file to be opened for reading
	 * @param sep
	 *            The field separator to be used; overwrites the default one
	 * @param qual
	 *            The text qualifier to be used; overwrites the default one
	 * @throws FileNotFoundException
	 *             If the file to be read does not exist
	 */
	public CSVFileReader(String inputFileName, char sep, char qual) throws FileNotFoundException {
		super(sep, qual);
		in = new BufferedReader(new FileReader(inputFileName));
	}

	/**
	 * CSVFileReader constructor with a given field separator.
	 * 
	 * @param inputFile
	 *            The CSV file to be opened for reading
	 * @param sep
	 *            The field separator to be used; overwrites the default one
	 * @throws FileNotFoundException
	 *             If the file to be read does not exist
	 */
	public CSVFileReader(File inputFile, char string) throws FileNotFoundException {
		this(inputFile.getAbsolutePath(), string);
	}
	
	/**
	 * CSVFileReader constructor with a given field separator.
	 * 
	 * @param inputFileName
	 *            The name of the CSV file to be opened for reading
	 * @param sep
	 *            The field separator to be used; overwrites the default one
	 * @throws FileNotFoundException
	 *             If the file to be read does not exist
	 */
	public CSVFileReader(String inputFileName, CSVDescriptor descriptor) throws FileNotFoundException {
		this(inputFileName, descriptor.getFieldSeparator(), descriptor.getTextQualifier());
		this.descriptor = descriptor;
	}

	/**
	 * Split the next line of the input CSV file into fields.
	 * <p>
	 * This is currently the most important function of the package.
	 * 
	 * @return List of strings containing each field from the next line of the
	 *         file
	 * @throws IOException
	 *             If an error occurs while reading the new line from the file
	 */
	public List<String> readFields() throws IOException {
		return readFields(in.readLine());
	}

	public List<String> readFields(String line) {
		if (line == null) {
			return null;
		}
		List<String> fields = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();

		if (line.length() == 0) {
			fields.add(line);
			return fields;
		}

		int i = 0;
		do {
			sb.setLength(0);
			if (i < line.length() && line.charAt(i) == textQualifier) {
				i = handleQuotedField(line, sb, ++i); // skip quote
			} else {
				i = handlePlainField(line, sb, i);
			}
			fields.add(sb.toString());
			i++;
		} while (i < line.length());

		return fields;

	}

	public Map<Integer, Object> readFieldsToMap() throws IOException, NumberFormatException, ParseException {
		Map<Integer, Object> returns = new HashMap<Integer, Object>();
		List<String> fields = readFields();
		int position = 0;
		for (String value : fields) {
			Class clazz = descriptor.getField(position);
			
			if (clazz == null)
				returns.put(position++, value);
			else
				returns.put(position++, castField(value, clazz));
		}
		return returns;
	}

	protected Object castField(String value, Class clazz) throws NumberFormatException, ParseException {
			if (clazz.equals(Double.class))
				return Double.valueOf(value);
			if (clazz.equals(Date.class))
				return new SimpleDateFormat(descriptor.getDatePattern()).parseObject(value); 
		return value;
	}

	/**
	 * Close the input CSV file.
	 * 
	 * @throws IOException
	 *             If an error occurs while closing the file
	 */
	public void close() throws IOException {
		in.close();
	}

	/**
	 * Handles a quoted field.
	 * 
	 * @return index of next separator
	 */
	protected int handleQuotedField(String s, StringBuffer sb, int i) {
		int j;
		int len = s.length();
		for (j = i; j < len; j++) {
			if ((s.charAt(j) == textQualifier) && (j + 1 < len)) {
				if (s.charAt(j + 1) == textQualifier) {
					j++; // skip escape char
				} else if (s.charAt(j + 1) == fieldSeparator) { // next
																// delimiter
					j++; // skip end quotes
					break;
				}
			} else if ((s.charAt(j) == textQualifier) && (j + 1 == len)) { // end
																			// quotes
																			// at
																			// end
																			// of
																			// line
				break; // done
			}
			sb.append(s.charAt(j)); // regular character
		}
		return j;
	}

	/**
	 * Handles an unquoted field.
	 * 
	 * @return index of next separator
	 */
	protected int handlePlainField(String s, StringBuffer sb, int i) {
		int j = s.indexOf(fieldSeparator, i); // look for separator
		if (j == -1) { // none found
			sb.append(s.substring(i));
			return s.length();
		} else {
			sb.append(s.substring(i, j));
			return j;
		}
	}
}
