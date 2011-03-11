package de.lmu.ifi.dbs.medmon.developer.ui.handler;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.graph.XDataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.XDPUEditor;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.XDPUInput;
import de.lmu.ifi.dbs.medmon.developer.ui.perspective.PerspectiveFactory;

public class LoadXDPUHandler extends AbstractHandler implements IHandler {

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
			JAXBContext context = JAXBContext.newInstance(XDataProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			File dpuFile = new File(dpuPath);
			XDataProcessingUnit dpu = (XDataProcessingUnit) um.unmarshal(dpuFile);
			XDPUInput input = new XDPUInput(dpu);
			//Opening Editor
			IWorkbench workbench = PlatformUI.getWorkbench();
			workbench.getActiveWorkbenchWindow().getActivePage().openEditor(input, XDPUEditor.ID);
			workbench.showPerspective(PerspectiveFactory.ID, workbench.getActiveWorkbenchWindow());
		} catch (PartInitException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (WorkbenchException e) {
			e.printStackTrace();
		}
		return null;
	}

}
