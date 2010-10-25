package de.lmu.ifi.dbs.medmon.sensor.core.converter;

import java.io.IOException;

import org.eclipse.swt.widgets.Shell;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

/**
 * 
 * Use<br>
 * 	<p><code>
 *  ISensor<?> sensor = ...;
 *  IConverter converter = sensor.getConverter(); <br>
 *	Block[] blocks =converter.convertToBlock(tSDRFile.getText(), Calendar.HOUR_OF_DAY); <br>
 *	RootSensorDataContainer root = new RootSensorDataContainer(); <br>
 *  converter.parseBlockToContainer(root, blocks);	 <br>
 *  </code><p>	
 * 
 * @author Nepomuk Seiler
 *
 * @param <E> - data entity
 * @version 0.4
 */
public interface IConverter<E> {

	public ISensorDataContainer<E> parseBlockToContainer(ISensorDataContainer<E> parent, Block[] blocks) throws IOException;
	
	public E[] parseBlockToData(Block block) throws IOException;
	
	/**
	 * 
	 * @param file
	 * @param calendarConstant: Calendar.HOUR_OF_DAY, Calendar.DAY_OF_YEAR
	 * @return Block[] containting the blocks parsed with the given calendarConstant
	 * @throws IOException
	 */
	public Block[] convertToBlock(String file, int calendarConstant) throws IOException;
	
	/**
	 * 
	 * @param shell
	 * @return Path to the location
	 */
	public String openChooseInputDialog(Shell shell);
}
