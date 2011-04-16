package de.lmu.ifi.dbs.knowing.core.graph;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.graph.xml.PersistentNode;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

/**
 * <p>
 * This class manages the evaluation process in a Processing Graph.<br>
 * It listens to every existing {@link INode} and reacts if errors are detected.
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 0.3
 */
public class GraphSupervisorOld {

	/* == Describing the Graph == */
	private final Map<String, Node> nodes = new HashMap<String, Node>();
	private final List<Edge> edges = new ArrayList<Edge>();

	/* == Managing threads == */
	private final ExecutorService executor;

	/* == Local history of events == */
	// TODO GraphSupervisor: Option to turn off history for memory reasons
//	private final BlockingQueue<NodeEvent> history = new LinkedBlockingQueue<NodeEvent>();
	
	/* == Logger == */
//	private static final Logger log = Logger.getLogger(GraphSupervisor.class);

	public GraphSupervisorOld() {
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * Clones nodes and edges and adds them to his internal lists.
	 * 
	 * @param dpu
	 */
	public GraphSupervisorOld(DataProcessingUnit dpu) {
		this();
//		for (Node node : dpu.getNodes())
//			nodes.put(node.getNodeId(), node.clone());
//		for (Edge edge : dpu.getEdges())
//			edges.add(edge.clone());

	}

	public void connectNodes() {
//		log.info("Connecting Nodes");
		for (Edge edge : edges) {
			String sourceId = edge.getSourceId();
			String targetId = edge.getTargetId();

			// get nodes from node map
			Node target = nodes.get(targetId);
			Node source = nodes.get(sourceId);

			// Set references
			edge.setSource(source);
			edge.setTarget(target);

			// Set listeners
//			source.addNodeListener(target);
			// Try it for both,so nothing is left out
//			source.setSupervisor(this);
//			target.setSupervisor(this);
//			log.debug(target + " listens to " + source);
		}
	}

	/**
	 * <p>
	 * Starting every node. Normally this means, the {@link InputNode}s trying<br>
	 * to get their data and sending {@link NodeEvent}s to their listeners.<br>
	 * However {@link IProcessor}s can doing some initializing stuff too in this
	 * time.
	 * </p>
	 * 
	 * 
	 * @throws Exception
	 */
	public void evaluate() throws Exception {
//		log.info("Start evaluation");
//		for (INode node : nodes.values())
//			node.run();

	}

	public void persistAll(OutputStream out) {

	}

	public void persistNode(String nodeId, OutputStream out) {
//		INode node = nodes.get(nodeId);
//		if (node instanceof PersistentNode) {
//			PersistentNode pNode = (PersistentNode) node;
//			((ProcessorNode) pNode.getNode()).getProcessor().persistModel(out);
//		} else if (node instanceof ProcessorNode) {
//			((ProcessorNode) nodes.get(nodeId)).getProcessor().persistModel(out);
//		}

	}

	public void execute(Runnable command) {
		executor.execute(command);
	}
	
	/**
	 * Prints history to the out PrintStream. * @param out
	 */
	public void printHistory(PrintStream out) {
//		for (NodeEvent event : history) {
//			out.println(event);
//		}
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

}
