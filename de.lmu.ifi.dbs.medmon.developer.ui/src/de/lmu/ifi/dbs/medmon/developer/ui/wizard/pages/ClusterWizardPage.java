package de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFile;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFileContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFileLabelProvider;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class ClusterWizardPage extends WizardPage {
	private DataBindingContext m_bindingContext;
	
	private Table table;
	private Text text;
	
	private List<ClusterFile> clusterfiles = new LinkedList<ClusterFile>();
	private ClusterFile current = null;

	private Button add, remove;

	private TableViewer clusterFileViewer;

	/**
	 * Create the wizard.
	 */
	public ClusterWizardPage() {
		super("wizardPage");
		setTitle("CSV2Cluster");
		setDescription("Erstellt Cluster aus CSV Dateien");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		clusterFileViewer = new TableViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
		clusterFileViewer.setContentProvider(new ClusterFileContentProvider());
		clusterFileViewer.setLabelProvider(new ClusterFileLabelProvider());
		table = clusterFileViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2));
		
		
		Controller controller = new Controller();
		add = new Button(container, SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		add.setText("add");
		add.addSelectionListener(controller);
		
		remove = new Button(container, SWT.NONE);
		remove.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		remove.setText("remove");
		remove.addSelectionListener(controller);
		
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lLabel.setText("Label");
		
		text = new Text(container, SWT.BORDER);
		text.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		//m_bindingContext = initDataBindings();
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue textObserveTextObserveWidget = SWTObservables.observeText(text, SWT.Modify);
		IObservableValue currentLabelObserveValue = PojoObservables.observeValue(current, "label");
		bindingContext.bindValue(textObserveTextObserveWidget, currentLabelObserveValue, null, null);
		//
		return bindingContext;
	}
	
	private class Controller extends SelectionAdapter implements ISelectionChangedListener {

		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			System.out.println("Set new Bindings");
			if(m_bindingContext != null)
				m_bindingContext.dispose();
			m_bindingContext = initDataBindings();
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(e.widget == add) {
				FileDialog dialog = new FileDialog(table.getShell(), SWT.MULTI);
				dialog.setFilterExtensions(new String[] {"*.csv"});
				dialog.setFilterNames(new String[] {"CSV File"});
				dialog.open();
				String[] files = dialog.getFileNames();
				
				for (String file : files) 
					clusterfiles.add(new ClusterFile(guessLabel(file),file));
				clusterFileViewer.setInput(clusterfiles);
				

			} else if(e.widget == remove) {
				System.out.println("Remove");
			}
		}
		
		private String guessLabel(String file) {
			//TODO guesLabel
			//remove all '/' and than all numbers
			return file.substring(file.length() - 15, file.length()-4);
		}
	}

}
