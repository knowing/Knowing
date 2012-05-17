package de.lmu.ifi.dbs.knowing.presenter

class TestPieChartPresenter extends IPieChartPresenter[Any] with TestPresenter {

  def addOrUpdatePiece(label: String, value: Double) = {
	  sender ! label
	  sender ! value
  }

  def update() = {
    sender ! "update"
  }

}