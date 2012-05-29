package de.lmu.ifi.dbs.knowing.core.common

import de.lmu.ifi.dbs.knowing.core.processing.{ TFilter, TSerializable }
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil.ATTRIBUTE_CLASS
import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory
import weka.core.{ Attribute, Instance, Instances }
import java.util.Properties
import java.io.{ InputStreamReader, LineNumberReader, PrintWriter }
import scala.collection.JavaConversions._
import AddClassAttribute._
import java.io.IOException

/**
 * <p> Adds a class-attribute to the instances </p>
 *
 * @author Nepomuk Seiler
 * @version 1.0
 * @since 2011-11-10
 */
class AddClassAttribute extends TFilter with TSerializable {

	private var classes = Array[String]()

	override def start() = try {
		inputStream() match {
			case None => log.warning("No classAttributes to deserialize")
			case Some(in) =>
				val reader = new LineNumberReader(new InputStreamReader(in))
				classes = reader.readLine.split(",")
				val classStr = classes.reduce((str, c) => str + "," + c)
				log.debug("Loaded classes " + classStr)
				reader.close()
		}
	} catch {
		case e: IOException => 
			log.warning("classAttribute.mdl could not be found")
			if(classes.isEmpty)
				throwException(e, "classAttribute.mdl could not be found")
	}

	override def postStop() {
		outputStream() match {
			case None =>
			case Some(out) =>
				val writer = new PrintWriter(out)
				classes.length match {
					case 0 =>
					case 1 => writer.println(classes(0))
					case n =>
						for (i <- 0 until n - 1)
							writer.println(classes(i) + ",")
						writer.println(classes(n - 1))
				}
				writer.flush
				writer.close
		}
	}

	override def filter(instances: Instances): Instances = {
		val ret = new Instances(instances)
		ret.insertAttributeAt(new Attribute(ATTRIBUTE_CLASS, asJavaList(classes)), ret.numAttributes)
		ret
	}

	def query(query: Instance): Instances = {
		val ret = new Instances(query.dataset, 1)
		ret.add(query)
		ret.insertAttributeAt(new Attribute(ATTRIBUTE_CLASS, asJavaList(classes)), query.dataset.numAttributes)
		ret
	}

	def configure(properties: Properties) {
		classes.length match {
			case 0 => classes = properties.getProperty(PROP_CLASSES, "").split(",")
			case _ => //got loaded already
		}

	}
}

object AddClassAttribute {
	val PROP_CLASSES = "classes"
}

class AddClassAttributeFactory extends ProcessorFactory(classOf[AddClassAttribute])