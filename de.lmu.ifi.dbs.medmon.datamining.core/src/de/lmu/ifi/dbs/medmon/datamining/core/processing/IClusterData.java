package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public interface IClusterData extends IAnalyzedData {

	public ClusterUnit getCluster();
}
