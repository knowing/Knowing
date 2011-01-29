package de.lmu.ifi.dbs.medmon.medic.ui.util;

import static de.lmu.ifi.dbs.medmon.medic.core.util.ApplicationConfigurationUtil.getPatientFolder;
import static de.lmu.ifi.dbs.medmon.medic.core.util.ApplicationConfigurationUtil.getPreferenceStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.lmu.ifi.dbs.medmon.base.ui.adapter.PatientClusterAdapter;
import de.lmu.ifi.dbs.medmon.base.ui.filter.XMLFileFilter;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.medic.core.preferences.IMedicPreferences;

public class MedicUtil {

	public static List<PatientClusterAdapter> loadClusterUnits(Patient patient) {
		String sep = getPreferenceStore().getString(IMedicPreferences.DIR_SEPERATOR_ID);
		String patientFolder = getPatientFolder(patient);
		List<ClusterUnit> units = loadClusterUnits(patientFolder + sep + "cluster");

		List<PatientClusterAdapter> returns = new ArrayList<PatientClusterAdapter>();
		for (ClusterUnit unit : units)
			returns.add(new PatientClusterAdapter(patient, unit));

		return returns;
	}

	public static List<ClusterUnit> loadClusterUnits(String path) {
		List<ClusterUnit> returns = new ArrayList<ClusterUnit>();
		File root = new File(path);
		if (!root.exists())
			return returns;
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Unmarshaller um = context.createUnmarshaller();

			// Only one element
			if (root.isFile()) {
				ClusterUnit cu = (ClusterUnit) um.unmarshal(root);
				return Collections.singletonList(cu);
			}

			File[] files = root.listFiles(new XMLFileFilter());
			for (File file : files) {
				ClusterUnit cu = (ClusterUnit) um.unmarshal(file);
				returns.add(cu);
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return returns;
	}
}
