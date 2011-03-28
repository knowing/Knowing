package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import de.lmu.ifi.dbs.medmon.base.ui.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerLabelProvider;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class SelectDataPage extends WizardPage {

	private Composite container;

	private TreeViewer treeViewer;
	private Button bImportAll;	
	
	private boolean importAll = true;
	private boolean persist = true;
	private boolean deleteAfter = true;

	private IStructuredSelection initialSelection;

	public SelectDataPage() {
		super("selectDataPage");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("de.lmu.ifi.dbs.medmon.rcp", "icons/48/gtk-removable.png"));
		setMessage("Die zu analysierenden Daten auswaehlen");
		setTitle("Sensordaten");
		setPageComplete(true);
	}
	
	public SelectDataPage(ISensorDataContainer[] initialSelection) {
		this();
		this.initialSelection = new StructuredSelection(initialSelection);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
		treeViewer.setContentProvider(new SensorContainerContentProvider());
		treeViewer.setLabelProvider(new SensorContainerLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {		
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				bImportAll.setSelection(false);
				importAll = false;
			}
		});
		/*if(initialSelection != null && !initialSelection.isEmpty())
			treeViewer.setSelection(initialSelection, true);*/
		
		final Button bToDB = new Button(container, SWT.CHECK);
		bToDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bToDB.setText("In Datenbank speichern");
		bToDB.setSelection(persist);
		bToDB.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				persist = bToDB.getSelection();
			}
		});
		
		bImportAll = new Button(container, SWT.CHECK);
		bImportAll.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bImportAll.setText("Alles importieren");
		bImportAll.setSelection(importAll);
		bImportAll.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				importAll = bImportAll.getSelection();
			}
		});
		
		final Button bDeleteAfter = new Button(container, SWT.CHECK);
		bDeleteAfter.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bDeleteAfter.setText("Daten danach loeschen");
		bDeleteAfter.setSelection(deleteAfter);
		bDeleteAfter.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				deleteAfter = bDeleteAfter.getSelection();
			}
		});

		setPageComplete(true);

	}
	
	public void setViewerInput(Object input) {
		treeViewer.setInput(input);
	}
	
	public ISensorDataContainer[] getSelection() {
		if(importAll)
			return ((ISensorDataContainer)treeViewer.getInput()).getChildren();
		
		ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
		if(selection.isEmpty())
			return new ISensorDataContainer[0];
		List list = selection.toList();
		ISensorDataContainer[] returns = new ISensorDataContainer[list.size()];
		for(int i=0; i < list.size(); i++) {
			returns[i] = (ISensorDataContainer) list.get(i);
		}
		return returns;
	}

	public boolean isDeleteAfter() {
		return deleteAfter;
	}
	
	public boolean isPersist() {
		return persist;
	}

}
