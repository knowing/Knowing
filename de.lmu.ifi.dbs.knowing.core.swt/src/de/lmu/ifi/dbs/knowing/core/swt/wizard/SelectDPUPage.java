package de.lmu.ifi.dbs.knowing.core.swt.wizard;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
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

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.util.OSGIUtil;

public class SelectDPUPage extends WizardPage {
	
	
	private Text tFile;
	private Text tRegistry;
	private Button bFile;
	private Button bRegistry;
	private Group gConfiguration;
	private Label lExePath;
	private Text tExePath;
	private Button bBrowseExePath;
	
	private static String lastExecutionPath = "";
	private static String lastDPU = "";
	private static boolean fileSelected = true;

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
				ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new LabelProvider());
				dialog.setMultipleSelection(false);
				dialog.setElements(OSGIUtil.registeredDPUs());
				dialog.setTitle("Select Data Processing Unit");
				// User pressed cancel
				if (dialog.open() != Window.OK)
					return;

				Object[] result = dialog.getResult();
				for (Object dpu : result)
					tRegistry.setText(((DataProcessingUnit) dpu).name());

			}
		});

		gConfiguration = new Group(container, SWT.NONE);
		gConfiguration.setLayout(new GridLayout(3, false));
		gConfiguration.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1));
		gConfiguration.setText("Configuration");

		lExePath = new Label(gConfiguration, SWT.NONE);
		lExePath.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lExePath.setText("Execution path");

		tExePath = new Text(gConfiguration, SWT.BORDER);
		tExePath.setText(lastExecutionPath);
		tExePath.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		bBrowseExePath = new Button(gConfiguration, SWT.NONE);
		bBrowseExePath.setText("Browse");
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

	public DataProcessingUnit getDPU() throws JAXBException {
		if (bFile.getSelection()) {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			return (DataProcessingUnit) um.unmarshal(new File(tFile.getText()));
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
	
	private String formURI(String path) {
		String step1 = path.replace("\\","/");
		if( step1.startsWith("/"))
			return step1;
		else
			return "/" + step1;
	}

}
