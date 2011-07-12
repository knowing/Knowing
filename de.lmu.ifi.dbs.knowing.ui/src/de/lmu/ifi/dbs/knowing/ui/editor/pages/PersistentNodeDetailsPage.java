package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.knowing.core.graph.*;
import de.lmu.ifi.dbs.knowing.core.graph.xml.*;
import de.lmu.ifi.dbs.knowing.ui.dialogs.NewPropertyDialog;
import de.lmu.ifi.dbs.knowing.ui.viewer.PropertyTableViewer;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.window.Window;


public class PersistentNodeDetailsPage implements IDetailsPage, PropertyChangeListener {
	public PersistentNodeDetailsPage() {
	}

	private IManagedForm managedForm;
	
	private Section sectionProperties;
	private Text tName;
	private Text tFactory;
	private PropertyTableViewer propertyTableViewer;
	
	private boolean dirty;
	
	private PersistentNode node;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private ComboViewer typeViewer;


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
		FillLayout layout = new FillLayout();
		layout.marginHeight = 5;
		layout.marginWidth = 5;
		parent.setLayout(layout);
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
		tName.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String oldValue = node.id();
				if(!oldValue.equals(tName.getText())) {
					node.id_$eq(tName.getText());
					dirty =  true;
					managedForm.dirtyStateChanged();
					propertyChangeSupport.firePropertyChange("node", oldValue, tName.getText());
				}

			}
		});
		new Label(cGeneral, SWT.NONE);
		
		Label lType = toolkit.createLabel(cGeneral, "Type", SWT.NONE);
		lType.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		typeViewer = new ComboViewer(cGeneral, SWT.NONE);
		Combo combo = typeViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.paintBordersFor(combo);
		typeViewer.add(new String[] {"loader", "processor", "presenter", "saver"});
		typeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = event.getSelection();
				if(selection.isEmpty())
					return;
				String oldValue = node.nodeType();
				String newValue = ((IStructuredSelection)selection).getFirstElement().toString();
				if(!oldValue.equals(newValue)) {
					node.nodeType_$eq(newValue);
					dirty = true;
					managedForm.dirtyStateChanged();
					propertyChangeSupport.firePropertyChange("node", oldValue, newValue);
				}
				
			}
		});
		new Label(cGeneral, SWT.NONE);
		
		Label lFactory = toolkit.createLabel(cGeneral, "Factory", SWT.NONE);
		lFactory.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tFactory = toolkit.createText(cGeneral, "New Text", SWT.NONE);
		tFactory.setText("");
		tFactory.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tFactory.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String oldValue = node.factoryId();
				if(!oldValue.equals(tFactory.getText())) {
					node.factoryId_$eq(tFactory.getText());
					dirty =  true;
					managedForm.dirtyStateChanged();
					propertyChangeSupport.firePropertyChange("node", oldValue, tFactory.getText());
				}
			}
		});
		
		Button bBrowse = toolkit.createButton(cGeneral, "Browse", SWT.NONE);
		
		sectionProperties = toolkit.createSection(cGeneral, Section.TWISTIE | Section.TITLE_BAR);
		sectionProperties.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		toolkit.paintBordersFor(sectionProperties);
		sectionProperties.setText("Properties");
		sectionProperties.setExpanded(true);
		
		Composite cProperties = toolkit.createComposite(sectionProperties, SWT.NONE);
		cProperties.setLayout(new GridLayout(1, false));
		toolkit.paintBordersFor(cProperties);
		sectionProperties.setClient(cProperties);
		
		Table propTable = toolkit.createTable(cProperties, SWT.NONE);
		propTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		propertyTableViewer = new PropertyTableViewer(propTable);
		propertyTableViewer.addPropertyChangeListener(this);
		toolkit.paintBordersFor(propTable);
		new Label(cGeneral, SWT.NONE);
		
		Button bRemove = toolkit.createButton(cGeneral, "Remove", SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) propertyTableViewer.getSelection();
				if(selection.isEmpty())
					return;
				Property property = (Property) selection.getFirstElement();
				propertyTableViewer.removeProperty(property.key());
				dirty =  true;
				managedForm.dirtyStateChanged();
			}
		});
		
		Button bAdd = toolkit.createButton(cGeneral, "Add", SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				NewPropertyDialog dialog = new NewPropertyDialog(managedForm.getForm().getShell());
				int ret = dialog.open();
				if(ret != Window.OK) 
					return;
				propertyTableViewer.addProperty(dialog.getProperty());
				dirty =  true;
				managedForm.dirtyStateChanged();
			}
		});

	}
	
	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if(selection.isEmpty())
			return;
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		node = (PersistentNode) structuredSelection.getFirstElement();
		update();
	}
	
	private void update() {
		if(node == null)
			return;
		tName.setText(node.id());
		tFactory.setText(node.factoryId());
		typeViewer.setSelection(new StructuredSelection(node.nodeType()));
		updateProperties();
	}
	
	private void updateProperties() {
		propertyTableViewer.setInput2(node);
		//That's not the way it should work. Update the UI proper here!
		sectionProperties.setExpanded(false);
		sectionProperties.setExpanded(true);
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
		if(onSave) 
			dirty = false;
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {
		update();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		dirty = true;
		managedForm.dirtyStateChanged();
	}

	/**
	 * @param listener
	 * @return this - for fluent interfaces
	 * @see java.beans.PropertyChangeSupport#addPropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public PersistentNodeDetailsPage addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
		return this;
	}

	/**
	 * @param listener
	 * @see java.beans.PropertyChangeSupport#removePropertyChangeListener(java.beans.PropertyChangeListener)
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
	
}
