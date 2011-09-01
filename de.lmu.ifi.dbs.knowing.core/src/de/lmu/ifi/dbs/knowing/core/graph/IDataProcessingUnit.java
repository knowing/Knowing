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
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

@GenerateImpl
@XmlRootBinding( elementName = "DataProcessingUnit" )
public interface IDataProcessingUnit extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IDataProcessingUnit.class);

	/* === Name === */

	@XmlBinding(path = "name")
	@Label(standard = "Name")
	@Required
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "name");

	Value<String> getName();

	void setName(String value);
	
	/* === Nodes === */

    @Type( base = INode.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "node", type = INode.class ) } )
    @Label( standard = "Nodes" )

    ListProperty PROP_NODES = new ListProperty( TYPE, "nodes" );

    ModelElementList<INode> getNodes();
    
	/* === Edges === */

    @Type( base = IEdge.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "edge", type = INode.class ) } )
    @Label( standard = "Edges" )

    ListProperty PROP_EDGES = new ListProperty( TYPE, "edges" );

    ModelElementList<IEdge> getEdges();

    
    /* == == */
//    @DelegateImplementation( IDataProcessingUnitOp.class )
//    Status execute( ProgressMonitor monitor );

}
