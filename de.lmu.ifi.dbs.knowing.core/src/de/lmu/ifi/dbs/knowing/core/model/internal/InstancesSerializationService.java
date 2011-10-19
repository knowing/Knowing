package de.lmu.ifi.dbs.knowing.core.model.internal;

import java.io.IOException;
import java.io.StringReader;

import org.eclipse.sapphire.modeling.serialization.ValueSerializationService;

import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.processing.ImmutableInstances;

/**
 * 
 * @author Nepomuk Seiler
 * @version 1.0
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
			if (value instanceof ImmutableInstances) {
				ImmutableInstances inst = (ImmutableInstances) value;
				return inst.toStringComplete();
			}
			return super.encode(value);
		} catch (Exception e) {
			return e.getMessage();
		}
	}

}
