package de.lmu.ifi.dbs.knowing.test

import de.lmu.ifi.dbs.knowing.presenter._
import de.lmu.ifi.dbs.knowing.core.events._
import weka.core.{ Instances, Attribute }

object EmbeddedPresenters {

	val EmbeddedUIComponentFactory = new EmbeddedUIComponentPresenterFactory

	val TablePresenterFactory = new EmbeddedUIComponentTablePresenterFactory()
}

/**
 *
 * @author Nepomuk Seiler
 * @version 0.1
 */
class EmbeddedUIComponentTablePresenter extends EmbeddedUIComponentPresenter with ITablePresenter[IEmbeddedUIComponent] {

	override def process(instances: Instances) = { case _ => parent.addInstances(instances) }

	def buildTableHeader(attributes: Array[Attribute]) = {}
	def addRow(content: Array[String]) = {}
	def update() = {}
}

class EmbeddedUIComponentTablePresenterFactory extends PresenterFactory(
	classOf[EmbeddedUIComponentTablePresenter],
	classOf[ITablePresenter[IEmbeddedUIComponent]])