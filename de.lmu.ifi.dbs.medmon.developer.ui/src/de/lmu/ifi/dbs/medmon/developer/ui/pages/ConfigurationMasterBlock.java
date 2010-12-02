package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.DPUContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsLabelProvider;

public class ConfigurationMasterBlock extends MasterDetailsBlock implements PropertyChangeListener {

	private FormToolkit toolkit;
	private final ProcessorUnitEditorInput input;
	private TableViewer viewer;

	/**
	 * Create the master details block.
	 */
	public ConfigurationMasterBlock(ProcessorUnitEditorInput input) {
		this.input = input;
		input.getDpu().addPropertyChangeListener(this);
	}

	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param master
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
				
		//
		Section dataProcessorsSection = toolkit.createSection(parent, Section.EXPANDED | Section.TITLE_BAR);
		dataProcessorsSection.setText("DPU Configuration");
		
		Composite dataProcessorsClient = toolkit.createComposite(dataProcessorsSection, SWT.NONE);
		toolkit.paintBordersFor(dataProcessorsClient);
		dataProcessorsSection.setClient(dataProcessorsClient);
		dataProcessorsClient.setLayout(new FillLayout(SWT.HORIZONTAL));
		
				
		viewer = new TableViewer(dataProcessorsClient, SWT.BORDER | SWT.FULL_SELECTION);
		viewer.setContentProvider(new DPUContentProvider());
		viewer.setLabelProvider(new ProcessorsLabelProvider());
		viewer.setInput(input.getDpu());
		
		toolkit.paintBordersFor(viewer.getTable());
		
		final SectionPart part = new SectionPart(dataProcessorsSection);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {		
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(part, event.getSelection());
			}
		});
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.registerPage(DataProcessor.class, new ConfigurationPage());
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		viewer.refresh();
	}
	
	
}
