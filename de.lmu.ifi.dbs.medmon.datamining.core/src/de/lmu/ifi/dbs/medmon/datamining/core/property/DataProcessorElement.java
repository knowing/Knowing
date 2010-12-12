package de.lmu.ifi.dbs.medmon.datamining.core.property;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;

public class DataProcessorElement implements IPropertySource {

	private static final String PROPERTY_NAME = "dataprocessor.name";
	private static final String PROPERTY_ID = "dataprocessor.id";
	private static final String PROPERTY_PROVIDER = "dataprocessor.provider";
	private static final String PROPERTY_PARAMETER = "dataprocessor.parameter.";
	
	private final DataProcessor processor;
	private IPropertyDescriptor[] descriptors;
	
	public DataProcessorElement(DataProcessor processor) {
		this.processor = processor;
	}
	
	@Override
	public Object getEditableValue() {
		
		return null;
	}

	@Override
	public IPropertyDescriptor[] getPropertyDescriptors() {
		if(descriptors != null) 
			return descriptors;
		
		PropertyDescriptor p1 = new PropertyDescriptor(PROPERTY_NAME, "Name");
		PropertyDescriptor p2 = new PropertyDescriptor(PROPERTY_ID, "ID");
		PropertyDescriptor p3 = new PropertyDescriptor(PROPERTY_PROVIDER, "Provider");
		descriptors = new IPropertyDescriptor[] { p1, p2, p3 };
		return descriptors;
	}

	@Override
	public Object getPropertyValue(Object id) {
		if(id.equals(PROPERTY_NAME))
			return processor.getName();
		if(id.equals(PROPERTY_ID))
			return processor.getId();
		if(id.equals(PROPERTY_PROVIDER))
				return processor.getProvidedby();
		return null;
	}

	@Override
	public boolean isPropertySet(Object id) {
		
		return false;
	}

	@Override
	public void resetPropertyValue(Object id) {
				
	}

	@Override
	public void setPropertyValue(Object id, Object value) {
		
	}


}
