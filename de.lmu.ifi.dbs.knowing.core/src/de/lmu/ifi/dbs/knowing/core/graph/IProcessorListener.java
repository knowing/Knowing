/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.graph;

import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public interface IProcessorListener {

	/**
	 * <p>If something changed. Normally this means the<br>
	 * processor is ready / not ready. </p>
	 * @param processor
	 */
	void processorChanged(IProcessor processor);
}
