package de.lmu.ifi.dbs.medmon.datamining.core.launch;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVDescriptor;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVFileReader;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.internal.Processor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.ClusterUtils;

public class DPULaunchDelegate extends LaunchConfigurationDelegate {

	
	
	public static final String DPU_FILE = "dpu.file";
	public static final String CSV_FILE = "csv.file";
	public static final String CSV_DATE_PATTERN = "csv.datepattern";
	public static final String CSV_SEPARATOR = "csv.separator";
	public static final String CSV_TEXT_QUALIFIER = "csv.textqualifier";
	public static final String CSV_FIELDS = "csv.fields";
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		monitor.beginTask("Initialize", 3);
		//Initialize the descriptor
		String sep = configuration.getAttribute(DPULaunchDelegate.CSV_SEPARATOR, ",");
		String qua = configuration.getAttribute(DPULaunchDelegate.CSV_TEXT_QUALIFIER, "\"");
		String pattern = configuration.getAttribute(DPULaunchDelegate.CSV_DATE_PATTERN, CSVDescriptor.DEFAULT_DATE_PATTERN);
		Map<String, String> fields = configuration.getAttribute(DPULaunchDelegate.CSV_FIELDS, new HashMap<String, String>());
		CSVDescriptor descriptor = new CSVDescriptor();
		descriptor.setFieldSeparator(sep.charAt(0));
		descriptor.setTextQualifier(qua.charAt(0));
		descriptor.setDatePattern(pattern);
		descriptor.setFields(xmlToNative(fields));
		monitor.worked(1);
		
		//Initialize the files
		String csvFile = configuration.getAttribute(DPULaunchDelegate.CSV_FILE, "");	
		String dpuFile = configuration.getAttribute(DPULaunchDelegate.DPU_FILE, "");
		
		try {
			CSVFileReader reader = new CSVFileReader(csvFile, descriptor);
			monitor.worked(1);
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			DataProcessingUnit dpu = (DataProcessingUnit) unmarshaller.unmarshal(new File(dpuFile));
			context.createMarshaller().marshal(dpu, System.out);
			monitor.worked(1);
			
			Processor processor = Processor.getInstance();
			Map<String, IAnalyzedData> output = processor.run(dpu, ClusterUtils.convertFromCSV(reader));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public static Map<String, String> nativeToXML(Map<Integer, Class> map) {
		Map<String, String> returns = new HashMap<String, String>();
		for (Integer key : map.keySet())
			returns.put(key.toString(), map.get(key).getName());
		return returns;
	}
	
	public static Map<Integer, Class> xmlToNative(Map<String, String> map) {
		Map<Integer, Class> returns = new HashMap<Integer, Class>();
		for (String key : map.keySet()) {
			try {
				returns.put(new Integer(key), Class.forName(map.get(key)));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return returns;
	}
	
}
