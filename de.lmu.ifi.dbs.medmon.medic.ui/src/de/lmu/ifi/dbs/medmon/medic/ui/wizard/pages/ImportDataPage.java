package de.lmu.ifi.dbs.medmon.medic.ui.wizard.pages;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.eclipse.jface.viewers.ITreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tree;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DataContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.DataLabelProvider;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;

public class ImportDataPage extends WizardPage {

	private Composite container;

	private DateChooserCombo startDateChooser, endDateChooser;
	private CDateTime startTimeChooser, endTimeChooser;

	private Button bFirstRecord, bLastRecord;
	private Button bValidate;

	private TreeViewer treeViewer;


	public ImportDataPage() {
		super("Daten auswaehlen");
		setImageDescriptor(ResourceManager.getPluginImageDescriptor("de.lmu.ifi.dbs.medmon.rcp", "icons/48/gtk-removable.png"));
		setMessage("Die zu analysierenden Daten auswaehlen");
		setTitle("Sensordaten");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);

		setControl(container);
		container.setLayout(new GridLayout(5, false));
		PageController controller = new PageController();

		Label lFrom = new Label(container, SWT.NONE);
		lFrom.setText("Von");

		startDateChooser = new DateChooserCombo(container, SWT.NONE);
		startDateChooser.setEnabled(false);
		startDateChooser.setValue(new Date());

		Label sep1 = new Label(container, SWT.NONE);
		sep1.setText(":");

		startTimeChooser = new CDateTime(container, CDT.TIME_SHORT);
		startTimeChooser.setEnabled(false);
		startTimeChooser.setSelection(new Date());

		bFirstRecord = new Button(container, SWT.CHECK);
		bFirstRecord.setSelection(true);
		bFirstRecord.setText("Ab erster Aufzeichnung");
		bFirstRecord.addListener(SWT.Selection, controller);

		Label lTill = new Label(container, SWT.NONE);
		lTill.setText("Bis");

		endDateChooser = new DateChooserCombo(container, SWT.NONE);
		endDateChooser.setEnabled(false);
		endDateChooser.setValue(new Date());

		Label sep2 = new Label(container, SWT.NONE);
		sep2.setText(":");

		endTimeChooser = new CDateTime(container, CDT.TIME_SHORT);
		endTimeChooser.setEnabled(false);
		endTimeChooser.setSelection(new Date());

		bLastRecord = new Button(container, SWT.CHECK);
		bLastRecord.setSelection(true);
		bLastRecord.setText("Bis letzte Aufzeichnung");
		bLastRecord.addListener(SWT.Selection, controller);

		treeViewer = new TreeViewer(container, SWT.BORDER | SWT.MULTI);
		treeViewer.setContentProvider(new DataContentProvider());
		treeViewer.setLabelProvider(new DataLabelProvider());
		Tree tree = treeViewer.getTree();
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 5, 1));

		bValidate = new Button(container, SWT.PUSH);
		bValidate.setEnabled(false);
		bValidate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 5, 1));
		bValidate.setText("Daten ueberpruefen");
		bValidate.addListener(SWT.Selection, controller);


		 setPageComplete(true);

	}
	
	public void setViewerInput(Object input) {
		treeViewer.setInput(input);
	}
	
	public ISensorDataContainer[] getSelection() {
		ITreeSelection selection = (ITreeSelection) treeViewer.getSelection();
		if(selection.isEmpty())
			return null;
		List list = selection.toList();
		ISensorDataContainer[] returns = new ISensorDataContainer[list.size()];
		for(int i=0; i < list.size(); i++) {
			returns[i] = (ISensorDataContainer) list.get(i);
		}
		return returns;
	}

	public Date getStart() {
		// TODO find a more convienet way to merge to dates

		Date returns = startDateChooser.getValue();
		Date time = startTimeChooser.getSelection();
		returns.setHours(time.getHours());
		returns.setMinutes(time.getMinutes());
		return returns;
	}

	public Date getEnd() {
		Date returns = endDateChooser.getValue();
		Date time = endTimeChooser.getSelection();
		returns.setHours(time.getHours());
		returns.setMinutes(time.getMinutes());
		return returns;
	}
	

	private class PageController implements Listener {

		@Override
		public void handleEvent(Event event) {
			if (event.type == SWT.Selection) {
				if(event.widget == bValidate) {
					validate();
				} else if(event.widget == bFirstRecord) {
					startDateChooser.setEnabled(!bFirstRecord.getSelection());
					startTimeChooser.setEnabled(!bFirstRecord.getSelection());
				} else if(event.widget == bLastRecord) {
					endDateChooser.setEnabled(!bLastRecord.getSelection());
					endTimeChooser.setEnabled(!bLastRecord.getSelection());
				}
			}
		}

		/**
		 * Compare the sensor-input data and the data persisted in the database.
		 * 
		 */
		//TODO Use arrays for faster comparison
		private void validate() {
			EntityManager em = JPAUtil.currentEntityManager();
			em.getTransaction().begin();
			Query allData = em.createNamedQuery("Data.findAll");
			List<Data> data = allData.getResultList();
			em.getTransaction().commit();
			System.out.println("-------------------Finding identical--------------------");
			for(int i=0; i < data.size(); i++) {
				Data each = data.get(i);
				for(int j=i+1; j < data.size(); j++) {
					Data compare = data.get(j);
					if(compare.equals(each)) {
						System.out.println("!!!!!--------------!!!!!!!!--------------!!!!!!!!!");
						System.out.println(each + " == " + compare);
					}
						
				}
			}
			System.out.println("--------------Finding identical finished----------------");
		}
	}

}
