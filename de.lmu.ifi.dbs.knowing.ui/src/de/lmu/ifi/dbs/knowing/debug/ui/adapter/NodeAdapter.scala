package de.lmu.ifi.dbs.knowing.debug.ui.adapter

import org.eclipse.jface.resource.ImageDescriptor
import org.eclipse.swt.graphics.Image
import de.lmu.ifi.dbs.knowing.core.model.INode

class NodeAdapter extends IWorkbenchColumnAdapter {

  def getColumnText(element: Object, columnIndex: Int): String = {
    val node = element.asInstanceOf[INode]
    columnIndex match {
      case 0 => node.getId.getContent
      case 1 => node.getType.getText
      case 4 => node.getFactoryId.getText
      case _ => "-"
    }
  }

  def getColumnImage(element: Object, columnIndex: Int): Image = null

  def getChildren(o: Object): Array[Object] = null

  def getImageDescriptor(obj: Object): ImageDescriptor = null

  def getLabel(o: Object): String = o.asInstanceOf[INode].getId.getContent

  def getParent(o: Object): Object = null

}