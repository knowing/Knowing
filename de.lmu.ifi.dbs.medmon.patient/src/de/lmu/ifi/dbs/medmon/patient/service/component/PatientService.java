package de.lmu.ifi.dbs.medmon.patient.service.component;

import java.util.HashMap;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.osgi.service.component.ComponentContext;

import de.lmu.ifi.dbs.medmon.algorithm.extension.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.algorithm.extension.ISensorDataAlgorithm;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.SensorData;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

/**
 * PatientService declarative Service.
 * @author muki
 * @version 1.0
 */
public class PatientService implements IPatientService {
		
	private ComponentContext context;
	private HashMap<String, Object> selections;
	
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
		}
		else if (first instanceof ISensorDataAlgorithm)
			selections.put(ALGORITHM, (ISensorDataAlgorithm)first);
		else if(first instanceof IAnalyzedData)
			selections.put(ANALYZED_DATA, (IAnalyzedData)first);
		else if(first instanceof SensorData)
			selections.put(SENSOR_DATA, convert(selection.toArray()));
		else
			return false;
		return true;
	}
	
	private SensorData[] convert(Object[] selection) {
		SensorData[] data = new SensorData[selection.length];
		for(int i=0; i<selection.length; i++)
			data[i] = (SensorData)selection[i];
		return data;
	}

	@Override
	public Object getSelection(String clazz) {
		return selections.get(clazz);
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
