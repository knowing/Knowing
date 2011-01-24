package de.lmu.ifi.dbs.medmon.base.ui.viewer.editing;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class SensorPathEditingSupport extends EditingSupport {


	public SensorPathEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new PathDialogCellEditor(((TableViewer)getViewer()).getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String defaultPath = ((SensorAdapter)element).getDefaultPath();
		System.out.println("GetValue:" + element);
		if(defaultPath == null)
			return "";
		return defaultPath;
	}

	@Override
	protected void setValue(Object element, Object value) {
		System.out.println("SetValue: " + element);
		SensorAdapter adapter = (SensorAdapter)element;
		adapter.setDefaultPath((String) value);
		getViewer().refresh();
		//TODO something changed!
	}

}
