package de.lmu.ifi.dbs.medmon.patient.service.component;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.xml.bind.PropertyException;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.service.component.ComponentContext;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

/**
 * PatientService declarative Service.
 * 
 * @author Nepomuk Seiler
 * @version 1.1
 */
public class PatientService implements IPatientService {
		
	private ComponentContext context;
	private HashMap<String, Object> selections;
	
	private PropertyChangeSupport support = new PropertyChangeSupport(this);
	
	@Override
	public boolean setSelection(ISelection selection) {
		if(selection.isEmpty())
			return false;
		if(selection instanceof IStructuredSelection)
			return handleIStructuredSelection((IStructuredSelection) selection);
		return false;
	}
	
	@Override
	public boolean setSelection(Object object, String clazz) {
		StructuredSelection selection = new StructuredSelection(object);
		return handleIStructuredSelection(selection);
	}
	
	/**
	 * Checks the selected elements
	 * @param selection
	 * @return true if selection was successfully set
	 */
	private boolean handleIStructuredSelection(IStructuredSelection selection) {
		Object first = selection.getFirstElement();
		if(first instanceof Patient) {
			selections.clear();			//Reset Selections if new patient is being set
			selections.put(PATIENT, (Patient)first);
		} else if (first instanceof IAlgorithm) {
			support.firePropertyChange(ALGORITHM, selections.get(ALGORITHM), first);
			selections.put(ALGORITHM, (IAlgorithm)first);
		} else if(first instanceof IAnalyzedData) {
			support.firePropertyChange(ANALYZED_DATA, selections.get(ANALYZED_DATA), first);
			selections.put(ANALYZED_DATA, (IAnalyzedData)first);
		} else if(first instanceof Data) {
			support.firePropertyChange(SENSOR_DATA, selections.get(SENSOR_DATA), first);
			selections.put(SENSOR_DATA, convert(selection.toArray()));
		} else if(first instanceof ISensorDataContainer) {
			support.firePropertyChange(SENSOR_CONTAINER, selections.get(SENSOR_CONTAINER), first);
			selections.put(SENSOR_CONTAINER, (ISensorDataContainer)first);
		} else if(first instanceof ISensor) {
			support.firePropertyChange(SENSOR, selections.get(SENSOR), first);
			selections.put(SENSOR, (ISensor)first);
		} else {
			return false;
		}
			
		return true;
	}
	
	private Data[] convert(Object[] selection) {
		Data[] data = new Data[selection.length];
		for(int i=0; i<selection.length; i++)
			data[i] = (Data)selection[i];
		return data;
	}

	@Override
	public Object getSelection(String clazz) {
		return selections.get(clazz);
	}
	
	@Override
	public void addPropertyChangeListener(String property,PropertyChangeListener listener) {
		if(property.equals(ALGORITHM) ||
				property.equals(ANALYZED_DATA) ||
				property.equals(PATIENT) ||
				property.equals(SENSOR) ||
				property.equals(SENSOR_CONTAINER) ||
				property.equals(SENSOR_DATA))
		support.addPropertyChangeListener(property, listener);
		else
			throw new IllegalArgumentException("Value " + property + " is invalid. Choose from IPatientService");
	}
	
	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		support.removePropertyChangeListener(listener);
	}
	
    protected void activate(ComponentContext context){
        this.context = context;
        selections = new HashMap<String, Object>();
        System.out.println("PatientServiceComponent activated");

    }

    protected void deactivate(ComponentContext context){
        this.context = null;
        selections = null;
    }    
}
