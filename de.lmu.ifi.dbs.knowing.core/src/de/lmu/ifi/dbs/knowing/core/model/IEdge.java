package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
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
	
	/**	Used when no port is given */
	String DEFAULT_PORT = "default";

	ModelElementType TYPE = new ModelElementType(IEdge.class);

	/* === Edge ID === */

	@XmlBinding(path = "@id")
	@Label(standard = "Id")
	@Required
	ValueProperty PROP_ID = new ValueProperty(TYPE, "id");

	Value<String> getId();
	void setId(String value);
	
	/* === Weight === */
	
	@XmlBinding(path = "@weight")
	@Label(standard = "Weight")
	@DefaultValue(text = "1")
	ValueProperty PROP_WEIGHT = new ValueProperty(TYPE, "weight");

	Value<String> getWeight();
	void setWeight(String value);

	/* === Source ID === */

	@Reference(target = INode.class)
	@Service(impl = NodeReferenceService.class)
	@XmlBinding(path = "@source")
	@Label(standard = "Source")
	@Required
	@PossibleValues(property = "/nodes/id")
	ValueProperty PROP_SOURCE = new ValueProperty(TYPE, "source");

	ReferenceValue<String, INode> getSource();
	void setSource(String value);
	
	/* === Source Port === */

	@XmlBinding(path = "@sourcePort")
	@Label(standard = "Port")
	@DefaultValue(text = DEFAULT_PORT)
	ValueProperty PROP_SOURCE_PORT = new ValueProperty(TYPE, "sourcePort");

	Value<String> getSourcePort();
	void setSourcePort(String value);

	/* === Target ID === */

	@Reference(target = INode.class)
	@Service(impl = NodeReferenceService.class)
	@XmlBinding(path = "@target")
	@Label(standard = "Target")
	@Required
	@PossibleValues(property = "/nodes/id")
	ValueProperty PROP_TARGET = new ValueProperty(TYPE, "target");

	ReferenceValue<String, INode> getTarget();
	void setTarget(String value);
	
	/* === Target Port === */

	@XmlBinding(path = "@targetPort")
	@Label(standard = "Port")
	@DefaultValue(text = DEFAULT_PORT)
	ValueProperty PROP_TARGET_PORT = new ValueProperty(TYPE, "targetPort");

	Value<String> getTargetPort();
	void setTargetPort(String value);

	
}
