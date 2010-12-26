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
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;


public class Processor {

	private static final Logger log = Logger.getLogger(Processor.class.getName());
	
	private static Processor instance;
	
	//TODO find other way to register listeners
	private static ListenerList listenerList = new ListenerList();
	
	private Processor() {
		
	}
	
	public static Processor getInstance() {
		if(instance == null)
			instance = new Processor();
		return instance;
	}
	
	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, Object[] input) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		RawData rawData = createRawData(input);
		return run(chain, rawData);
	}
	
	public Map<String, IAnalyzedData> run(DataProcessingUnit dpu, RawData rawData) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		return run(chain, rawData);
	}
	
	private Map<String, IAnalyzedData> run(IDataProcessor[] chain, RawData rawData) {
		log.info("Start processing DPU");
		Map<String, IAnalyzedData> returns = null;
		RawData newData = null;
		for (int i = 0; i < chain.length; i++) {
			if(i == 0)
				newData = (RawData) chain[i].process(rawData);
			else if (i == chain.length - 1)
				returns = (Map<String, IAnalyzedData>) chain[i].process(newData);
			else
				newData = (RawData) chain[i].process(newData);
		}
		fireEvent(returns);
		return returns;
	}
	
	private IDataProcessor[] createProcessorChain(DataProcessingUnit dpu) {
		List<DataProcessor> processors = dpu.getProcessors();
		List<IDataProcessor> iProcessors = new ArrayList<IDataProcessor>(processors.size() + 10);
		for (DataProcessor dp : processors) {
			//Doesn't check anything - just fit the peaces together
			IDataProcessor idp = FrameworkUtil.findDataProcessor(dp.getId());
			iProcessors.add(idp);
		}
		return iProcessors.toArray(new IDataProcessor[iProcessors.size()]);
	}
	
	private RawData createRawData(Object[] input) {
		return DataConverter.convert(input);
	}
	
	private void fireEvent(Map<String, IAnalyzedData> value) {
		System.out.println("Event fired: " + value);
		 Object[] listeners = listenerList.getListeners();
		 PropertyChangeEvent event = new PropertyChangeEvent(this, "data", null, value);
		 for (int i = 0; i < listeners.length; ++i) {
		        ((IPropertyChangeListener) listeners[i]).propertyChange(event);
		 }
	}

	public static void add(IPropertyChangeListener listener) {
		listenerList.add(listener);
	}

	public static void remove(IPropertyChangeListener listener) {
		listenerList.remove(listener);
	}
	
	
}
