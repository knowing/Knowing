package de.lmu.ifi.dbs.knowing.core.graph;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.IFactory;


/**
 * Represents a Node in a Processing-Graph stored in a {@link XDataProcessingUnit}
 * 
 * @author Nepomuk Seiler
 *
 */
public interface INode extends INodeListener {
	
	/**
	 * If the node is ready and everything is properly initialized,
	 * than the process is started.
	 * @throws Exception
	 */
	void run() throws Exception;
	
	/**
	 * @return true if the node was properly initialized
	 */
	boolean isReady();
	
	
	/**
	 * Initialize this node. Try to instance the give service via
	 * his corresponding {@link IFactory}. Uses the properties stored
	 * in the DataProcessingUnit
	 */
	void initialize();
	
	/**
	 * 
	 * @param supervisor
	 */
	void setSupervisor(GraphSupervisor supervisor);
				
	
	/**
	 * Human readable name
	 * @return Node's name: e.g. The {@link IDataProcessor} name
	 */
	String getName();
	
	/**
	 * A unique identifier.
	 * 
	 * Should be composed by <code>getName()</code> and incremented int
	 * @return
	 */
	String getNodeId();

	/**
	 * The serviceFactory this node represents
	 * @return
	 */
	String getFactoryId();
	
	/**
	 * 
	 * @return
	 */
	Properties getProperties();
	
	/**
	 * Loading parameters from an external source.<br>
	 * This method should call initialize after finishing. 
	 * @param in - the properties source
	 */
	void setProperties(Properties properties);
		
	void addNodeListener(INodeListener listener);
	
	void removeNodeListener(INodeListener listener);
	
	INode clone();
}
