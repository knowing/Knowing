package de.lmu.ifi.dbs.medmon.algorithm.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.core.databinding.beans.PojoObservables;

public class CustomScale extends Composite {
	
	private DataBindingContext bindingContext;
	
	private Text value;
	private Scale scale;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public CustomScale(Composite parent, int style) {
		super(parent, style);
		createContent(parent);
	}
	
	public CustomScale(Composite parent, int style, int minimum, int maximum) {
		this(parent, style);
		scale.setMinimum(minimum);
		scale.setMaximum(maximum);
	}
	
	private void createContent(Composite parent) {
		setLayout(new GridLayout(2, false));
		
		scale = new Scale(this, SWT.NONE);
		scale.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		value = new Text(this, SWT.BORDER);
		GridData data = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
		data.widthHint = 30;
		value.setLayoutData(data);
		bindingContext = initDataBindings();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	@Override
	public void dispose() {
		bindingContext.dispose();
		super.dispose();
	}
	
	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//Connects Scale with Text
		IObservableValue valueObserveTextObserveWidget = SWTObservables.observeText(value, SWT.Modify);
		IObservableValue scaleSelectionObserveValue = PojoObservables.observeValue(scale, "selection");
		bindingContext.bindValue(valueObserveTextObserveWidget, scaleSelectionObserveValue, null, null);
		//Connects Text with Scale
		IObservableValue scaleObserveSelectionObserveWidget = SWTObservables.observeSelection(scale);
		IObservableValue valueTextObserveValue = PojoObservables.observeValue(value, "text");
		bindingContext.bindValue(scaleObserveSelectionObserveWidget, valueTextObserveValue, null, null);
		//
		return bindingContext;
	}

	public int getMaximum() {
		return scale.getMaximum();
	}

	public int getMinimum() {
		return scale.getMinimum();
	}

	public int getSelection() {
		return scale.getSelection();
	}

	public void setMaximum(int value) {
		scale.setMaximum(value);
	}

	public void setMinimum(int value) {
		scale.setMinimum(value);
	}
	
	public void setSelection(int value) {
		scale.setSelection(value);
	}

	public Scale getScale() {
		return scale;
	}
	

}
