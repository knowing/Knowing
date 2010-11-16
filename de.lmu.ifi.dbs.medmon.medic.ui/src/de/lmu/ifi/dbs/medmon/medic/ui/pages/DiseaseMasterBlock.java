package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.medic.ui.provider.DiseaseContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DiseaseDetailsPageProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DiseaseLabelProvider;

public class DiseaseMasterBlock extends MasterDetailsBlock {

	private FormToolkit toolkit;

	/**
	 * Create the master details block.
	 */
	public DiseaseMasterBlock() {
		// Create the master details block
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		//		
		Section section = toolkit.createSection(parent,
				ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Krankheiten / Diagnosen");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new GridLayout(4, false));
		
		TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		tableViewer.setContentProvider(new DiseaseContentProvider());
		tableViewer.setLabelProvider(new DiseaseLabelProvider());
		tableViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		toolkit.paintBordersFor(tableViewer.getTable());
		
		final SectionPart part = new SectionPart(section);
		managedForm.addPart(part);
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {		
			@Override
			public void selectionChanged(SelectionChangedEvent event) {	
				managedForm.fireSelectionChanged(part, event.getSelection());
			}
		});
		tableViewer.setInput(this);
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.setPageProvider(new DiseaseDetailsPageProvider());
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}

}
