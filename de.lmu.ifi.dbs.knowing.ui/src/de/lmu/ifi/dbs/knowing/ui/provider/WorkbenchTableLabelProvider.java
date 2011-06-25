package de.lmu.ifi.dbs.knowing.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import de.lmu.ifi.dbs.knowing.ui.adapter.IWorkbenchColumnAdapter;


public class WorkbenchTableLabelProvider extends WorkbenchLabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		// obtain the base image by querying the element
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null || !(adapter instanceof IWorkbenchColumnAdapter)) {
			return null;
		}

		IWorkbenchColumnAdapter colAdapter = (IWorkbenchColumnAdapter) adapter;
		return colAdapter.getColumnImage(element, columnIndex);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		// obtain the base image by querying the element
		IWorkbenchAdapter adapter = getAdapter(element);
		if (adapter == null || !(adapter instanceof IWorkbenchColumnAdapter)) {
			return "";
		}
		if (!(adapter instanceof IWorkbenchColumnAdapter)) {
			return adapter.getLabel(element);
		}
		IWorkbenchColumnAdapter colAdapter = (IWorkbenchColumnAdapter) adapter;
		return colAdapter.getColumnText(element, columnIndex);
	}

}
