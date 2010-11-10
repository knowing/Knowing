package de.lmu.ifi.dbs.medmon.developer.ui.wizard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.clustering.TrainCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.CSVFileReader;
import de.lmu.ifi.dbs.medmon.developer.ui.provider.ClusterFile;
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
		TrainCluster clusterer = new TrainCluster();
		ClusterUnit clusterUnit = new ClusterUnit();
		clusterUnit.setName(page.getClusterUnit());
		ClusterFile[] clusterFiles = page.getClusterFiles();
		for (ClusterFile clusterFile : clusterFiles) {
			try {
				CSVFileReader csv = new CSVFileReader(clusterFile.getFile(), ';');
				List<List<String>> csvlist = new LinkedList<List<String>>();
				List<String> fields = csv.readFields();
				while(fields != null && !fields.isEmpty()) {
					System.out.println(fields);
					csvlist.add(fields);
					fields = csv.readFields();
				}
				List<DoubleCluster> cluster = clusterer.cluster(csvlist, clusterFile.getLabel());
				for (DoubleCluster doubleCluster : cluster) {
					System.out.println("Cluster: " + cluster);
					cluster.add(doubleCluster);
				}
				
				
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (UnableToComplyException e) {
				e.printStackTrace();
			}
		}
		
		
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(clusterUnit, System.out);
			
			File clusterUnitXML = new File(IMedmonConstants.DIR_DPU + 
											IMedmonConstants.DIR_SEPERATOR + 
											clusterUnit.getName() + ".xml");
			m.marshal(clusterUnit, clusterUnitXML);
		} catch (JAXBException e) {
			e.printStackTrace();
		} 
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}

}
