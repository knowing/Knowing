package de.lmu.ifi.dbs.medmon.datamining.core.filter;

import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.AbstractDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IFilter;

/**
 * This filter just forwards the input
 * Just for testing issues
 * @author Nepomuk Seiler
 *
 */
public class BypassFilter extends AbstractDataProcessor implements IFilter {

	public BypassFilter() {
		super("Bypass Filter", INDEFINITE_DIMENSION, INDEFINITE_DIMENSION);
	}

	@Override
	public RawData process(RawData data) {
		return data;
	}


}
