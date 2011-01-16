package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ISharedImages;

public class DPUDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	private Text tName;
	private Text tProvider;
	private Text tDescription;
	private Text tTags;
	private DataProcessingUnit dpu;

	/**
	 * Create the details page.
	 */
	public DPUDetailsPage() {
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
		Section sDPUDetail = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		sDPUDetail.setText("Verfahren");
		//
		Composite composite = toolkit.createComposite(sDPUDetail, SWT.NONE);
		toolkit.paintBordersFor(composite);
		sDPUDetail.setClient(composite);
		GridLayout gl_composite = new GridLayout(3, false);
		gl_composite.horizontalSpacing = 10;
		gl_composite.verticalSpacing = 10;
		composite.setLayout(gl_composite);
		
		Label lName = toolkit.createLabel(composite, "Name", SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tName = toolkit.createText(composite, "<name>", SWT.NONE);
		tName.setText("");
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lProvider = toolkit.createLabel(composite, "Provider", SWT.NONE);
		lProvider.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tProvider = toolkit.createText(composite, "New Text", SWT.READ_ONLY);
		tProvider.setText("");
		tProvider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lDescription = toolkit.createLabel(composite, "Beschreibung", SWT.NONE);
		lDescription.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		tDescription = toolkit.createText(composite, "New Text", SWT.READ_ONLY | SWT.MULTI);
		tDescription.setText("");
		tDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
		Label lTags = toolkit.createLabel(composite, "Tags", SWT.NONE);
		lTags.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tTags = toolkit.createText(composite, "New Text", SWT.NONE);
		tTags.setText("");
		tTags.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bNewTag = toolkit.createButton(composite, "Neuer Tag", SWT.NONE);
		bNewTag.setImage(Activator.getImageDescriptor(ISharedImages.IMG_ADD_TAG_16).createImage());
		bNewTag.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				InputDialog dialog = new InputDialog(tTags.getShell(), "Tag", "Tag hinzufuegen", "", null);
				dialog.open();
				String value = dialog.getValue();
				if(value != null && !value.isEmpty()) {
					dpu.addTag(value);
					if(tTags.getText().isEmpty())
						tTags.setText(value);
					else 
						tTags.setText(tTags.getText() + "," + value);
					commit(false);
				}
			}
		});
	}



	private void update() {
		tName.setText(dpu.getName());
		if(dpu.getDescription() != null)
			tDescription.setText(dpu.getDescription());
		if(dpu.getTags() != null)
			tTags.setText(dpu.getTags());
	}


	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		if(structuredSelection.isEmpty())
			return;
		dpu = (DataProcessingUnit) structuredSelection.getFirstElement();
		update();
	}
	
	@Override
	public void setFocus() {
		tTags.setFocus();
	}

	@Override
	public void commit(boolean onSave) {
		File currentfile = new File(dpu.getFile());
		if(!tName.getText().equals(dpu.getName())) {
			String oldName = dpu.getName();
			String newName = tName.getText();
			String path = dpu.getFile().substring(0, dpu.getFile().length() - oldName.length());
			
			dpu.setName(newName);
			dpu.setFile(path + newName);
			File newFile = new File(path + newName);
			if(currentfile.renameTo(newFile)) {
				currentfile.delete();
				currentfile = newFile;
			}
				
		}
		
		dpu.setTags(tTags.getText());
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(dpu, currentfile);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isDirty() {
		return true;
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
	public void dispose() {

	}

	
	@Override
	public boolean setFormInput(Object input) {
		return false;
	}
	
}
