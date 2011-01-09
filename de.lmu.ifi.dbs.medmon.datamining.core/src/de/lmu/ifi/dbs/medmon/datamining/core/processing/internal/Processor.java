package de.lmu.ifi.dbs.medmon.datamining.core.processing.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

public class Processor {

	private static final Logger log = Logger.getLogger(Processor.class.getName());

	private static Processor instance;

	// TODO find other way to register listeners
	private static ListenerList listenerList = new ListenerList();

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
		fireEvent(acc);
		return acc;
	}

	private IDataProcessor[] createProcessorChain(DataProcessingUnit dpu) {
		List<DataProcessor> processors = dpu.getProcessors();
		List<IDataProcessor> iProcessors = new ArrayList<IDataProcessor>(processors.size() + 10);
		for (DataProcessor dp : processors) {
			// Doesn't check anything - just fit the peaces together
			IDataProcessor idp = FrameworkUtil.findDataProcessor(dp.getId());
			iProcessors.add(idp);
		}
		return iProcessors.toArray(new IDataProcessor[iProcessors.size()]);
	}

	private RawData createRawData(Object[] input) {
		return DataConverter.convert(input);
	}

	private void fireEvent(Map<String, IAnalyzedData> value) {
		Object[] listeners = listenerList.getListeners();
		PropertyChangeEvent event = new PropertyChangeEvent(this, "data", null, value);
		for (int i = 0; i < listeners.length; ++i) {
			((IPropertyChangeListener) listeners[i]).propertyChange(event);
		}
	}

	public static void addPropertyChangeListener(IPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public static void removePropertyChangeListener(IPropertyChangeListener listener) {
		listenerList.remove(listener);
	}

}
