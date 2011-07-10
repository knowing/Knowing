package de.lmu.ifi.dbs.knowing.ui.adapter

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.graph.Edge

class EdgeAdapter extends IWorkbenchColumnAdapter {

    def getColumnText(element: Object, columnIndex: Int): String = {
    val edge = element.asInstanceOf[Edge]
    columnIndex match {
      case 0 => edge.id
      case 1 => edge.getPlainSourceId
      case 2 => edge.getSourcePort
      case 3 => edge.getPlainTargetId
      case 4 => edge.getTargetPort
      case 5 => edge.weight.toString
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null
  
  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[Edge].id

  def getParent(o: Object): Object = null

}