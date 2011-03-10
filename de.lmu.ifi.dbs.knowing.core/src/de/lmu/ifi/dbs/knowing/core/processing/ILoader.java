package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import weka.core.Instances;
import weka.core.converters.Loader;
import weka.core.converters.URLSourcedLoader;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

public interface ILoader {

	/**
	 * 
	 * @throws Exception
	 * @see {@link Loader}
	 */
	void reset() throws Exception;
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @throws IOException
	 * @see {@link Loader}
	 */
	void getStructure(QueryTicket ticket) throws IOException;
	
	/**
	 * 
	 * @param listener
	 * @return
	 * @throws IOException
	 * @see {@link Loader}
	 */
	void getDataSet(QueryTicket ticket) throws IOException;;
	
	/**
	 * 
	 * @param structure
	 * @return
	 * @throws IOException
	 * @see {@link Loader}
	 */
	void getNextInstance(Instances structure) throws IOException;
	
	/**
	 * 
	 * @param input
	 * @throws IOException
	 * @see {@link Loader}
	 */
	void setSource(InputStream input) throws IOException;
	
	/**
	 * 
	 * @param file
	 * @throws IOException
	 * @see {@link Loader}
	 */
	void setSource(File file) throws IOException;
	
	/**
	 * 
	 * @param url
	 * @throws Exception
	 * @see {@link URLSourcedLoader}
	 */
	void setURL(String url) throws Exception;

}
