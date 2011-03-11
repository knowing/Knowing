package de.lmu.ifi.dbs.medmon.developer.ui.graph;

import java.util.Collection;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.ProcessorNode;

public class Nodes {

	private final Collection<ProcessorNode> nodes;

	public Nodes(Collection<ProcessorNode> collection) {
		this.nodes = collection;
	}
	
	public ProcessorNode[] toArray() {
		return nodes.toArray(new ProcessorNode[nodes.size()]);
	}
	
	@Override
	public String toString() {
		return "Nodes";
	}
	
}
