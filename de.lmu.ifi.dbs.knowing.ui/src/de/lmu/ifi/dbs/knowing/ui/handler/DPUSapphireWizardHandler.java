package de.lmu.ifi.dbs.knowing.ui.handler;

import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.PLUGIN_ID;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.ui.wizard.SapphireCustomWizard;

public class DPUSapphireWizardHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

//		try {
//			XmlResourceStore store = new XmlResourceStore(new File("/home/muki/sapphire.dpu.xml"));
//			RootXmlResource resource = new RootXmlResource(store);
//			IDataProcessingUnit dpu = IDataProcessingUnit.TYPE.instantiate(resource);
//			final SapphireWizard<IDataProcessingUnit> wizard = new SapphireWizard<IDataProcessingUnit>(dpu, PLUGIN_ID + DPU_SDEF
//					+ "!dpu.wizard");
//			final WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
//			dialog.open();
//			store.save();
//		} catch (ResourceStoreException e) {
//			e.printStackTrace();
//		}
		IDataProcessingUnit dpu = IDataProcessingUnit.TYPE.instantiate();
		SapphireCustomWizard wizard = new SapphireCustomWizard(dpu, PLUGIN_ID + DPU_SDEF + "!dpu.wizard");
		final WizardDialog dialog = new WizardDialog(HandlerUtil.getActiveShell(event), wizard);
		dialog.open();
		return null;
	}

}
