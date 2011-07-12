package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.io.IOException;

import javax.xml.bind.JAXBException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.ui.editor.DPUEditor;

public class MainPage extends FormPage {

	public static final String ID = MainPage.class.getName();
	public static final String NAME = "Main Page";

	private Text tName;
	private Text tTags;
	private Text tDescription;
	private Composite cPreview;

	private boolean created;
	private boolean dirty;
	private IManagedForm managedForm;
	
	private DataProcessingUnit dpu;
	private IFile file;

	/**
	 * Create the form page.
	 * 
	 * @param editor
	 * @wbp.parser.constructor
	 * @wbp.eval.method.parameter id "Some id"
	 * @wbp.eval.method.parameter title "Some title"
	 */
	public MainPage(FormEditor editor) {
		super(editor, ID, NAME);
	}

	/**
	 * Create contents of the form.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createFormContent(final IManagedForm managedForm) {
		this.managedForm = managedForm;
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Data Processing Unit");
		Composite body = form.getBody();
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
		GridLayout layout = new GridLayout(3, false);
		layout.horizontalSpacing = 10;
		layout.verticalSpacing = 10;
		layout.marginTop = 5;
		layout.marginBottom = 5;
		body.setLayout(layout);

		Label lName = toolkit.createLabel(body, "Name", SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tName = toolkit.createText(body, "New Text", SWT.NONE);
		tName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Section sectionPreview = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sectionPreview.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 3));
		toolkit.paintBordersFor(sectionPreview);
		sectionPreview.setText("Preview");
		sectionPreview.setExpanded(true);

		cPreview = toolkit.createComposite(sectionPreview, SWT.NONE);
		toolkit.paintBordersFor(cPreview);
		sectionPreview.setClient(cPreview);
		cPreview.setLayout(new FillLayout(SWT.HORIZONTAL));

		Label lTags = toolkit.createLabel(body, "Tags", SWT.NONE);
		lTags.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));

		tTags = toolkit.createText(body, "", SWT.NONE);
		tTags.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Section sectionDescription = toolkit.createSection(body, Section.TWISTIE | Section.TITLE_BAR);
		sectionDescription.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.paintBordersFor(sectionDescription);
		sectionDescription.setText("Description");
		sectionDescription.setExpanded(true);

		Composite cDescription = toolkit.createComposite(sectionDescription, SWT.NONE);
		toolkit.paintBordersFor(cDescription);
		sectionDescription.setClient(cDescription);
		cDescription.setLayout(new FillLayout(SWT.HORIZONTAL));

		tDescription = toolkit.createText(cDescription, "New Text", SWT.BORDER | SWT.WRAP | SWT.MULTI);
		created = true;
		update();

	}

	private void addListener() {
		tName.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dpu.name_$eq(tName.getText());
				dirty = true;
				managedForm.dirtyStateChanged();
			}
		});

		tTags.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dpu.tags_$eq(tTags.getText());
				dirty = true;
				managedForm.dirtyStateChanged();
			}
		});

		tDescription.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				dpu.description_$eq(tDescription.getText());
				dirty = true;
				managedForm.dirtyStateChanged();
			}
		});
	}
	
	private void removeListener() {
		for(Listener listener : tName.getListeners(SWT.Modify))
			tName.removeListener(SWT.Modify, listener);
		for(Listener listener : tTags.getListeners(SWT.Modify))
			tTags.removeListener(SWT.Modify, listener);
		for(Listener listener : tDescription.getListeners(SWT.Modify))
			tDescription.removeListener(SWT.Modify, listener);
	}

	@Override
	public boolean isDirty() {
		return dirty || super.isDirty();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		DPUEditor.doSave(dpu, file);
		dirty = false;
		managedForm.dirtyStateChanged();
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		update(input);
	}

	public void update(IEditorInput input) {
		try {
			file = (IFile) input.getAdapter(IFile.class);
			dpu = DPUEditor.convert(input);
			update();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void update() {
		if (dpu != null && created) {
			removeListener();
			GraphicPage.createGraph(dpu, cPreview);
			tName.setText(dpu.name());
			tTags.setText(dpu.tags());
			tDescription.setText(dpu.description());
			cPreview.layout(true);
			addListener();
		}

	}
}
