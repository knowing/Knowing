package de.lmu.ifi.dbs.knowing.ui.editor;

import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.PLUGIN_ID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.internal.SapphireUiFrameworkPlugin;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditor;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorFactory;
import org.eclipse.sapphire.ui.swt.graphiti.editor.SapphireDiagramEditorInput;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;

public class DPUSapphireEditor extends SapphireEditor {

	private StructuredTextEditor sourceEditor;
	private MasterDetailsEditorPage detailsPage;
	private SapphireDiagramEditor diagramPage;

	private IModelElement dpuModel;

	public DPUSapphireEditor() {
		super("de.lmu.ifi.dbs.knowing.ui.editor");

		// setRootModelElementType(IDataProcessingUnit.TYPE);
		// setEditorDefinitionPath(PLUGIN_ID + DPU_SDEF+ "/dpu.editor.page");
	}

	@Override
	protected IModelElement createModel() {
		dpuModel = IDataProcessingUnit.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, sourceEditor)));
		return dpuModel;
	}

	@Override
	protected void createSourcePages() throws PartInitException {
		sourceEditor = new StructuredTextEditor();
		sourceEditor.setEditorPart(this);

		final FileEditorInput rootEditorInput = (FileEditorInput) getEditorInput();

		int index = addPage(sourceEditor, rootEditorInput);
		setPageText(index, "source");
	}

	@Override
	protected void createFormPages() throws PartInitException {
		IPath path = new Path(PLUGIN_ID + DPU_SDEF + "/dpu.editor.detail");
		detailsPage = new MasterDetailsEditorPage(this, dpuModel, path);
		addPage(0, this.detailsPage);
	}

	@Override
	protected void createDiagramPages() throws PartInitException {
		IPath path = new Path(PLUGIN_ID + DPU_SDEF+ "/dpu.editor.diagram");
		diagramPage = new SapphireDiagramEditor(dpuModel, path);
		SapphireDiagramEditorInput diagramEditorInput = null;

		try {
			diagramEditorInput = SapphireDiagramEditorFactory.createEditorInput(dpuModel.adapt(IFile.class));
		} catch (Exception e) {
			SapphireUiFrameworkPlugin.log(e);
		}

		if (diagramEditorInput != null) {
			addPage(0, diagramPage, diagramEditorInput);
			setPageText(0, "Diagram");
			setPageId(this.pages.get(0), "diagram", diagramPage.getPart());
		}
	}
}