package de.lmu.ifi.dbs.medmon.developer.ui.property;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class ClusterParameterPropertySection extends AbstractPropertySection {

	private IDataProcessor dataProcessor;


	
	@Override
	public void createControls(Composite parent, TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		
	}
	
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
        super.setInput(part, selection);
        Assert.isTrue(selection instanceof IStructuredSelection);
        Object input = ((IStructuredSelection) selection).getFirstElement();
	}
	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		super.refresh();
	}

}
