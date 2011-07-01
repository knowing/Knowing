package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;

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

public class ConfigurationPage extends FormPage {

	public static final String id = ConfigurationPage.class.getName();
	public static final String title = "Configuration";

	private ConfigurationMasterDetailBlock block;
	private DataProcessingUnit dpu;
	private IFile file;

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
		FormToolkit toolkit = managedForm.getToolkit();
		ScrolledForm form = managedForm.getForm();
		form.setText("Configuration");
		Composite body = form.getBody();
		block.createContent(managedForm);
		getSite().setSelectionProvider(block.getNodeTableViewer());
		toolkit.decorateFormHeading(form.getForm());
		toolkit.paintBordersFor(body);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);
		update(input);

	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		super.doSave(monitor);
		System.out.println("DoSave in ConfigurationPage");
		if (dpu == null || file == null)
			return;
		//TODO ConfigurationPage -> Save changes!
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutputStream oos = new ObjectOutputStream(bos);
//			oos.writeObject(dpu);
//			oos.flush();
//			oos.close();
//			bos.close();
//			byte[] data = bos.toByteArray();
//			ByteArrayInputStream source = new ByteArrayInputStream(data);
//			file.setContents(source, IFile.FORCE, null);
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (CoreException e) {
//			e.printStackTrace();
//		}

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

}
