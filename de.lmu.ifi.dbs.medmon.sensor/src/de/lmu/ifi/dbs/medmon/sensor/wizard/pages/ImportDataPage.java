package de.lmu.ifi.dbs.medmon.sensor.wizard.pages;

import java.util.GregorianCalendar;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import org.eclipse.nebula.widgets.datechooser.DateChooserCombo;
import org.eclipse.nebula.widgets.cdatetime.CDateTime;
import org.eclipse.nebula.widgets.cdatetime.CDT;
import org.eclipse.swt.widgets.Button;

public class ImportDataPage extends WizardPage{

	private Composite container;
	
	private GregorianCalendar start = new GregorianCalendar();
	private GregorianCalendar end = new GregorianCalendar();
	
	public ImportDataPage() {
		super("Daten auswaehlen");
		setMessage("Die zu analysierenden Daten auswaehlen");
		setTitle("Sensordaten");
		setPageComplete(true);
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
	
		setControl(container);
		container.setLayout(new GridLayout(5, false));
		
		Label lFrom = new Label(container, SWT.NONE);
		lFrom.setText("Von");
		
		DateChooserCombo startDateChooser = new DateChooserCombo(container, SWT.NONE);
		startDateChooser.setValue(start.getTime());
		
		Label sep1 = new Label(container, SWT.NONE);
		sep1.setText(":");
		
		CDateTime startTimeChooser = new CDateTime(container, CDT.TIME_SHORT);
		startTimeChooser.setSelection(start.getTime());
		
		Button btnAbErsterAufzeichnung = new Button(container, SWT.CHECK);
		btnAbErsterAufzeichnung.setText("Ab erster Aufzeichnung");
		
		Label lTill = new Label(container, SWT.NONE);
		lTill.setText("Bis");
		
		DateChooserCombo endDateChooser = new DateChooserCombo(container, SWT.NONE);
		endDateChooser.setValue(end.getTime());
		
		Label sep2 = new Label(container, SWT.NONE);
		sep2.setText(":");
		
		CDateTime endTimeChooser = new CDateTime(container, CDT.TIME_SHORT);
		endTimeChooser.setSelection(end.getTime());
		
		Button btnBisHeute = new Button(container, SWT.CHECK);
		btnBisHeute.setText("Bis letzte Aufzeichnung");
		
		//setPageComplete(false);
	}
}
