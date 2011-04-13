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
public class GraphSupervisor implements INodeListener {

	/* == Describing the Graph == */
	private final Map<String, INode> nodes = new HashMap<String, INode>();
	private final List<Edge> edges = new ArrayList<Edge>();

	/* == Managing threads == */
	private final ExecutorService executor;

	/* == Local history of events == */
	// TODO GraphSupervisor: Option to turn off history for memory reasons
	private final BlockingQueue<NodeEvent> history = new LinkedBlockingQueue<NodeEvent>();
	
	/* == Logger == */
//	private static final Logger log = Logger.getLogger(GraphSupervisor.class);

	public GraphSupervisor() {
		executor = Executors.newCachedThreadPool();
	}

	/**
	 * Clones nodes and edges and adds them to his internal lists.
	 * 
	 * @param dpu
	 */
	public GraphSupervisor(DataProcessingUnit dpu) {
		this();
		for (INode node : dpu.getNodes())
			nodes.put(node.getNodeId(), node.clone());
		for (Edge edge : dpu.getEdges())
			edges.add(edge.clone());

	}

	public void connectNodes() {
//		log.info("Connecting Nodes");
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
		for (INode node : nodes.values())
			node.run();

	}

	public void persistAll(OutputStream out) {

	}

	public void persistNode(String nodeId, OutputStream out) {
		INode node = nodes.get(nodeId);
		if (node instanceof PersistentNode) {
			PersistentNode pNode = (PersistentNode) node;
			((ProcessorNode) pNode.getNode()).getProcessor().persistModel(out);
		} else if (node instanceof ProcessorNode) {
			((ProcessorNode) nodes.get(nodeId)).getProcessor().persistModel(out);
		}

	}

	public void execute(Runnable command) {
		executor.execute(command);
	}
	
	/**
	 * Prints history to the out PrintStream. * @param out
	 */
	public void printHistory(PrintStream out) {
		for (NodeEvent event : history) {
			out.println(event);
		}
	}

	@Override
	public void nodeChanged(NodeEvent event) {
		history.add(event);
	}
	
	public List<PresenterNode> getPresenterNodes() {
		ArrayList<PresenterNode> returns = new ArrayList<PresenterNode>();
		for (INode node : nodes.values()) {
			if(node instanceof PresenterNode)
				returns.add((PresenterNode) node);
			else if(node instanceof PersistentNode) {
				if(((PersistentNode) node).getNode() instanceof PresenterNode)
					returns.add((PresenterNode) ((PersistentNode) node).getNode());
			}
		}
		return returns;
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

}
