package de.lmu.ifi.dbs.medmon.algorithm.editor;

import java.util.Set;

import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;
import org.eclipse.swt.widgets.Label;

public class AlgorithmDetailsPage implements IDetailsPage {
	public AlgorithmDetailsPage() {
	}

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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub

	}

	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		
		/* Sensor DB */
		
		Section pSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.TWISTIE);
		pSection.setText("Eigenschaften");
		pSection.setDescription("Algorithmus konfigurieren");
		
		Composite pClient = toolkit.createComposite(pSection);
		GridLayout sLayout = new GridLayout(3, false);
		pClient.setLayout(sLayout);
		
		Button analyse = toolkit.createButton(pClient, "speichern", SWT.PUSH);
		analyse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		new Label(pClient, SWT.NONE);
		
		toolkit.paintBordersFor(pClient);
		pSection.setClient(pClient);
		new Label(pClient, SWT.NONE);
		
		//TableViewer viewer = new SensorTableViewer(table);
		//Set<SensorData> set = SampleDataFactory.getSensorData();
		//viewer.setInput(set.toArray(new SensorData[set.size()]));
	}

}
