package de.lmu.ifi.dbs.knowing.core.swt.wizard;

import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
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
		IViewPart view = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(PresenterView.ID());
		PresenterView pView = (PresenterView) view;
		Activator.evaluateService().evaluate(dpu, pView.uifactory(), execPath);
	}
}
