package de.lmu.ifi.dbs.knowing.core.graph;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 */
public class GraphValidator {

	private final List<Edge> edges;
	private final List<INode> nodes;
	
	public GraphValidator(List<Edge> edges, List<INode> nodes) {
		this.edges = edges;
		this.nodes = nodes;
	}
	
	public static boolean validate(List<Edge> edges, List<INode> nodes ) {
		GraphValidator validator = new GraphValidator(edges, nodes);
		return validator.validEdges() && !validator.hasCircle();
	}
	
	/**
	 * Test if all InputNodes are roots
	 * @return
	 */
	public boolean validEdges() {
		List<InputNode> inputNodes = new ArrayList<InputNode>();
		//Input Nodes
		for (INode n : nodes) 
			if(n instanceof InputNode)
				inputNodes.add((InputNode) n);
		
		for (Edge e : edges) {
			String targetId = e.getTargetId();
			for (InputNode n : inputNodes) {
				if(n.getNodeId().equals(targetId))
					return false;
			}
		}
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
	
}
