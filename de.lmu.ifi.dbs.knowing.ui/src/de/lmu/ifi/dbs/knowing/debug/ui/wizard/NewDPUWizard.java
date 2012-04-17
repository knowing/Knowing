package de.lmu.ifi.dbs.knowing.debug.ui.wizard;

import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.debug.ui.interal.Activator.PLUGIN_ID;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;

public class NewDPUWizard extends SapphireCreateWizard<IDataProcessingUnit> {

	public NewDPUWizard() {
		super((IDataProcessingUnit) IDataProcessingUnit.TYPE.instantiate(),PLUGIN_ID + DPU_SDEF + "!dpu.wizard" );
	}
	
	@Override
	protected void copyContents(IDataProcessingUnit source, IDataProcessingUnit destination) {
		destination.copy(source);
	}

}
