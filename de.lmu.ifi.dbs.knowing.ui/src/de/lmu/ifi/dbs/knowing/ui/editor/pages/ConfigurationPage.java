package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.ui.editor.DPUEditor;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.5
 * @since 28.06.2011
 * 
 */
public class ConfigurationPage extends FormPage implements PropertyChangeListener {

	public static final String id = ConfigurationPage.class.getName();
	public static final String title = "Configuration";

	private ConfigurationMasterDetailBlock block;
	private DataProcessingUnit dpu;
	private IFile file;

	private boolean dirty;
	private IManagedForm managedForm;

	/**
	 * 
	 * @param editor
	 */
	public ConfigurationPage(FormEditor editor) {
		this(editor, id, title);
	}

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
	public ConfigurationPage(FormEditor editor, String id, String title) {
		super(editor, id, title);
		block = new ConfigurationMasterDetailBlock();
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
		form.setText("Configuration");
		Composite body = form.getBody();
		block.createContent(managedForm);
		block.getNodeTableViewer().addPropertyChangeListener(this);
		block.getEdgeTableViewer().addPropertyChangeListener(this);
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		update(input);

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

	public void update(IEditorInput input) {
		file = (IFile) input.getAdapter(IFile.class);
		try {
			dpu = DPUEditor.convert(input);
			block.setInput(dpu);
			block.refresh();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		dirty = true;
		managedForm.dirtyStateChanged();
	}

}
