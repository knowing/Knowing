package de.lmu.ifi.dbs.medmon.sensor.core.converter;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.RootSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.container.TimeSensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.util.TimeUtil;

public abstract class AbstractConverter<E> implements IConverter<E> {

	@Override
	public ISensorDataContainer<E> convertToContainer(String file, ContainerType root, ContainerType leaf,
			List<ISensorDataContainer<E>> acc) throws IOException {
		// Initialize first list containing Block
		if (acc == null) {
			acc = new LinkedList<ISensorDataContainer<E>>();
			Block[] blocks = convertToBlock(file, leaf);
			for (Block block : blocks)
				acc.add(new TimeSensorDataContainer<E>(leaf, block));
			System.out.println("------------------------------");
			System.out.println("First created Acc-List: " + acc);
			return convertToContainer(file, root, TimeUtil.getNext(leaf), acc);
		}

		// Recursion end
		if (root == leaf) {
			String name = file.substring(file.lastIndexOf(System.getProperty("file.separator")) + 1);
			RootSensorDataContainer<E> returns = new RootSensorDataContainer<E>(name,
					acc.toArray(new ISensorDataContainer[acc.size()]));
			System.out.println("---Merge root---");
			mergeChildren(returns);
			return returns;
		}

		int cLeaf = TimeUtil.getCalendarConstant(leaf);

		TimeSensorDataContainer<E> newContainer = new TimeSensorDataContainer<E>(leaf, null);
		LinkedList<ISensorDataContainer<E>> newAcc = new LinkedList<ISensorDataContainer<E>>();

		Calendar start = null;
		Calendar end = GregorianCalendar.getInstance();
		for (ISensorDataContainer<E> container : acc) {

			// first loop
			if (start == null) {
				start = GregorianCalendar.getInstance();
				start.setTime(container.getTimestamp());
				newContainer.addChild(container);
				continue;
			}

			end.setTime(container.getTimestamp());

			if (start.get(cLeaf) == end.get(cLeaf)) {
				// Same CalendarConstant value
				newContainer.addChild(container);
			} else {
				// Merge blocks
				mergeChildren(newContainer);
				// Fill AcclerationList
				newAcc.add(newContainer);
				// Start grouping next Containers
				newContainer = new TimeSensorDataContainer<E>(leaf);
				newContainer.addChild(container);
				// New compare date
				start.setTime(end.getTime());
			}

		}
		
		// Final merge
		mergeChildren(newContainer);
		// Fill AcclerationList
		newAcc.add(newContainer);
		
		return convertToContainer(file, root, TimeUtil.getNext(leaf), newAcc);
	}
	
	@Override
	public E[] readData(ISensorDataContainer<E> root) throws IOException {
		Block block = root.getBlock();
		if(block == null)
			block = mergeChildren(root);			
		return readData(block);
	}
	
	protected Block mergeChildren(ISensorDataContainer<E> root) {
		Block returns = null;
		for (ISensorDataContainer<E> container : root.getChildren()) {
			returns = container.getBlock().merge(returns);
		}
		root.setBlock(returns);
		return returns;
	}
}
