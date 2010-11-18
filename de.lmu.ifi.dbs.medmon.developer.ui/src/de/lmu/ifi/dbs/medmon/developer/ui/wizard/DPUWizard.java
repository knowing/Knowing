package de.lmu.ifi.dbs.medmon.developer.ui.wizard;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorListFormEditor;
import de.lmu.ifi.dbs.medmon.developer.ui.wizard.pages.DPUWizardPage;

public class DPUWizard extends Wizard implements IWorkbenchWizard {

	private DPUWizardPage page;
	private IWorkbench workbench;

	public DPUWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		page = new DPUWizardPage();
		addPage(page);
	}

	@Override
	public boolean performFinish() {
		DataProcessingUnit dpu = new DataProcessingUnit();
		dpu.setName(page.getDPUName());
		dpu.setProcessors(page.getDataProcessors());
		ProcessorUnitEditorInput input = new ProcessorUnitEditorInput(dpu);
		try {
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(input, ProcessorListFormEditor.ID);
			workbench.showPerspective("de.lmu.ifi.dbs.medmon.developer.perspective", workbench.getActiveWorkbenchWindow());
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.workbench = workbench;
				
	}	
	

}
