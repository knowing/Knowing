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

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.model.internal.InstancesSerializationService;

/**
 * <p>
 * String representation of a message send between two {@link INode} connected
 * by an {@link IEdge}.
 * </p>
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 */
@GenerateImpl
public interface IMessage extends IModelElement {

	ModelElementType	TYPE		= new ModelElementType(IMessage.class);

	/* === Sender === */

	@XmlBinding(path = "@source")
	@Label(standard = "Source")
	ValueProperty		PROP_SOURCE	= new ValueProperty(TYPE, "Source");

	Value<String> getSource();

	void setSource(String value);

	/* === Sender Port === */

	@XmlBinding(path = "@source-port")
	@Label(standard = "Port")
	ValueProperty	PROP_SOURCE_PORT	= new ValueProperty(TYPE, "Source-Port");

	Value<String> getSourcePort();

	void setSourcePort(String value);

	/* === Target === */

	@XmlBinding(path = "@target")
	@Label(standard = "Target")
	ValueProperty	PROP_TARGET	= new ValueProperty(TYPE, "Target");

	Value<String> getTarget();

	void setTarget(String value);

	/* === Target Port === */

	@XmlBinding(path = "@target-port")
	@Label(standard = "Port")
	ValueProperty	PROP_TARGET_PORT	= new ValueProperty(TYPE, "Target-Port");

	Value<String> getTargetPort();

	void setTargetPort(String value);

	/* === Type === */

	@XmlBinding(path = "@type")
	@Label(standard = "Type")
	@Type(base = EventType.class)
	ValueProperty	PROP_TYPE	= new ValueProperty(TYPE, "Type");

	Value<EventType> getType();

	void setType(String value);

	void setType(EventType value);

	/* === Content === */

	@XmlBinding(path = "content")
	@Label(standard = "Content")
	@Service(impl = InstancesSerializationService.class)
	@Type(base = Instances.class)
	@LongString
	ValueProperty	PROP_CONTENT	= new ValueProperty(TYPE, "Content");

	Value<Instances> getContent();

	void setContent(String value);

	void setContent(Instances value);

	// TODO Implement DelimitedListBindingImpl -> see
	// http://www.eclipse.org/forums/index.php/t/244613/

	/* === ContentList === */

	// @Label(standard = "instances")
	// @Type(base = Instance.class)
	// @CustomXmlListBinding(impl = InstancesListController.class)
	// ListProperty PROP_INSTANCES = new ListProperty(TYPE, "Instances");
	//
	// ModelElementList<Instance> getInstances();
}
