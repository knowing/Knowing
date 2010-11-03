package de.lmu.ifi.dbs.elki.gui.configurator;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.lmu.ifi.dbs.elki.gui.util.DynamicParameters;
import de.lmu.ifi.dbs.elki.logging.LoggingUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.TrackParameters;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Parameter;

public class ClassParameterConfigurator extends AbstractSingleParameterConfigurator<ClassParameter<?>> implements ActionListener, ChangeListener {
  final JComboBox value;

  final ConfiguratorPanel child;

  public ClassParameterConfigurator(ClassParameter<?> cp, JComponent parent) {
    super(cp, parent);
    // Input field
    {
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 1.0;
      value = new JComboBox();
      value.setToolTipText(param.getShortDescription());
      value.setPrototypeDisplayValue(cp.getRestrictionClass().getSimpleName());
      parent.add(value, constraints);
      finishGridRow();
    }

    if(!param.tookDefaultValue() && param.isDefined() && param.getGivenValue() != null) {
      value.addItem(param.getValueAsString());
      value.setSelectedIndex(0);
    }

    // For parameters with a default value, offer using the default
    // For optional parameters, offer not specifying them.
    if(cp.hasDefaultValue()) {
      value.addItem(DynamicParameters.STRING_USE_DEFAULT);
    }
    else if(cp.isOptional()) {
      value.addItem(DynamicParameters.STRING_OPTIONAL);
    }
    // Offer the shorthand version of class names.
    for(Class<?> impl : cp.getKnownImplementations()) {
      value.addItem(ClassParameter.canonicalClassName(impl, cp.getRestrictionClass()));
    }
    // Child options
    {
      GridBagConstraints constraints = new GridBagConstraints();
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      constraints.fill = GridBagConstraints.HORIZONTAL;
      constraints.weightx = 1.0;
      constraints.insets = new Insets(0, 10, 0, 0);
      child = new ConfiguratorPanel();
      child.addChangeListener(this);
      parent.add(child, constraints);
    }
    value.addActionListener(this);
  }

  @Override
  public void addParameter(Object owner, Parameter<?, ?> param, TrackParameters track) {
    child.addParameter(owner, param, track);
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if(e.getSource() == value) {
      fireValueChanged();
    }
    else {
      LoggingUtil.warning("actionPerformed triggered by unknown source: " + e.getSource());
    }
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    if(e.getSource() == child) {
      fireValueChanged();
    }
    else {
      LoggingUtil.warning("stateChanged triggered by unknown source: " + e.getSource());
    }
  }

  @Override
  public String getUserInput() {
    String val = (String) value.getSelectedItem();
    if(val == DynamicParameters.STRING_USE_DEFAULT) {
      return null;
    }
    if(val == DynamicParameters.STRING_OPTIONAL) {
      return null;
    }
    return val;
  }

  @Override
  public void appendParameters(ListParameterization params) {
    super.appendParameters(params);
    child.appendParameters(params);
  }
}