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

import de.lmu.ifi.dbs.knowing.core.processing.TPresenter
import java.nio.file.{ Files, Path }
import java.io.IOException
import java.util.Properties
import weka.core.converters.{ Saver, ArffSaver }
import weka.core.Instances
import org.slf4j.LoggerFactory

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2011-04-22
 *
 */
class DebugPresenter extends TPresenter[Path] {

  val name = "Debug Presenter"

  private var saver: ArffSaver = _
  private var file: Path = _
  private var nodeId: String = _

  def buildPresentation(instances: Instances) = try {
    log.info("Writing instances[" + instances.relationName + " | " + instances.size + "] to file " + file)
    saver.setInstances(new Instances(instances, 0))
    for (i <- 0 until instances.size) {
      saver.writeIncremental(instances.get(i))
    }
    // Flush buffer
    saver.getWriter.flush
  } catch {
    case e: IOException => log.error("Error on writing Instances[" + instances.relationName + "]", e)
  }

  def createContainer(file: Path) = try {
    this.file = file

    saver = new ArffSaver
    saver.setRetrieval(Saver.INCREMENTAL)
    saver.setFile(file.toFile)

  } catch {
    case e: IOException =>
      log.error("Error on creating container for file " + file, e)
      throwException(e, "Error creating file container destination")
  }

  def configure(properties: Properties) {}

  override def postStop() = try {
    saver.resetOptions
    saver.resetStructure
    saver.resetWriter
  } catch {
    case e: Exception =>
      log.error("Error on resetting", e)
      throwException(e, "Could not reset (close) ArffSaver")
  }

  def sync(parent: Path)(syncFun: => Unit) = syncFun

  def sync(parent: Path, runnable: Runnable) = runnable.run()

  def getContainerClass(): String = "file"

  def getParent(): Path = file.getParent

}

