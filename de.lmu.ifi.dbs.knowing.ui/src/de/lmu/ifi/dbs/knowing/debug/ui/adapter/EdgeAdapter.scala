package de.lmu.ifi.dbs.knowing.debug.ui.adapter

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.model.IEdge

class EdgeAdapter extends IWorkbenchColumnAdapter {

    def getColumnText(element: Object, columnIndex: Int): String = {
    val edge = element.asInstanceOf[IEdge]
    columnIndex match {
      case 0 => edge.getId.getContent
      case 1 => edge.getSource.getContent
      case 2 => edge.getSourcePort.getContent
      case 3 => edge.getTarget.getContent
      case 4 => edge.getTargetPort.getContent
      case 5 => edge.getWeight.getContent
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null
  
  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[IEdge].getId.getContent

  def getParent(o: Object): Object = null

}