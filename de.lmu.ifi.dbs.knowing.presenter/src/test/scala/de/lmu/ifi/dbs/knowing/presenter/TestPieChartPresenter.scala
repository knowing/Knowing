package de.lmu.ifi.dbs.knowing.presenter

import de.lmu.ifi.dbs.knowing.test.TestPresenter
import akka.actor.ScalaActorRef

class TestPieChartPresenter extends IPieChartPresenter[Any] with TestPresenter {

  def addOrUpdatePiece(label: String, value: Double) = {
	  self.sender.get ! label
	  self.sender.get ! value
  }

  def update() = {
    self.sender.get ! "update"
  }

}