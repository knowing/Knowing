package de.lmu.ifi.dbs.medmon.base.ui.provider;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterTableItem;

public class ClusterTableItemContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof ClusterTableItem<?>[])
			return (ClusterTableItem<?>[])inputElement;
		if(inputElement instanceof List<?>)
			return listInput((List<?>)inputElement);
		return new Object[0];
	}

	public static ClusterTableItem<?>[] listInput(List<?> list) {
		if(list.isEmpty())
			return new ClusterFile[0];
		if(!(list.get(0) instanceof ClusterTableItem<?>))
			return new ClusterTableItem<?>[0];
		
		
		ClusterTableItem<?>[] returns = new ClusterTableItem<?>[list.size()];
		int index = 0;
		for (Object clusterFile : list) 
			returns[index++] = (ClusterTableItem<?>) clusterFile;
		return returns;
	}

}
