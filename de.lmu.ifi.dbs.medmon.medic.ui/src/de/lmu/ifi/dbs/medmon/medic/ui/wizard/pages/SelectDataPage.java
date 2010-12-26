package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.List;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;

import de.lmu.ifi.dbs.medmon.medic.ui.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DataLabelProvider;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class SelectDataPage extends WizardPage {

	private Composite container;

	private TreeViewer treeViewer;


	public SelectDataPage() {
		super("selectDataPage");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("de.lmu.ifi.dbs.medmon.rcp", "icons/48/gtk-removable.png"));
		setMessage("Die zu analysierenden Daten auswaehlen");
		setTitle("Sensordaten");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(2, false));

		treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
		treeViewer.setContentProvider(new DataContentProvider());
		treeViewer.setLabelProvider(new DataLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		Button bToDB = new Button(container, SWT.CHECK);
		bToDB.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bToDB.setText("In Datenbank speichern");
		
		Button bImportAll = new Button(container, SWT.CHECK);
		bImportAll.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bImportAll.setText("Alles importieren");
		
		Button bDeleteAfter = new Button(container, SWT.CHECK);
		bDeleteAfter.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		bDeleteAfter.setText("Daten danach loeschen");

		setPageComplete(true);

	}
	
	public void setViewerInput(Object input) {
		treeViewer.setInput(input);
	}
	
	public ISensorDataContainer[] getSelection() {
		ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
		if(selection.isEmpty())
			return null;
		List list = selection.toList();
		ISensorDataContainer[] returns = new ISensorDataContainer[list.size()];
		for(int i=0; i < list.size(); i++) {
			returns[i] = (ISensorDataContainer) list.get(i);
		}
		return returns;
	}



}
