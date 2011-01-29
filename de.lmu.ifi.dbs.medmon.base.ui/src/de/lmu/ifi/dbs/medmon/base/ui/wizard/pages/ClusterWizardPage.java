package de.lmu.ifi.dbs.medmon.base.ui.wizard.pages;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
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
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchLabelProvider;

import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterContainer;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterTableItem;
import de.lmu.ifi.dbs.medmon.base.ui.provider.ClusterTableItemContentProvider;
import de.lmu.ifi.dbs.medmon.base.ui.provider.ClusterTableItemEditingSupport;
import de.lmu.ifi.dbs.medmon.base.ui.provider.ClusterTableItemLabelProvider;
import de.lmu.ifi.dbs.medmon.base.ui.provider.SensorContainerContentProvider;
import de.lmu.ifi.dbs.medmon.base.ui.widgets.SensorSourceWidget;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class ClusterWizardPage extends WizardPage {

	private Table table;
	private Text tName;

	private List<ClusterTableItem<?>> clusterfiles = new LinkedList<ClusterTableItem<?>>();
	
	private Button bAdd, remove;
	private TableViewer clusterFileViewer;
	private Composite container;
	private SelectDataSourcePage prevPage;

	/**
	 * Create the wizard.
	 */
	public ClusterWizardPage() {
		super("wizardPage");
		setTitle("Vergleichsdaten");
		setDescription("Benannte Vergleichsdaten erstellen");
	}

	/**
	 * Create contents of the wizard.
	 * 
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
		bAdd = new Button(container, SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bAdd.setText("add");
		bAdd.addSelectionListener(controller);

		remove = new Button(container, SWT.NONE);
		remove.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		remove.setText("remove");
		remove.addSelectionListener(controller);

		prevPage = (SelectDataSourcePage) getWizard().getPreviousPage(this);
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
		tableViewer.setContentProvider(new ClusterTableItemContentProvider());
		tableViewer.setLabelProvider(new ClusterTableItemLabelProvider());

		return tableViewer;
	}

	private void createColumns(TableViewer viewer) {
		String[] titles = { "Label", "Source" };
		int[] bounds = { 100, 150 };

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn column = new TableViewerColumn(viewer, SWT.NONE);
			column.getColumn().setText(titles[i]);
			column.getColumn().setWidth(bounds[i]);
			column.getColumn().setResizable(true);
			column.getColumn().setMoveable(true);
			// enable editing support
			column.setEditingSupport(new ClusterTableItemEditingSupport(viewer, i));
		}
		Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	public String getClusterUnit() {
		return tName.getText();
	}
	
	public List<ClusterTableItem<?>> getClusterfiles() {
		return clusterfiles;
	}

	private class Controller extends SelectionAdapter {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (e.widget == bAdd) {
				if (prevPage.getCurrentKey().equals(SelectDataSourcePage.CSV)) {
					FileDialog dialog = new FileDialog(table.getShell(), SWT.MULTI);
					dialog.setFilterExtensions(new String[] { "*.csv" });
					dialog.setFilterNames(new String[] { "CSV File" });
					String path = dialog.open(); // Of the last selected file
					String[] files = dialog.getFileNames();
					path = getPath(path);
					for (String file : files) {
						clusterfiles.add(new ClusterFile(guessLabel(file), path + file));
					}

				} else if (prevPage.getCurrentKey().equals(SelectDataSourcePage.SENSOR)) {
					ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(),
							new WorkbenchLabelProvider(), new SensorContainerContentProvider());
					SensorSourceWidget source = (SensorSourceWidget) prevPage.getConfiguration();
					try {
						ISensorDataContainer<?> input = source.getSensor().getData();
						dialog.setInput(input);
						dialog.open();
						Object[] result = dialog.getResult();
						for (Object element : result) {
							ISensorDataContainer<?> c = (ISensorDataContainer<?>) element;
							clusterfiles.add(new ClusterContainer(c.getName(), c));
						}
						
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}

			} else if (e.widget == remove) {
				ISelection selection = clusterFileViewer.getSelection();
				if(selection.isEmpty())
					return;
				IStructuredSelection sselection = (IStructuredSelection) selection;
				Object[] elements = sselection.toArray();
				for (Object object : elements) {
					clusterfiles.remove(object);
				}
			}
			clusterFileViewer.setInput(clusterfiles);
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
			if (file.length() > 15)
				return file.substring(file.length() - 15, file.length() - 4);
			return file.substring(0, file.length() - 4);
		}
	}

}
