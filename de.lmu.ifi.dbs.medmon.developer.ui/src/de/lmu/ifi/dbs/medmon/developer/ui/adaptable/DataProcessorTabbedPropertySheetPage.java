package de.lmu.ifi.dbs.medmon.developer.ui.adaptable;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.views.properties.IPropertySheetPage;

public class DataProcessorTabbedPropertySheetPage extends Page implements IPropertySheetPage {

	private Composite container;

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		System.out.println("Selection changed: " + selection);
	}

	@Override
	public void createControl(Composite parent) {
		System.out.println("DataProcessorTabbedPropertySheetPage.createControl()");
		System.out.println("Layout: " + parent.getLayout());
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());
		new Label(container, SWT.NONE).setText("Test");
	}

	@Override
	public Control getControl() {
		return container;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
