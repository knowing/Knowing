package de.lmu.ifi.dbs.medmon.sensor.viewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.sensor.provider.SensorContentProvider;
import de.lmu.ifi.dbs.medmon.sensor.provider.SensorLabelProvider;

public class SensorTableViewer extends TableViewer {

	public static final String[] columns = new String[] { "Datum", "Importiert", "Analysiert" };
	public static final int[] width = new int[] {120,120, 50};
	
	public SensorTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}
	
	public SensorTableViewer(Table table) {
		super(table);
		init();
	}
	
	private void init() {
		initColumns();
		initProvider();
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
		}
	}
	
	private void initProvider() {
		setContentProvider(new SensorContentProvider());
		setLabelProvider(new SensorLabelProvider());
	}

}