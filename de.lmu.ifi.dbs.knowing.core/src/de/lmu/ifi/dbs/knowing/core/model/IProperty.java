package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

@GenerateImpl
public interface IProperty extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IProperty.class);

	/* === Key === */

	@XmlBinding(path = "key")
	@Label(standard = "key")
	@Required
	ValueProperty PROP_KEY = new ValueProperty(TYPE, "key");

	Value<String> getKey();

	void setKey(String value);
	
	/* === Value === */

	@XmlBinding(path = "value")
	@Label(standard = "value")
	@Required
	ValueProperty PROP_VALUE = new ValueProperty(TYPE, "value");

	Value<String> getValue();

	void setValue(String value);
	
}
