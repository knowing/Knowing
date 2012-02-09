/*																*\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|	**
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,	**
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|	**
** 																**
** Knowing Framework											**
** Apache License - http://www.apache.org/licenses/				**
** LMU Munich - Database Systems Group							**
** http://www.dbs.ifi.lmu.de/									**
\*																*/
package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.services.ReferenceService;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IEdge;
import de.lmu.ifi.dbs.knowing.core.model.INode;

/**
 * <p>Used by {@link IEdge} to provide options for the source/target port property</p>
 * @author Nepomuk Seiler
 * @version 0.2
 *
 */
public class NodeReferenceService extends ReferenceService {

	@Override
	public Object resolve(String reference) {
		if (reference == null)
			return null;

//		context().find(IDataProcessingUnit.class).nearest(IDataProcessingUnit.class);
		final IDataProcessingUnit dpu = context(IDataProcessingUnit.class);
		for (INode node : dpu.getNodes()) {
			if (reference.equals(node.getId().getText()))
				return node;
		}
		//non found
		return null;

	}

}
