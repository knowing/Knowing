package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.java.JavaType;
import org.eclipse.sapphire.java.JavaTypeConstraint;
import org.eclipse.sapphire.java.JavaTypeKind;
import org.eclipse.sapphire.java.JavaTypeName;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * <p>Represents a data mining step. Holds factory-id for the
 * factory-osgi-service and its configuration.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 *
 */
@GenerateImpl
public interface INode extends IModelElement {

	ModelElementType TYPE = new ModelElementType(INode.class);

	/* === Node ID === */

	@XmlBinding(path = "@id")
	@Label(standard = "id")
	@Required
	ValueProperty PROP_ID = new ValueProperty(TYPE, "id");

	Value<String> getId();

	void setId(String value);

	/* === Type === */

	@Type(base = NodeType.class)
	@XmlBinding(path = "@type")
	@Label(standard = "type")
	@Required
	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "type");

	Value<NodeType> getType();

	void setType(String value);

	void setType(NodeType value);

	/* === Factory ID === */

	@Type(base = JavaTypeName.class)
	@Reference(target = JavaType.class)
	@JavaTypeConstraint(kind = { JavaTypeKind.CLASS, JavaTypeKind.ABSTRACT_CLASS, JavaTypeKind.INTERFACE }, 
			type = {"de.lmu.ifi.dbs.knowing.core.processing.TProcessor", "de.lmu.ifi.dbs.knowing.core.japi.IProcessor"})
	@XmlBinding(path = "@factoryId")
	@Label(standard = "factoryId")
	@Required
	ValueProperty PROP_FACTORY_ID = new ValueProperty(TYPE, "factoryId");

	ReferenceValue<JavaTypeName, JavaType> getFactoryId();

	void setFactoryId(String value);

	void setFactoryId(JavaTypeName value);

	/* === Properties === */

	@Type(base = IProperty.class)
	@XmlListBinding(path = "properties", mappings = { @XmlListBinding.Mapping(element = "property", type = IProperty.class) })
	@Label(standard = "Properties")
	ListProperty PROP_PROPERTIES = new ListProperty(TYPE, "properties");

	ModelElementList<IProperty> getProperties();

}
