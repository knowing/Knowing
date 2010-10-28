package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.layout.FillLayout;

public class ConfigurationMasterBlock extends MasterDetailsBlock {

	private FormToolkit toolkit;
	private Table table;

	/**
	 * Create the master details block.
	 */
	public ConfigurationMasterBlock() {
		// Create the master details block
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param master
	 */
	@Override
	protected void createMasterPart(IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
				
		//
		Section dataProcessorsSection = toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
		dataProcessorsSection.setText("Data Processing Unit");
		
		Composite dataProcessorsClient = toolkit.createComposite(dataProcessorsSection, SWT.NONE);
		toolkit.paintBordersFor(dataProcessorsClient);
		dataProcessorsSection.setClient(dataProcessorsClient);
		dataProcessorsClient.setLayout(new FillLayout(SWT.HORIZONTAL));
		
				
		TableViewer tableViewer = new TableViewer(dataProcessorsClient, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		toolkit.paintBordersFor(table);
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		// Register the pages
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}
}
