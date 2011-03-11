package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class ProcessorNodeAdapter extends XmlAdapter<ProcessorNode[], Map<String, ProcessorNode>> {

	@Override
	public Map<String, ProcessorNode> unmarshal(ProcessorNode[] v) throws Exception {
		Map<String, ProcessorNode> returns = new HashMap<String, ProcessorNode>();
		if(v == null || v.length == 0)
			return returns;
		for (ProcessorNode node : v) 
			returns.put(node.getNodeId(), node);
		
		return returns;
	}

	@Override
	public ProcessorNode[] marshal(Map<String, ProcessorNode> v) throws Exception {
		if(v == null || v.isEmpty())
			return new ProcessorNode[0];
		
		return v.values().toArray(new ProcessorNode[v.values().size()]);
	}

}
