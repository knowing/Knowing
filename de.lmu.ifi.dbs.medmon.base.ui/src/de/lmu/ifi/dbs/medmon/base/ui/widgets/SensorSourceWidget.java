package de.lmu.ifi.dbs.medmon.base.ui.widgets;

import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.base.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class SensorSourceWidget extends Composite {
	
	private ListenerList listenerList = new ListenerList();
	
	private Text tFile;
	private SensorTableViewer sensorViewer;

	private String file;
	protected SensorAdapter sensor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SensorSourceWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		sensorViewer = new SensorTableViewer(this, SWT.BORDER | SWT.SINGLE);
		sensorViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		sensorViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				firePropertyChanged("sensor", sensor, ((IStructuredSelection)event.getSelection()).getFirstElement());		
			}
		});
		
		tFile = new Text(this, SWT.BORDER);
		tFile.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Button bSDRFile = new Button(this, SWT.NONE);
		bSDRFile.setText("Datei oeffnen");
		bSDRFile.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ISelection selection = sensorViewer.getSelection();
				if(selection.isEmpty())
					return;
				sensor = (SensorAdapter) ((IStructuredSelection)selection).getFirstElement();
				file = sensor.getSensorExtension().getConverter().openChooseInputDialog(getShell());
				if(file != null) {
					firePropertyChanged("file", tFile.getText(), file);
					tFile.setText(file);
				}
					
			}
		});

	}
	
	public String getFile() {
		return file;
	}
	
	public SensorAdapter getSensor() {
		return sensor;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		listenerList.add(listener);
	}
	
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		listenerList.add(listener);
	}
	
	private void firePropertyChanged(String property,Object oldValue, Object newValue) {
		for (Object listener : listenerList.getListeners()) {
			IPropertyChangeListener propertyListener = (IPropertyChangeListener) listener;
			PropertyChangeEvent event = new PropertyChangeEvent(this, property, oldValue, newValue);
			propertyListener.propertyChange(event);
		}
	}

}
