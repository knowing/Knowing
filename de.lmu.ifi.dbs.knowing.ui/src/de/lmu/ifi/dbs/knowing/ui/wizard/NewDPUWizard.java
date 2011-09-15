package de.lmu.ifi.dbs.knowing.ui.wizard;

import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.DPU_SDEF;
import static de.lmu.ifi.dbs.knowing.ui.interal.Activator.PLUGIN_ID;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IEdge;
import de.lmu.ifi.dbs.knowing.core.model.INode;

public class NewDPUWizard extends SapphireCreateWizard<IDataProcessingUnit> {

	public NewDPUWizard() {
		super((IDataProcessingUnit) IDataProcessingUnit.TYPE.instantiate(),PLUGIN_ID + DPU_SDEF + "!dpu.wizard" );
	}
	
	@Override
	protected void copyContents(IDataProcessingUnit source, IDataProcessingUnit destination) {
		destination.setName(source.getName().getContent());
		destination.setDescription(source.getDescription().getContent());
		destination.setTags(source.getTags().getContent());
		for(INode node : source.getNodes()) {
			INode nodeNew = destination.getNodes().addNewElement();
			nodeNew.setId(node.getId().getContent());
			nodeNew.setFactoryId(node.getFactoryId().getContent());
			nodeNew.setType(node.getType().getContent());
		}
		for(IEdge edge : source.getEdges()) {
			IEdge edgeNew = destination.getEdges().addNewElement();
			edgeNew.setId(edge.getId().getContent());
			edgeNew.setSource(edge.getSource().getContent());
			edgeNew.setTarget(edge.getTarget().getContent());
		}
	}

}
