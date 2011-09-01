package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

@GenerateImpl
public interface INode extends IModelElement {

	ModelElementType TYPE = new ModelElementType(INode.class);

	/* === Node ID === */

	@XmlBinding(path = "id")
	@Label(standard = "id")
	@Required
	ValueProperty PROP_ID = new ValueProperty(TYPE, "id");

	Value<String> getId();

	void setId(String value);
	
	/* === Factory ID === */

	@XmlBinding(path = "factoryId")
	@Label(standard = "factoryId")
	@Required
	ValueProperty PROP_FACTORY_ID = new ValueProperty(TYPE, "factoryId");

	Value<String> getFactoryId();

	void setFactoryId(String value);
	
	/* === Type === */

	@Type( base = NodeType.class )
	@XmlBinding(path = "type")
	@Label(standard = "type")
	@Required
	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "type");

	Value<NodeType> getType();

	void setType(String value);
	void setType(NodeType value);
	
    
	/* === Properties === */

    @Type( base = IProperty.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "property", type = IProperty.class ) } )
    @Label( standard = "Properties" )

    ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "properties" );

    ModelElementList<IProperty> getProperties();
	
}
