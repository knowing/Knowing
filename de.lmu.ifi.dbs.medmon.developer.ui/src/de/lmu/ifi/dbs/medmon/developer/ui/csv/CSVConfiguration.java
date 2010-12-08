package de.lmu.ifi.dbs.medmon.developer.ui.csv;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVDescriptor;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVField;

public class CSVConfiguration extends Composite {

	private CSVDescriptor descriptor = new CSVDescriptor();
	private Table table;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVConfiguration(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lblSeperator = new Label(this, SWT.NONE);
		lblSeperator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSeperator.setText("Seperator");

		Combo combo = new Combo(this, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		ComboViewer separatorViewer = new ComboViewer(combo);
		separatorViewer.add(new String[] {"," , ";" });

		Label unkownField = new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);

		Group gFields = new Group(this, SWT.NONE);
		gFields.setText("Fields");
		gFields.setLayout(new GridLayout(2, false));
		gFields.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		final TableViewer tableViewer = createFieldViewer(gFields);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		Button bAdd = new Button(gFields, SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bAdd.setText("add");
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				tableViewer.add(new CSVField(0, Double.class));
			}
		});

		Button bRemove = new Button(gFields, SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bRemove.setText("remove");
		bRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				System.out.println(selection);
			}
		});
		new Label(this, SWT.NONE);

	}

	private TableViewer createFieldViewer(Composite parent) {
		TableViewer viewer = new TableViewer(parent, SWT.FULL_SELECTION);
		createColumns(viewer);
		viewer.setContentProvider(new CSVFieldContentProvider());
		viewer.setLabelProvider(new CSVFieldLabelProvider());

		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		return viewer;
	}

	private void createColumns(TableViewer viewer) {
		String[] titles = { "i", "Type", "Formatter"};
		int[] bounds = { 40, 100, 100};

		// Index
		createTableViewerColumn(viewer, titles[0], bounds[0], 0);

		// Type
		TableViewerColumn col = createTableViewerColumn(viewer,titles[1], bounds[1], 1);
		col.setEditingSupport(new TypeEditingSupport(viewer));

		// Formatter
		createTableViewerColumn(viewer,titles[2], bounds[2], 2);
		
	}

	private TableViewerColumn createTableViewerColumn(TableViewer viewer, String title, int bound, final int colNumber) {
		final TableViewerColumn viewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		final TableColumn column = viewerColumn.getColumn();
		column.setText(title);
		column.setWidth(bound);
		column.setResizable(true);
		column.setMoveable(true);
		return viewerColumn;

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
