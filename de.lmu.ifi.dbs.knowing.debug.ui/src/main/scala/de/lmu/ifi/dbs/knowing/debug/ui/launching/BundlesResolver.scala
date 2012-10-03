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
package de.lmu.ifi.dbs.knowing.debug.ui.launching

import java.util.{List => JList}
import scala.collection.JavaConversions._
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy
import de.lmu.ifi.dbs.knowing.launcher.LaunchConfiguration

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
class BundlesResolver(config: ILaunchConfigurationWorkingCopy) {

	lazy val availableBundles = config.getAttribute("target_platform", "").split(",")
	lazy val missingBundles: JList[String] = LaunchConfiguration.REQUIRED_BUNDLES.intersect(availableBundles)
	
	lazy val isBundleMissing = missingBundles.size > 0
	
	lazy val selectedBundles = LaunchConfiguration.REQUIRED_BUNDLES.reduceLeft((bundles, b) => bundles + "," + b)
}