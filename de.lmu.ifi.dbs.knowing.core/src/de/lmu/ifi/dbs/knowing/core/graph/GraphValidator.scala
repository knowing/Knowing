package de.lmu.ifi.dbs.knowing.core.graph

import scala.collection.immutable.HashMap
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit

class GraphValidator(nodes: Array[Node], edges: Array[Edge]) {

  val errors = new HashMap[String, String]

  def this(dpu: DataProcessingUnit) = this(dpu.nodes, dpu.edges)

  def validate: Boolean = {
    true
  }

  def hasCircle: Boolean = {
    hasCircle(null, null, 0);
  }

  def hasCircle(edgesLeft: Array[Edge], start: Edge, size: Int): Boolean = {
    /*
		//All edges could be removed, no circle
		if(edgesLeft.isEmpty())
			return false;
		
		//Get next edge (reduce list.size())
		Edge edge = edgesLeft.pollFirst();
		//List doesnt changed in the iteration -> circle
		if(edge.equals(start) && size == edgesLeft.size()+1)
			return true;
		
		String source = edge.getSourceId();
		String target = edge.getTargetId();
		
		boolean rootSource = true;
		boolean leafTarget = true;
		
		for (Edge each : edgesLeft) {
			if(each.getTargetId().equals(source))
				rootSource = false; // edge source is not a root
			if(each.getSourceId().equals(target))
				leafTarget = false; // edge target is not a leaf
		}
		
		if(rootSource || leafTarget) 
			return hasCircle(edgesLeft, null, 0);
		
		edgesLeft.addLast(edge);
		
		if(start == null)
			return hasCircle(edgesLeft, edge, edgesLeft.size());
		else if(edge.equals(start))
			return hasCircle(edgesLeft, edge, size);
		return hasCircle(edgesLeft, start, edgesLeft.size());
		*/
    false
  }

}

object GraphValidator {

  def validate(dpu: DataProcessingUnit): Boolean = true

  def validate(nodes: Array[Node], edges: Array[Edge]): Boolean = true
}