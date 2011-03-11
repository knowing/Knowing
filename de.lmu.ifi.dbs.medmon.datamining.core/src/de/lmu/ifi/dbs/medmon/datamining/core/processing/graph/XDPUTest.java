package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
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
		try {
			test1();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private static void test1() throws JAXBException {
		XDataProcessingUnit xdpu = sampleXDPU();
		List<XMLDataProcessor> processors = sampleProcessors();
		List<ProcessorNode> nodes = sampleNodes(processors);
		
		for (ProcessorNode n : nodes) 
			xdpu.addNode(n);
		
		List<InputNode> inputNodes = sampleInputNodes();
		for (InputNode n : inputNodes) 
			xdpu.addNode(n);
		
		List<Edge> edges = sampleEdges(nodes);
		xdpu.setEdges(edges);

		JAXBContext context = JAXBContext.newInstance(XDataProcessingUnit.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(xdpu, System.out);
		xdpu.buildGraph();
		System.out.println(xdpu);
	}

	private static void test2() throws JAXBException {
		XDataProcessingUnit xdpu = sampleXDPU();
		List<InputNode> nodes = sampleInputNodes();
		JAXBContext context = JAXBContext.newInstance(InputNode.class);
		Marshaller m = context.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.marshal(nodes.get(0), System.out);
		
		URL url = nodes.get(0).getUrl();
		try {
			InputStream in = url.openStream();
			System.out.println("Class: " + in.getClass());
			File file = new File(url.toURI());
			System.out.println("File: " + file);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private static XDataProcessingUnit sampleXDPU() {
		XDataProcessingUnit xdpu = new XDataProcessingUnit();
		xdpu.setName("Test DPU");
		xdpu.setTags("Tag1, Tag2, Tag3");
		xdpu.addTag("Tag4");
		xdpu.setDescription("My description");
		return xdpu;
	}

	private static List<XMLDataProcessor> sampleProcessors() {
		List<XMLDataProcessor> processors = new ArrayList<XMLDataProcessor>();
		for (int i = 0; i < 5; i++) {
			processors.add(new XMLDataProcessor("Processor" + i,
					"de.lmu.ifi.dbs.medmon.datamining.core.filter.BypassFilter.Bypass Filter.", "Provider" + i, null));
		}

		return processors;
	}

	private static List<ProcessorNode> sampleNodes(List<XMLDataProcessor> processors) {
		List<ProcessorNode> nodes = new ArrayList<ProcessorNode>();
		for (XMLDataProcessor p : processors) {
			nodes.add(new ProcessorNode(p));
		}
		return nodes;
	}

	private static List<Edge> sampleEdges(List<ProcessorNode> nodes) {
		List<Edge> edges = new ArrayList<Edge>();
		for (int i = 1; i < nodes.size(); i++) {
			edges.add(new Edge(nodes.get(i - 1), nodes.get(i)));
		}
		for (int i = 1; i < nodes.size() - 1; i++) {
			edges.add(new Edge(nodes.get(nodes.size() - 1), nodes.get(i)));
		}
		return edges;
	}

	private static List<InputNode> sampleInputNodes() {
		List<InputNode> nodes = new ArrayList<InputNode>();

		try {
			InputNode n1 = new InputNode("Input1", new URL("file:/home/muki/input.sdr"));
			InputNode n2 = new InputNode("Input1", new URL("file", "", "/home/muki/input.sdr"));
			InputNode n3 = new InputNode("Input1", new URL("file", "", "/home/muki/input.sdr"));
			nodes.add(n1);
			nodes.add(n2);
			nodes.add(n3);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}

		return nodes;
	}

}
