package de.lmu.ifi.dbs.medmon.developer.ui.property;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class ParameterPropertySection extends AbstractPropertySection {

	protected Composite container;

	protected IDataProcessor dataProcessor;
	protected XMLDataProcessor xmlDataProcessor;
	
	protected Map<String, IProcessorParameter> parameters = new HashMap<String, IProcessorParameter>();
		
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		container = getWidgetFactory().createComposite(parent);
		container.setLayout(new GridLayout(2, false));
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue((input instanceof XMLDataProcessor) || (input instanceof IDataProcessor));
		if (input instanceof XMLDataProcessor) {
			xmlDataProcessor = (XMLDataProcessor) input;
			parameters = xmlDataProcessor.getParameters();
			dataProcessor = null;
		} else {
			dataProcessor = (IDataProcessor) input;
			parameters = dataProcessor.getParameters();
			xmlDataProcessor = null;
			
		}
	}
	
	@Override
	public void refresh() {
		cleanContainer();			
	}
		
	public void cleanContainer() {
		// Clean up
		if (container != null && !container.isDisposed()) {
			for (Control c : container.getChildren()) {
				c.dispose();
			}
		}
		container.layout();
	}	
		
}
