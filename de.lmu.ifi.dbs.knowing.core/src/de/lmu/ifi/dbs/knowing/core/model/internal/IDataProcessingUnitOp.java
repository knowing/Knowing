package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;

public final class IDataProcessingUnitOp {

	public static final Status execute(final IDataProcessingUnit context, final ProgressMonitor monitor) {
		// Do something here.
		System.out.println("Store it!");
		return Status.createOkStatus();
	}

}
