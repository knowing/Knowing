package de.lmu.ifi.dbs.medmon.medic.core.job;

import java.io.IOException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.progress.IProgressConstants;

import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.core.container.Block;
import de.lmu.ifi.dbs.medmon.sensor.core.converter.IConverter;

public class ImportJob extends Job {

	private Object[] data;
	private final IConverter<?> converter;
	private final Block block;

	public ImportJob(Block block, IConverter<?> converter) {
		super("Importiere " + block);
		this.block = block;
		this.converter = converter;
		setUser(true);
		setProperty(IProgressConstants.ICON_PROPERTY,
				ResourceManager.getPluginImageDescriptor(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_REFRESH_24));
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {

		try {
			data = converter.parseBlockToData(block);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (isModal(this)) {
			// The progress dialog is still open so
			// just open the message
			showResults();
		} else {
			setProperty(IProgressConstants.KEEP_PROPERTY, Boolean.TRUE);
			setProperty(IProgressConstants.ACTION_PROPERTY, getImportCompletedAction());
		}
		return Status.OK_STATUS;
	}

	public boolean isModal(Job job) {
		Boolean isModal = (Boolean) job.getProperty(IProgressConstants.PROPERTY_IN_DIALOG);
		if (isModal == null)
			return false;
		return isModal.booleanValue();
	}

	protected static Action getImportCompletedAction() {
		return new Action("View reservation status") {
			public void run() {
				MessageDialog.openInformation(null, "Import Complete", "Your import has been completed");
			}
		};
	}

	protected static void showResults() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getImportCompletedAction().run();
			}
		});
	}

	public Object[] getData() {
		return data;
	}

}
