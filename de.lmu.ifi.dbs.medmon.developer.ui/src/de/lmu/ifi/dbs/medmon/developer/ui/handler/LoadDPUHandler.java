package de.lmu.ifi.dbs.medmon.developer.ui.handler;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.DPUFormEditor;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;

public class LoadDPUHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//Check out the workspace location
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();	
		IPath location = root.getLocation();
		
		FileDialog dpuDialog = new FileDialog(HandlerUtil.getActiveShell(event), SWT.OPEN | SWT.SINGLE);
		dpuDialog.setFilterExtensions(new String[] { "*.xml" });
		dpuDialog.setFilterNames(new String[] {"DPU XML File"});
		dpuDialog.setFilterPath(location.toOSString());
		String dpuPath = dpuDialog.open();
				
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			File dpuFile = new File(dpuPath);
			DataProcessingUnit dpu = (DataProcessingUnit) um.unmarshal(dpuFile);
			ProcessorUnitEditorInput input = new ProcessorUnitEditorInput(dpu);
			System.out.println("Opening Editor");
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, DPUFormEditor.ID);
			//PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().openEditor(input, DPUEditor.ID);
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

}
