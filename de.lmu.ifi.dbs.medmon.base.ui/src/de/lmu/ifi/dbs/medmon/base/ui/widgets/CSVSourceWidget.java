package de.lmu.ifi.dbs.medmon.base.ui.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import de.lmu.ifi.dbs.medmon.datamining.core.csv.io.CSVDescriptor;
import de.lmu.ifi.dbs.medmon.datamining.core.csv.widget.CSVConfiguration;

public class CSVSourceWidget extends Composite {

	private CSVConfiguration configuration;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public CSVSourceWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));

		Group group = new Group(this, SWT.NONE);
		group.setLayout(new FillLayout());
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		configuration = new CSVConfiguration(group, SWT.NONE);

	}

	public CSVDescriptor getDescriptor() {
		return configuration.getDescriptor();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
