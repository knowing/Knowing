package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

@GenerateImpl
public interface IMessage extends IModelElement {
	
	ModelElementType TYPE = new ModelElementType(IMessage.class);
	
	/* === Sender === */

	@XmlBinding(path = "@sender")
	@Label(standard = "Sender")
	@DefaultValue(text = "none")
	ValueProperty PROP_SENDER = new ValueProperty(TYPE, "Sender");

	Value<String> getSender();
	void setSender(String value);
	
	/* === Receiver === */

	@XmlBinding(path = "@receiver")
	@Label(standard = "Receiver")
	@Required
	ValueProperty PROP_RECEIVER = new ValueProperty(TYPE, "Receiver");

	Value<String> getReceiver();
	void setReceiver(String value);
	
	/* === Type === */

	@XmlBinding(path = "@type")
	@Label(standard = "Type")
	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "Type");

	Value<String> getType();
	void setType(String value);
}
