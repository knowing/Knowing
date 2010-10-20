package de.lmu.ifi.dbs.medmon.sensor.core.converter;

import java.io.IOException;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

/**
 * 
 * @author Nepomuk Seiler
 *
 * @param <E> - data entity
 * @version 0.1
 */
public interface IConverter<E> {

	/**
	 * This method may not be important
	 * @param file
	 * @param begin
	 * @param end
	 * @return
	 * @throws IOException
	 */
	@Deprecated
	public ISensorDataContainer convertToContainer(String file, int begin,int end) throws IOException;
	
	public E convertToData(Block block) throws IOException;
	
	public Block[] convertToBlock(String file, int calendarConstant) throws IOException;
}
