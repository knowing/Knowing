package de.lmu.ifi.dbs.medmon.sensor.core.converter;

import java.io.IOException;
import java.util.List;

import org.eclipse.swt.widgets.Shell;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

/**
 * 
 * 
 * @author Nepomuk Seiler
 *
 * @param <E> - data entity
 * @version 0.9
 */
public interface IConverter<E> {
	
	public E[] readData(ISensorDataContainer<E> root) throws IOException;
	
	public E[] readData(Block block) throws IOException;
	
	public Block[] convertToBlock(String file, ContainerType type) throws IOException;
	
	public ISensorDataContainer<E> readFile(String file, ContainerType root, ContainerType leaf, List<ISensorDataContainer<E>> acc) throws IOException;
	
	/**
	 * 
	 * @param shell
	 * @return Path to the location
	 */
	public String openChooseInputDialog(Shell shell);
}
