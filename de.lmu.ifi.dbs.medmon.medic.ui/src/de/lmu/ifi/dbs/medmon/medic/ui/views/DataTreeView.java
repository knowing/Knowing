package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.base.ui.widgets.SensorSourceWidget;
import de.lmu.ifi.dbs.medmon.medic.core.service.IPatientService;
import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerLabelProvider;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ContainerType;
import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class DataTreeView extends ViewPart implements PropertyChangeListener, IPropertyChangeListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";

	private Action saveAction;

	private TreeViewer dataTreeViewer;

	private TabFolder tabFolder;

	private SensorSourceWidget sensorSource;

	private TabItem tabData;

	public DataTreeView() {
		Activator.getPatientService().addPropertyChangeListener(IPatientService.SENSOR_CONTAINER, this);
	}

	@Override
	public void createPartControl(Composite parent) {
		parent.setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(parent, SWT.BOTTOM);

		/* Sensor Tab */
		TabItem tabSensor = new TabItem(tabFolder, SWT.NONE);
		tabSensor.setText("Sensoren");

		Composite cSensor = new Composite(tabFolder, SWT.NONE);
		tabSensor.setControl(cSensor);
		sensorSource = new SensorSourceWidget(cSensor, SWT.NONE);
		sensorSource.addPropertyChangeListener(this);
		cSensor.setLayout(new FillLayout(SWT.HORIZONTAL));

		/* Current Data Tab */
		tabData = new TabItem(tabFolder, SWT.NONE);
		tabData.setText("Aktuelle Daten");

		Composite cData = new Composite(tabFolder, SWT.NONE);
		tabData.setControl(cData);
		cData.setLayout(new FillLayout(SWT.HORIZONTAL));

		dataTreeViewer = new TreeViewer(cData, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);

		dataTreeViewer.setContentProvider(new SensorContainerContentProvider());
		dataTreeViewer.setLabelProvider(new SensorContainerLabelProvider());
		dataTreeViewer.setInput(Activator.getPatientService().getSelection(IPatientService.SENSOR_CONTAINER));

		createActions();
		initializeToolBar();
		initializeMenu();

	}

	@Override
	public void setFocus() {
		tabFolder.setFocus();
	}

	/**
	 * Listens to IPatientService
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		dataTreeViewer.setInput(event.getNewValue());
	}

	/**
	 * Listens to SensorSourceWidget
	 */
	@Override
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
		if (event.getProperty().equals("sensor")) {
			Activator.getPatientService().setSelection(event.getNewValue(), IPatientService.SENSOR);
		} else if (event.getProperty().equals("file")) {
			String file = (String) event.getNewValue();
			ISensor sensor = sensorSource.getSensor().getSensorExtension();
			try {
				ISensorDataContainer c = sensor.getConverter().convertToContainer(file, ContainerType.WEEK, ContainerType.HOUR,
						null);
				Activator.getPatientService().setSelection(c, IPatientService.SENSOR_CONTAINER);
				tabFolder.setSelection(tabData);
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

}
