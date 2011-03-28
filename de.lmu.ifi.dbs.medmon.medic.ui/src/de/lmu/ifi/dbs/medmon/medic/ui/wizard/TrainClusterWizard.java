package de.lmu.ifi.dbs.medmon.medic.ui.wizard;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterContainer;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterFile;
import de.lmu.ifi.dbs.medmon.base.ui.cluster.ClusterTableItem;
import de.lmu.ifi.dbs.medmon.base.ui.dialog.DialogFactory;
import de.lmu.ifi.dbs.medmon.base.ui.widgets.SensorSourceWidget;
import de.lmu.ifi.dbs.medmon.base.ui.wizard.pages.ClusterWizardPage;
import de.lmu.ifi.dbs.medmon.base.ui.wizard.pages.SelectDataSourcePage;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVDescriptor;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVFileReader;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IClusterData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.DataConverter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.ClusterUtils;
import de.lmu.ifi.dbs.medmon.medic.core.service.IPatientService;
import de.lmu.ifi.dbs.medmon.medic.core.util.ApplicationConfigurationUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages.SelectDPUPage;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class TrainClusterWizard extends Wizard implements IWorkbenchWizard, IExecutableExtension {

	private SelectDataSourcePage dataSourcePage;
	private ClusterWizardPage clusterPage;
	private SelectDPUPage dpuPage;

	private String finalPerspectiveId;

	public TrainClusterWizard() {
		setWindowTitle("Vergleichsdaten erzeugen");
	}

	@Override
	public void addPages() {
		addPage(dataSourcePage = new SelectDataSourcePage());
		addPage(clusterPage = new ClusterWizardPage());
		addPage(dpuPage = new SelectDPUPage());
	}

	@Override
	public boolean performFinish() {
		if(finalPerspectiveId != null && !finalPerspectiveId.isEmpty()) {
			try {
				PlatformUI.getWorkbench().showPerspective(finalPerspectiveId,
						PlatformUI.getWorkbench().getActiveWorkbenchWindow());
			} catch (WorkbenchException e) {
				e.printStackTrace();
			}
		}

		DataProcessingUnit dpu = (DataProcessingUnit) Activator.getPatientService().getSelection(IPatientService.DPU);
		Processor processor = Processor.getInstance();
		ClusterUnit cu = null;
		if (dataSourcePage.getCurrentKey().equals(SelectDataSourcePage.CSV)) {
			CSVDescriptor descriptor = (CSVDescriptor) dataSourcePage.getConfiguration();
			List<ClusterTableItem<?>> items = clusterPage.getClusterfiles();
			ClusterFile[] files = new ClusterFile[items.size()];
			int index = 0;
			for (ClusterTableItem<?> item : items)
				files[index++] = (ClusterFile) item;
			cu = clusterCSV(dpu, processor, descriptor, files);
		} else if (dataSourcePage.getCurrentKey().equals(SelectDataSourcePage.SENSOR)) {
			SensorSourceWidget widget = (SensorSourceWidget) dataSourcePage.getConfiguration();
			List<ClusterTableItem<?>> items = clusterPage.getClusterfiles();
			ClusterContainer[] container = new ClusterContainer[items.size()];
			int index = 0;
			for (ClusterTableItem<?> item : items)
				container[index++] = (ClusterContainer) item;
			cu = clusterSensor(dpu, processor, widget.getSensor().getSensorExtension(), container);
		}	
		saveClusterUnit(cu);
		return true;
	}

	private ClusterUnit clusterCSV(DataProcessingUnit dpu, Processor processor, CSVDescriptor descriptor,
			ClusterFile[] files) {
		Map<String, IAnalyzedData> acc = null;
		for (ClusterFile file : files) {
			try {
				CSVFileReader reader = new CSVFileReader(file.getSource(), descriptor);
				RawData rawData = ClusterUtils.convertFromCSV(reader);
				rawData.setLabel(file.getLabel());
				acc = processor.run(dpu, rawData, acc);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		ClusterUnit cu = getClusterUnit(acc);
		cu.setName(clusterPage.getClusterUnit());
		return cu;
	}

	private ClusterUnit clusterSensor(DataProcessingUnit dpu, Processor processor, ISensor sensor,
			ClusterContainer[] container) {
		Map<String, IAnalyzedData> acc = null;
		// String family = "";
		for (ClusterContainer each : container) {
			try {
				Object[] data = sensor.getConverter().readData(each.getSource());
				RawData rawData = DataConverter.convert(data);
				rawData.setLabel(each.getLabel());
				acc = processor.run(dpu, rawData, acc);
				// family = schedule(family,each.getSource(),
				// sensor.getConverter(), dpu, processor, rawData, acc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ClusterUnit cu = getClusterUnit(acc);
		cu.setName(clusterPage.getClusterUnit());
		return cu;
	}

	private ClusterUnit getClusterUnit(Map<String, IAnalyzedData> data) {
		for (IAnalyzedData each : data.values()) {
			if (each instanceof IClusterData)
				return ((IClusterData) each).getCluster();
		}
		return null;
	}

	private void saveClusterUnit(ClusterUnit cu) {
		if (cu == null)
			return;
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			Patient patient = DialogFactory.openPatientSelectionDialog(getShell());
			if (patient == null)
				return;
			
			String pathname = ApplicationConfigurationUtil.createClusterUnitFile(cu, patient);
			File file = new File(pathname);
			if (file.exists()) {
				boolean confirm = MessageDialog.openConfirm(getShell(), "Bereits vorhanden", "Datei ueberschreiben?");
				if (!confirm)
					return;
				file.delete();
			}
			file.createNewFile();
			m.marshal(cu, file);

		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*
	 * private String schedule(String familyToJoin, ISensorDataContainer c,
	 * IConverter converter, DataProcessingUnit dpu, Processor p, RawData data,
	 * Map<String, IAnalyzedData> acc) { String family = data.getLabel();
	 * ConvertJob convertJob = new ConvertJob("Import", c, converter, family);
	 * DataProcessJob processJob = new DataProcessJob("Process", dpu, p, data,
	 * acc, family); IJobManager jm = Job.getJobManager(); IProgressMonitor pm =
	 * jm.createProgressGroup(); try { pm.beginTask("Building", 10);
	 * convertJob.setProgressGroup(pm, 5); convertJob.schedule();
	 * processJob.setProgressGroup(pm, 5); processJob.schedule();
	 * convertJob.join(); processJob.join(); jm.join(familyToJoin, pm); } catch
	 * (InterruptedException e) { e.printStackTrace(); } finally { pm.done(); }
	 * return family; }
	 */

	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data)
			throws CoreException {
		finalPerspectiveId = config.getAttribute("finalPerspective"); //$NON-NLS-1$		

	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {

	}

}
