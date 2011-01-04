package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.base.ui.filter.ClusterDPUFilter;
import de.lmu.ifi.dbs.medmon.base.ui.filter.DPUFilter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.medic.core.unit.MedicProcessingUnit;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DPULabelProvider;

public class MPUDetailsPage implements IDetailsPage {

	private IManagedForm managedForm;
	
	private MedicProcessingUnit mpu;
	private Text tDescription;

	private TableViewer dpuViewer;
	private Text tSearch;

	private ClusterDPUFilter clusterFilter;

	private DPUFilter dpuFilter;

	/**
	 * Create the details page.
	 */
	public MPUDetailsPage() {
		// Create the details page
	}

	/**
	 * Initialize the details page.
	 * @param form
	 */
	@Override
	public void initialize(IManagedForm form) {
		managedForm = form;
	}

	/**
	 * Create contents of the details page.
	 * @param parent
	 */
	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		parent.setLayout(new FillLayout());
		//		
		Section section = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("MPU Details");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(3, false));
		
		Label lDescription = toolkit.createLabel(composite, "Beschreibung", SWT.NONE);
		lDescription.setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, false, false, 1, 1));
		
		tDescription = toolkit.createText(composite, "New Text", SWT.MULTI);
		tDescription.setText("");
		GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
		data.heightHint = 75;
		tDescription.setLayoutData(data);
		
		Label lProcessor = toolkit.createLabel(composite, "Verfahren", SWT.NONE);
		lProcessor.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		
		tSearch = toolkit.createText(composite, "", SWT.BORDER | SWT.SEARCH);
		tSearch.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				dpuFilter.setSearchText(tSearch.getText());
				dpuViewer.refresh();
			}
		});
		
		final Button bCluster = toolkit.createButton(composite, "Cluster", SWT.CHECK);
		bCluster.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				clusterFilter.setCluster(bCluster.getSelection());
				dpuViewer.refresh();
			}
		});
		new Label(composite, SWT.NONE);
		
		dpuViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		dpuViewer.setContentProvider(new ArrayContentProvider());
		dpuViewer.setLabelProvider(new DPULabelProvider());
		dpuViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));	
		dpuViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				Activator.getPatientService().setSelection(event.getSelection());
			}
		});
		clusterFilter = new ClusterDPUFilter();
		dpuFilter = new DPUFilter();
		dpuViewer.addFilter(clusterFilter);
		dpuViewer.addFilter(dpuFilter);
	}

	@Override
	public void dispose() {
		// Dispose
	}

	@Override
	public void setFocus() {
		// Set focus
	}

	private void update() {
		// Update
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
		MedicProcessingUnit mpu = (MedicProcessingUnit) structuredSelection.getFirstElement();
		List<DataProcessingUnit> dpus = mpu.getDpus();
		dpuViewer.setInput(dpus.toArray());
		tDescription.setText(mpu.getDescription());
		update();
	}

	@Override
	public void commit(boolean onSave) {
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
	
	public TableViewer getDpuViewer() {
		return dpuViewer;
	}

}
