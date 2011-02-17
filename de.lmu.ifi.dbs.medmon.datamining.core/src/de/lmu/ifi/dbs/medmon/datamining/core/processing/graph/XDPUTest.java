package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

public class XDPUTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<XMLDataProcessor> processors = sampleProcessors();
		List<ProcessorNode> nodes = sampleNodes(processors);
		List<Edge> edges = sampleEdges(nodes);
		XDataProcessingUnit xdpu = new XDataProcessingUnit();
		xdpu.setName("Test DPU");
		xdpu.addNodes(nodes);
		xdpu.setEdges(edges);
		xdpu.setTags("Tag1, Tag2, Tag3");
		xdpu.addTag("Tag4");
		xdpu.setDescription("My description");
		try {
			JAXBContext context = JAXBContext.newInstance(XDataProcessingUnit.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(xdpu, System.out);
			xdpu.buildGraph();
			System.out.println(xdpu);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}
	
	private static List<XMLDataProcessor> sampleProcessors() {
		List<XMLDataProcessor> processors = new ArrayList<XMLDataProcessor>();
		for (int i = 0; i < 5; i++) {
			processors.add(new XMLDataProcessor("Processor"+i, i+"PID", "Provider"+i, null));
		}
		
		return processors;
	}
	
	private static List<ProcessorNode> sampleNodes(List<XMLDataProcessor> processors) {
		List<ProcessorNode> nodes = new ArrayList<ProcessorNode>();
		for (XMLDataProcessor p : processors) {
			nodes.add(new ProcessorNode(p, 0));
		}
		return nodes;
	}
	
	private static List<Edge> sampleEdges(List<ProcessorNode> nodes) {
		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 1; i < nodes.size(); i++) {
			edges.add(new Edge(nodes.get(i-1), nodes.get(i)));
		}
		for (int i = 1; i < nodes.size()-1; i++) {
			edges.add(new Edge(nodes.get(nodes.size()-1), nodes.get(i)));
		}
		return edges;
	}

}
