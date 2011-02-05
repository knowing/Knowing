package de.lmu.ifi.dbs.medmon.datamining.core.processing.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.ProcessEvent;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IProcessListener;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

public class Processor {

	private static final Logger log = Logger.getLogger(Processor.class.getName());

	private static Processor instance;

	// TODO find other way to register listeners
	private static ListenerList processListener = new ListenerList();

	private Processor() {

	}

	public static Processor getInstance() {
		if (instance == null)
			instance = new Processor();
		return instance;
	}

	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, Object[] input) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		RawData rawData = createRawData(input);
		return run(chain, rawData, null);
	}

	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, Object[] input, Map<String, IAnalyzedData> acc) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		RawData rawData = createRawData(input);
		return run(chain, rawData, acc);
	}

	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, RawData rawData) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		return run(chain, rawData, null);
	}

	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, RawData rawData, Map<String, IAnalyzedData> acc) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		return run(chain, rawData, acc);
	}

	private Map<String, IAnalyzedData> run(IDataProcessor[] chain, RawData rawData, Map<String, IAnalyzedData> acc) {
		log.info("Start processing DPU");
		log.fine("RawData: " + rawData);
		for (int i = 0; i < chain.length; i++) {
			if (i == chain.length - 1) {
				IAlgorithm algorithm = (IAlgorithm) chain[i];
				if (acc == null)
					acc = algorithm.process(rawData);
				else
					acc = algorithm.process(rawData, acc);
				

			} else {
				rawData = (RawData) chain[i].process(rawData);
			}

		}
		fireEvent(new ProcessEvent(chain[chain.length-1],ProcessEvent.FINISHED, acc));
		return acc;
	}

	private IDataProcessor[] createProcessorChain(DataProcessingUnit dpu) {
		List<XMLDataProcessor> processors = dpu.getProcessors();
		List<IDataProcessor> iProcessors = new ArrayList<IDataProcessor>(processors.size() + 10);
		for (XMLDataProcessor dp : processors) {
			// Doesn't check anything - just fit the pieces together
			IDataProcessor idp = FrameworkUtil.findDataProcessor(dp.getId());
			Map<String, IProcessorParameter> parameters = dp.getParameters();
			for (String key : parameters.keySet()) {
				idp.getParameter(key).setValue(parameters.get(key).getValue());
			}
			iProcessors.add(idp);
		}
		return iProcessors.toArray(new IDataProcessor[iProcessors.size()]);
	}

	private RawData createRawData(Object[] input) {
		return DataConverter.convert(input);
	}

	
	private void fireEvent(ProcessEvent event) {
		Object[] listeners = processListener.getListeners();
		for (int i = 0; i < listeners.length; ++i) {
			((IProcessListener) listeners[i]).processChanged(event);
		}
	}
	

	public static void addProcessListener(IProcessListener listener) {
		processListener.add(listener);
	}

	public static void removeProcessListener(IProcessListener listener) {
		processListener.remove(listener);
	}
	
	

}
