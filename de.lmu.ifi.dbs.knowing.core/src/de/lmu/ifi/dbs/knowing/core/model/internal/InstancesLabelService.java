package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.ValueLabelService;

public class InstancesLabelService extends ValueLabelService {

	@Override
	public String provide(String value) {
		System.out.println("Provide: " + value);
		return value;
	}

}
