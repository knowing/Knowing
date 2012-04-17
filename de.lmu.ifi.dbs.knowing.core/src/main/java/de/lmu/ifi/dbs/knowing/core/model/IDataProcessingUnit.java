/*                                                               *\
 ** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|  **
 ** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---, **
 ** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|  **
 **                                                              **
 ** Knowing Framework                                            **
 ** Apache License - http://www.apache.org/licenses/             **
 ** LMU Munich - Database Systems Group                          **
 ** http://www.dbs.ifi.lmu.de/                                   **
\*                                                               */
package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IExecutableModelElement;
import org.eclipse.sapphire.modeling.ImpliedElementProperty;
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
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

import de.lmu.ifi.dbs.knowing.core.model.internal.IDataProcessingUnitOp;

/**
 * <p> A DataProcessingUnit (DPU) encapsulates a data mining process.<br>
 * It's a simple graph where each node represents a step in the process.<br>
 * The edges symbolize the data flow inside the data mining process.
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 1.0
 * @see<a href="https://github.com/knowing/Knowing/wiki/DataProcessingUnit">GitHub Wiki - DataProcessingUnit</a>
 * @see<a href="https://github.com/knowing/Knowing/wiki/Knowing-Framework">GitHub Wiki - Knowing Framework </a>
 */
@GenerateImpl
@XmlBinding(path = "DataProcessingUnit")
public interface IDataProcessingUnit extends IExecutableModelElement {

	ModelElementType TYPE = new ModelElementType(IDataProcessingUnit.class);

	/* === Name === */

	@XmlBinding(path = "@name")
	@Label(standard = "Name")
	@Required
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();

	void setName(String value);

	/* === Description === */

	@XmlBinding(path = "description")
	@Label(standard = "Description")
	@LongString
	ValueProperty PROP_DESCRIPTION = new ValueProperty(TYPE, "Description");

	Value<String> getDescription();

	void setDescription(String value);

	/* ===Tags === */

	@XmlBinding(path = "tags")
	@Label(standard = "Tags")
	ValueProperty PROP_TAGS = new ValueProperty(TYPE, "Tags");

	Value<String> getTags();

	void setTags(String value);

	/* === Nodes === */

	@Type(base = INode.class)
	@XmlListBinding(path = "nodes", mappings = { @XmlListBinding.Mapping(element = "node", type = INode.class) })
	@Label(standard = "Nodes")
	ListProperty PROP_NODES = new ListProperty(TYPE, "Nodes");

	ModelElementList<INode> getNodes();

	/* === Edges === */

	@Type(base = IEdge.class)
	@XmlListBinding(path = "edges", mappings = { @XmlListBinding.Mapping(element = "edge", type = IEdge.class) })
	@Label(standard = "Edges")
	ListProperty PROP_EDGES = new ListProperty(TYPE, "Edges");

	ModelElementList<IEdge> getEdges();

	/* === Configuration === */

	@XmlBinding(path = "configuration")
	@Label(standard = "Configuration")
	@Type(base = IConfiguration.class)
	ImpliedElementProperty PROP_CONFIGURATION = new ImpliedElementProperty(TYPE, "Configuration");

	IConfiguration getConfiguration();

	/* == == */
	@DelegateImplementation(IDataProcessingUnitOp.class)
	Status execute(ProgressMonitor monitor);
}
