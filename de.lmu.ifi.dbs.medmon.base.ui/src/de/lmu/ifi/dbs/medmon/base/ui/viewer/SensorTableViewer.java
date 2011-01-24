package de.lmu.ifi.dbs.medmon.base.ui.viewer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.base.ui.provider.WorkbenchTableLabelProvider;
import de.lmu.ifi.dbs.medmon.base.ui.viewer.editing.SensorPathEditingSupport;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorDaemon;

public class SensorTableViewer extends TableViewer implements PropertyChangeListener {

	//TODO do not extend TableViewer, delegate!
	public static final int COL_NAME 	= 0;
	public static final int COL_VERSION = 1;
	public static final int COL_TYPE 	= 2;
	
	private static final String[] columns = new String[] { "Name", "Version", "Typ", "Pfad", "<>" };
	private static final int[] width = new int[] {120,70, 100,150, 50};
	
	public SensorTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}
	
	public SensorTableViewer(Composite parent, int style, IStructuredSelection initialSelection) {
		super(parent, style);
		init();
		if(initialSelection != null && !initialSelection.isEmpty())
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
		for(int i=0; i < columns.length; i++) {
			TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.LEAD);
			// Kopfzeile und Breite der jeweiligen Spalten festlegen
			viewerColumn.getColumn().setText(columns[i]);
			viewerColumn.getColumn().setWidth(width[i]);
			// Spaltengroesse laesst sich zur Laufzeit aendern
			viewerColumn.getColumn().setResizable(false);
			// Spalten lassen sich untereinander verschieben
			viewerColumn.getColumn().setMoveable(false);
			if(i == 3) {
				viewerColumn.setEditingSupport(new SensorPathEditingSupport(this));
			}
		}
	}
	
	private void initProvider() {
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new WorkbenchTableLabelProvider());
	}
	
	private void initInput() {
		Collection<SensorAdapter> adapters = SensorDaemon.getInstance().getModel().values();
		setInput(adapters.toArray());
		SensorDaemon.getInstance().addPropertyChangeListener(this);	
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		System.out.println("Property Changed");
	}

}
