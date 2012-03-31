package de.lmu.ifi.dbs.knowing.core.swt.charts.events

import de.lmu.ifi.dbs.knowing.core.events.UIEvent
import org.jfree.chart.event.{ChartProgressListener, ChartChangeListener }

case class ChartProgressListenerRegister(listener: ChartProgressListener) extends UIEvent
case class ChartChangeListenerRegister(listener: ChartChangeListener) extends UIEvent