package de.lmu.ifi.dbs.knowing.ui.handler;

import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.PLUGIN_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.swt.SapphireWizard;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.knowing.core.graph.IFileDataProcessingUnit;

public class DPUSapphireWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IFileDataProcessingUnit dpu = IFileDataProcessingUnit.TYPE.instantiate();
        final SapphireWizard<IFileDataProcessingUnit> wizard  = new SapphireWizard<IFileDataProcessingUnit>( dpu, PLUGIN_ID + DPU_SDEF+ "!dpu.wizard" );
        final WizardDialog dialog = new WizardDialog( HandlerUtil.getActiveShell(event), wizard );
        
        dialog.open();
		return null;
	}

}
