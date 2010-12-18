package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Map;
import java.util.Set;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAnalyzedData;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.layout.FillLayout;

public class MonitorView extends ViewPart implements PropertyChangeListener {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.views.MonitorView";

	private TabFolder tabFolder;

	public MonitorView() {
		Activator.getPatientService().addPropertyChangeListener(IPatientService.ANALYZED_DATA, this);
	}

	@Override
	public void createPartControl(Composite parent) {
		tabFolder = new TabFolder(parent, SWT.NONE);

		Map<String, IAnalyzedData> data = (Map<String, IAnalyzedData>) Activator.getPatientService().getSelection(
				IPatientService.ANALYZED_DATA);
		System.out.println("AnalyzedData: " + data);
		if (data != null)
			createTabItems(data, tabFolder);
		System.out.println("PartControl created");
	}

	public void setFocus() {
		tabFolder.setFocus();
	}

	private void createTabItems(Map<String, IAnalyzedData> data, TabFolder tabFolder) {
		Set<String> keys = data.keySet();
		System.out.println("Keys: " + keys);
		for (String key : keys) {
			if(key.equals(IAlgorithm.DEFAULT_DATA))
				continue;
			System.out.println("Create Tab for: " + key);
			TabItem tabItem = new TabItem(tabFolder, SWT.NONE);
			tabItem.setText(key);
			Composite composite = new Composite(tabFolder, SWT.NONE);
			composite.setLayout(new FillLayout(SWT.HORIZONTAL));
			data.get(key).createContent(composite);
			tabItem.setControl(composite);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (tabFolder == null)
			return;

		for (TabItem item : tabFolder.getItems())
			item.dispose();
		if (evt.getNewValue() != null) {
			Map<String, IAnalyzedData> data = (Map<String, IAnalyzedData>) evt.getNewValue();
			createTabItems(data, tabFolder);
		}
		tabFolder.layout(true);
	}
}