package de.lmu.ifi.dbs.knowing.core.model.internal;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;

/**
 * <p>Called after creation wizard in Eclipse</p>
 * @author Nepomuk Seiler
 * @version 0.1
 *
 */
public final class IDataProcessingUnitOp {

	public static final Status execute(final IDataProcessingUnit context, final ProgressMonitor monitor) {
		// Do something here.
		return Status.createOkStatus();
	}

}
