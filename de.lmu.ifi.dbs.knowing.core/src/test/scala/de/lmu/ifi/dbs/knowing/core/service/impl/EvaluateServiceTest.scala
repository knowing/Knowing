/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.core.service.impl

import org.scalatest.{ BeforeAndAfter, FunSuite }
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import de.lmu.ifi.dbs.knowing.core.processing.DPUExecutor
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit
import de.lmu.ifi.dbs.knowing.core.events._

@RunWith(classOf[JUnitRunner])
class EvaluateServiceTest extends FunSuite with ShouldMatchers with BeforeAndAfter {

	var service: EvaluateService = _

	before {
		service = new EvaluateService

		val asm = new ActorSystemManager
		service.bindActorSystemManager(asm)
		// service.bindDirectoryService(embeddedFactoryDirectory)
		// service.bindModelStoreService(embeddedModelStore)
		// service.bindResourceStoreService(embeddedResourceStore)
		// service.bindDPUDirectoryService(embeddedDPUDirectory)
		// service.bindUIFactory(embeddedUIFactory)

	}

	after {

	}

	test("test evaluate(config)") {

	}
}