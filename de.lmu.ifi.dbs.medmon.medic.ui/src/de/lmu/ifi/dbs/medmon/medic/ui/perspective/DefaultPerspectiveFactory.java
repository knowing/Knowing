package de.lmu.ifi.dbs.medmon.medic.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class DefaultPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.default";
	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(false);

	}

}
