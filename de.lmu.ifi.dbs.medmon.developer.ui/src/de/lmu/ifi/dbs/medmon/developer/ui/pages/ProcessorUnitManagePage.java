package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
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
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.handlers.IHandlerService;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DPUValidator;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;
import de.lmu.ifi.dbs.medmon.developer.ui.Activator;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorDragListener;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorDropListener;
import de.lmu.ifi.dbs.medmon.developer.ui.dnd.ProcessorTransfer;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;
import de.lmu.ifi.dbs.medmon.developer.ui.handler.ExportDPUHandler;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.DPUContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsLabelProvider;

public class ProcessorUnitManagePage extends FormPage {
	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.pages.UnitFormPage";

	private Text fText;
	private String fFilter;
	private Text tName, text;

	private Button bAdd, bRemove;

	private boolean dirty;

	private ListViewer processorsViewer;
	private ListViewer unitListViewer;

	private DataProcessingUnit dpu;
	private DPUValidator validator;
	private DPUController controller = new DPUController();

	private IManagedForm managedForm;

	private DataBindingContext m_bindingContext;

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public ProcessorUnitManagePage(FormEditor editor) {
		super(editor, ID, "Processing Unit");
		dpu = ((ProcessorUnitEditorInput) editor.getEditorInput()).getDpu();
		dpu.addPropertyChangeListener(controller);
		validator = new DPUValidator(dpu);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		this.managedForm = managedForm;
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Data Processor Unit");
		Composite body = form.getBody();
		body.setLayout(new GridLayout(4, false));
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		Label lName = managedForm.getToolkit().createLabel(managedForm.getForm().getBody(), "Name", SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		dpu.addPropertyChangeListener(controller);
		tName = managedForm.getToolkit().createText(managedForm.getForm().getBody(), "New Text", SWT.NONE);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tName.addListener(SWT.Modify, controller);
		Label label = new Label(managedForm.getForm().getBody(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		fText = managedForm.getToolkit().createText(body, "<filter>", SWT.NONE);
		fText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		unitListViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		unitListViewer.setContentProvider(new DPUContentProvider());
		unitListViewer.setLabelProvider(new ProcessorsLabelProvider());
		unitListViewer.getList().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 5));
		unitListViewer.setInput(dpu);
		getSite().setSelectionProvider(unitListViewer);
		int operations = DND.DROP_COPY | DND.DROP_MOVE;
		Transfer[] transferTypes = new Transfer[] { ProcessorTransfer.getInstance() };
		unitListViewer.addDropSupport(operations, transferTypes, new ProcessorDropListener(unitListViewer));

		bAdd = managedForm.getToolkit().createButton(body, "add", SWT.NONE);
		bAdd.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bAdd.addListener(SWT.Selection, controller);

		processorsViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		processorsViewer.setContentProvider(new ProcessorsContentProvider());
		processorsViewer.setLabelProvider(new ProcessorsLabelProvider());
		processorsViewer.setInput(FrameworkUtil.evaluateDataProcessors());
		processorsViewer.addDragSupport(operations, transferTypes, new ProcessorDragListener(processorsViewer));

		List processorsList = processorsViewer.getList();
		processorsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));

		bRemove = managedForm.getToolkit().createButton(body, "remove", SWT.NONE);
		bRemove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		bRemove.addListener(SWT.Selection, controller);
		new Label(body, SWT.NONE);

