/*                                                               *\
** |¯¯|/¯¯/|¯¯ \|¯¯| /¯¯/\¯¯\'|¯¯|  |¯¯||¯¯||¯¯ \|¯¯| /¯¯/|__|   **
** | '| '( | '|\  '||  |  | '|| '|/\| '|| '|| '|\  '||  | ,---,  **
** |__|\__\|__|'|__| \__\/__/'|__,/\'__||__||__|'|__| \__\/__|   **
**                                                               **
** Knowing Framework                                             **
** Apache License - http://www.apache.org/licenses/              **
** LMU Munich - Database Systems Group                           **
** http://www.dbs.ifi.lmu.de/                                    **
\*                                                               */
package de.lmu.ifi.dbs.knowing.core.model.internal;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.sapphire.services.ValueSerializationService;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.util.ResultsUtil;

/**
 * <p>(De)serialize {@link Instances} objects. Used by
 * the IProcessHistory model.</p>
 * 
 * @author Nepomuk Seiler
 * @version 0.2
 * @since 2011-10-05
 */
public class InstancesSerializationService extends ValueSerializationService {

	@Override
	protected Object decodeFromString(String value) {
		StringReader reader = new StringReader(value);
		try {
			return new Instances(reader);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String encode(Object value) {
		// Handle ImmutableInstances format
		try {
/*			if (value instanceof ImmutableInstances) {
				ImmutableInstances inst = (ImmutableInstances) value;
				return inst.toStringComplete();
			}
			return super.encode(value);*/
			//TODO Fixed amount of printed data should be set elsewhere
			Instances instances = (Instances) value;
			return ResultsUtil.appendInstancesPartial(instances, instances, 50).toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
