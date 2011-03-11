package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class InputNodeAdapter extends XmlAdapter<InputNode[], Map<String, InputNode>> {

	@Override
	public Map<String, InputNode> unmarshal(InputNode[] v) throws Exception {
		Map<String, InputNode> returns = new HashMap<String, InputNode>();
		if(v == null || v.length == 0)
			return returns;
		
		for (InputNode node : v) 
			returns.put(node.getNodeId(), node);
		
		return returns;
	}

	@Override
	public InputNode[] marshal(Map<String, InputNode> v) throws Exception {
		if(v == null || v.isEmpty())
			return new InputNode[0];
		
		return v.values().toArray(new InputNode[v.values().size()]);
	}

}
