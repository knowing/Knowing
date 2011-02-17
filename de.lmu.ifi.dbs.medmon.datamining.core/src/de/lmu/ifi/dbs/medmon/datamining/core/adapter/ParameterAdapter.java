package de.lmu.ifi.dbs.medmon.datamining.core.adapter;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.ClusterParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.XMLParameterWrapper;

public class ParameterAdapter extends XmlAdapter<XMLParameterWrapper[], Map<String, IProcessorParameter>> {

	@Override
	public Map<String, IProcessorParameter> unmarshal(XMLParameterWrapper[] v) throws Exception {
		Map<String, IProcessorParameter> returns = new HashMap<String, IProcessorParameter>();
		if(v == null)
			return returns;
		for (XMLParameterWrapper wrapper : v) {
			String type = wrapper.getType();
			if(type.equals(IProcessorParameter.INT_TYPE)) {
				int value = Integer.valueOf(wrapper.getValue());
				int min = Integer.MIN_VALUE;
				if(wrapper.getMin() != null)
					min = Integer.valueOf(wrapper.getMin());
				int max = Integer.MAX_VALUE;
				if(wrapper.getMax() != null)
					max = Integer.valueOf(wrapper.getMax());
				returns.put(wrapper.getKey(), new NumericParameter(wrapper.getKey(), min, max, value ));
			} else if(type.equals(IProcessorParameter.BOOL_TYPE)) {
				
			} else if(type.equals(IProcessorParameter.STRING_TYPE)) {
				
			} else if(type.equals(IProcessorParameter.STATIC_TYPE)) {
				
			} else if(type.equals(IProcessorParameter.CLUSTER_TYPE)) {
				returns.put(wrapper.getKey(), new ClusterParameter(wrapper.getKey(), wrapper.getValue()));
			}
		}

		return returns;
	}

	@Override
	public XMLParameterWrapper[] marshal(Map<String, IProcessorParameter> v) throws Exception {
		if(v == null || v.isEmpty())
			return new XMLParameterWrapper[0];
		XMLParameterWrapper[] returns = new XMLParameterWrapper[v.keySet().size()];
		int index = 0;
		for (String key : v.keySet()) {
			IProcessorParameter parameter = v.get(key);
			if(parameter instanceof ClusterParameter) {
				ClusterParameter p = (ClusterParameter)parameter;
				XMLParameterWrapper wrapper = new XMLParameterWrapper();
				wrapper.setKey(parameter.getName());
				wrapper.setValue(p.getDescriptor());
				wrapper.setEmbedded(p.isEmbedded());
				wrapper.setType(IProcessorParameter.CLUSTER_TYPE);
				returns[index++] = wrapper;
			} else if(parameter instanceof NumericParameter) {
				NumericParameter p = (NumericParameter)parameter;
				XMLParameterWrapper wrapper = new XMLParameterWrapper();
				wrapper.setKey(parameter.getName());
				wrapper.setValue(String.valueOf(p.getValue()));
				wrapper.setType(IProcessorParameter.INT_TYPE);
				if(p.getMaximum() != Integer.MAX_VALUE)
					wrapper.setMax(String.valueOf(p.getMaximum()));
				if(p.getMinimum() != Integer.MIN_VALUE)
					wrapper.setMin(String.valueOf(p.getMinimum()));
				returns[index++] = wrapper;
			} else {
				returns[index++] = new XMLParameterWrapper(parameter);
			}
		}

		return returns;
	}

}
