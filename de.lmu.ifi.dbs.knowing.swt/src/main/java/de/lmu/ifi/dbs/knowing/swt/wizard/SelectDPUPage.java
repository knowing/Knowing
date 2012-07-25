package de.lmu.ifi.dbs.knowing.swt.wizard;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;

import akka.actor.ActorSystem;
import de.lmu.ifi.dbs.knowing.core.model.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil;
import de.lmu.ifi.dbs.knowing.swt.internal.Activator;

public class SelectDPUPage extends WizardPage {
	
	public static String lastExecutionPath = "";
	public static String lastDPU = "";
	public static boolean fileSelected = true;
	
	private Text tFile, tRegistry,tExePath;
	private Button bFile, bRegistry, bBrowseExePath;
	private Group gConfiguration;
	private ComboViewer actorSystemViewer;

	/**
	 * Create the wizard.
	 */
	public SelectDPUPage() {
		super("wizardPage");
		setTitle("Select Data Processing Unit");
		setDescription("");
	}

	/**
	 * Create contents of the wizard.
	 * 
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
		container.setLayout(new GridLayout(3, false));

		bFile = new Button(container, SWT.RADIO);
		bFile.setSelection(fileSelected);
		bFile.setText("File:");

		tFile = new Text(container, SWT.BORDER);
		tFile.setEnabled(fileSelected);
		tFile.setText(lastDPU);
		tFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button bBrowseFile = new Button(container, SWT.NONE);
		bBrowseFile.setEnabled(fileSelected);
		bBrowseFile.setText("Browse");
		bBrowseFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(getShell());
				dialog.setFilterExtensions(new String[] { "*.dpu" });
				dialog.setFilterNames(new String[] { "Data Processing Unit" });
				String path = dialog.open();
				if (path != null) {
					tFile.setText(path);
					lastDPU = path;
				}
					
			}
		});

		bRegistry = new Button(container, SWT.RADIO);
		bRegistry.setSelection(!fileSelected);
		bRegistry.setText("Registry: ");

		tRegistry = new Text(container, SWT.BORDER);
		tRegistry.setEditable(false);
		tRegistry.setEnabled(!fileSelected);
		tRegistry.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		final Button bBrowseRegistry = new Button(container, SWT.NONE);
		bBrowseRegistry.setEnabled(!fileSelected);
		bBrowseRegistry.setText("Browse");

		bBrowseRegistry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider() {
					@Override
					public String getText(Object element) {
						return ((IDataProcessingUnit)element).getName().getContent();
					}
				});
				dialog.setMultipleSelection(false);
				dialog.setElements(OSGIUtil.registeredDPUs());
				dialog.setTitle("Select Data Processing Unit");
				// User pressed cancel
				if (dialog.open() != Window.OK)
					return;

				Object[] result = dialog.getResult();
				for (Object dpu : result)
					tRegistry.setText(((IDataProcessingUnit) dpu).getName().getContent());

			}
		});

		gConfiguration = new Group(container, SWT.NONE);
		gConfiguration.setLayout(new GridLayout(3, false));
		gConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		gConfiguration.setText("Configuration");

		Label lExePath = new Label(gConfiguration, SWT.NONE);
		lExePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lExePath.setText("Execution path");

		tExePath = new Text(gConfiguration, SWT.BORDER);
		tExePath.setText(lastExecutionPath);
		tExePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		tExePath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				lastExecutionPath = tExePath.getText();
			}
		});

		bBrowseExePath = new Button(gConfiguration, SWT.NONE);
		bBrowseExePath.setText("Browse");
		
		Label lblSystem = new Label(gConfiguration, SWT.NONE);
		lblSystem.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSystem.setText("System");
		
		actorSystemViewer = new ComboViewer(gConfiguration, SWT.READ_ONLY);
		actorSystemViewer.getCombo().setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		List<ActorSystem> systems = Activator.actorSystemManager().getSystems();
		for (ActorSystem actorSystem : systems) 
			actorSystemViewer.add(actorSystem);
		if(!systems.isEmpty())
			actorSystemViewer.setSelection(new StructuredSelection(systems.get(0)));
		
		new Label(gConfiguration, SWT.NONE);
		
		
		bBrowseExePath.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				dialog.setText("Execution Path");
				dialog.setMessage("Select execution directory");
				String dir = dialog.open();
				if (dir == null)
					return;
				String sep = System.getProperty("file.separator");
				if (!dir.endsWith(sep))
					dir = dir + sep;
				tExePath.setText(dir);
				lastExecutionPath = dir;
			}
		});

		// Radio Button Listener
		bFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileSelected = true;
				tFile.setEnabled(true);
				bBrowseFile.setEnabled(true);

				tRegistry.setEnabled(false);
				bBrowseRegistry.setEnabled(false);
				
			}
		});

		bRegistry.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				fileSelected = false;
				tFile.setEnabled(false);
				bBrowseFile.setEnabled(false);

				tRegistry.setEnabled(true);
				bBrowseRegistry.setEnabled(true);
			}
		});
		
		if(fileSelected)
			bBrowseFile.setFocus();
		else
			bBrowseRegistry.setFocus();
	}

	public IDataProcessingUnit getDPU() throws ResourceStoreException {
		if (bFile.getSelection()) {
			XmlResourceStore store = new XmlResourceStore(new File(tFile.getText()));
			RootXmlResource resource = new RootXmlResource(store);
			return (IDataProcessingUnit) IDataProcessingUnit.TYPE.instantiate(resource);
		} else if (bRegistry.getSelection()) {
			return OSGIUtil.registeredDPU(tRegistry.getText());
		}
		return null;
	}

	public URI getExecutionPath() throws URISyntaxException {
		if (!tExePath.getText().isEmpty()) {
			return new URI("file", formURI(tExePath.getText()), null);
		} else if (bFile.getSelection()) {
			return new URI("file", formURI(tFile.getText()), null);
		} else if (bRegistry.getSelection()) {
			return OSGIUtil.registeredURLtoDPU(tRegistry.getText()).toURI();
		}
		return null;
	}
	
	public ActorSystem getActorSystem() {
		if(actorSystemViewer.getSelection().isEmpty())
			return null;
		
		IStructuredSelection selection = (IStructuredSelection) actorSystemViewer.getSelection();
		return (ActorSystem) selection.getFirstElement();
	}
	
	private String formURI(String path) {
		String step1 = path.replace("\\","/");
		if( step1.startsWith("/"))
			return step1;
		else
			return "/" + step1;
	}

}
