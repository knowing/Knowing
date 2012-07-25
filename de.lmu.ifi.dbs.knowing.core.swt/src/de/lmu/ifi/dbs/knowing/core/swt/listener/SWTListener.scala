package de.lmu.ifi.dbs.knowing.core.swt.listener

import de.lmu.ifi.dbs.knowing.core.events.UIEvent
import org.eclipse.swt.widgets.Listener
import org.eclipse.jface.viewers.ISelectionChangedListener

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 11.06.2011
 *
 */
case class SWTListener(typ: Int, listener: Listener) extends UIEvent

case class SelectionChangedListener(listener:ISelectionChangedListener) extends UIEvent