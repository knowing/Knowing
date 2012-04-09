/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.exceptions

import scala.collection.JavaConversions.asJavaList
import de.lmu.ifi.dbs.knowing.core.util.Validation

class KnowingException(msg:String, cause: Throwable = null) extends Exception(msg, cause)

class ValidationException(msg: String, val validation: Validation) extends Exception(msg) {
	
	def getErrors(): java.util.List[String] = asJavaList[String](validation.getErrors)
	
	def getWarnings(): java.util.List[String] = asJavaList[String](validation.getWarnings)
	
}