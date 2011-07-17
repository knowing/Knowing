package de.lmu.ifi.dbs.knowing.core.swt.wizard;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBException;

import org.eclipse.jface.wizard.Wizard;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
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
			DataProcessingUnit dpu = dpuPage.getDPU();
			URI uri = dpuPage.getExecutionPath();
			EvaluateHandler.evaluate(dpu, uri);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return true;
	}

}
