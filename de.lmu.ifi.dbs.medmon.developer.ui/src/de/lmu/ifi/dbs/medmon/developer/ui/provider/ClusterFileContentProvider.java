package de.lmu.ifi.dbs.medmon.developer.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterFile;

public class ClusterFileContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof ClusterFile[])
			return (ClusterFile[])inputElement;
		if(inputElement instanceof List<?>)
			return listInput((List<?>)inputElement);
		return new Object[0];
	}

	public static ClusterFile[] listInput(List<?> list) {
		if(list.isEmpty())
			return new ClusterFile[0];
		if(!(list.get(0) instanceof ClusterFile))
			return new ClusterFile[0];
		
		
		ClusterFile[] returns = new ClusterFile[list.size()];
		int index = 0;
		for (Object clusterFile : list) 
			returns[index++] = (ClusterFile) clusterFile;
		return returns;
	}

}
