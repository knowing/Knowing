package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerContentProvider;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.SensorContainerLabelProvider;
import de.lmu.ifi.dbs.medmon.patient.service.IPatientService;

public class DataTreeView extends ViewPart implements PropertyChangeListener {

	public static final String ID = "de.lmu.ifi.dbs.medmon.sensor.DataTreeView";
	
	private TreeViewer viewer;

	private Action saveAction;
	
	public DataTreeView() {
		Activator.getPatientService().addPropertyChangeListener(IPatientService.SENSOR_CONTAINER, this);
	}

	@Override
	public void createPartControl(Composite parent) {
		viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(new SensorContainerContentProvider());
		viewer.setLabelProvider(new SensorContainerLabelProvider());
		viewer.setInput(Activator.getPatientService().getSelection(IPatientService.SENSOR_CONTAINER));
		
		createActions();
		initializeToolBar();
		initializeMenu();

	}

	@Override
	public void setFocus() {
		viewer.getTree().setFocus();
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		viewer.setInput(event.getNewValue());
	}
	
	/**
	 * Create the actions.
	 */
	private void createActions() {
		saveAction = new Action("Speichern", PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_SAVE_EDIT)) {
			@Override
			public void run() {
				System.out.println("SAVE ME!");
			}
		};

	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		toolbarManager.add(saveAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
		menuManager.add(saveAction);
	}
	
}
