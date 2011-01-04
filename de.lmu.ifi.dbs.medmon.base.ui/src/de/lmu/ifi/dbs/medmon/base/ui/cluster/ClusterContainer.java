package de.lmu.ifi.dbs.medmon.base.ui.cluster;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class ClusterContainer extends ClusterTableItem<ISensorDataContainer> {

	public ClusterContainer(String label, ISensorDataContainer source) {
		super(label, source);
	}
}
