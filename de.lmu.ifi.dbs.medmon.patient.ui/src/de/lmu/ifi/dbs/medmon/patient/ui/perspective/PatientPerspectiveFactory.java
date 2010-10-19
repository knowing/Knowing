package de.lmu.ifi.dbs.medmon.patient.ui.perspective;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

public class PatientPerspectiveFactory implements IPerspectiveFactory {

	public static final String ID = "de.lmu.ifi.dbs.medmon.patient.PatientManagement";

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		
		IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		try {
			handlerService.executeCommand("de.lmu.ifi.dbs.medmon.patient.OpenPatientEditor", null);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new RuntimeException("de.lmu.ifi.dbs.medmon.patient.OpenPatientEditor");
		}
		
	}

}
