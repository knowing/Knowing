package de.lmu.ifi.dbs.knowing.ui.viewer;

import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.knowing.ui.provider.WorkbenchTableLabelProvider;

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 08.05.2011
 * 
 */
public class NodeTableViewer extends TableViewer {

	protected static final String[] columns = new String[] { "Name", "Type", "In", "Out", "Class" };
	private static final int[] width = new int[] { 120, 100, 40, 40, 200 };

	private NodeEditingSupport[] nodeEditingSupport = new NodeEditingSupport[5];

	/**
	 * @param parent
	 * @param style
	 */
	public NodeTableViewer(Composite parent, int style) {
		super(parent, style);
		init();
	}

	/**
	 * 
	 * @param table
	 */
	public NodeTableViewer(Table table) {
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
			nodeEditingSupport[i] = new NodeEditingSupport(this, i);
			viewerColumn.setEditingSupport(nodeEditingSupport[i]);
		}
	}

	private void initProvider() {
		setContentProvider(new ArrayContentProvider());
		setLabelProvider(new WorkbenchTableLabelProvider());
	}

	/**
	 * @param listener
	 * @see de.lmu.ifi.dbs.knowing.ui.viewer.TPropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		for (NodeEditingSupport pes : nodeEditingSupport)
			pes.addPropertyChangeListener(listener);
	}

	/**
	 * @param listener
	 * @see de.lmu.ifi.dbs.knowing.ui.viewer.TPropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		for (NodeEditingSupport pes : nodeEditingSupport)
			pes.removePropertyChangeListener(listener);
	}

}
