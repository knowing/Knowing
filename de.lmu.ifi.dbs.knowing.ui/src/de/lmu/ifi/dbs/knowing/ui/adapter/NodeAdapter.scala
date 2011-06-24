package de.lmu.ifi.dbs.knowing.ui.adapter

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.graph.Node

class NodeAdapter extends IWorkbenchColumnAdapter {

  def getColumnText(element: Object, columnIndex: Int): String = {
    val node = element.asInstanceOf[Node]
    columnIndex match {
      case 0 => node.id
      case 1 => node.nodeType
      case 4 => node.factoryId
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null

  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[Node].id

  def getParent(o: Object): Object = null

}