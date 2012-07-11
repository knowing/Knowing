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
package de.lmu.ifi.dbs.knowing.debug.ui.adapter

import de.lmu.ifi.dbs.knowing.core.model.IParameter
import org.eclipse.core.runtime.IAdapterFactory
import org.eclipse.swt.graphics.Image
import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.ui.model.IWorkbenchAdapter

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 2012-04-18
 *
 */
class ParameterAdapter extends IWorkbenchColumnAdapter {

	def getColumnText(element: Object, columnIndex: Int): String = {
		val parameter = element.asInstanceOf[IParameter]
		columnIndex match {
			case 0 => parameter.getKey.getContent
			case 1 => parameter.getValue.getContent
			case _ => "-"
		}
	}

	def getColumnImage(element: Object, columnIndex: Int): Image = null

	def getChildren(o: Object): Array[Object] = null

	def getImageDescriptor(obj: Object): ImageDescriptor = null

	def getLabel(o: Object): String = o.asInstanceOf[IParameter].getKey.getContent

	def getParent(o: Object): Object = null

}

class ParameterAdapterFactory extends IAdapterFactory {

	import ParameterAdapterFactory._
	
	def getAdapter(adaptableObject: Object, adapterType: Class[_]): Object = (adaptableObject, adapterType) match {
		case (null, _) => null
		case (p: IParameter, t) if types.exists(_.equals(t)) => new ParameterAdapter
		case (_, _) => null
	}

	def getAdapterList(): Array[Class[_]] = ParameterAdapterFactory.types
}

object ParameterAdapterFactory {
	val types: Array[Class[_]] = Array(classOf[IWorkbenchAdapter], classOf[IWorkbenchColumnAdapter])
}