/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import de.lmu.ifi.dbs.knowing.core.swt.provider.InstanceContentProvider;
import de.lmu.ifi.dbs.knowing.core.swt.provider.InstanceLabelProvider;

import weka.core.Attribute;
import weka.core.Instances;


/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 21.03.2011
 */
public class TablePresenter extends SWTPresenter {

	private TableViewer viewer;
	private boolean columnsInit;

	/** 
	 * @see de.lmu.ifi.dbs.knowing.core.swt.SWTPresenter#createControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	protected void createControl(Composite parent) {
		viewer = new TableViewer(parent);
	}
	
	@Override
	protected void createContent(Instances dataset) {
		createColumns(dataset.enumerateAttributes());
		viewer.setInput(dataset);
		viewer.refresh();
	}

	/**
	 * @param eAttr
	 */
	private void createColumns(Enumeration<Attribute> eAttr) {
		if(columnsInit)
			return;
		viewer.getTable().setHeaderVisible(true);
		viewer.getTable().setLinesVisible(true);
		ArrayList<Attribute> attributes = Collections.list(eAttr);
		for (Attribute a : attributes) {
			TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.LEAD);
			viewerColumn.getColumn().setText(a.name());
			viewerColumn.getColumn().setWidth(70);
			viewerColumn.getColumn().setResizable(true);
			viewerColumn.getColumn().setMoveable(true);
		}
		viewer.setLabelProvider(new InstanceLabelProvider());
		viewer.setContentProvider(new InstanceContentProvider(true));
		columnsInit = true;
	}


	@Override
	public String getName() {
		return "Table Presenter";
	}

	@Override
	public Instances getModel(List<String> labels) {
		return null;
	}

}
