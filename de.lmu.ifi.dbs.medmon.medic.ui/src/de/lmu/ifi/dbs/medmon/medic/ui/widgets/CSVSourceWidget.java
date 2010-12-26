package de.lmu.ifi.dbs.medmon.medic.ui.widgets;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.widget.CSVConfiguration;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ClusterFileContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ClusterFileEditingSupport;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ClusterFileLabelProvider;

public class CSVSourceWidget extends Composite {
	
	private Table table;
	
	private List<ClusterFile> clusterfiles = new LinkedList<ClusterFile>();
	private ClusterFile current = null;
	
	private CSVConfiguration configuration;
	private TableViewer clusterFileViewer;

	private Button bAdd;
	private Button bRemove;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVSourceWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		clusterFileViewer = createViewer(this);
		table = clusterFileViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		Controller controller = new Controller();
		bAdd = new Button(this, SWT.NONE);
		bAdd.setText("hinzufuegen");
		bAdd.addSelectionListener(controller);

		bRemove = new Button(this, SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bRemove.setText("entfernen");
		bRemove.addSelectionListener(controller);

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new FillLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		configuration = new CSVConfiguration(group, SWT.NONE);

	}

	private TableViewer createViewer(Composite parent) {
		TableViewer tableViewer = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		createColumns(tableViewer);
		tableViewer.setContentProvider(new ClusterFileContentProvider());
		tableViewer.setLabelProvider(new ClusterFileLabelProvider());

		return tableViewer;
	}

	private void createColumns(TableViewer viewer) {
		String[] titles = { "Label", "File" };
		int[] bounds = { 100, 150 };

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
		if (clusterFileViewer.getInput() instanceof ClusterFile[])
			return (ClusterFile[]) clusterFileViewer.getInput();
		return ClusterFileContentProvider.listInput((List<?>) clusterFileViewer.getInput());
	}

	private class Controller extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == bAdd) {
				FileDialog dialog = new FileDialog(table.getShell(), SWT.MULTI);
				dialog.setFilterExtensions(new String[] { "*.csv" });
				dialog.setFilterNames(new String[] { "CSV File" });
				String path = dialog.open(); // Of the last selected file
				System.out.println("The originalPath: " + path);
				String[] files = dialog.getFileNames();
				path = getPath(path);
				System.out.println("The Path: " + path);
				for (String file : files) {
					clusterfiles.add(new ClusterFile(guessLabel(file), path + file));
				}

				clusterFileViewer.setInput(clusterfiles);

			} else if (e.widget == bRemove) {
				System.out.println("Remove");
			}
		}

		private String getPath(String filepath) {
			int index = filepath.indexOf(File.separator);
			int last = index;
			while (index != -1) {
				last = index;
				index = filepath.indexOf(File.separator, ++index);
			}
			return filepath.substring(0, last + 1);
		}

		private String guessLabel(String file) {
			// TODO guesLabel
			// remove all '/' and than all numbers
			if (file.length() > 15)
				return file.substring(file.length() - 15, file.length() - 4);
			return file.substring(0, file.length() - 4);
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
