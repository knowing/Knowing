package de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;

public class DPUWizardPage extends WizardPage {
	
	private Text text;
	private ListViewer viewer;

	/**
	 * Create the wizard.
	 */
	public DPUWizardPage() {
		super("Create Data Processing Unit Wizard");
		setTitle("Create Data Processing Unit");
		setDescription("Fill this");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		GridLayout gl_container = new GridLayout(3, false);
		gl_container.horizontalSpacing = 10;
		container.setLayout(gl_container);
		
		Label lName = new Label(container, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lName.setText("Name");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		
		viewer = new ListViewer(container, SWT.BORDER);
		viewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
		
		Button bAdd = new Button(container, SWT.NONE);
		bAdd.setEnabled(false);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bAdd.setText("add");
		
		Button bRemove = new Button(container, SWT.NONE);
		bRemove.setEnabled(false);
		bRemove.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bRemove.setText("remove");
	}
	
	public String getDPUName() {
		return text.getText();
	}
	
	public List<XMLDataProcessor> getDataProcessors() {
		LinkedList<XMLDataProcessor> returns = new LinkedList<XMLDataProcessor>();
		return returns;
	}
}
