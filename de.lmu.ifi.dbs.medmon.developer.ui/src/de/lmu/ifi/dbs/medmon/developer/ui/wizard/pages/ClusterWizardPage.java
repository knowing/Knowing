package de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFileContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFileEditingSupport;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFileLabelProvider;

public class ClusterWizardPage extends WizardPage {
	
	
	private Table table;
	private Text tName;
	
	private List<ClusterFile> clusterfiles = new LinkedList<ClusterFile>();
	private ClusterFile current = null;

	private Button add, remove;

	private TableViewer clusterFileViewer;
	private Composite container;

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
		container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));
		
		clusterFileViewer = createViewer(container);
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
		
	}
	
	private TableViewer createViewer(Composite parent) {
		
		Label lLabel = new Label(container, SWT.NONE);
		lLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lLabel.setText("Name");
		
		tName = new Text(container, SWT.BORDER);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(container, SWT.NONE);
		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		createColumns(tableViewer);
		tableViewer.setContentProvider(new ClusterFileContentProvider());
		tableViewer.setLabelProvider(new ClusterFileLabelProvider());
		
		return tableViewer;		
	}
	
	private void createColumns(TableViewer viewer) {
		String[] titles = { "Label", "File" };
		int[] bounds = { 100, 150};

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			// enable editing support
			column.setEditingSupport(new ClusterFileEditingSupport(viewer, i));
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}
	
	public ClusterFile[] getClusterFiles() {
		if(clusterFileViewer.getInput() instanceof ClusterFile[])
			return (ClusterFile[]) clusterFileViewer.getInput();
		return ClusterFileContentProvider.listInput((List<?>) clusterFileViewer.getInput());
	}
	
	public String getClusterUnit() {
		return tName.getText();
	}
	
	private class Controller extends SelectionAdapter {
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			if(e.widget == add) {
				FileDialog dialog = new FileDialog(table.getShell(), SWT.MULTI);
				dialog.setFilterExtensions(new String[] {"*.csv"});
				dialog.setFilterNames(new String[] {"CSV File"});
				String path = dialog.open(); //Of the last selected file
				System.out.println("The originalPath: " + path);
				String[] files = dialog.getFileNames();
				path = getPath(path);
				System.out.println("The Path: " + path);
				for (String file : files) {
					clusterfiles.add(new ClusterFile(guessLabel(file),path + file));
				}
					
				clusterFileViewer.setInput(clusterfiles);
				

			} else if(e.widget == remove) {
				System.out.println("Remove");
			}
		}
		
		private String getPath(String filepath) {
			int index = filepath.indexOf(File.separator);
			int last = index;
			while(index != -1) {
				last = index;
				index = filepath.indexOf(File.separator, ++index);
			}
			return filepath.substring(0, last+1);
		}
		
		private String guessLabel(String file) {
			//TODO guesLabel
			//remove all '/' and than all numbers
			if(file.length() > 15)
				return file.substring(file.length() - 15, file.length()-4);
			return file.substring(0, file.length() - 4);
		}
	}

}
