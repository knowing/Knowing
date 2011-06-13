package de.lmu.ifi.dbs.knowing.core.swt.handler

import de.lmu.ifi.dbs.knowing.core.events.Event
import org.eclipse.swt.widgets.Listener
import org.eclipse.jface.viewers.ISelectionChangedListener

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 11.06.2011
 *
 */
case class SWTListener(typ: Int, listener: Listener) extends Event

case class SelectionChangedListener(listener:ISelectionChangedListener) extends Event