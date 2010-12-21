package de.lmu.ifi.dbs.medmon.datamining.core.analyzed;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.cluster.DoubleCluster;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IClusterData;

public class ClusterAnalyzedData implements IClusterData {

	private final ClusterUnit cu = new ClusterUnit();
	private Composite container;
	private Text tName;

	/**
	 * @wbp.parser.entryPoint
	 */
	@Override
	public void createContent(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(2, false));

		Label lName = new Label(container, SWT.NONE);
		lName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lName.setText("Name");

		tName = new Text(container, SWT.BORDER);
		GridData gd_text = new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1);
		gd_text.widthHint = 150;
		tName.setLayoutData(gd_text);
		new Label(container, SWT.NONE);

		Button bExport = new Button(container, SWT.NONE);
		bExport.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bExport.setText("save");
		bExport.setImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_SAVE_EDIT));
		bExport.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(container.getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.xml" });
				dialog.setFilterNames(new String[] { "Cluster Unit" });
				String file = dialog.open();
				if(file != null && !file.isEmpty())
					saveClusterUnit(file);
			}
		});
	}
	
	private void saveClusterUnit(String file) {
		cu.setName(tName.getText());
		try {
			JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(cu, new File(file));
		} catch (JAXBException e) {
			e.printStackTrace();
			MessageDialog.openError(container.getShell(), e.getErrorCode(), e.getMessage());
		}		
	}

	@Override
	public void dispose() {
		container.dispose();
	}

	@Override
	public ClusterUnit getCluster() {
		return cu;
	}

	public void setName(String name) {
		cu.setName(name);
	}

	public List<DoubleCluster> getClusterlist() {
		return cu.getClusterlist();
	}

	public void setClusterlist(List<DoubleCluster> clusterlist) {
		cu.setClusterlist(clusterlist);
	}

	public void addCluster(DoubleCluster cluster) {
		cu.addCluster(cluster);
	}

}
