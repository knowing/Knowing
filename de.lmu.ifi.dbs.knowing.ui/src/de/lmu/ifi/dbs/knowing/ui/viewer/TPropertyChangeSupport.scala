package de.lmu.ifi.dbs.knowing.ui.viewer
import java.beans.{PropertyChangeListener, PropertyChangeSupport}

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 01.07.2011
 *
 */
trait TPropertyChangeSupport {

  protected val propertyChangeSupport = new PropertyChangeSupport(this)

  def addPropertyChangeListener(listener: PropertyChangeListener) = propertyChangeSupport.addPropertyChangeListener(listener)

  def removePropertyChangeListener(listener: PropertyChangeListener) = propertyChangeSupport.removePropertyChangeListener(listener)

}