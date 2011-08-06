package de.lmu.ifi.dbs.knowing.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import akka.actor.ActorRef;

import weka.core.Instance;
import weka.core.Instances;

import de.lmu.ifi.dbs.knowing.core.japi.JProcessor;
import de.lmu.ifi.dbs.knowing.ui.wizard.pages.CreateProcessorPage;

public class NewProcessorWizard extends Wizard implements IWorkbenchWizard {

	private IStructuredSelection selection;
	private CreateProcessorPage page;

	public NewProcessorWizard() {
		setWindowTitle("New Wizard");
	}

	@Override
	public void addPages() {
		addPage(page = new CreateProcessorPage(selection));
	}

	@Override
	public boolean performFinish() {
		IPath path = page.getPath();
		final IFile file = locatePath(path);
		try {
			if(page.isScala())
				file.create(createScalaProcessor(), true, null);
			else if(page.isJava())
				file.create(createJavaProcessor(), true,null);
		} catch (CoreException e) {
			e.printStackTrace();
		}

		getShell().getDisplay().asyncExec(new Runnable() {
			public void run() {
				IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
				try {
					IDE.openEditor(page, file, true);
				} catch (PartInitException e) {
				}
			}
		});
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}

	private InputStream createJavaProcessor() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bos);
		String clazz = page.getClassName().replaceAll(".java", "");

		writer.println("package " + page.getClassPackage().getElementName() + ";\n");

		writer.println("import java.util.Properties; \n");

		writer.println("import akka.actor.Actor; ");
		writer.println("import akka.actor.ActorRef; \n");
		
		writer.println("import de.lmu.ifi.dbs.knowing.core.events.*;");
		writer.println("import de.lmu.ifi.dbs.knowing.core.processing.TProcessor;");
		writer.println("import weka.core.Instance;");
		writer.println("import weka.core.Instances; \n");

		writer.println("class " + clazz + " extends AbstractProcessor { \n");

		writer.println("	public TestJavaProcessor(JProcessor wrapper) { ");
		writer.println("		super(wrapper);");
		writer.println("	} \n");
		
		writer.println("	@Override");
		writer.println("	public void build(Instances instances) { } \n");
		
		writer.println("	@Override");
		writer.println("	public Instances query(Instance query, ActorRef ref) { return null; } \n");
		
		writer.println("	@Override");
		writer.println("	public void result(Instances result, Instance query) { } \n");
		
		writer.println("	@Override");
		writer.println("	public void configure(Properties properties) { } \n");
		
		writer.println("	@Override");
		writer.println("	public void messageException(Object message) { } \n");
				
		writer.println("} \n");

		writer.flush();
		writer.close();
		return new ByteArrayInputStream(bos.toByteArray());
	}

	private InputStream createScalaProcessor() {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(bos);
		String clazz = page.getClassName().replaceAll(".scala", "");
		writer.println("package " + page.getClassPackage().getElementName() + "\n");

		writer.println("import java.util.Properties \n");

		writer.println("import akka.actor.{Actor, ActorRef } \n");
		writer.println("import akka.event.EventHandler.{ debug, info, warning, error } \n");
		
		writer.println("import de.lmu.ifi.dbs.knowing.core.events._");
		writer.println("import de.lmu.ifi.dbs.knowing.core.factory.ProcessorFactory");
		writer.println("import de.lmu.ifi.dbs.knowing.core.processing.TProcessor");
		writer.println("import weka.core.{Instance, Instances} \n");

		writer.println("class " + clazz + " extends TProcessor { \n");

		writer.println("	def build(inst: Instances) { } \n");
		writer.println("	def query(query: Instance): Instances = { null }  \n");
		writer.println("	def result(result: Instances, query: Instance) = {} \n");
		writer.println("	def configure(properties: Properties) = {} \n");
		writer.println("} \n");

		writer.println("class " + clazz + "Factory extends ProcessorFactory(classOf[" + clazz + "])");

		writer.flush();
		writer.close();
		return new ByteArrayInputStream(bos.toByteArray());
	}

	public static IFile locatePath(IPath path) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		return workspace.getRoot().getFile(path);
	}

}
