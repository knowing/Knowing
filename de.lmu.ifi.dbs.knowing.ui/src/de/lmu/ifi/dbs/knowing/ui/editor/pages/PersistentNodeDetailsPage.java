package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.util.Properties;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.knowing.core.graph.PersistentNode;

public class PersistentNodeDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	
	private Text tName;
	private Text tFactory;
	
	private Section sectionProperties;

	private PersistentNode node;


	/**
	 * Create the details page.
	 */
	public PersistentNodeDetailsPage() {
		// Create the details page
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new FillLayout());
		//		
		Section section = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Persistent Node");
		//
		Composite cGeneral = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(cGeneral);
		section.setClient(cGeneral);
		cGeneral.setLayout(new GridLayout(3, false));
		
		Label lName = toolkit.createLabel(cGeneral, "Name", SWT.NONE);
		lName.setBounds(0, 0, 58, 15);
		
		tName = toolkit.createText(cGeneral, "New Text", SWT.NONE);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tName.setText("");
		tName.setBounds(0, 0, 56, 19);
		new Label(cGeneral, SWT.NONE);
		
		Label lFactory = toolkit.createLabel(cGeneral, "Factory", SWT.NONE);
		lFactory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tFactory = toolkit.createText(cGeneral, "New Text", SWT.NONE);
		tFactory.setText("");
		tFactory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bBrowse = toolkit.createButton(cGeneral, "Browse", SWT.NONE);
		
		sectionProperties = toolkit.createSection(cGeneral, Section.TWISTIE | Section.TITLE_BAR);
		sectionProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		toolkit.paintBordersFor(sectionProperties);
		sectionProperties.setText("Properties");
		sectionProperties.setExpanded(true);
		
		Composite cProperties = toolkit.createComposite(sectionProperties, SWT.NONE);
		cProperties.setLayout(new GridLayout(2, false));
		toolkit.paintBordersFor(cProperties);
		sectionProperties.setClient(cProperties);
	}
	
	private void updateProperties() {
		Composite cProperties = (Composite) sectionProperties.getClient();
		for(Control c : cProperties.getChildren()) 
			c.dispose();
		
		FormToolkit toolkit = managedForm.getToolkit();
		//TODO get TFactory for property!
		Properties properties = node.properties();
		for(String name : properties.stringPropertyNames()) {
			String value = properties.getProperty(name);
			toolkit.createLabel(cProperties, name);
			toolkit.createLabel(cProperties, value);
		}
		//That's not the way it should work. Update the UI proper here!
		sectionProperties.setExpanded(false);
		sectionProperties.setExpanded(true);
	}
	

	private void update() {
		if(node == null)
			return;
		tName.setText(node.id());
		tFactory.setText(node.factoryId());
		updateProperties();
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if(selection.isEmpty())
			return;
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		node = (PersistentNode) structuredSelection.getFirstElement();
		update();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void setFocus() {
		tName.setFocus();
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}


	@Override
	public void commit(boolean onSave) {
		System.out.println("Commit on save: " + onSave);
		// Commit
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		update();
	}
}
