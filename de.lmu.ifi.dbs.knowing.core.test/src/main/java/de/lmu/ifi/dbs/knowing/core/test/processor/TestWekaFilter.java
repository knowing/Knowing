package de.lmu.ifi.dbs.knowing.core.test.processor;

import weka.core.Instances;
import weka.filters.SimpleBatchFilter;
import de.lmu.ifi.dbs.knowing.core.events.*;
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor;
import de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor;
import de.lmu.ifi.dbs.knowing.core.results.EmptyResults;

public class TestWekaFilter extends SimpleBatchFilter implements ILoggableProcessor {

	private static final long serialVersionUID = -1911272743884897903L;
	private LoggableProcessor log;

	@Override
	public String globalInfo() {
		return "";
	}

	@Override
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		log.info( "DetermineOutputFormat ");
		for(int i=0; i < 5; i++) {
			log.statusChanged(new Progress("DetermineOutputFormat", i, 5));
			Thread.sleep(500);
		}
		log.warning("Just returning emptyResult");
		return EmptyResults.newInstances();
	}

	@Override
	protected Instances process(Instances instances) throws Exception {
		log.debug("Process!");
		return EmptyResults.newInstances();
	}

	@Override
	public void setProcessor(TProcessor processor) {
		log = new LoggableProcessor(processor);
	}

}
