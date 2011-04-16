package de.lmu.ifi.dbs.knowing.core.test

import akka.actor.Actor
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.events._

import java.util.Properties

class Tester extends Actor {

  def receive = {
    case msg: String => println(msg); test
    case _ => test
  }
  def test() {
    val processor = actorOf[TestProcessor].start
    val processor2 = actorOf(new TestProcessor()).start
    processor ! Results(null)
    processor ! Query(null)
    processor ! Start(new Properties())

    processor2 !! Results(null)

    processor.stop
    processor2.stop
  }
}