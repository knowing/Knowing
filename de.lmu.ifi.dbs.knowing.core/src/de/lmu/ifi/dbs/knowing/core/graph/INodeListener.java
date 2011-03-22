package de.lmu.ifi.dbs.knowing.core.graph;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 11.03.2011
 */
public interface INodeListener {

	/**
	 * 
	 * @param event
	 */
	void nodeChanged(NodeEvent event);
}
