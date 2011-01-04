package de.lmu.ifi.dbs.medmon.developer.ui.wizard;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.clustering.TrainCluster;
import de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages.ClusterWizardPage;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class ClusterWizard extends Wizard implements IWorkbenchWizard {

	private ClusterWizardPage page;

	@Override
	public void addPages() {
		page = new ClusterWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		//TODO CHANGE THIS
		TrainCluster clusterer = new TrainCluster();
		ClusterUnit clusterUnit = new ClusterUnit();
		clusterUnit.setName(page.getClusterUnit());
		ClusterFile[] clusterFiles = page.getClusterFiles();
		File[] files = new File[clusterFiles.length];
		String[] lables = new String[clusterFiles.length];
		int index = 0;
		for (ClusterFile clusterFile : clusterFiles) {
			files[index] = new File(clusterFile.getSource());
			lables[index++] = clusterFile.getLabel();
		}

		try {
			List<DoubleCluster> cluster = clusterer.cluster(files, lables);
			for (DoubleCluster doubleCluster : cluster) {
				clusterUnit.addCluster(doubleCluster);
			}
		} catch (UnableToComplyException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(clusterUnit, System.out);

			File clusterUnitXML = new File(IMedmonConstants.DIR_CU + File.separator	+ clusterUnit.getName() + ".xml");
			clusterUnitXML.createNewFile();
			m.marshal(clusterUnit, clusterUnitXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		
	}

}
