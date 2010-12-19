package de.lmu.ifi.dbs.medmon.developer.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class PerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.perspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
	}

}
