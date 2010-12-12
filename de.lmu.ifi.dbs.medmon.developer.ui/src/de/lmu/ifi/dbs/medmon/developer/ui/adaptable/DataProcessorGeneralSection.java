package de.lmu.ifi.dbs.medmon.developer.ui.adaptable;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertyConstants;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

public class DataProcessorGeneralSection extends AbstractPropertySection {

	private Text labelText;

	public DataProcessorGeneralSection() {
	}

	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		Composite composite = getWidgetFactory().createFlatFormComposite(parent);
		FormData data;

		labelText = getWidgetFactory().createText(composite, ""); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, STANDARD_LABEL_WIDTH);
		data.right = new FormAttachment(100, 0);
		data.top = new FormAttachment(0, ITabbedPropertyConstants.VSPACE);
		labelText.setLayoutData(data);
		//labelText.addModifyListener(listener);

		CLabel labelLabel = getWidgetFactory().createCLabel(composite, "Label:"); //$NON-NLS-1$
		data = new FormData();
		data.left = new FormAttachment(0, 0);
		data.right = new FormAttachment(labelText, -ITabbedPropertyConstants.HSPACE);
		data.top = new FormAttachment(labelText, 0, SWT.CENTER);
		labelLabel.setLayoutData(data);
	}

}
