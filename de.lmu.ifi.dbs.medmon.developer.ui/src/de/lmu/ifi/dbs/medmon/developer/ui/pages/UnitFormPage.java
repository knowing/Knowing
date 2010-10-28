package de.lmu.ifi.dbs.medmon.developer.ui.pages;

import org.eclipse.swt.widgets.Composite;
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

public class UnitFormPage extends FormPage {
	private Text fText;
	private String fFilter;

	/**
	 * Create the form page.
	 * @param id
	 * @param title
	 */
	public UnitFormPage(String id, String title) {
		super(id, title);
	}

	/**
	 * Create the form page.
	 * @param editor
	 * @param id
	 * @param title
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public UnitFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
	}

	/**
	 * Create contents of the form.
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Data Processor Configuration");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		body.setLayout(new GridLayout(3, false));
		
		ListViewer listViewer = new ListViewer(managedForm.getForm().getBody(), SWT.BORDER | SWT.V_SCROLL);
		List list = listViewer.getList();
		list.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4));
		
		Button add = managedForm.getToolkit().createButton(body, "add", SWT.NONE);
		add.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		
		fText = managedForm.getToolkit().createText(body, "<filter>", SWT.SEARCH);
		fText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button remove = managedForm.getToolkit().createButton(body, "remove", SWT.NONE);
		remove.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		
		ListViewer processorsViewer = new ListViewer(body, SWT.BORDER | SWT.V_SCROLL);
		List list_1 = processorsViewer.getList();
		list_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		
		Button up = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move up", SWT.NONE);
		up.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		
		Button down = managedForm.getToolkit().createButton(managedForm.getForm().getBody(), "move down", SWT.NONE);
		down.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}

}
