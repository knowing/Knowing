package de.lmu.ifi.dbs.knowing.core.test.processor

import de.lmu.ifi.dbs.knowing.core.test.processor.TestWekaFilter;
import de.lmu.ifi.dbs.knowing.core.weka.WekaFilterFactory
import de.lmu.ifi.dbs.knowing.core.weka.WekaFilter

class TestWekaFilterFactory extends WekaFilterFactory[TestWekaFilterWrapper, TestWekaFilter](classOf[TestWekaFilterWrapper], classOf[TestWekaFilter]) {

}

class TestWekaFilterWrapper extends WekaFilter(new TestWekaFilter) {

}