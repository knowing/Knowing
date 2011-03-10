package de.lmu.ifi.dbs.knowing.core.graph;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

/**
 * <p>
 * This class manages the evaluation process in a Processing Graph.<br>
 * It listens to every existing {@link INode} and reacts if errors are detected.
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class GraphSupervisor implements INodeListener {

	/* == Describing the Graph == */
	private final Map<String, INode> nodes = new HashMap<String, INode>();
	private final List<Edge> edges = new ArrayList<Edge>();

	/* == Managing threads == */
	private final ExecutorService executor;

	/* == Local history of events == */
	// TODO GraphSupervisor: Option to turn off history for memory reasons
	private final BlockingQueue<NodeEvent> history = new LinkedBlockingQueue<NodeEvent>();

	public GraphSupervisor() {
		executor = Executors.newCachedThreadPool();
	}

	public void connectNodes() {
		for (Edge edge : edges) {
			String sourceId = edge.getSourceId();
			String targetId = edge.getTargetId();

			// get nodes from node map
			INode target = nodes.get(targetId);
			INode source = nodes.get(sourceId);

			// Set references
			edge.setSource(source);
			edge.setTarget(target);

			// Set listeners
			source.addNodeListener(target);
			// Try it for both,so nothing is left out
			source.setSupervisor(this);
			target.setSupervisor(this);

		}
	}

	/**
	 * <p>
	 * Starting every node. Normally this means, the {@link InputNode}s trying<br>
	 * to get their data and sending {@link NodeEvent}s to their listeners.<br>
	 * However {@link IProcessor}s can doing some initializing stuff too in this
	 * time.</p>
	 * 
	 * 
	 * @throws Exception 
	 */
	public void evaluate() throws Exception {
		for (INode node : nodes.values())
			node.run();

	}

	public void execute(Runnable command) {
		executor.execute(command);
	}

	@Override
	public void nodeChanged(NodeEvent event) {
		history.add(event);
	}

	public INode getNode(Object key) {
		return nodes.get(key);
	}

	public INode putNode(INode value) {
		return nodes.put(value.getNodeId(), value);
	}

	public INode removeNode(String key) {
		return nodes.remove(key);
	}

	public void clearNodes() {
		nodes.clear();
	}

	public boolean addEdge(Edge e) {
		return edges.add(e);
	}

	public boolean removeEdge(Edge edge) {
		return edges.remove(edge);
	}

	public void clearEdges() {
		edges.clear();
	}

	/**
	 * Prints history to the out PrintStream. * @param out
	 */
	public void printHistory(PrintStream out) {
		for (NodeEvent event : history) {
			out.println(event);
		}
	}

}
