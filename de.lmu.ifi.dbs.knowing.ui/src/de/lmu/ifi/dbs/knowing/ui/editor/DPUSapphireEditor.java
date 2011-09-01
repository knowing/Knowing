package de.lmu.ifi.dbs.knowing.ui.editor;

import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.PLUGIN_ID;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.DPU_SDEF;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;

public class DPUSapphireEditor extends SapphireEditorForXml {

	public DPUSapphireEditor() {
		super("de.lmu.ifi.dbs.knowing.ui.editor");

		setRootModelElementType(IDataProcessingUnit.TYPE);
		setEditorDefinitionPath(PLUGIN_ID + DPU_SDEF+ "/dpu.editor.page");
	}

}