package de.lmu.ifi.dbs.medmon.datamining.core.processing.graph;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

import org.eclipse.core.runtime.ListenerList;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IDataProcessor;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IProcessListener;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.ProcessEvent;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.XMLDataProcessor;

@XmlAccessorType(XmlAccessType.FIELD)
public class ProcessorNode extends Node implements IProcessListener {

	private transient IDataProcessor iProcessor;

	private final transient ListenerList listeners = new ListenerList();

	@XmlElement
	private XMLDataProcessor processor;


	protected ProcessorNode() {
	}

	public ProcessorNode(XMLDataProcessor processor) {
		this.processor = processor;
		this.setNodeId(processor.getId());
	}

	
	@Override
	public String getName() {
		return processor.getName();
	}

	@Override
	public Object getData() {
		return processor;
	}

	public List<Edge> getEdgesIn() {
		return edgesIn;
	}

	public void setEdgesIn(List<Edge> edgesIn) {
		this.edgesIn = edgesIn;
	}

	public List<Edge> getEdgesOut() {
		return edgesOut;
	}

	public void setEdgesOut(List<Edge> edgesOut) {
		this.edgesOut = edgesOut;
	}

	public XMLDataProcessor getProcessor() {
		return processor;
	}

	public void setProcessor(XMLDataProcessor processor) {
		this.processor = processor;
	}


	/* ================ */
	/* == Processing == */
	/* ================ */

	public void process() {
		System.out.println("ProcessorNode.process(): " );
		// Map<String, IAnalyzedData> result = event.getResult();
		// IProcessor.setLoader(result);
		// if(iProcessor.isReady)
		iProcessor = processor.loadProcessor();
		if (!iProcessor.isReady())
			return;
		iProcessor.addProcessListener(this);
		
		//Others can prepare to get data
		fireProcessEvent(ProcessEvent.RUNNING);
		new Thread(iProcessor).start();
	}

	@Override
	public void processChanged(ProcessEvent event) {
		//System.out.println("ProcessorNode.processChanged() in " + id + " :" + event);
		if (event.getSource().equals(iProcessor)) {
			fireProcessEvent(ProcessEvent.FINISHED);
			iProcessor.removeProcessListener(this);
			iProcessor = null;
		} else if(event.getSource() instanceof ProcessorNode) {
			
			switch (event.getStatus()) {
			case ProcessEvent.FINISHED:
				System.out.println("Started by " + ((ProcessorNode)event.getSource()));
				process();
				break;
			case ProcessEvent.RUNNING:
				break;
			case ProcessEvent.WAITING:
				break;
			}
		}

	}
	
	
	public void addProcessListener(IProcessListener listener) {
		listeners.add(listener);
	}

	public void removeProcessListener(IProcessListener listener) {
		listeners.remove(listener);
	}

	protected void fireProcessEvent(int status) {
		ProcessEvent event = new ProcessEvent(this, status);
		for (Object l : listeners.getListeners()) {
			((IProcessListener) l).processChanged(event);
		}
	}

	@Override
	public String toString() {
		return getNodeId() + "@" + hashCode();
	}


}
