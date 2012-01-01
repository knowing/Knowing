package knowing.test.processor

import de.lmu.ifi.dbs.knowing.core.weka.WekaFilterFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaFilter
import knowing.test.processor.TestWekaFilter

class TestWekaFilterFactory extends WekaFilterFactory[TestWekaFilterWrapper, TestWekaFilter](classOf[TestWekaFilterWrapper], classOf[TestWekaFilter]) {

}

class TestWekaFilterWrapper extends WekaFilter(new TestWekaFilter) {

}