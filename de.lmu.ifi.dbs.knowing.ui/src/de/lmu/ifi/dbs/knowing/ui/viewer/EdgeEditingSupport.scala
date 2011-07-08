package de.lmu.ifi.dbs.knowing.ui.viewer

import org.eclipse.jface.viewers.EditingSupport
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.CellEditor
import org.eclipse.jface.viewers.TextCellEditor
import org.eclipse.jface.viewers.ComboBoxCellEditor
import de.lmu.ifi.dbs.knowing.core.graph.{ Edge, Node }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit

class EdgeEditingSupport(viewer: TableViewer, colum: Int) extends EditingSupport(viewer) with TPropertyChangeSupport {

  private var dpu: DataProcessingUnit = _
  private var nodeIds: Array[String] = Array()

  protected def setValue(element: Object, value: Object) = {
    val edge = element.asInstanceOf[Edge]
    val oldValue = getValue(element)
    var refresh = true
    colum match {
      case 0 => edge.id = value.toString
      case 1 => edge.sourceId = nodeIds(value.asInstanceOf[Int])
      case 2 => edge.targetId = nodeIds(value.asInstanceOf[Int])
      case 3 => edge.weight = value.asInstanceOf[Int]
      case _ => refresh = false
    }
    val newValue = getValue(element)
    if (refresh && !oldValue.eq(newValue)) {
      viewer.refresh()
      propertyChangeSupport.firePropertyChange("property-value", oldValue, newValue)
    }
  }

  protected def getValue(element: Object): Object = {
    val edge = element.asInstanceOf[Edge]
    colum match {
      case 0 => edge.id
      case 1 => nodeIds.indexOf(edge.sourceId).asInstanceOf[AnyRef]
      case 2 => nodeIds.indexOf(edge.targetId).asInstanceOf[AnyRef]
      case 3 => edge.weight.toString
      case _ => "-"
    }
  }

  protected def getCellEditor(element: Object): CellEditor = {
    colum match {
      case 0 => new TextCellEditor(viewer.getTable)
      case 1 => new ComboBoxCellEditor(viewer.getTable(), nodeIds toArray)
      case 2 => new ComboBoxCellEditor(viewer.getTable(), nodeIds toArray)
      case 3 => new TextCellEditor(viewer.getTable)
      case _ => null
    }
  }

  protected def canEdit(element: Object): Boolean = true

  def updateDPU(dpu: DataProcessingUnit) {
    this.dpu = dpu
    nodeIds = dpu.nodes map (n => n.id)
  }

}