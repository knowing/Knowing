package de.lmu.ifi.dbs.medmon.developer.ui.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class GeneralPropertySection extends AbstractPropertySection {

	private Text tName;
	private Text tProvider;
	private Text tID;

	private IDataProcessor dataProcessor;
	private XMLDataProcessor xmlDataProcessor;

	private boolean xml;

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		Composite composite = getWidgetFactory().createComposite(parent);
		composite.setLayout(new GridLayout(2, false));

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Name:"); //$NON-NLS-1$
		tName = getWidgetFactory().createText(composite, "", SWT.READ_ONLY); //$NON-NLS-1$

		getWidgetFactory().createCLabel(composite, "ID:"); //$NON-NLS-1$
		tID = getWidgetFactory().createText(composite, "", SWT.READ_ONLY); //$NON-NLS-1$

		getWidgetFactory().createCLabel(composite, "Provider:"); //$NON-NLS-1$
		tProvider = getWidgetFactory().createText(composite, "", SWT.READ_ONLY); //$NON-NLS-1$
	}

	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		// Assert.isTrue(input instanceof IDataProcessor);
		if (input instanceof XMLDataProcessor) {
			this.xmlDataProcessor = (XMLDataProcessor) input;
			xml = true;
		} else {
			this.dataProcessor = (IDataProcessor) input;
			xml = false;
		}

	}

	public void refresh() {
		if(xml) {
			tName.setText(xmlDataProcessor.getName());
			tID.setText(xmlDataProcessor.getId());
			tProvider.setText(xmlDataProcessor.getProvidedby());
		} else {
			tName.setText(dataProcessor.getName());
			tID.setText(dataProcessor.getId());
			tProvider.setText(dataProcessor.getClass().getPackage().toString());
		}

	}
}
