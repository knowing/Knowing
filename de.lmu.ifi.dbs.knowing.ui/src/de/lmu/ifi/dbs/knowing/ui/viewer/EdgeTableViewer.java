package de.lmu.ifi.dbs.knowing.ui.viewer;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.knowing.ui.provider.WorkbenchTableLabelProvider;
import de.lmu.ifi.dbs.knowing.core.graph.xml.*;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 23.07.2011
 */
public class EdgeTableViewer  extends TableViewer {

	private static final String[] columns = new String[] { "Name", "Source", "Target", "W" };
	private static final int[] width = new int[] { 150, 150, 150, 30};
	
	private EdgeEditingSupport[] edgeEditingSupport = new EdgeEditingSupport[4];
	private DataProcessingUnit dpu;
	
	/**
	 * @param parent
	 * @param style
	 */
	public EdgeTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}

	/**
	 * 
	 * @param table
	 */
	public EdgeTableViewer(Table table) {
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
		for (int i = 0; i < columns.length; i++) {
			TableViewerColumn viewerColumn = new TableViewerColumn(this, SWT.LEAD);
			// Kopfzeile und Breite der jeweiligen Spalten festlegen
			viewerColumn.getColumn().setText(columns[i]);
			viewerColumn.getColumn().setWidth(width[i]);
			// Spaltengroesse laesst sich zur Laufzeit aendern
			viewerColumn.getColumn().setResizable(true);
			// Spalten lassen sich untereinander verschieben
			viewerColumn.getColumn().setMoveable(true);
			edgeEditingSupport[i] = new EdgeEditingSupport(this, i);
			viewerColumn.setEditingSupport(edgeEditingSupport[i]);
		}
	}

	private void initProvider() {
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new WorkbenchTableLabelProvider());
	}

	public DataProcessingUnit getDpu() {
		return dpu;
	}

	public void setDpu(DataProcessingUnit dpu) {
		this.dpu = dpu;
		for (EdgeEditingSupport support : edgeEditingSupport)
			support.updateDPU(dpu);
	}
	
	/**
	 * @param listener
	 * @see de.lmu.ifi.dbs.knowing.ui.viewer.TPropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		for (EdgeEditingSupport pes : edgeEditingSupport)
			pes.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see de.lmu.ifi.dbs.knowing.ui.viewer.TPropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		for (EdgeEditingSupport pes : edgeEditingSupport)
			pes.removePropertyChangeListener(listener);
	}
	
}
