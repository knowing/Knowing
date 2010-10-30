package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.dialogs.AbstractElementListSelectionDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsContentProvider;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ProcessorsLabelProvider;

public class ProcessorUnitManagePage extends FormPage {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.pages.UnitFormPage";

	private Text fText;
	private String fFilter;
	private Text tName;
	private Text text;

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
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Data Processor Unit");
		Composite body = form.getBody();
		body.setLayout(new GridLayout(4, false));
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);

		Label lName = managedForm.getToolkit().createLabel(managedForm.getForm().getBody(), "Name", SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tName = managedForm.getToolkit().createText(managedForm.getForm().getBody(), "New Text", SWT.NONE);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		Label label = new Label(managedForm.getForm().getBody(), SWT.NONE);
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		fText = managedForm.getToolkit().createText(body, "<filter>", SWT.NONE);
		fText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		ListViewer unitListViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		List unitList = unitListViewer.getList();
		unitList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 6));

		Button add = managedForm.getToolkit().createButton(body, "add", SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		ListViewer processorsViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		processorsViewer.setContentProvider(new ProcessorsContentProvider());
		processorsViewer.setLabelProvider(new ProcessorsLabelProvider());
		processorsViewer.setInput(this);
		
		List processorsList = processorsViewer.getList();
		processorsList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 5));

		Button remove = managedForm.getToolkit().createButton(body, "remove", SWT.NONE);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		new Label(body, SWT.NONE);

		Button up = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move up", SWT.NONE);
		up.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		Button down = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move down", SWT.NONE);
		down.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		new Label(body, SWT.NONE);

		Section sDescription = managedForm.getToolkit().createSection(body,	Section.TWISTIE | Section.TITLE_BAR);
		sDescription.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		sDescription.setText("Description");	

		Composite descriptionClient = toolkit.createComposite(sDescription);
		descriptionClient.setLayout(new FillLayout(SWT.HORIZONTAL));
		text = toolkit.createText(descriptionClient, "", SWT.WRAP | SWT.V_SCROLL | SWT.MULTI);
		
		sDescription.setClient(descriptionClient);
		toolkit.paintBordersFor(sDescription);

	}
	
	
}
