package de.lmu.ifi.dbs.knowing.swt.wizard;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import akka.actor.ActorSystem;
import de.lmu.ifi.dbs.knowing.core.exceptions.KnowingException;
import de.lmu.ifi.dbs.knowing.core.exceptions.ValidationException;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.swt.view.PresenterView;
import de.lmu.ifi.dbs.knowing.swt.internal.Activator;

public class SelectDPUWizard extends Wizard {

	private SelectDPUPage	dpuPage;

	public SelectDPUWizard() {
		setWindowTitle("Select DPU");
	}

	@Override
	public void addPages() {
		addPage(dpuPage = new SelectDPUPage());
	}

	@Override
	public boolean performFinish() {
		try {
			IDataProcessingUnit dpu = dpuPage.getDPU();
			URI uri = dpuPage.getExecutionPath();
			ActorSystem system = dpuPage.getActorSystem();
			evaluate(dpu, uri, system);
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void evaluate(IDataProcessingUnit dpu, URI execPath, ActorSystem system) throws PartInitException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID());

		try {
			//Config config = EvaluationProperties.buildConfig(dpu.getName().getContent(), execPath, PresenterView.ID(), PresenterView.ACTOR_SYSTEM_NAME());
			//Activator.evaluateService().evaluate(config);
			Activator.evaluateService().evaluate(dpu, execPath, PresenterView.getUIFactory(), system,null, null, null);
		} catch (ValidationException e) {
			MultiStatus info = new MultiStatus("knowing.core", 1, "Error on execution", e);
			for (String error : e.getErrors()) {
				info.add(new Status(IStatus.ERROR, "de.lmu.ifi.dbs.knowing.core", error));
			}
			for (String warning : e.getWarnings()) {
				info.add(new Status(IStatus.WARNING, "de.lmu.ifi.dbs.knowing.core", warning));
			}
			ErrorDialog.openError(getShell(), "Error on execution", null, info);
			e.printStackTrace();
		} catch (KnowingException e) {
			e.printStackTrace();
		}

	}
}
