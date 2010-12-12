package de.lmu.ifi.dbs.medmon.developer.ui.adaptable;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.property.DataProcessorElement;

public class DataProcessorGeneralSection extends AbstractPropertySection {

	private Text labelText;
	private DataProcessorElement processorElement;

	public DataProcessorGeneralSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		System.out.println("DataProcessorGeneralSection.createControls()");
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		labelText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelText.setLayoutData(data);
		// labelText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Label:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(labelText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}
	
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		System.out.println("DataProcessorGeneralSection.setInput()");
		super.setInput(part, selection);
		Assert.isTrue(selection instanceof IStructuredSelection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		Assert.isTrue(input instanceof DataProcessorElement);
		this.processorElement = (DataProcessorElement) input;
	}

	@Override
	public void refresh() {
		System.out.println("DataProcessorGeneralSection.refresh()");
		//labelText.removeModifyListener(listener);
		//ButtonElementProperties properties = (ButtonElementProperties) buttonElement.getAdapter(IPropertySource.class);
		//labelText.setText(properties.strText);
		//labelText.addModifyListener(listener);
	}

}
