package de.lmu.ifi.dbs.knowing.debug.core.launching;

import static de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate.DPU_PARAMETERS;
import static de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate.DPU_PATH;
import static de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate.DPU_PROJECT;
import static de.lmu.ifi.dbs.knowing.debug.core.launching.DPULaunchConfigurationDelegate.stringToParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.pde.launching.OSGiLaunchConfigurationDelegate;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.workspace.WorkspaceFileResourceStore;

import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.model.IParameter;

/**
 * 
 * @author Nepomuk Seiler
 * 
 */
public class DPULaunchConfigurationDelegate extends OSGiLaunchConfigurationDelegate {

	public static final String	DPU_PROJECT		= "knowing.dpu.project";

	/** Relative to project */
	public static final String	DPU_PATH		= "knowing.dpu.path";

	public static final String	DPU_PARAMETERS	= "knowing.dpu.parameters";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor) throws CoreException {
		monitor.subTask("Loading Data Processing Unit");
		String projectName = configuration.getAttribute(DPU_PROJECT, (String) null);
		String relativePath = configuration.getAttribute(DPU_PATH, (String) null);
		String vmArguments = configuration.getAttribute("org.eclipse.jdt.launching.VM_ARGUMENTS", (String)null);
		
		IFile dpuFile = findDPUFile(projectName, relativePath);
		try {
			IDataProcessingUnit loadDPU = loadDPU(dpuFile);
			String arguments = vmArguments + " -Dknowing.dpu.uri=" + dpuFile.getLocationURI();
			ILaunchConfigurationWorkingCopy copy = configuration.copy("WithDPUSettings");
			copy.setAttribute("org.eclipse.jdt.launching.VM_ARGUMENTS", arguments);
			super.launch(configuration, mode, launch, monitor);
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		}
		
	}
	

	public static  IFile findDPUFile(String projectName, String relativePath) {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		return (IFile) project.findMember(relativePath);
	}

	public static IDataProcessingUnit loadDPU(IFile dpuFile) throws ResourceStoreException {
		XmlResourceStore store = new XmlResourceStore(new WorkspaceFileResourceStore(dpuFile));
		RootXmlResource resource = new RootXmlResource(store);
		IDataProcessingUnit original = IDataProcessingUnit.TYPE.instantiate(resource);
		IDataProcessingUnit dpu = IDataProcessingUnit.TYPE.instantiate();
		dpu.copy(original);
		return dpu;
	}

	public static List<IParameter> stringToParameters(List<String> tokens) {
		ArrayList<IParameter> result = new ArrayList<>(tokens.size());
		for (String token : tokens) {
			IParameter p = IParameter.TYPE.instantiate();
			String[] keyValue = token.split("=");
			p.setKey(keyValue[0]);
			p.setValue(keyValue[1]);
			result.add(p);
		}
		return result;
	}

	public static List<String> parametersToString(List<IParameter> parameters) {
		ArrayList<String> result = new ArrayList<>(parameters.size());
		for (IParameter p : parameters) {
			result.add(p.getKey().getContent() + "=" + p.getValue().getContent());
		}
		return result;
	}
}
