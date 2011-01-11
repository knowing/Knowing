package de.lmu.ifi.dbs.medmon.developer.ui.property;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.ClusterParameter;


public class ClusterParameterPropertySection extends ParameterPropertySection {
	
	@Override
	public void refresh() {
		super.refresh();
		
		Map<String, ClusterParameter> cluster = new HashMap<String, ClusterParameter>();
		for (String key : parameters.keySet()) {
			if (parameters.get(key) instanceof ClusterParameter)
				cluster.put(key, (ClusterParameter) parameters.get(key));
		}
		
		for (String key : cluster.keySet()) {
			ClusterParameter p = cluster.get(key);
			getWidgetFactory().createCLabel(container, key);
			
			Composite cCluster = getWidgetFactory().createComposite(container, SWT.NONE);
			cCluster.setLayout(new GridLayout(2, false));
			cCluster.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			
			Text text = getWidgetFactory().createText(cCluster, "");
			text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			Button bLoad = getWidgetFactory().createButton(cCluster, "Choose", SWT.PUSH);
			bLoad.addSelectionListener(new ClusterSelectionListener(text, p));
		}
	}
	
	private class ClusterSelectionListener extends SelectionAdapter {
		
		private final Text text;
		private final ClusterParameter parameter;
				
		public ClusterSelectionListener(Text text, ClusterParameter parameter) {	
			this.text = text;
			this.parameter = parameter;
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			FileDialog dialog = new FileDialog(text.getShell(), SWT.OPEN);
			dialog.setFilterExtensions(new String[] { "*.xml" });
			dialog.setFilterNames(new String[] { "ClusterUnit" });
			String file = dialog.open();
			if(file == null || file.isEmpty())
				return;
			try {
				JAXBContext context = JAXBContext.newInstance(ClusterUnit.class);
				Unmarshaller um = context.createUnmarshaller();
				ClusterUnit cluster = (ClusterUnit) um.unmarshal(new File(file));
				text.setText(file);	
			} catch (JAXBException e1) {
				e1.printStackTrace();
				MessageDialog.openError(text.getShell(), "No ClusterUnit", e1.getMessage());
			}
				
		}
		
	}

}
