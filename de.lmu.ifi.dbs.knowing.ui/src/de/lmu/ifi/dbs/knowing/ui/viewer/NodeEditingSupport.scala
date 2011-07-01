package de.lmu.ifi.dbs.knowing.ui.viewer

import de.lmu.ifi.dbs.knowing.core.graph.Node
import org.eclipse.jface.viewers.{ ComboBoxCellEditor, TextCellEditor, TableViewer, CellEditor, EditingSupport }
import NodeEditingSupport._

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 01.07.2011
 *
 */
class NodeEditingSupport(viewer: TableViewer, colum: Int) extends EditingSupport(viewer) with TPropertyChangeSupport {

  protected def setValue(element: Object, value: Object) = {
    val node = element.asInstanceOf[Node]
    var refresh = true
    colum match {
      case 0 => node.id = value.toString
      case 1 => node.nodeType = types(value.asInstanceOf[Int])
      case 4 => node.factoryId = value.toString
      case _ => refresh = false
    }
    if (refresh) {
      viewer.refresh()
      propertyChangeSupport.firePropertyChange("property", null, element)
    }
  }

  protected def getValue(element: Object): Object = {
    val node = element.asInstanceOf[Node]
    colum match {
      case 0 => node.id
      case 1 => types.indexOf(node.nodeType).asInstanceOf[AnyRef]
      case 4 => node.factoryId
      case _ => "-"
    }
  }

  protected def getCellEditor(element: Object): CellEditor = {
    colum match {
      case 0 => new TextCellEditor(viewer.getTable)
      case 1 => new ComboBoxCellEditor(viewer.getTable(), types);
      case 4 => new TextCellEditor(viewer.getTable)
      case _ => null
    }

  }

  protected def canEdit(element: Object): Boolean = {
    colum match {
      case 0 => true
      case 1 => true
      case 4 => true
      case _ => false
    }
  }
}

object NodeEditingSupport {
  val types = Array("loader", "processor", "presenter")
}