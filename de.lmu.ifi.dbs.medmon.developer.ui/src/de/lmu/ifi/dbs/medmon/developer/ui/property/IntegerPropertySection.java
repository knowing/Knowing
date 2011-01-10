package de.lmu.ifi.dbs.medmon.developer.ui.property;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.developer.ui.widgets.CustomScale;

public class IntegerPropertySection extends AbstractPropertySection {

	private Composite container;
	
	private IDataProcessor dataProcessor;
	private XMLDataProcessor xmlDataProcessor;
	private boolean xml;


	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		container = getWidgetFactory().createComposite(parent);
		container.setLayout(new GridLayout(2, false));
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		if (input instanceof XMLDataProcessor) {
			this.xmlDataProcessor = (XMLDataProcessor) input;
			xml = true;
		} else {
			this.dataProcessor = (IDataProcessor) input;
			xml = false;
		}

	}
	
	@Override
	public void refresh() {
		//Clean up
		if(container != null && !container.isDisposed()) {
			for (Control c : container.getChildren()) {
				c.dispose();
			}
		}
			
		Map<String, IProcessorParameter> parameters;
		if(xml) {
			parameters = xmlDataProcessor.getParameters();
		} else {
			parameters = dataProcessor.getParameters();
		}
		Map<String, NumericParameter> numeric = new HashMap<String, NumericParameter>();
		for (String key : parameters.keySet()) {
			if(parameters.get(key) instanceof NumericParameter)
				numeric.put(key, (NumericParameter) parameters.get(key));
		}
		
		for (String key : numeric.keySet()) {
			getWidgetFactory().createCLabel(container, key);
			CustomScale scale = new CustomScale(container, SWT.NONE);
			NumericParameter parameter = numeric.get(key);
			scale.setMaximum(parameter.getMaximum());
			scale.setMinimum(parameter.getMinimum());
			scale.setSelection(parameter.getValue());
			getWidgetFactory().adapt(scale, true, true);
			scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		container.layout();
	}
	
}
