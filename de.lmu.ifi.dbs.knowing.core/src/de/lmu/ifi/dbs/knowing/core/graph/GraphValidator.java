package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class GraphValidator {

	private final List<Edge> edges;
	private final List<Node> nodes;
	
	private final Map<String, String> errors = new HashMap<String, String>();
	
	public GraphValidator(List<Edge> edges, List<Node> nodes) {
		this.edges = edges;
		this.nodes = nodes;
	}
	
	public GraphValidator(DataProcessingUnit dpu) {
		this.nodes = null; // dpu.getNodes();
		this.edges = dpu.getEdges();
	}
	
	public boolean validate() {
		//TODO GraphValidator -> validate()
		System.err.println("validate - NOT IMPLEMENTED YET");
		return validate(edges, nodes);
	}
	
	public Map<String, String> getErrors() {
		//TODO GraphValidator -> errors
		System.err.println("errors - NOT IMPLEMENTED YET");
		return errors;
	}
	/**
	 * Test if all InputNodes are roots
	 * @return
	 */
	public boolean validEdges() {
//		List<InputNode> inputNodes = new ArrayList<InputNode>();
//		//Input Nodes
//		for (INode n : nodes) 
//			if(n instanceof InputNode)
//				inputNodes.add((InputNode) n);
//		
//		for (Edge e : edges) {
//			String targetId = e.getTargetId();
//			for (InputNode n : inputNodes) {
//				if(n.getNodeId().equals(targetId))
//					return false;
//			}
//		}
		return true;
	}

	/**
	 * Test for circles in the processing graph
	 * @return
	 */
	public boolean hasCircle() {
		return hasCircle(new LinkedList<Edge>(edges), null, 0);
	}
	
	private boolean hasCircle(LinkedList<Edge> edgesLeft, Edge start, int size) {
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
	}
	
	public static boolean validate(List<Edge> edges, List<Node> nodes ) {
		GraphValidator validator = new GraphValidator(edges, nodes);
		return validator.validEdges() && !validator.hasCircle();
	}
	
	public static boolean validate(DataProcessingUnit dpu) {
		//TODO GraphValidator -> validate(DPU)
		System.err.println("validate DPU - NOT IMPLEMENTED YET");
		return true;
	}
	
}
