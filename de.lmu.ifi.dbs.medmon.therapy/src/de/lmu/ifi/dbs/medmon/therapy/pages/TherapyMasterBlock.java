package de.lmu.ifi.dbs.medmon.therapy.pages;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.therapy.IDisease;
import de.lmu.ifi.dbs.medmon.therapy.ITherapy;
import de.lmu.ifi.dbs.medmon.therapy.provider.TherapyContentProvider;
import de.lmu.ifi.dbs.medmon.therapy.provider.TherapyDetailsPageProvider;
import de.lmu.ifi.dbs.medmon.therapy.provider.TherapyLabelProvider;

import org.eclipse.swt.layout.FillLayout;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;

public class TherapyMasterBlock extends MasterDetailsBlock {

	private FormToolkit toolkit;
	
	private ListViewer listViewer;

	/**
	 * Create the master details block.
	 */
	public TherapyMasterBlock() {
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
		Section section = toolkit.createSection(parent,	Section.NO_TITLE);
		section.setText("Therapien");
		//	
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		listViewer = new ListViewer(composite, SWT.BORDER | SWT.V_SCROLL);
		listViewer.setContentProvider(new TherapyContentProvider());
		listViewer.setLabelProvider(new TherapyLabelProvider());
		
		final SectionPart part = new SectionPart(section);
		managedForm.addPart(part);
		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {	
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(part, event.getSelection());
			}
		});
		
		sashForm.setOrientation(SWT.VERTICAL);
		
	}
	
	public void setInput(IDisease disease) {
		listViewer.setInput(disease);
		
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.setPageProvider(new TherapyDetailsPageProvider());
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
