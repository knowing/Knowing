package de.lmu.ifi.dbs.medmon.base.ui.provider;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterTableItem;

public class ClusterTableItemEditingSupport extends EditingSupport {

	private CellEditor editor;
	private int column;

	public ClusterTableItemEditingSupport(ColumnViewer viewer, int column) {
		super(viewer);

		// Create the correct editor based on the column index
		switch (column) {
		case 0:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
			break;
		default:
			editor = new TextCellEditor(((TableViewer) viewer).getTable());
		}
		this.column = column;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected boolean canEdit(Object element) {
		if(column == 0)
			return true;
		return false;
	}

	@Override
	protected Object getValue(Object element) {
		ClusterTableItem<?> item = (ClusterTableItem<?>)element;
		switch (column) {
		case 0:
			return item.getLabel();
		default:
			return element;
		}
	}

	@Override
	protected void setValue(Object element, Object value) {
		ClusterTableItem<?> item = (ClusterTableItem<?>)element;
		switch (column) {
		case 0:
			item.setLabel(String.valueOf(value));
			break;
		}
		getViewer().update(element, null);
	}

}
