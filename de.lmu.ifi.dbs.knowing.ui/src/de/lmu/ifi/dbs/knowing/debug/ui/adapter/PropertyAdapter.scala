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

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.model.IProperty

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 26.06.2011
 *
 */
class PropertyAdapter extends IWorkbenchColumnAdapter {

  def getColumnText(element: Object, columnIndex: Int): String = {
    val property = element.asInstanceOf[IProperty]
    columnIndex match {
      case 0 => property.getKey.getContent
      case 1 => property.getValue.getContent
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null

  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[IProperty].getKey.getContent

  def getParent(o: Object): Object = null

}