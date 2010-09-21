package de.lmu.ifi.dbs.medmon.algorithm.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.ComboViewer;

public class AlgorithmDetailComposite  {

	private Composite control;
	
	private Text description;
	private Slider slider;
	private ComboViewer comboViewer;

	private final int style;
	
	public AlgorithmDetailComposite(Composite parent, int style) {
		this.style = style;
		createContent(parent);	
	}
	
	protected void createContent(Composite parent) {
		control = new Composite(parent, style);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 10;
		layout.marginWidth = 10;
		layout.horizontalSpacing = 15;
		layout.verticalSpacing = 15;
		control.setLayout(layout);
		
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		
		toolkit.createLabel(control, "Beschreibung");
		
		description = new Text(control, SWT.READ_ONLY | SWT.V_SCROLL);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);
		data.heightHint = 80;
		description.setLayoutData(data);
		
		toolkit.createLabel(control, "Toleranz");
		
		slider = new Slider(control, SWT.NONE);
		slider.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		toolkit.adapt(slider, true, false);
		
		toolkit.createLabel(control, "Darstellung");
		
		comboViewer = new ComboViewer(control, SWT.NONE);
		Combo combo = comboViewer.getCombo();
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		toolkit.adapt(combo, true, false);
	}

	public Composite getControl() {
		return control;
	}

}