		Button up = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move up", SWT.NONE);
		up.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		Button down = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move down", SWT.NONE);
		down.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));

		Section runSection = managedForm.getToolkit().createSection(managedForm.getForm().getBody(), Section.TITLE_BAR);
		runSection.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		managedForm.getToolkit().paintBordersFor(runSection);
		runSection.setText("Running the DPU");

		Composite cRunSection = managedForm.getToolkit().createComposite(runSection, SWT.NONE);
		managedForm.getToolkit().paintBordersFor(cRunSection);
		runSection.setClient(cRunSection);
		cRunSection.setLayout(new GridLayout(1, false));

		ImageHyperlink linkRunDpu = managedForm.getToolkit().createImageHyperlink(cRunSection, SWT.NONE);
		linkRunDpu.setText("Run DPU");
		linkRunDpu.setImage(Activator.getImageDescriptor("icons/run_24.png").createImage());
		linkRunDpu.setHref(dpu);
		linkRunDpu.setEnabled(false);
		linkRunDpu.addHyperlinkListener(controller);

		ImageHyperlink linkExport = managedForm.getToolkit().createImageHyperlink(cRunSection, SWT.NONE);
		linkExport.setText("Export");
		linkExport.setImage(Activator.getImageDescriptor("icons/document-export_24.png").createImage());
		linkExport.setHref(dpu);
		linkExport.addHyperlinkListener(controller);
		

		new Label(managedForm.getForm().getBody(), SWT.NONE);

		Section sDescription = managedForm.getToolkit().createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sDescription.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		sDescription.setText("Description");

		Composite descriptionClient = toolkit.createComposite(sDescription);
		descriptionClient.setLayout(new FillLayout(SWT.HORIZONTAL));
		text = toolkit.createText(descriptionClient, "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);

		sDescription.setClient(descriptionClient);
		toolkit.paintBordersFor(sDescription);
		m_bindingContext = initDataBindings();

		// Everything ok
		dirty = false;
		getEditor().editorDirtyStateChanged();
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
	public void doSave(IProgressMonitor monitor) {
		dirty = false;
		getEditor().editorDirtyStateChanged();
		super.doSave(monitor);
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private class DPUController implements Listener, IHyperlinkListener, PropertyChangeListener {

		@Override
		public void handleEvent(Event event) {
			if (event.type == SWT.Modify)
				update();

			if (event.widget == bAdd) {
				IStructuredSelection selection = (IStructuredSelection) processorsViewer.getSelection();
				if (!selection.isEmpty()) {
					if (selection.getFirstElement() instanceof IDataProcessor) {
						IDataProcessor processor = (IDataProcessor) selection.getFirstElement();
						dpu.add(new XMLDataProcessor(processor));
					} else if (selection.getFirstElement() instanceof XMLDataProcessor) {
						XMLDataProcessor processor = (XMLDataProcessor) selection.getFirstElement();
						dpu.add(processor);
					}
					unitListViewer.refresh();
				}
			} else if (event.widget == bRemove) {
				IStructuredSelection selection = (IStructuredSelection) unitListViewer.getSelection();
				if (!selection.isEmpty()) {
					XMLDataProcessor processor = (XMLDataProcessor) selection.getFirstElement();
					dpu.remove(processor);
					unitListViewer.refresh();
				}
			}
		}

		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			if (!evt.getPropertyName().startsWith("parameter."))
				validator.validate();
			managedForm.getMessageManager().removeAllMessages();
			Map<String, String> errors = validator.getErrors();
			// Insert new errors
			for (String key : errors.keySet())
				managedForm.getMessageManager().addMessage(key, errors.get(key), null, IMessageProvider.ERROR);
			update();

		}

		@Override
		public void linkActivated(HyperlinkEvent event) {
			DataProcessingUnit dpu = (DataProcessingUnit) event.getHref();
			IHandlerService handlerService = (IHandlerService) getEditorSite().getWorkbenchWindow().getWorkbench()
					.getService(IHandlerService.class);
			try {
				handlerService.executeCommand(ExportDPUHandler.ID, null);
			}  catch (Exception ex) {
				throw new RuntimeException(ExportDPUHandler.ID, ex);
			}
			
			/* Fuer LaunchConfiguration
			 * 1) Ueberpruefen ob es schon eine LaunchConfiguration gibt 1a)
			 * Diese ausführen 1b) Sonst LaunchConfiguration erstellen 2)
			 * Datensatz abfragen (CSV Dialog) 3)
			 */
		}

		@Override
		public void linkEntered(HyperlinkEvent event) {

		}

		@Override
		public void linkExited(HyperlinkEvent event) {

		}

		private void update() {
			if (!dirty) {
				dirty = true;
				getEditor().editorDirtyStateChanged();
			}
		}
	}

}
