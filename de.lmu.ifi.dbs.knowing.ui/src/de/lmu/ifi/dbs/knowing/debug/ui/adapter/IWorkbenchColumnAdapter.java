package de.lmu.ifi.dbs.knowing.debug.ui.adapter;

import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.model.IWorkbenchAdapter;

public interface IWorkbenchColumnAdapter extends IWorkbenchAdapter {

	public String getColumnText(Object element, int columnIndex);
	
	public Image getColumnImage(Object element, int columnIndex);

}
