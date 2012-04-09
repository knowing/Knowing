package de.lmu.ifi.dbs.knowing.core.swt.wizard;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.exceptions.*;
import de.lmu.ifi.dbs.knowing.core.swt.internal.Activator;
import de.lmu.ifi.dbs.knowing.core.swt.view.PresenterView;

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
			evaluate(dpu, uri);
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return true;
	}

	private void evaluate(IDataProcessingUnit dpu, URI execPath) throws PartInitException {
		PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID());

		try {
			Activator.evaluateService().evaluate(dpu, execPath);
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
