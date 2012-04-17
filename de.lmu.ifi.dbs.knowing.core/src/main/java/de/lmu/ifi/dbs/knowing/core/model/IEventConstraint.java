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
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * <p>Process History constraint. Which event types should be logged.</p>
 * @author Nepomuk Seiler
 * @version 0.1
 *
 */
@GenerateImpl
public interface IEventConstraint extends IModelElement{

	ModelElementType TYPE = new ModelElementType(IEventConstraint.class);
	
	/* === Type === */

	@Type(base = EventType.class)
	@XmlBinding(path = "@type")
	@Label(standard = "type")
	@Required
	ValueProperty PROP_TYPE = new ValueProperty(TYPE, "type");

	Value<EventType> getType();

	void setType(String value);
	void setType(EventType value);
	
	/* === Log === */

	@XmlBinding(path = "@log")
	@Label(standard = "Log")
	@Type(base = Boolean.class)
	@DefaultValue(text = "true")
	ValueProperty PROP_LOG = new ValueProperty(TYPE, "Log");

	Value<Boolean> getLog();

	void setLog(String value);
	void setLog(Boolean value);
	
}
