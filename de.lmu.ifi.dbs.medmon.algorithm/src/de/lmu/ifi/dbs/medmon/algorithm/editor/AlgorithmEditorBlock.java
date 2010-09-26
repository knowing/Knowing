package de.lmu.ifi.dbs.medmon.algorithm.editor;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.algorithm.Activator;
import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmDetailsPageProvider;
import de.lmu.ifi.dbs.medmon.algorithm.ui.AlgorithmTableViewer;

public class AlgorithmEditorBlock extends MasterDetailsBlock {
	
	private TableViewer viewer;
	
	public AlgorithmEditorBlock() {
	}

	@Override
	protected void createMasterPart(final IManagedForm managedForm,	Composite parent) {
		FormToolkit	toolkit = managedForm.getToolkit();
		//		
		Section section = toolkit.createSection(parent,	Section.EXPANDED |Section.TITLE_BAR);
		section.setText("Algorithmen");

		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(2, false));
		
		Table table = new Table(composite, SWT.NULL);
		table.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true, 1, 2));
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		viewer = new AlgorithmTableViewer(table);
		final SectionPart part = new SectionPart(section);
		managedForm.addPart(part);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(part, event.getSelection());
				Activator.getPatientService().setSelection(event.getSelection());
			}
		});
		viewer.setInput(this);
		toolkit.paintBordersFor(table);

	}

	@Override
	protected void registerPages(DetailsPart detailsPart) {
		detailsPart.setPageProvider(new AlgorithmDetailsPageProvider());
	}

	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		

	}

}
