/*                                                              *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
**                                                              **
** Knowing Framework                                            **
** Apache License - http://www.apache.org/licenses/             **
** LMU Munich - Database Systems Group                          **
** http://www.dbs.ifi.lmu.de/                                   **
\*                                                              */
package de.lmu.ifi.dbs.knowing.core.weka

import weka.core.{ Attribute, Instances }
import java.util.{ Properties, ArrayList }
import java.io.File
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffLoader._
import de.lmu.ifi.dbs.knowing.core.factory.{ TFactory, ProcessorFactory }
import akka.actor.{ ActorContext, ActorSystem, ActorRef, Props }
import ExtendedWekaArffLoader._
import de.lmu.ifi.dbs.knowing.core.processing.ExecutionContext

class ExtendedWekaArffLoader extends WekaArffLoader {

	override def getDataSet(): Instances = {
		var inst: Instances = super.getDataSet

		//    	val filepath = uri.getRawSchemeSpecificPart
		val filepath = ""

		var filename: String = null
		if (filepath.contains('/')) {
			filename = filepath.substring(filepath.lastIndexOf('/') + 1, filepath.length)
		} else {
			filename = filepath
		}

		val filenames = new ArrayList[String]
		filenames.add(filename)
		inst.insertAttributeAt(new Attribute(SOURCE_ATTRIBUTE, filenames), inst.numAttributes)

		var actClass: String = null
		if (assignClassFromFilename) {
			val splits: Array[String] = filename.split(splitChar)
			if (splits != null && splitPosition < splits.length) {
				actClass = splits(splitPosition)
				val classes = new ArrayList[String]
				classes.add(actClass)
				inst.insertAttributeAt(new Attribute(CLASS_ATTRIBUTE, classes), inst.numAttributes)
				inst.setClassIndex(inst.numAttributes - 1);
			}
		}

		var i = 0;
		while (i < inst.numInstances) {
			inst.get(i).setValue(inst.attribute(SOURCE_ATTRIBUTE), filename);
			if (actClass != null) {
				inst.get(i).setClassValue(actClass)
			}
			i += 1
		}

		inst
	}

	override def configure(properties: Properties) = {
		super.configure(properties)
		val acff = properties.getProperty(PROP_ASSIGN_CLASS_FROM_FILENAME);
		if (acff != null) {
			setAssignClassFromFilename(acff.toBoolean)
		}
		val sc = properties.getProperty(PROP_SPLIT_CHAR)
		if (sc != null) {
			setSplitChar(sc.toCharArray()(0))
		}
		val sp = properties.getProperty(PROP_SPLIT_POS)
		if (sp != null) {
			try {
				val spInt = Integer.valueOf(sp)
				setSplitPositon(spInt)
			} catch {
				case nfe: NumberFormatException =>
			}
		}
	}

	def getSplitChar(): Char = splitChar

	private def setSplitChar(c: Char) = {
		if (c != 0) {
			splitChar = c
		}
	}

	def getSplitPosition(): Int = splitPosition

	private def setSplitPositon(p: Int) {
		if (p >= 0) {
			splitPosition = p;
		}
	}

	def isAssignClassFromFilename(): Boolean = assignClassFromFilename

	private def setAssignClassFromFilename(b: Boolean) {
		assignClassFromFilename = b
	}
}

object ExtendedWekaArffLoader {
	val PROP_SPLIT_CHAR = "splitchar"
	val PROP_SPLIT_POS = "splitposition"
	val PROP_ASSIGN_CLASS_FROM_FILENAME = "assignClassFromFilename"

	val SOURCE_ATTRIBUTE = "source"
	val CLASS_ATTRIBUTE = "class"

	var splitChar: Char = '-';
	var splitPosition: Int = 0;
	var assignClassFromFilename: Boolean = false;
}

class ExtendedWekaArffLoaderFactory extends WekaArffLoaderFactory {
	override val name: String = ExtendedWekaArffLoaderFactory.name
	override val id: String = ExtendedWekaArffLoaderFactory.id

	override def getInstance(context: ExecutionContext, factory: TFactory.ActorFactory): ActorRef = {
	  factory.actorOf(Props(new ExtendedWekaArffLoader), context.name)
	}

	override def createDefaultProperties: Properties = {
		val returns = super.createDefaultProperties

		returns setProperty (PROP_SPLIT_CHAR, "-")
		returns.setProperty(PROP_SPLIT_POS, "1")
		returns.setProperty(PROP_ASSIGN_CLASS_FROM_FILENAME, "false")

		returns
	}

	override def createPropertyValues: Map[String, Array[_ <: Any]] = {
		val map = super.createPropertyValues
		map
	}

	override def createPropertyDescription: Map[String, String] = {
		val map1 = super.createPropertyDescription
		val map2: Map[String, String] = Map[String, String]((PROP_ASSIGN_CLASS_FROM_FILENAME -> "Determine the class value out of the filename and assign it to the dataset"),
			(PROP_SPLIT_CHAR -> "Character used to split the filename for the class value"),
			(PROP_SPLIT_POS -> "The index to determine the class out of the splitted parts"))
		val map = map1 ++ map2
		map
	}

}

object ExtendedWekaArffLoaderFactory {
	val name: String = "Extended Weka ARFF Loader"
	val id: String = classOf[ExtendedWekaArffLoader].getName
}