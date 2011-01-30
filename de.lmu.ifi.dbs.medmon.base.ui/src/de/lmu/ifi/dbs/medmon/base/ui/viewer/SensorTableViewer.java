package de.lmu.ifi.dbs.medmon.base.ui.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.base.ui.provider.WorkbenchTableLabelProvider;
import de.lmu.ifi.dbs.medmon.base.ui.viewer.editing.SensorPathEditingSupport;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorDaemon;

public class SensorTableViewer extends TableViewer implements PropertyChangeListener {

	private static final String[] columns = new String[] { "Name", "Version", "Typ", "Pfad", "Status" };
	private static final int[] width = new int[] { 120, 70, 60, 150, 50 };

	public SensorTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}

	public SensorTableViewer(Composite parent, int style, IStructuredSelection initialSelection) {
		super(parent, style);
		init();
		if (initialSelection != null && !initialSelection.isEmpty())
			setSelection(initialSelection);
	}

	public SensorTableViewer(Table table) {
		super(table);
		init();
	}

	private void init() {
		initColumns();
		initProvider();
		initInput();
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
			if (i == 3) {
				viewerColumn.setEditingSupport(new SensorPathEditingSupport(this));
			}
		}
	}

	private void initProvider() {
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new WorkbenchTableLabelProvider());
	}

	private void initInput() {
		SensorDaemon daemon = SensorDaemon.getInstance();
		//TODO wait for daemon not blocking the GUI Thread
		Map<String, SensorAdapter> model = daemon.getModel();
		if (model != null)
			setInput(model.values().toArray());
		daemon.addPropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				// TODO Refresh Input too
				if(!getTable().isDisposed())
					refresh();
			}
		});
	}

}
