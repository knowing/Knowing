package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import de.lmu.ifi.dbs.knowing.core.model.internal.NodeReferenceService;

@GenerateImpl
public interface IEdge extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IEdge.class);
	
	/* === Edge ID === */

	@Reference( target = INode.class )
	@Service( impl = NodeReferenceService.class )
	@XmlBinding(path = "@edgeId")
	@Label(standard = "EdgeId")
	@Required
	ValueProperty PROP_EDGE_ID = new ValueProperty(TYPE, "edgeId");

	Value<String> getEdgeId();

	void setEdgeId(String value);
	

	/* === Source ID === */

	@XmlBinding(path = "@sourceId")
	@Label(standard = "SourceId")
	@Required
    @PossibleValues( property = "/nodes/id" )
	ValueProperty PROP_SOURCE_ID = new ValueProperty(TYPE, "sourceId");

	Value<String> getSourceId();

	void setSourceID(String value);
	
	
	/* === Target ID === */

	@XmlBinding(path = "@targetId")
	@Label(standard = "TargetId")
	@Required
	@PossibleValues( property = "/nodes/id" )
	ValueProperty PROP_TARGET_ID = new ValueProperty(TYPE, "targetId");

	Value<String> getTargetId();

	void setTargetID(String value);
	
}
