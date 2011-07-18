package de.lmu.ifi.dbs.knowing.core.weka

import akka.actor.ScalaActorRef
import weka.core.Instances
import weka.core.Attribute
import java.util.ArrayList
import de.lmu.ifi.dbs.knowing.core.weka._
import de.lmu.ifi.dbs.knowing.core.weka.WekaArffLoaderFactory._
import ExtendedWekaArffLoader._
import akka.actor.ActorRef
import akka.actor.Actor.actorOf
import java.io.File

class ExtendedWekaArffLoader extends WekaArffLoader {    

    override def getDataSet(): Instances = { 
    	var inst:Instances = loader.getDataSet
    	  
    	//var file:File = new File(uri);
    	var filepath = uri.getRawSchemeSpecificPart
    	var actClass:String = filepath.split("-")(1)	   
    	val list = new ArrayList[String]
    	list.add(actClass)    	
    	inst.insertAttributeAt(new Attribute("class",list), inst.numAttributes)
    	inst.setClassIndex(inst.numAttributes-1);
    	var i = 0;
    	while (i < inst.numInstances) {
    		inst.get(i).setClassValue(actClass)
    		i += 1
    	}
    	inst
    }
 

}

object ExtendedWekaArffLoader extends WekaArffLoader {


}

class ExtendedWekaArffLoaderFactory extends WekaArffLoaderFactory {
  override val name: String = ExtendedWekaArffLoaderFactory.name
  override val id: String = ExtendedWekaArffLoaderFactory.id
  
  override def getInstance: ActorRef = actorOf[ExtendedWekaArffLoader]
 
}

object ExtendedWekaArffLoaderFactory {
  val name: String = "Extended Weka ARFF Loader"
  val id: String = classOf[ExtendedWekaArffLoader].getName
}