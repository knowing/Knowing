package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmContentProvider;
import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmLabelProvider;
import de.lmu.ifi.dbs.medmon.database.model.SensorData;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class SensorDetailPage implements IDetailsPage {
	
	private DataBindingContext bindingContext;
	
	public SensorDetailPage() {
	}

	private IManagedForm managedForm;
	private SensorData data;
	private Text tImport;
	private Text tRecord;

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
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			if(bindingContext != null) bindingContext.dispose();
			data = (SensorData)((IStructuredSelection)selection).getFirstElement();
			tImport.setText(date2String(data.getTimestamp()));
			tRecord.setText(date2String(data.getRecorded()));
			bindingContext = initDataBindings();
		}

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
	
		Label label = toolkit.createLabel(cClient, "Importiert");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tImport = toolkit.createText(cClient, "", SWT.READ_ONLY);
		tImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		toolkit.adapt(tImport, true, true);
		toolkit.createLabel(cClient, "Aufgezeichnet");
		tRecord = toolkit.createText(cClient, "", SWT.READ_ONLY);	
		
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

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//

		//
		return bindingContext;
	}
	
	private String date2String(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");
		if(date == null)
			return "";
		return df.format(date);
	}
}
