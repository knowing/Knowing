package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

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
		getSite().setSelectionProvider(block.getNodeTableViewer());
		block.getNodeTableViewer().addPropertyChangeListener(this);
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
		if (dpu == null || file == null)
			return;
		System.out.println("DoSave in ConfigurationPage: " + dpu);
		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(dpu, bos);
			bos.close();
			byte[] data = bos.toByteArray();
			ByteArrayInputStream source = new ByteArrayInputStream(data);
			file.setContents(source, IFile.FORCE, null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (CoreException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		dirty = false;
		managedForm.dirtyStateChanged();
	}

	public void update(IEditorInput input) {
		file = (IFile) input.getAdapter(IFile.class);
		InputStream in = null;
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			in = file.getContents();
			dpu = (DataProcessingUnit) um.unmarshal(in);
			block.setInput(dpu);

		} catch (CoreException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} finally {
			if (in != null)
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		dirty = true;
		managedForm.dirtyStateChanged();
	}

}
