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
package de.lmu.ifi.dbs.knowing.debug.presenter

import java.io.{ LineNumberReader, Reader }
import java.util.LinkedList
import de.lmu.ifi.dbs.knowing.core.events._

/**
 * Reads files written with ProgressWriter.
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-05-01
 * @see ProgressWriter
 */
class ProgressReader(r: Reader) extends LineNumberReader(r) {

	def readNextStatus(): (String, Status) = readLine() match {
		case null => null
		case line => 
			val classStatus = line.split(";")
			val status = classStatus(1) match {
				case "Created()" => Created()
				case "Ready()" => Ready()
				case "Waiting()" => Waiting()
				case "Running()" => Running()
				case "Finished()" => Finished()
				case "Shutdown()" => Shutdown()
				case "UpdateUI()" => UpdateUI()
				case p if p.startsWith("Progress(") =>
					val content = p.substring(9, p.length -1).split(",")
					Progress(content(0), content(1).toInt, content(2).toInt)
				case ex if ex.startsWith("ExceptionEvent(") =>
					val content = ex.substring(9, ex.length -1).split(",")
					new ExceptionEvent(new Exception(content(0)), content(1))
			}
		
		(classStatus(0), status)
	}

	def readAllStatus(): java.util.List[(String, Status)] = {
		val result = new LinkedList[(String, Status)]
		var classStatus = readNextStatus()
		while (classStatus != null) {
			result.add(classStatus)
			classStatus = readNextStatus()
		}
		result
	}

}