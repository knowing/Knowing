package de.lmu.ifi.dbs.knowing.ui.viewer

import org.eclipse.jface.viewers.EditingSupport
import org.eclipse.jface.viewers.TableViewer
import org.eclipse.jface.viewers.CellEditor
import org.eclipse.jface.viewers.TextCellEditor
import org.eclipse.jface.viewers.ComboBoxCellEditor
import de.lmu.ifi.dbs.knowing.core.graph.{ Edge, Node }
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import EdgeEditingSupport._

class EdgeEditingSupport(viewer: TableViewer, colum: Int) extends EditingSupport(viewer) with TPropertyChangeSupport {

  private var dpu: DataProcessingUnit = _
  private var nodeIds: Array[String] = Array()

  protected def setValue(element: Object, value: Object) = {
    val edge = element.asInstanceOf[Edge]
    val oldValue = getValue(element)
    var refresh = true
    colum match {
      case EDGE_ID => edge.id = value.toString
      case SOURCE_ID => edge.setPlainSourceId(nodeIds(value.asInstanceOf[Int]))
      case SOURCE_PORT => edge.setSourcePort(value.toString)
      case TARGET_ID => edge.setPlainTargetId(nodeIds(value.asInstanceOf[Int]))
      case TARGET_PORT => edge.setTargetPort(value.toString)
      case EDGE_WEIGHT => edge.weight = value.asInstanceOf[Int]
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
      case EDGE_ID => edge.id
      case SOURCE_ID => nodeIds.indexOf(edge.sourceId).asInstanceOf[AnyRef]
      case SOURCE_PORT => edge.getSourcePort
      case TARGET_ID => nodeIds.indexOf(edge.targetId).asInstanceOf[AnyRef]
      case TARGET_PORT => edge.getTargetPort
      case 5 => edge.weight.toString
      case _ => "-"
    }
  }

  protected def getCellEditor(element: Object): CellEditor = {
    colum match {
      case EDGE_ID => new TextCellEditor(viewer.getTable)
      case SOURCE_ID => new ComboBoxCellEditor(viewer.getTable(), nodeIds toArray)
      case SOURCE_PORT => new TextCellEditor(viewer.getTable)
      case TARGET_ID => new ComboBoxCellEditor(viewer.getTable(), nodeIds toArray)
      case TARGET_PORT => new TextCellEditor(viewer.getTable)
      case EDGE_WEIGHT => new TextCellEditor(viewer.getTable)
      case _ => null
    }
  }

  protected def canEdit(element: Object): Boolean = true

  def updateDPU(dpu: DataProcessingUnit) {
    this.dpu = dpu
    nodeIds = dpu.nodes map (n => n.id)
  }

}

object EdgeEditingSupport {
  val EDGE_ID = 0
  val SOURCE_ID = 1
  val SOURCE_PORT = 2
  val TARGET_ID = 3
  val TARGET_PORT = 4
  val EDGE_WEIGHT = 5
}