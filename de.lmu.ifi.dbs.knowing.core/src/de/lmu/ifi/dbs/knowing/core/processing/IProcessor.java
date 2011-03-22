package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import weka.core.CapabilitiesHandler;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.graph.IProcessorListener;
import de.lmu.ifi.dbs.knowing.core.query.IQueryListener;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * <p>An IProcessor encapsulates a data processing algorithm.
 * The main purpose is to ensure a highly parallel and robust
 * execution.</p>
 * 
 * <p>The main concept behind this interface is question-&-answer.
 * Queries a executed and answered asynchronous.
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 */
public interface IProcessor extends IQueryListener, CapabilitiesHandler {
	
	/**
	 * <p>This method build the internal model which is used<br>
	 * to answer queries.</p>
	 * 
	 * <p>The build process should be implemented in an own<br>
	 * thread, so other processors could build up their models<br>
	 * too.</p>
	 * 
	 * <p>Calling this method more than once should generate a<br>
	 * new model based on the old one, instead of building a<br>
	 * completly new model. For reseting the model use {@link #resetModel()}</p>
	 * 
	 * @param loader - to get the dataset
	 */
	void buildModel(ILoader loader);
	
	/**
	 * <p>This method build the internal model which is used<br>
	 * to answer queries. For the generation it uses the given<br>
	 * {@link IProcessor} by querying it.</p>
	 * 
	 * <p>The build process should be implemented in an own<br>
	 * thread, so other processors could build up their models<br>
	 * too.</p>
	 *
	 * <p>Calling this method more than once should generate a<br>
	 * new model based on the old one, instead of building a<br>
	 * completly new model. For reseting the model use {@link #resetModel()}</p>
	 * 
	 * @param processor - query for model generation
	 */
	void buildModel(IProcessor processor);
	
	/**
	 * Reset the model if something goes wrong during generation.
	 */
	void resetModel();
	
	/**
	 * @return true if this processor can be queried
	 */
	boolean isReady();
	
	/**
	 * <p>A listener listening to the current state of the processor.<br>
	 * This is used for processing graphs. </p>
	 * @param listener
	 */
	void setProcessorListener(IProcessorListener listener);
	
	
	/* ======================= */
	/* ===== Persistence ===== */
	/* ======================= */
	
	void persistModel(OutputStream out);
	
	void loadModel(InputStream in);
	
	/* ======================= */
	/* ======== Query ======== */
	/* ======================= */
	
	/**
	 * <p>A query is answered via the interal model build by the buildModel method.<br>
	 * The question should be proposed asynchronous and the answer will be sent<br>
	 * asynchronous as well to the IQueryListener.</p> 
	 * 
	 * <p>Every query should run in it's own thread.</p>
	 * 
	 * @param ticket - the ticket to work with
	 * @throws InterruptedException 
	 * @see QueryTicket
	 * @see QueryResult
	 */
	void query(QueryTicket ticket) throws InterruptedException;
	
	Instances[] supportedQueries();
	
	
	/* ======================= */
	/* ==== Configuration ==== */
	/* ======================= */
	
	/**
	 * <p>The returned Object should not be used for setting properties.<br>
	 * For this purpose use the given delegates.</p>
	 * 
	 * @return Properties - Default Properties
	 */
	Properties getProperties();
	
	void setProperty(String key, String value);
	
	String getProperty(String key);
	
	/**
	 * Checks if the IProcessor was properly configured.
	 * 
	 * @return Array of error messages
	 */
	String[] validate();
	
}
