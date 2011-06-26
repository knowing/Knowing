package de.lmu.ifi.dbs.knowing.ui.viewer

import PropertyTableViewer._
import java.util.Properties
import scala.collection.JavaConversions.asScalaSet
import org.eclipse.jface.viewers.{ TableViewer, TableViewerColumn, ArrayContentProvider }
import org.eclipse.swt.widgets.Table
import org.eclipse.swt.SWT
import de.lmu.ifi.dbs.knowing.ui.provider.WorkbenchTableLabelProvider
import de.lmu.ifi.dbs.knowing.core.graph.Node
import de.lmu.ifi.dbs.knowing.core.graph.xml.Property

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 26.06.2011
 *
 */
class PropertyTableViewer(table: Table) extends TableViewer(table) {

  getTable.setHeaderVisible(true)
  getTable.setLinesVisible(true)

  val propertyColumn = new TableViewerColumn(this, SWT.LEAD)
  propertyColumn.getColumn.setText(columns(0))
  propertyColumn.getColumn.setWidth(width(0))
  propertyColumn.getColumn.setResizable(true)
  propertyColumn.getColumn.setMoveable(true)

  val valueColumn = new TableViewerColumn(this, SWT.LEAD)
  val valueEditingSupport = new PropertyEditingSupport(this, new Properties)
  valueColumn.getColumn.setText(columns(1))
  valueColumn.getColumn.setWidth(width(1))
  valueColumn.getColumn.setResizable(true)
  valueColumn.getColumn.setMoveable(true)
  valueColumn.setEditingSupport(valueEditingSupport)

  setContentProvider(new ArrayContentProvider)
  setLabelProvider(new WorkbenchTableLabelProvider)

  def setInput2(node: Node)  {
    valueEditingSupport.properties = node.properties
    setInput(convert(node.properties))
  }
  
  private def convert(properties: Properties): Array[Property] = {
    val keys = asScalaSet(properties.stringPropertyNames)
    val propList = for(key <- keys) yield new Property(key, properties.getProperty(key))
    //TODO Random order every time!
    propList.toArray
  }

}

object PropertyTableViewer {
  val columns = Array("Property", "Value")
  val width = Array(150, 150)
}

//class Property(var key: String, var value: String)