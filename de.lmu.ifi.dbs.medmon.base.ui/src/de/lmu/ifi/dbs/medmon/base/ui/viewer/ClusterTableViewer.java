package de.lmu.ifi.dbs.medmon.base.ui.viewer;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.medmon.base.ui.provider.WorkbenchTableLabelProvider;
import de.lmu.ifi.dbs.medmon.base.ui.viewer.editing.DefaultClusterEditingSupport;

public class ClusterTableViewer extends TableViewer {

	private static final String[] columns = new String[] { "Name", "Beschreibung", "Default" };
	private static final int[] width = new int[] { 80, 170, 60 };
	
	public ClusterTableViewer(Composite parent) {
		super(parent);
		init();
	}

	private void init() {
		initColumns();
		initProvider();
	}

	private void initColumns() {
		getTable().setHeaderVisible(true);
		getTable().setLinesVisible(true);
		for (int i = 0; i < columns.length; i++) {
			TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.LEAD);
			// Kopfzeile und Breite der jeweiligen Spalten festlegen
			viewerColumn.getColumn().setText(columns[i]);
			viewerColumn.getColumn().setWidth(width[i]);
			// Spaltengroesse laesst sich zur Laufzeit aendern
			viewerColumn.getColumn().setResizable(true);
			// Spalten lassen sich untereinander verschieben
			viewerColumn.getColumn().setMoveable(true);
			if (i == 2) {
				viewerColumn.setEditingSupport(new DefaultClusterEditingSupport(this));
			}
		}
	}

	private void initProvider() {
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new WorkbenchTableLabelProvider());
	}

}
