package de.lmu.ifi.dbs.medmon.datamining.core.analyzed;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IClusterData;

public class ClusterAnalyzedData implements IClusterData {

	private final ClusterUnit cu = new ClusterUnit();
	
	@Override
	public void createContent(Composite parent) {

	}

	@Override
	public void dispose() {
		
	}

	@Override
	public ClusterUnit getCluster() {
		return cu;
	}

	public void setName(String name) {
		cu.setName(name);
	}

	public List<DoubleCluster> getClusterlist() {
		return cu.getClusterlist();
	}

	public void setClusterlist(List<DoubleCluster> clusterlist) {
		cu.setClusterlist(clusterlist);
	}

	public void addCluster(DoubleCluster cluster) {
		cu.addCluster(cluster);
	}
	
	

}
