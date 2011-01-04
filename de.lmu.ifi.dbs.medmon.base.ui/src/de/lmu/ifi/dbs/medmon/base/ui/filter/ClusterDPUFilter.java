package de.lmu.ifi.dbs.medmon.base.ui.filter;

import java.util.List;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ClusterDPUFilter extends ViewerFilter {

	/* Search for clustering Algorithms only*/
	private boolean cluster;
	
	public boolean isCluster() {
		return cluster;
	}
	
	public void setCluster(boolean cluster) {
		this.cluster = cluster;
	}
	
	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if(!(element instanceof DataProcessingUnit))
			return false;
		if(!cluster)
			return true;
		
		DataProcessingUnit dpu = (DataProcessingUnit) element;
		List<DataProcessor> processors = dpu.getProcessors();
		DataProcessor processor = processors.get(processors.size()-1);
		IDataProcessor iProcessor= processor.loadProcessor();
		
		if(iProcessor == null || !(iProcessor instanceof IAlgorithm))
			return false;
		IAlgorithm algorithm = (IAlgorithm) iProcessor;
		
		for (String key : algorithm.analyzedDataKeys()) {
			if(key.equals(IAlgorithm.CLUSTER_DATA))
				return true;
		}
		return false;
	}

}
