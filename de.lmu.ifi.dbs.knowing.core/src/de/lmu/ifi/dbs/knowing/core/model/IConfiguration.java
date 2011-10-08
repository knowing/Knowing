package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.annotations.ValidFileExtensions;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;
import org.eclipse.sapphire.workspace.WorkspaceRelativePath;

@GenerateImpl
public interface IConfiguration extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IConfiguration.class);

	/* === Process History === */

	@XmlBinding(path = "history")
	@Label(standard = "History")
	@Type(base = Boolean.class)
	@DefaultValue(text = "false")
	ValueProperty PROP_HISTORY = new ValueProperty(TYPE, "History");

	Value<Boolean> getHistory();

	void setHistory(String value);

	void setHistory(Boolean value);

	/* === Output File === */

	@XmlBinding(path = "output")
	@Label(standard = "Output")
	@Type(base = Path.class)
	@WorkspaceRelativePath
	@ValidFileSystemResourceType(FileSystemResourceType.FILE)
	@ValidFileExtensions("hst")
	ValueProperty PROP_OUTPUT = new ValueProperty(TYPE, "Output");

	Value<Path> getOutput();

	void setOutput(String value);
	void setOutput(Path output);

	/* === Output File === */

	@XmlBinding(path = "output/absolute")
	@Label(standard = "Absolute")
	@Type(base = Boolean.class)
	@DefaultValue(text = "true")
	ValueProperty PROP_ABSOLUTE = new ValueProperty(TYPE, "Absolute");

	Value<Boolean> getAbsolute();

	void setAbsolute(String value);

	void setAbsolute(Boolean value); 

	/* === Event Constraints === */

	@Type(base = IEventConstraint.class)
	@XmlListBinding(path = "event-constraints", mappings = { @XmlListBinding.Mapping(element = "constraint", type = IEventConstraint.class) })
	@Label(standard = "Event constraints")
	ListProperty PROP_EVENT_CONSTRAINTS = new ListProperty(TYPE, "eventConstraints");

	ModelElementList<IEventConstraint> getEventConstraints();
	
	/* === Node Constraints === */

	@Type(base = INodeConstraint.class)
	@XmlListBinding(path = "node-constraints", mappings = { @XmlListBinding.Mapping(element = "constraint", type = INodeConstraint.class) })
	@Label(standard = "Node constraints")
	ListProperty PROP_NODE_CONSTRAINTS = new ListProperty(TYPE, "nodeConstraints");

	ModelElementList<INodeConstraint> getNodeConstraints();

}
