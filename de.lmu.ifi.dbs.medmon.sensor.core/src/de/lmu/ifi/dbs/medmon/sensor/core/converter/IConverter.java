package de.lmu.ifi.dbs.medmon.sensor.core.converter;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

/**
 * 
 * @author Nepomuk Seiler
 *
 * @param <E> - data entity
 * @version 0.4
 */
public interface IConverter<E> {

	public ISensorDataContainer<E> parseBlockToContainer(ISensorDataContainer<E> parent, Block[] blocks) throws IOException;
	
	public E[] parseBlockToData(Block block) throws IOException;
	
	public Block[] convertToBlock(String file, int calendarConstant) throws IOException;
	
	/**
	 * 
	 * @param shell
	 * @return Path to the location
	 */
	public String openChooseInputDialog(Shell shell);
}
