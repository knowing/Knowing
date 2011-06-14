package de.lmu.ifi.dbs.knowing.core.swt.charts.events

import de.lmu.ifi.dbs.knowing.core.events.Event
import org.jfree.chart.event.{ChartProgressListener, ChartChangeListener }

case class ChartProgressListenerRegister(listener: ChartProgressListener) extends Event
case class ChartChangeListenerRegister(listener: ChartChangeListener) extends Event