package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlRootBinding;

import de.lmu.ifi.dbs.knowing.core.model.internal.IDataProcessingUnitOp;

@GenerateImpl
@XmlRootBinding( elementName = "DataProcessingUnit" )
public interface IDataProcessingUnit extends IExecutableModelElement {

	ModelElementType TYPE = new ModelElementType(IDataProcessingUnit.class);

	/* === Name === */

	@XmlBinding(path = "name")
	@Label(standard = "Name")
	@Required
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "name");

	Value<String> getName();

	void setName(String value);
	
	/* === Description === */

	@XmlBinding(path = "description")
	@Label(standard = "Description")
	@Required
	ValueProperty PROP_DESCRIPTION = new ValueProperty(TYPE, "description");

	Value<String> getDescription();

	void setDescription(String value);
	
	/* ===Tags === */

	@XmlBinding(path = "tags")
	@Label(standard = "Tags")
	@Required
	ValueProperty PROP_TAGS = new ValueProperty(TYPE, "tags");

	Value<String> getTags();

	void setTags(String value);
	
	/* === Nodes === */

    @Type( base = INode.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "node", type = INode.class ) } )
    @Label( standard = "Nodes" )

    ListProperty PROP_NODES = new ListProperty( TYPE, "nodes" );

    ModelElementList<INode> getNodes();
    
	/* === Edges === */

    @Type( base = IEdge.class )
    @XmlListBinding( mappings = { @XmlListBinding.Mapping( element = "edge", type = IEdge.class ) } )
    @Label( standard = "Edges" )

    ListProperty PROP_EDGES = new ListProperty( TYPE, "edges" );

    ModelElementList<IEdge> getEdges();

    
    /* == == */
    @DelegateImplementation( IDataProcessingUnitOp.class )
    Status execute( ProgressMonitor monitor );

}
