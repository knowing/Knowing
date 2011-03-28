package de.lmu.ifi.dbs.medmon.medic.ui.action;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;

import de.lmu.ifi.dbs.medmon.base.ui.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.medic.core.util.IMedmonConstants;

public class SaveCommentAction extends Action implements IWorkbenchAction {

	public SaveCommentAction(String name) {
		super(name, ResourceManager.getPluginImageDescriptor(IMedmonConstants.BASE_UI_PLUGIN, IMedmonConstants.IMG_SAVE_AS_16));
	}
	
	@Override
	public void run() {
		System.out.println("Run it");
	}
	
	@Override
	public void runWithEvent(Event event) {
		System.out.println("Run with event: " + event);
		super.runWithEvent(event);
	}
	
	@Override
	public String getId() {
		return "de.lmu.ifi.dbs.medmon.medic.ui.action.SaveCommentAction";
	}
	
	@Override
	public void dispose() {
		
	}

}
