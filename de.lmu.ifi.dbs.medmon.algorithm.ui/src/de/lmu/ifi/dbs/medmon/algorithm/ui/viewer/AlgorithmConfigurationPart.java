package de.lmu.ifi.dbs.medmon.algorithm.ui.viewer;

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.algorithm.ui.provider.ClusterUnitLabelProvider;
import de.lmu.ifi.dbs.medmon.algorithm.ui.widgets.CustomScale;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.ClusterParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.IProcessorParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.NumericParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.parameter.StringParameter;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;

public class AlgorithmConfigurationPart extends SectionPart {

	private FormToolkit toolkit;
	private DataBindingContext bindingContext;
	private Map<String, IProcessorParameter> parameters;

	public AlgorithmConfigurationPart(Section section) {
		this(section, null);
	}

	public AlgorithmConfigurationPart(Section section, Map<String, IProcessorParameter> parameters) {
		super(section);
		this.parameters = parameters;
		toolkit = new FormToolkit(section.getDisplay());
		initialize(new ManagedForm(toolkit, toolkit.createScrolledForm(section)));
		createClient(section, toolkit);

	}

	/**
	 * Fill the section.
	 */
	private void createClient(Section section, FormToolkit toolkit) {
		section.setText("Eigenschaften");
		Composite container = toolkit.createComposite(section);
		GridLayout layout = new GridLayout(2, false);
		layout.verticalSpacing = 8;
		layout.horizontalSpacing = 15;
		container.setLayout(layout);
		bindingContext = initDataBindings();

		if (parameters != null && !parameters.isEmpty())
			createParameterContent(container);
		else
			toolkit.createLabel(container, "Keine Konfiguration moeglich");

		section.setClient(container);
	}

	private void createParameterContent(Composite parent) {
		for (String key : parameters.keySet()) {
			IProcessorParameter parameter = parameters.get(key);
			if (parameter instanceof NumericParameter)
				createNumericParameterContent(parent, (NumericParameter) parameter);
			else if (parameter instanceof StringParameter)
				createStringParameterContent(parent, (StringParameter) parameter);
			else if (parameter instanceof ClusterParameter) 
				createClusterParameterContent(parent, (ClusterParameter) parameter);
			else if (parameter instanceof IProcessorParameter)
				createUnkownParameterContent(parent, parameter);
		}
	}

	@Override
	public boolean setFormInput(Object input) {
		if (input instanceof IAlgorithm) {
			update(((IAlgorithm) input).getParameters());
			return true;
		}
		return super.setFormInput(input);
	}

	public void update(Map<String, IProcessorParameter> parameters) {
		this.parameters = parameters;
		// Clean up
		bindingContext.dispose();
		getSection().getClient().dispose();
		// Create new ConfigurationProperties
		createClient(getSection(), toolkit);
		getSection().layout(true);
	}

	public CustomScale createNumericParameterContent(Composite parent, NumericParameter parameter) {
		toolkit.createLabel(parent, parameter.getName());

		CustomScale scale = new CustomScale(parent, SWT.NONE);
		scale.setMaximum(parameter.getMaximum());
		scale.setMinimum(parameter.getMinimum());
		scale.setSelection(parameter.getValue());
		scale.setLayoutData(new GridData(250, SWT.DEFAULT));
		toolkit.adapt(scale);

		IObservableValue valueObserveTextObserveWidget = SWTObservables.observeSelection(scale.getScale());
		IObservableValue scaleSelectionObserveValue = PojoObservables.observeValue(parameter, "value");
		bindingContext.bindValue(valueObserveTextObserveWidget, scaleSelectionObserveValue, null, null);

		return scale;
	}

	public Combo createStringParameterContent(Composite parent, StringParameter parameter) {
		toolkit.createLabel(parent, parameter.getName());

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.add(parameter.getValues());
		viewer.setSelection(new StructuredSelection(parameter.getValues()[0]));
		toolkit.adapt(combo);

		IObservableValue valueObserveTextObserveWidget = SWTObservables.observeSelection(combo);
		IObservableValue comboSelectionObserveValue = PojoObservables.observeValue(parameter, "value");
		bindingContext.bindValue(valueObserveTextObserveWidget, comboSelectionObserveValue, null, null);

		return combo;
	}

	public Combo createClusterParameterContent(Composite parent, ClusterParameter parameter) {
		toolkit.createLabel(parent, parameter.getName());

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.setLabelProvider(new ClusterUnitLabelProvider());
		viewer.add(parameter.getValues());
		viewer.setSelection(new StructuredSelection(parameter.getValue()));
		toolkit.adapt(combo);
		
		IObservableValue valueObserveTextObserveWidget = SWTObservables.observeSelection(combo);
		IObservableValue comboSelectionObserveValue = PojoObservables.observeValue(parameter, "value");
		bindingContext.bindValue(valueObserveTextObserveWidget, comboSelectionObserveValue, null, null);
		
		return combo;
	}

	public Composite createUnkownParameterContent(Composite parent, IProcessorParameter parameter) {
		toolkit.createLabel(parent, parameter.getName());

		Combo combo = new Combo(parent, SWT.READ_ONLY);
		ComboViewer viewer = new ComboViewer(combo);
		viewer.add(parameter.getValues());
		viewer.setSelection(new StructuredSelection(parameter.getValue()));
		toolkit.adapt(combo);
		
		IObservableValue valueObserveTextObserveWidget = SWTObservables.observeSelection(combo);
		IObservableValue comboSelectionObserveValue = PojoObservables.observeValue(parameter, "value");
		bindingContext.bindValue(valueObserveTextObserveWidget, comboSelectionObserveValue, null, null);
		
		return combo;
	}

	protected DataBindingContext initDataBindings() {
		return new DataBindingContext();
	}

}
