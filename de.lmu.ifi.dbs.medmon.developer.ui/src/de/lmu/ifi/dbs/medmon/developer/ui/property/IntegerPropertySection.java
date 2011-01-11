package de.lmu.ifi.dbs.medmon.developer.ui.property;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;

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
			getWidgetFactory().createCLabel(container, key);
			CustomScale scale = new CustomScale(container, SWT.NONE);
			NumericParameter parameter = numeric.get(key);
			scale.setMaximum(parameter.getMaximum());
			scale.setMinimum(parameter.getMinimum());
			scale.setSelection(parameter.getValue());
			getWidgetFactory().adapt(scale, true, true);
			scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		}
		container.layout();
	}

}
