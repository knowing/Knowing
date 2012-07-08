/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.debug.ui.editor;

import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.PLUGIN_ID;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

import de.lmu.ifi.dbs.knowing.core.model.IProcessHistory;
import de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator;

public class ProcessHistoryEditor extends SapphireEditor {

	private StructuredTextEditor sourceEditor;
	private MasterDetailsEditorPage overviewPage;

	private IModelElement historyModel;

	
	public ProcessHistoryEditor() {
		super(Activator.PLUGIN_ID);
	}

	@Override
	protected IModelElement createModel() {
		historyModel = IProcessHistory.TYPE.instantiate(new RootXmlResource(new XmlEditorResourceStore(this, sourceEditor)));
		return historyModel;
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
		IPath path = new Path(PLUGIN_ID + DPU_SDEF + "/history.editor.overview");
		overviewPage = new MasterDetailsEditorPage(this, historyModel, path);
		addPage(0, overviewPage);
		setPageText(0, "overview");
	}

}
