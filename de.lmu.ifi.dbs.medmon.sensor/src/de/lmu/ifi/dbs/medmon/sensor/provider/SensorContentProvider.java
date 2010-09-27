package de.lmu.ifi.dbs.medmon.sensor.provider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;

/**
 * Can be used for Tree and TableViewers
 * @author Nepomuk Seiler
 *
 */
public class SensorContentProvider implements  ITreeContentProvider {

	
	@Override
	public Object[] getElements(Object inputElement) {
		if(inputElement instanceof Data[]){
			System.out.println("SensorData: " + inputElement);
			return (Data[])inputElement;
		} else if(inputElement instanceof ISensorDataContainer){
			ISensorDataContainer node = (ISensorDataContainer)inputElement;
			if(node.hasChildren())
				return node.getChildren();
			return node.getSensorData();
		}	
		return new Object[0];
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if(parentElement instanceof ISensorDataContainer) {
			ISensorDataContainer node = (ISensorDataContainer)parentElement;
			if(node.hasChildren())
				return node.getChildren();
			return node.getSensorData();
		}
		return new Object[0];
	}

	@Override
	public Object getParent(Object element) {
		if(element instanceof ISensorDataContainer)
			return ((ISensorDataContainer)element).getParent();
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if(element instanceof ISensorDataContainer) {
			ISensorDataContainer node = (ISensorDataContainer)element;
			//Provides a detail view to see every SensorData Element
			return node.hasChildren() || (node.getSensorData() != null);
		}	
		return false;
	}
	
	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
