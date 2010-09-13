package de.lmu.ifi.dbs.medmon.patient.editor;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

public class PatientDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void commit(boolean onSave) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean setFormInput(Object input) {
		System.out.println("SetFormInput: " + input);
		return false;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isStale() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void refresh() {
		// TODO Auto-generated method stub

	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		System.out.println("SelectionChanged: " + selection);

	}

	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		

		/* Patient Information */
		Section gSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR);
		gSection.setText("Patienteninformationen");

		Composite gClient = toolkit.createComposite(gSection, SWT.WRAP);
		GridLayout gLayout = new GridLayout(2, false);
		gLayout.marginWidth = 5;
		gLayout.marginHeight = 5;
		gClient.setLayout(gLayout);
	
		toolkit.createLabel(gClient, "Name");
		Text firstname = toolkit.createText(gClient, "", SWT.BORDER);
		GridData data = new GridData(150, SWT.DEFAULT);
		firstname.setLayoutData(data);
		toolkit.createLabel(gClient, "Nachname");
		Text lastname = toolkit.createText(gClient, "", SWT.BORDER);
		data = new GridData(150, SWT.DEFAULT);
		lastname.setLayoutData(data);
		
		gSection.setClient(gClient);
		
		/* Sensor DB */
		
		Section sSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE);
		sSection.setText("Sensordaten");
		sSection.setDescription("Bereits importierte Sensordaten");
		
		Composite sClient = toolkit.createComposite(sSection);
		GridLayout sLayout = new GridLayout(3, false);
		sClient.setLayout(sLayout);
		
		Table table = new Table(sClient, SWT.NULL);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 20;
		data.widthHint = 100;
		data.horizontalSpan = 3;
		table.setLayoutData(data);
		
		Button analyse = toolkit.createButton(sClient, "analysieren", SWT.PUSH);
		analyse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button delete  = toolkit.createButton(sClient, "entfernen", SWT.PUSH);
		delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button iimport = toolkit.createButton(sClient, "importieren", SWT.PUSH);
		iimport.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		toolkit.paintBordersFor(sClient);
		sSection.setClient(sClient);
		
		final SectionPart spart = new SectionPart(sSection);
		managedForm.addPart(spart);
		TableViewer viewer = new TableViewer(table);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});
		
	}

}
