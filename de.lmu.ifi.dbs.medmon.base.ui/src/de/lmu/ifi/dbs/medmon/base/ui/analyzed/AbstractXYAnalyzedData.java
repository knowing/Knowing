package de.lmu.ifi.dbs.medmon.base.ui.analyzed;

import org.jfree.data.xy.XYDataset;

public abstract class AbstractXYAnalyzedData extends AbstractAnalyzedData {

	
	@Override
	protected abstract XYDataset createDataset();


}
