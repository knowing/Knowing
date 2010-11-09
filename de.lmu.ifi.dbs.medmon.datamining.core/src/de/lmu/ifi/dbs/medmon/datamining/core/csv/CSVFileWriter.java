package de.lmu.ifi.dbs.medmon.datamining.core.csv;

import java.util.*;
import java.io.*;

/**
 * CSVFileWriter is a class derived from CSVFile used to format some fields into a new CSV file.
 *
 * @author  Fabrizio Fazzino
 * @author  Franz Graf (changed some Vector to List)
 * @see {@link http://sourceforge.net/projects/csvfile/}
 * @version %I%, %G%
 */
public class CSVFileWriter extends CSVFile {
	/**
	 * The print writer linked to the CSV file to be written.
	 */
  protected PrintWriter out;

	/**
	 * CSVFileWriter constructor just need the name of the CSV file that will be written.
	 *
	 * @param  outputFileName The name of the CSV file to be opened for writing
	 * @throws IOException    If an error occurs while creating the file
	 */
  public CSVFileWriter(String outputFileName) throws IOException {
    super();
    out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
  }

	/**
	 * CSVFileWriter constructor with a given field separator.
	 *
	 * @param outputFileName  The name of the CSV file to be opened for reading
	 * @param sep             The field separator to be used; overwrites the default one
	 * @throws IOException    If an error occurs while creating the file
	 */
  public CSVFileWriter(String outputFileName, char sep) throws IOException {
    super(sep);
    out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
  }

	/**
	 * CSVFileWriter constructor with given field separator and text qualifier.
	 *
	 * @param outputFileName  The name of the CSV file to be opened for reading
	 * @param sep             The field separator to be used; overwrites the default one
	 * @param qual            The text qualifier to be used; overwrites the default one
	 * @throws IOException    If an error occurs while creating the file
	 */
  public CSVFileWriter(String outputFileName, char sep, char qual) throws IOException {
    super(sep, qual);
    out = new PrintWriter(new BufferedWriter(new FileWriter(outputFileName)));
  }

	/**
	 * Close the output CSV file.
	 *
	 * @throws IOException If an error occurs while closing the file
	 */
  public void close() throws IOException {
    out.flush();
    out.close();
  }

  /**
   * Join the fields and write them as a new line to the CSV file.
   *
   * @param fields The vector of strings containing the fields
   */
  public void writeFields(List<String> fields) {
    int n = fields.size();
    for(int i=0; i<n; i++) {
      out.print(textQualifier + fields.get(i) + textQualifier);
      if(i<(n-1)) out.print(fieldSeparator);
    }
    out.println();
  }
}

