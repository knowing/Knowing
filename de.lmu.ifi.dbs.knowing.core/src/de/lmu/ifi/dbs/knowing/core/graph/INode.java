package de.lmu.ifi.dbs.knowing.core.graph;

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

	@XmlBinding(path = "nodeId")
	@Label(standard = "NodeId")
	@Required
	ValueProperty PROP_NODE_ID = new ValueProperty(TYPE, "nodeId");

	Value<String> getNodeId();

	void setNodeId(String value);
		
	/* === Properties === */

    @Type( base = IProperty.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "property", type = IProperty.class ) } )
    @Label( standard = "Properties" )

    ListProperty PROP_PROPERTIES = new ListProperty( TYPE, "properties" );

    ModelElementList<IProperty> getProperties();
	
}
