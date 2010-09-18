package de.lmu.ifi.dbs.medmon.algorithm.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import org.eclipse.swt.widgets.Label;

public class AlgorithmEditorBlock extends MasterDetailsBlock {
	public AlgorithmEditorBlock() {
	}

	private Table table;

	@Override
	protected void createMasterPart(final IManagedForm managedForm,
			Composite parent) {
		FormToolkit	toolkit = managedForm.getToolkit();
		//		
		Section section = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Empty Master Section");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		table = tableViewer.getTable();
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		toolkit.paintBordersFor(table);
		
		Button add = new Button(composite, SWT.NONE);
		//add.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		toolkit.adapt(add, true, true);
		add.setText("Hinzufuegen");
		
		Button del = new Button(composite, SWT.NONE);
		del.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		toolkit.adapt(del, true, true);
		del.setText("Entfernen");

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.registerPage(Patient.class, new AlgorithmDetailsPage());

	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		

	}

}
