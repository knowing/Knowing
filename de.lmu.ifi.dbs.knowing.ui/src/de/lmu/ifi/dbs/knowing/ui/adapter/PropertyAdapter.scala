package de.lmu.ifi.dbs.knowing.ui.adapter

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.graph.xml.Property

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 26.06.2011
 *
 */
class PropertyAdapter extends IWorkbenchColumnAdapter {

  def getColumnText(element: Object, columnIndex: Int): String = {
    val property = element.asInstanceOf[Property]
    columnIndex match {
      case 0 => property.key
      case 1 => property.value
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null

  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[Property].key

  def getParent(o: Object): Object = null

}