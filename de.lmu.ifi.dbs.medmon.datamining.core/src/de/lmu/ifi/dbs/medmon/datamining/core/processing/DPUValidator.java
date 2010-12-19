package de.lmu.ifi.dbs.medmon.datamining.core.processing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.medmon.datamining.core.util.FrameworkUtil;

public class DPUValidator {

	private final DataProcessingUnit dpu;

	private final Map<String, String> errors = new HashMap<String, String>();

	public DPUValidator(DataProcessingUnit dpu) {
		this.dpu = dpu;
	}

	public DataProcessingUnit createRunning(boolean correction) {
		if (validate())
			return dpu;
		return new DataProcessingUnit();
	}

	/**
	 * checks for IDataProcessor service availability, interoperability
	 * and proper order.
	 * 
	 * @return true - if no errors were found
	 */
	public boolean validate() {
		errors.clear();
		boolean returns = true;
		List<IDataProcessor> processors = new ArrayList<IDataProcessor>();
		for (DataProcessor processor : dpu.getProcessors()) {
			IDataProcessor p = checkAvailability(processor);
			if (p != null)
				processors.add(p);
			returns = returns && (p != null);
		}
		returns = returns && checkOrder(processors);
		returns = returns && checkCompatibilty(processors);
		return returns;
	}

	public boolean solve(DataProcessor processor) {
		return true;
	}

	/**
	 * {@link DataProcessor} is just a wrapper for an {@link IDataProcessor}
	 * which is an OSGi-service. If the service exists ist checked by this
	 * method.
	 * 
	 * @param processor
	 * @return null or the IDataProcessor
	 */
	private IDataProcessor checkAvailability(DataProcessor processor) {
		IDataProcessor iDataProcessor = FrameworkUtil.findDataProcessor(processor.getId());
		if (iDataProcessor == null)
			errors.put(processor.getId(), "N/A");
		return iDataProcessor;
	}

	/**
	 * An algorithm is always the finisher, so if a predecessor is an algorithm
	 * the order is wrong.
	 * 
	 * @param processors
	 * @return true for proper order
	 */
	private boolean checkOrder(List<IDataProcessor> processors) {
		if (processors.isEmpty())
			return true;
		for (int i = 1; i < processors.size(); i++) {
			IDataProcessor previous = processors.get(i - 1);
			IDataProcessor current = processors.get(i);
			if (previous instanceof IAlgorithm) {
				String msg = "Wrong order: " + previous.getName() + " must be last one";
				errors.put(previous.getId(), msg);
				return false;
			}

		}
		return true;
	}

	/**
	 * Currently the only criteria for interoperability is the algorithms
	 * dimension.
	 * 
	 * @param processors
	 * @return
	 */
	private boolean checkCompatibilty(List<IDataProcessor> processors) {
		if (processors.isEmpty())
			return true;
		int dimension = processors.get(0).dimension();
		for (IDataProcessor p : processors) {
			if (p.dimension() != IAlgorithm.INDEFINITE_DIMENSION && dimension != p.dimension()) {
				String msg = "Processors incompatible: " + dimension + " != " + p.dimension();
				errors.put(p.getId(), msg );
				return false;
			}
		}
		return true;
	}

	public DataProcessingUnit getDpu() {
		return dpu;
	}
	
	public Map<String, String> getErrors() {
		return errors;
	}

}
