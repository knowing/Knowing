package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.ReferenceService;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.INode;

public class NodeReferenceService extends ReferenceService {

	@Override
	public Object resolve(String reference) {
		if (reference == null)
			return null;

		final IDataProcessingUnit dpu = element().nearest(IDataProcessingUnit.class);
		for (INode node : dpu.getNodes()) {
			if (reference.equals(node.getId().getText()))
				return node;
		}
		//non found
		return null;

	}

}
