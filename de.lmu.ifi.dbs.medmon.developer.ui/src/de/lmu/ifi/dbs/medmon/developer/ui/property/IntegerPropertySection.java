package de.lmu.ifi.dbs.medmon.developer.ui.property;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Scale;

import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.developer.ui.widgets.CustomScale;

public class IntegerPropertySection extends ParameterPropertySection {

	@Override
	public void refresh() {
		super.refresh();

		//Get numeric parameters
		Map<String, NumericParameter> numeric = new HashMap<String, NumericParameter>();
		for (String key : parameters.keySet()) {
			if (parameters.get(key) instanceof NumericParameter)
				numeric.put(key, (NumericParameter) parameters.get(key));
		}

		for (String key : numeric.keySet()) {
			getWidgetFactory().createCLabel(container, key).setLayoutData(
					new GridData(SWT.BEGINNING,SWT.CENTER, false, false, 2, 1));
			CustomScale scale = new CustomScale(container, SWT.NONE);
			NumericParameter parameter = numeric.get(key);
			scale.setMaximum(parameter.getMaximum());
			scale.setMinimum(parameter.getMinimum());
			scale.setSelection(parameter.getValue());
			scale.getScale().addSelectionListener(new ScaleSelectionListener(numeric.get(key)));
			getWidgetFactory().adapt(scale, true, true);
			scale.setLayoutData(new GridData(SWT.FILL,SWT.CENTER, true, false, 2, 1));
		}
		container.layout();
	}
	
	
	private class ScaleSelectionListener extends SelectionAdapter {
		
		private final NumericParameter parameter;

		public ScaleSelectionListener(NumericParameter parameter) {
			this.parameter = parameter;		
		}
		
		@Override
		public void widgetSelected(SelectionEvent e) {
			Scale scale = (Scale) e.widget;
			parameter.setValue(scale.getSelection());
		}
		
	}

}
