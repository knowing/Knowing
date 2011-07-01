package de.lmu.ifi.dbs.knowing.ui.viewer

import org.eclipse.jface.viewers.{ EditingSupport, CellEditor, TableViewer, TextCellEditor }
import java.util.Properties
import de.lmu.ifi.dbs.knowing.core.graph.xml.Property

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 26.06.2011
 *
 */
class PropertyEditingSupport(viewer: TableViewer, var properties: Properties) extends EditingSupport(viewer) with TPropertyChangeSupport {

  protected def setValue(element: Object, value: Object) {
    val property = element.asInstanceOf[Property]
    val contains = properties.containsKey(property.key)
    contains match {
      case true => 
        val oldVal = properties.setProperty(property.key, value.toString)
        property.value = value.toString
        viewer.refresh()
        propertyChangeSupport.firePropertyChange("property", null, property)
      case false => println("Key[" + property.key + "] not found. Value[" + value + "] not set")
    }
  }

  protected def getValue(element: Object): Object = element.asInstanceOf[Property].value

  protected def getCellEditor(element: Object): CellEditor = new TextCellEditor(viewer.getTable)

  protected def canEdit(element: Object): Boolean = true


  
}