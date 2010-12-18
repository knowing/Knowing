package de.lmu.ifi.dbs.medmon.developer.ui.analyzed;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;

public class TableAnalyzedData implements IAnalyzedData {

	private List<String>  colnames = new ArrayList<String>();
	private List<Integer> colwidth = new ArrayList<Integer>();
	private List<Boolean> colresiz = new ArrayList<Boolean>();
	private List<Boolean> colmovea = new ArrayList<Boolean>();
	
	private List<String[]> rows = new LinkedList<String[]>();
		
	public void addRow(String... values) {
		rows.add(values);
	}
	
	public void addColumn(String name, int width, boolean resizeable, boolean moveable) {
		colnames.add(name);
		colwidth.add(width);
		colresiz.add(resizeable);
		colmovea.add(moveable);
	}
	
	@Override
	public void createContent(Composite parent) {
		createViewer(parent);
	}
	
	private TableViewer createViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		createColumns(viewer);
		viewer.setContentProvider(new TableContentProvider());
		viewer.setLabelProvider(new TableLabelProvider());
		
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		return viewer;
	}

	private void createColumns(TableViewer viewer) {
		for (int i = 0; i < colnames.size(); i++) {
			final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
			final TableColumn column = viewerColumn.getColumn();
			column.setText(colnames.get(i));
			column.setWidth(colwidth.get(i));
			column.setResizable(colresiz.get(i));
			column.setMoveable(colmovea.get(i));
		}
		
	}
	
	private class TableContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {	}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {	}

		@Override
		public Object[] getElements(Object inputElement) {
			List<String[]> list = (List<String[]>) inputElement;
			return list.toArray(new String[list.size()][]);
		}
		
	}
	
	private class TableLabelProvider extends LabelProvider implements ITableLabelProvider {

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			String[] value = (String[]) element;
			return value[columnIndex];
		}
		
	}

}