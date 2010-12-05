package de.lmu.ifi.dbs.medmon.datamining.core.processing.internal;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;


public class Processor {

	private static Processor instance;
	
	private Processor() {
		
	}
	
	public static Processor getInstance() {
		if(instance == null)
			instance = new Processor();
		return instance;
	}
	
	public IAnalyzedData run(DataProcessingUnit dpu, Object[] input) {
		IDataProcessor[] chain = createProcessorChain(dpu);
		RawData rawData = createRawData(input);
		return run(chain, rawData);
	}
	
	private IAnalyzedData run(IDataProcessor[] chain, RawData rawData) {
		return null;
	}
	
	private IDataProcessor[] createProcessorChain(DataProcessingUnit dpu) {
		List<DataProcessor> processors = dpu.getProcessors();
		List<IDataProcessor> iProcessors = new ArrayList<IDataProcessor>(processors.size() + 10);
		for (DataProcessor dp : processors) {
			//Doesn't check anything - just fit the peaces together
			IDataProcessor idp = FrameworkUtil.findDataProcessor(dp.getId());
			System.out.println("IDataProcessor: " + idp);
			iProcessors.add(idp);
		}
		System.out.println("ProcessorChain: " + iProcessors);
		return iProcessors.toArray(new IDataProcessor[iProcessors.size()]);
	}
	
	private RawData createRawData(Object[] input) {
		return DataConverter.convert(input);
	}
	
}
