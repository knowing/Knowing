package de.lmu.ifi.dbs.knowing.core.test

import javax.xml.bind.{ JAXBContext, Marshaller }
import akka.actor.Actor
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.graph._
import de.lmu.ifi.dbs.knowing.core.graph.xml._
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

object Tester extends Application {

//  testValidator
  testDPU
  
  def testValidator {
    val nodes: Array[Node] = new Array(0)
    val validator = new GraphValidator(nodes, testEdges)
    println("Has circle: " + validator.hasCircle)
  }
  
  def testDPU {
    val dpu = new DataProcessingUnit("TestDPU")
    dpu.description = "Simple Description here"
    dpu.tags = "Tag1, Tag2"
    dpu.nodes = testNodes
    dpu.edges = testEdges
    
    val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
    val m = context.createMarshaller
    m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true)
    
    m.marshal(dpu, System.out)
  }
  
  def testNodes:Array[PersistentNode] = {
    val n1 = PersistentNode("n1", "factory1", "loader")
    val n2 = PersistentNode("n2", "factory2", "processor")
    val n3 = PersistentNode("n3", "factory3", "presenter")
    Array(n1,n2,n3)
  }
  
  def testEdges:Array[Edge] = {
    val edges = Array(new Edge("e1", "n1", "n2"),
      new Edge("e2", "n1", "n3"),
      new Edge("e3", "n2", "n4"),
      new Edge("e4", "n2", "n5"),
      new Edge("e5", "n3", "n5"),
      new Edge("e6", "n4", "n6"),
      new Edge("e7", "n4", "n1"))
      edges
  }
}