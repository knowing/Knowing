package de.lmu.ifi.dbs.knowing.core.test

import weka.core.converters.ArffLoader
import javax.xml.bind.{ JAXBContext, Marshaller }
import akka.actor.Actor
import akka.actor.Actor.actorOf
import de.lmu.ifi.dbs.knowing.core.graph._
import de.lmu.ifi.dbs.knowing.core.graph.xml._
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.util._
import de.lmu.ifi.dbs.knowing.core.weka._

import java.util.Properties

class Tester extends Actor {

  def receive = {
    case msg: String => log debug ("Message:" + msg); runDPU
    case Registered => log debug ("### Registered: " + self)
    case msg: Event => log debug ("### Event: " + msg.getClass().getSimpleName)
    case _ => log error ("Unknow Message for tester")
  }

  def testPlain {
    val arff = Util.getFactoryService(WekaArffLoaderFactory.id)
    val naiveBayes = Util.getFactoryService(NaiveBayesFactory.id)
    val oneR = Util.getFactoryService(classOf[weka.classifiers.rules.OneR].getName)
    if (arff.isDefined && naiveBayes.isDefined && oneR.isDefined) {
      val arffActor = arff.get.getInstance().start
      val naiveActor = naiveBayes.get.getInstance().start
      val oneRActor = oneR.get.getInstance().start

      arffActor ! Register(naiveActor)
      arffActor ! Register(oneRActor)
      arffActor ! Configure(arffProperties)
      arffActor ! Start
    } else {
      log error ("One actor not defined:" + arff + " / " + naiveBayes)
    }
  }
  
  def runDPU {
    val supervisor = actorOf(new GraphSupervisor(createDPU)).start
    supervisor ! Start
  }

  def createDPU:DataProcessingUnit = {
    val dpu = new DataProcessingUnit("TestDPU")
    dpu.description = "Simple Description here"
    dpu.tags = "Tag1, Tag2"
    dpu.edges = testEdges
    testNodes foreach (node => dpu addNode (node))

    val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
    val m = context.createMarshaller
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    m.marshal(dpu, System.out)
    dpu
  }

  def testNodes: Array[Node] = {
    val n1 = LoaderNode("ARFF", classOf[ArffLoader].getName, arffProperties)
    val n2 = ProcessorNode("OneR", classOf[weka.classifiers.rules.OneR].getName, new Properties)
    val n3 = ProcessorNode("NaiveBayes", classOf[weka.classifiers.bayes.NaiveBayes].getName, new Properties)
    Array(n1, n2, n3)
  }

  def testEdges: Array[Edge] = {
    val arff_OneR = new Edge("arff_OneR", "ARFF", "OneR")
    val arff_NaiveBayes = new Edge("arff_NaiveBayes", "ARFF", "NaiveBayes")
    Array(arff_OneR, arff_NaiveBayes)
  }

  def arffProperties: Properties = {
    val properties = new Properties()
    properties.setProperty(WekaArffLoader.PROP_FILE, "/home/muki/iris.arff")
    properties
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
    dpu.nodes = testNodesPersistent
    dpu.edges = testEdges

    val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
    val m = context.createMarshaller
    m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true)
    m.marshal(dpu, System.out)
  }

  def testNodesPersistent: Array[PersistentNode] = {
    val n1 = PersistentNode("n1", "factory1", "loader")
    val n2 = PersistentNode("n2", "factory2", "processor")
    val n3 = PersistentNode("n3", "factory3", "presenter")
    Array(n1, n2, n3)
  }

  def testEdges: Array[Edge] = {
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