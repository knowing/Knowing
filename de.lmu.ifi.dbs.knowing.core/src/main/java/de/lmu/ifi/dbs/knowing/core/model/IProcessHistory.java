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

/**
 * <p>Process history is a log file which contains all the<br>
 * data send between all nodes inside a dpu during the<br>
 * data mining process. The process history can be configured<br>
 * via constraints:
 * <li> {@link INodeConstraint}</li>
 * <li> {@link IEventConstraint}</li>
 * </p>
 * @author Nepomuk Seiler
 * @version 0.2
 *
 */
@GenerateImpl
@XmlBinding(path = "ProcessHistory")
public interface IProcessHistory extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IProcessHistory.class);

	/* === Name === */

	@XmlBinding(path = "@name")
	@Label(standard = "Name")
	@Required
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();
	void setName(String value);
	
	/* === Messages === */

	@Type(base = IMessage.class)
	@XmlListBinding(path = "messages", mappings = { @XmlListBinding.Mapping(element = "message", type = IMessage.class) })
	@Label(standard = "Messages")
	ListProperty PROP_MESSAGES = new ListProperty(TYPE, "Messages");

	ModelElementList<IMessage> getMessages();
}
