package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.List;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

/**
 * Represents a Node in a Processing-Graph stored in a {@link XDataProcessingUnit}
 * 
 * @author Nepomuk Seiler
 *
 */
public interface INode {

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
	 * @return Edges pointing a this node
	 */
	List<Edge> getEdgesIn();
	
	/**
	 * @return Outgoing edges
	 */
	List<Edge> getEdgesOut();
	
	/**
	 * Every node encapsulates a data-object.
	 * 
	 * @return the information hold by this node
	 */
	Object getData();
	
}
