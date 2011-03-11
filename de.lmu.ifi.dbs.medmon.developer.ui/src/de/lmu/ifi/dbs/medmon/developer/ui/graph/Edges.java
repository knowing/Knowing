package de.lmu.ifi.dbs.medmon.developer.ui.graph;

import java.util.List;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.Edge;

public class Edges {

	private final List<Edge> edges;

	public Edges(List<Edge> edges) {
		this.edges = edges;
	}
	
	public Edge[] toArray() {
		return edges.toArray(new Edge[edges.size()]);
	}
	
	@Override
	public String toString() {
		return "Edges";
	}
	
}
