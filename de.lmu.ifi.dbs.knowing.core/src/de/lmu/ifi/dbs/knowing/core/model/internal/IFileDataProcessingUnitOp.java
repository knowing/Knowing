package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IFileDataProcessingUnit;

public final class IFileDataProcessingUnitOp {

	public static final Status execute(final IFileDataProcessingUnit context, final ProgressMonitor monitor) {
		// Do something here.

		return Status.createOkStatus();
	}

}
