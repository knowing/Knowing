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
package de.lmu.ifi.dbs.knowing.core.processing

import scala.collection.JavaConversions._
import TClassPropertyResolver._

/**
 * Resolve class from processor properties.
 *
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-02
 */
trait TClassPropertyResolver { this: TProcessor =>

	@throws(classOf[ClassNotFoundException])
	def resolveClass[T](key: String)(implicit m: Manifest[T]): Option[T] = resolveClass[T](key, getClass.getClassLoader)(m)

	@throws(classOf[ClassNotFoundException])
	def resolveClass[T](key: String, classloader: ClassLoader)(implicit m: Manifest[T]): Option[T] = {
		val className = properties.getProperty(key)
		if(className == null)
			return None
		//TODO check Manifest
		val clazz = Class.forName(className, true, classloader)

		val arguments = properties.stringPropertyNames
			.filter(_.startsWith(key + "."))
			.toList
			.sortBy { key =>
				val splitIndex = key.lastIndexOf(TYPE_SEPARATOR)
				key.splitAt(splitIndex + 1)._2.toInt
			}
			.map { key =>
				val value = properties.getProperty(key)
				val splitIndex = value.lastIndexOf(TYPE_SEPARATOR)
				val valueType = value.splitAt(splitIndex)
				//remove :
				valueAndType(valueType._1, valueType._2.substring(1))
			}

		//sameElements seem to work. 
		val parameterTypes = arguments.map(_._2).toArray
		val constructors = clazz.getConstructors
		constructors.find(_.getParameterTypes sameElements parameterTypes).flatMap {
			constructor =>
				val args = arguments.map(_._1).toSeq
				log.debug("Generating instance of " + className )
				Some(constructor.newInstance(args: _*).asInstanceOf[T])
		}
	}

	/**
	 * @param value as string representation
	 * @param typ: case insensitive. Allowed values int | long | double | float | boolean | string | class
	 */
	def valueAndType(value: String, typ: String): (Object, Class[_]) = typ.toLowerCase match {
		case "int" => (java.lang.Integer.valueOf(value), classOf[Integer])
		case "long" => (java.lang.Long.valueOf(value), classOf[Long])
		case "double" => (java.lang.Double.valueOf(value), classOf[Double])
		case "float" => (java.lang.Float.valueOf(value), classOf[Float])
		case "boolean" => (java.lang.Boolean.valueOf(value), classOf[Boolean])
		case "string" => (value, classOf[String])

		//TODO Recursive call needed
		case "class" => (Unit, classOf[AnyRef])
	}

}

object TClassPropertyResolver {
	val TYPE_SEPARATOR = ":"
}