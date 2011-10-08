package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.NoDuplicates;
import org.eclipse.sapphire.modeling.annotations.PossibleValues;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

@GenerateImpl
public interface INodeConstraint extends IModelElement {

	ModelElementType TYPE = new ModelElementType(INodeConstraint.class);
	
	/* === Node === */

	@XmlBinding(path = "@node")
	@Label(standard = "node")
	@Required
	@NoDuplicates
	@PossibleValues(property="/nodes/id")
	ValueProperty PROP_NODE = new ValueProperty(TYPE, "node");

	Value<String> getNode();

	void setNode(String value);
	
	/* === Log === */

	@XmlBinding(path = "@log")
	@Label(standard = "Log")
	@Type(base = Boolean.class)
	@DefaultValue(text = "true")
	ValueProperty PROP_LOG = new ValueProperty(TYPE, "Log");

	Value<Boolean> getLog();

	void setLog(String value);
	void setLog(Boolean value);
	
}
