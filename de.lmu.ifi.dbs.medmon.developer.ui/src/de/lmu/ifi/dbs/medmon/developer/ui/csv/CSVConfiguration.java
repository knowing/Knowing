package de.lmu.ifi.dbs.medmon.developer.ui.csv;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
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
import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVFileReader;

import org.eclipse.swt.widgets.Text;

public class CSVConfiguration extends Composite {

	
	private String testfile;
	private Button bTest;
	
	private CSVDescriptor descriptor = new CSVDescriptor();
	private List<CSVField> fields = new ArrayList<CSVField>();
	
	private Text tFormatter;
	private TableViewer fieldViewer;
	private ComboViewer separatorViewer;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVConfiguration(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Label lSeperator = new Label(this, SWT.NONE);
		lSeperator.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lSeperator.setText("Seperator");

		Combo combo = new Combo(this, SWT.NONE);
		combo.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1));
		separatorViewer = new ComboViewer(combo);
		separatorViewer.add(new String[] {"," , ";" });

		Label lFormatter = new Label(this, SWT.NONE);
		lFormatter.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lFormatter.setText("Date Formatter");
		
		tFormatter = new Text(this, SWT.BORDER);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, true, false, 1, 1);
		data.widthHint = 150;
		tFormatter.setLayoutData(data);
		tFormatter.setText(descriptor.getDatePattern());

		Group gFields = new Group(this, SWT.NONE);
		gFields.setText("Fields");
		gFields.setLayout(new GridLayout(2, false));
		gFields.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));

		final TableViewer tableViewer = createFieldViewer(gFields);
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));

		Button bAdd = new Button(gFields, SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bAdd.setText("add");
		bAdd.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fields.add(new CSVField(nextPosition(), Double.class));
				tableViewer.setInput(fields);
			}
		});

		Button bRemove = new Button(gFields, SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bRemove.setText("remove");
		bRemove.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
				if(selection.isEmpty())
					return;		
				removeField((CSVField) selection.getFirstElement());
				fieldViewer.refresh();
			}
		});
		
		bTest = new Button(this, SWT.NONE);
		bTest.setEnabled(false);
		bTest.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		bTest.setText("test");
		bTest.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				testCSVConversion();
			}
		});
		new Label(this, SWT.NONE);

	}

	private TableViewer createFieldViewer(Composite parent) {
		fieldViewer = new TableViewer(parent, SWT.FULL_SELECTION);
		createColumns(fieldViewer);
		fieldViewer.setContentProvider(new CSVFieldContentProvider());
		fieldViewer.setLabelProvider(new CSVFieldLabelProvider());
		fieldViewer.setInput(fields);

		final Table table = fieldViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		
		return fieldViewer;
	}

	private void createColumns(TableViewer viewer) {
		String[] titles = { "i", "Type"};
		int[] bounds = { 40, 100};

		// Index
		createTableViewerColumn(viewer, titles[0], bounds[0], 0);

		// Type
		TableViewerColumn col = createTableViewerColumn(viewer,titles[1], bounds[1], 1);
		col.setEditingSupport(new TypeEditingSupport(viewer));
		
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

	public void setTestfile(String testfile) {
		this.testfile = testfile;
		bTest.setEnabled(testfile != null);	
	}
	
	private boolean testCSVConversion() {
		descriptor = new CSVDescriptor();
		descriptor.setFieldSeparator(getSeparatorSelection());
		descriptor.setDatePattern(tFormatter.getText());
		for (CSVField field : fields)
			descriptor.addField(field.getPosition(), field.getType());
		
		Map<Integer, Object> example;
		try {
			CSVFileReader csvFileReader = new CSVFileReader(testfile, descriptor);
			example = csvFileReader.readFieldsToMap();
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return false;
		} catch (NumberFormatException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return false;
		} catch (ParseException e) {
			e.printStackTrace();
			MessageDialog.openError(getShell(), "Error", e.getMessage());
			return false;
		}
		StringBuffer sb = new StringBuffer();
		for (Object value : example.values()) {
			sb.append("\n");
			sb.append("value: ");
			sb.append(value);
			sb.append(" type: ");
			sb.append(value.getClass());
		}
		MessageDialog.openInformation(getShell(), "Success", "Conversion Sucessfull: " + sb.toString());
		return true;
	}
	
	private int nextPosition() {
		return fields.size();
	}
	
	private void removeField(CSVField field) {
		boolean remove = fields.remove(field);
		if(remove) {
			int position = 0;
			for (CSVField f : fields)
				f.setPosition(position++);
		}
	}
	
	private char getSeparatorSelection() {
		IStructuredSelection selection = (IStructuredSelection) separatorViewer.getSelection();
		if(selection.isEmpty())
			return ',';
		String sep = (String) selection.getFirstElement();
		return sep.charAt(0);
	}
	
	private void setSeparatorSelection(String separator)  {
		separatorViewer.setSelection(new StructuredSelection(separator));	
	}
	
	private void setSeparatorSelection(char separator) {
		setSeparatorSelection(new String(new char[] { separator}));
	}
	
	public CSVDescriptor getDescriptor() {
		descriptor.getFields().clear();
		descriptor.setDatePattern(tFormatter.getText());
		descriptor.setFieldSeparator(getSeparatorSelection());
		for (CSVField field : fields)
			descriptor.addField(field.getPosition(), field.getType());

		return descriptor;
	}
	
	public void setDescriptor(CSVDescriptor descriptor) {
		this.descriptor = descriptor;
		if(descriptor == null)
			return;
		fields.clear();
		for (Integer position : descriptor.getFields().keySet())
			fields.add(new CSVField(position, descriptor.getField(position)));
		fieldViewer.setInput(fields);
		tFormatter.setText(descriptor.getDatePattern());
		setSeparatorSelection(descriptor.getFieldSeparator());
	}
			
	
	public void addModifyListener(ModifyListener listener) {
		tFormatter.addModifyListener(listener);
		separatorViewer.getCombo().addModifyListener(listener);
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}


}
