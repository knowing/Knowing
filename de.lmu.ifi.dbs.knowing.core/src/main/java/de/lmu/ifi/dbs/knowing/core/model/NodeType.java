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

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * <p>NodeTypes represent the function a {@link INode} has in the
 * data mining process.</p>
 * @author Nepomuk Seiler
 * @version 0.1
 *
 */
@Label(standard = "type", full = "node type")
public enum NodeType {

	/**
	 * Generic processor node
	 */
	@Label(standard = "processor")
	@EnumSerialization(primary = "processor")
	PROCESSOR,

	/**
	 * Retrieves data - send results immediately after being
	 * started and ready loading files.
	 */
	@Label(standard = "loader")
	@EnumSerialization(primary = "loader")
	LOADER,

	/**
	 * Stores data
	 */
	@Label(standard = "saver")
	@EnumSerialization(primary = "saver")
	SAVER,

	/**
	 * Visualize data in the given WidgetSystem
	 */
	@Label(standard = "presenter")
	@EnumSerialization(primary = "presenter")
	PRESENTER,

	@Label(standard = "classifier")
	@EnumSerialization(primary = "classifier")
	CLASSIFIER,

	@Label(standard = "filter")
	@EnumSerialization(primary = "filter")
	FILTER
}
