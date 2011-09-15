package de.lmu.ifi.dbs.knowing.core.swt.wizard;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.sapphire.modeling.ResourceStoreException;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.swt.handler.EvaluateHandler;

public class SelectDPUWizard extends Wizard {

	private SelectDPUPage dpuPage;

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
			EvaluateHandler.evaluate(dpu, uri);
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}

}
