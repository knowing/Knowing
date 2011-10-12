package knowing.test.processor;

import weka.core.Instances;
import weka.filters.SimpleBatchFilter;
import de.lmu.ifi.dbs.knowing.core.events.*;
import de.lmu.ifi.dbs.knowing.core.japi.ILoggableProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.TProcessor;
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil;

import static de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor.debug;
import static de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor.info;
import static de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor.warning;
import static de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor.error;
import static de.lmu.ifi.dbs.knowing.core.japi.LoggableProcessor.statusChanged;

public class TestWekaFilter extends SimpleBatchFilter implements ILoggableProcessor {

	private static final long serialVersionUID = -1911272743884897903L;
	
	private TProcessor processor;

	@Override
	public String globalInfo() {
		return "";
	}

	@Override
	protected Instances determineOutputFormat(Instances inputFormat) throws Exception {
		info(processor, "DetermineOutputFormat ");
		for(int i=0; i < 5; i++) {
			statusChanged(processor, new Progress("DetermineOutputFormat", i, 5));
			Thread.sleep(500);
		}
		warning(processor, "Just returning emptyResult");
		return ResultsUtil.emptyResult();
	}

	@Override
	protected Instances process(Instances instances) throws Exception {
		debug(processor, "Process!");
		return ResultsUtil.emptyResult();
	}

	@Override
	public void setProcessor(TProcessor processor) {
		this.processor = processor;
	}

}
