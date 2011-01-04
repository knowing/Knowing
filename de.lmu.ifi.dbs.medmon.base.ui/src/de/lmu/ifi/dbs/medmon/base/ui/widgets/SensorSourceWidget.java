package de.lmu.ifi.dbs.medmon.base.ui.widgets;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import de.lmu.ifi.dbs.medmon.base.ui.provider.SensorContentProvider;
import de.lmu.ifi.dbs.medmon.base.ui.provider.SensorLabelProvider;
import de.lmu.ifi.dbs.medmon.base.ui.viewer.SensorTableViewer;
import de.lmu.ifi.dbs.medmon.sensor.core.sensor.ISensor;

public class SensorSourceWidget extends Composite {
	
	private Text tFile;
	private SensorTableViewer sensorViewer;

	private String file;
	protected ISensor sensor;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SensorSourceWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		sensorViewer = new SensorTableViewer(this, SWT.BORDER | SWT.SINGLE);
		sensorViewer.setLabelProvider(new SensorLabelProvider());
		sensorViewer.setContentProvider(new SensorContentProvider());
		sensorViewer.setInput(this);
		sensorViewer.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		
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
				sensor = (ISensor) ((IStructuredSelection)selection).getFirstElement();
				file = sensor.getConverter().openChooseInputDialog(getShell());
				if(file != null)
					tFile.setText(file);
			}
		});

	}
	
	public String getFile() {
		return file;
	}
	
	public ISensor getSensor() {
		return sensor;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
