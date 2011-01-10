package de.lmu.ifi.dbs.medmon.developer.ui.editor;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorDragListener;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorDropListener;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorTransfer;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.DPUContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsLabelProvider;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;

public class DPUEditor extends EditorPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.editor.DPUEditor"; //$NON-NLS-1$
	private FormToolkit toolkit;
	
	private Text fText;
	private String fFilter;
	private Text tName, text;

	private Button bAdd, bRemove;

	private boolean dirty;

	private ListViewer processorsViewer;

	private ListViewer unitListViewer;

	private DataProcessingUnit dpu;
	private DataBindingContext m_bindingContext;

	public DPUEditor() {
	}

	/**
	 * Create contents of the editor part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Data Processor Unit");
		Composite body = form.getBody();
		body.setLayout(new GridLayout(4, false));
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		Label lName = toolkit.createLabel(body, "Name", SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		DPUController controller = new DPUController();
		tName = toolkit.createText(body, "New Text", SWT.NONE);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tName.addListener(SWT.Modify, controller);
		Label label = new Label(body, SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		fText = toolkit.createText(body, "<filter>", SWT.NONE);
		fText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		unitListViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		unitListViewer.setContentProvider(new DPUContentProvider());
		unitListViewer.setLabelProvider(new ProcessorsLabelProvider());
		unitListViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 5));
		unitListViewer.setInput(((ProcessorUnitEditorInput) getEditorInput()).getDpu());
		getSite().setSelectionProvider(unitListViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { ProcessorTransfer.getInstance() };
		unitListViewer.addDropSupport(operations, transferTypes, new ProcessorDropListener(unitListViewer));

		bAdd = toolkit.createButton(body, "add", SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bAdd.addListener(SWT.Selection, controller);

		processorsViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		processorsViewer.setContentProvider(new ProcessorsContentProvider());
		processorsViewer.setLabelProvider(new ProcessorsLabelProvider());
		processorsViewer.setInput(FrameworkUtil.evaluateDataProcessors());
		processorsViewer.addDragSupport(operations, transferTypes, new ProcessorDragListener(processorsViewer));

		List processorsList = processorsViewer.getList();
		processorsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));

		bRemove = toolkit.createButton(body, "remove", SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bRemove.addListener(SWT.Selection, controller);
		new Label(body, SWT.NONE);

		Button up = toolkit.createButton(body, "move up", SWT.NONE);
		up.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		Button down = toolkit.createButton(body, "move down", SWT.NONE);
		down.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		Section runSection = toolkit.createSection(body, Section.TITLE_BAR);
		runSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.paintBordersFor(runSection);
		runSection.setText("Running the DPU");

		Composite composite = toolkit.createComposite(runSection, SWT.NONE);
		toolkit.paintBordersFor(composite);
		runSection.setClient(composite);
		composite.setLayout(new GridLayout(1, false));

		ImageHyperlink linkRunDpu = toolkit.createImageHyperlink(composite, SWT.NONE);
		toolkit.paintBordersFor(linkRunDpu);
		linkRunDpu.setText("Run DPU");
		linkRunDpu.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_PLAY_24));
		linkRunDpu.addHyperlinkListener(controller);
		new Label(body, SWT.NONE);

		Section sDescription = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sDescription.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		sDescription.setText("Description");

		Composite descriptionClient = toolkit.createComposite(sDescription);
		descriptionClient.setLayout(new FillLayout(SWT.HORIZONTAL));
		text = toolkit.createText(descriptionClient, "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		sDescription.setClient(descriptionClient);
		toolkit.paintBordersFor(sDescription);
		//m_bindingContext = initDataBindings();

		// Everything ok
		dirty = false;

	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//
		IObservableValue tNameObserveTextObserveWidget = SWTObservables.observeText(tName, SWT.Modify);
		IObservableValue dpuNameObserveValue = PojoObservables.observeValue(dpu, "name");
		bindingContext.bindValue(tNameObserveTextObserveWidget, dpuNameObserveValue, null, null);
		//
		return bindingContext;
	}

	@Override
	public void setFocus() {
		// Set the focus
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// Do the Save operation
	}

	@Override
	public void doSaveAs() {
		// Do the Save As operation
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
	
	private class DPUController implements Listener, IHyperlinkListener {

		@Override
		public void handleEvent(Event event) {
			if (event.type == SWT.Modify) {
				dirty = true;
				//getEditor().editorDirtyStateChanged();
			}
			if (event.widget == bAdd) {
				IStructuredSelection selection = (IStructuredSelection) processorsViewer.getSelection();
				if (!selection.isEmpty()) {
					DataProcessingUnit input = (DataProcessingUnit) unitListViewer.getInput();
					if (selection.getFirstElement() instanceof IDataProcessor) {
						IDataProcessor processor = (IDataProcessor) selection.getFirstElement();
						input.add(new XMLDataProcessor(processor));
					} else if (selection.getFirstElement() instanceof XMLDataProcessor) {
						XMLDataProcessor processor = (XMLDataProcessor) selection.getFirstElement();
						input.add(processor);
					}
					unitListViewer.refresh();
				}
			} else if (event.widget == bRemove) {
				IStructuredSelection selection = (IStructuredSelection) unitListViewer.getSelection();
				if (!selection.isEmpty()) {
					DataProcessingUnit input = (DataProcessingUnit) unitListViewer.getInput();
					XMLDataProcessor processor = (XMLDataProcessor) selection.getFirstElement();
					input.remove(processor);
					unitListViewer.refresh();
				}
			}
		}

		@Override
		public void linkActivated(HyperlinkEvent event) {
			System.out.println("Link activated");
		}

		@Override
		public void linkEntered(HyperlinkEvent event) {

		}

		@Override
		public void linkExited(HyperlinkEvent event) {

		}
	}

}
