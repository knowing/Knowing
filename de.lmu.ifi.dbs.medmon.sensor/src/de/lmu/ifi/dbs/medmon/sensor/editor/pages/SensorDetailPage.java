package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

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

import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmContentProvider;
import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmLabelProvider;

public class SensorDetailPage implements IDetailsPage {

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
		System.out.println("SelectionChanged: " + selection);

	}

	@Override
	public void createContents(Composite parent) {
		System.out.println("CreateContents in SensorDetailPage");
		FormToolkit toolkit = managedForm.getToolkit();	
		parent.setLayout(new ColumnLayout());
		

		/* Comments */
		Section cSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE);
		cSection.setText("Allgemein");

		Composite cClient = toolkit.createComposite(cSection, SWT.WRAP);
		GridLayout cLayout = new GridLayout(2, false);
		cLayout.marginWidth = 5;
		cLayout.marginHeight = 5;
		cLayout.horizontalSpacing = 10;
		cLayout.verticalSpacing = 10;
		cClient.setLayout(cLayout);
	
		toolkit.createLabel(cClient, "Importiert");
		toolkit.createLabel(cClient, "JJJJ-MM-TT");
		toolkit.createLabel(cClient, "Aufgezeichnet");
		toolkit.createLabel(cClient, "JJJJ-MM-TT");		
		
		Text comments = toolkit.createText(cClient, "Kommentare", SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 100;
		data.horizontalSpan = 2;
		//TODO Listener um automatisch groesser zu machen
		comments.setLayoutData(data);
		
		cSection.setClient(cClient);
		
		/* Analyse */
		
		Section aSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE);
		aSection.setText("Analyse-Algorithmen");
		aSection.setDescription("Bereits importierte Sensordaten");
		
		Composite aClient = toolkit.createComposite(aSection);
		GridLayout sLayout = new GridLayout(3, false);
		aClient.setLayout(sLayout);
		
		Table table = new Table(aClient, SWT.NULL);
		data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 3;
		table.setLayoutData(data);
		
		Button analyse = toolkit.createButton(aClient, "analysieren", SWT.PUSH);
		analyse.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button delete  = toolkit.createButton(aClient, "entfernen", SWT.PUSH);
		delete.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Button iimport = toolkit.createButton(aClient, "importieren", SWT.PUSH);
		iimport.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		toolkit.paintBordersFor(aClient);
		aSection.setClient(aClient);
		
		TableViewer viewer = new TableViewer(table);
		viewer.setContentProvider(new AlgorithmContentProvider());
		viewer.setLabelProvider(new AlgorithmLabelProvider());
		viewer.setInput(this);

	}

}
