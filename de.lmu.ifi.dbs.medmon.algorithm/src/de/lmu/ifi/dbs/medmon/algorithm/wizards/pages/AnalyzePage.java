package de.lmu.ifi.dbs.medmon.algorithm.wizards.pages;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;

import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmContentProvider;
import de.lmu.ifi.dbs.medmon.algorithm.provider.AlgorithmLabelProvider;
import de.lmu.ifi.dbs.medmon.algorithm.provider.ISensorDataAlgorithm;

public class AnalyzePage extends WizardPage {

	private Composite container;
	
	private ISensorDataAlgorithm algorithm;
	
	public AnalyzePage() {
		super("Analyse");
		setDescription("Konfigurieren Sie den Algorithmus zum Visualisieren der Daten");
		setTitle("Analyse und Visualisierung");
	}

	@Override
	public void createControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, true);
		container.setLayout(layout);
		
		/* Algorithm Table*/
		Group viewGroup = new Group(container, SWT.SHADOW_ETCHED_IN);
		viewGroup.setText("Algorithmus");
		viewGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		viewGroup.setLayout(new FillLayout());
		TableViewer viewer = new TableViewer(viewGroup, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);
		viewer.setContentProvider(new AlgorithmContentProvider());
		viewer.setLabelProvider(new AlgorithmLabelProvider());
		viewer.setInput(getContainer());
		//viewer.getTable().setLayoutData(new GridData(GridData.FILL_BOTH));
		
		/* Algorithm Properties*/
		Group props = new Group(container, SWT.SHADOW_ETCHED_IN);
		props.setText("Eigenschaften");
		props.setLayout(new GridLayout(2, false));
		props.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		new Label(props, SWT.NONE).setText("Toleranz");
		
		Scale scale = new Scale(props, SWT.NONE);
		scale.setMaximum(100);
		scale.setMaximum(0);
		scale.setSelection(50);
		scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		new Label(props, SWT.NONE).setText("Darstellung ");
		Combo combo = new Combo(props, SWT.READ_ONLY);
		combo.add("Pie Chart");
		combo.add("Bar Chart");
		combo.select(0);
		
		setControl(container);
		setPageComplete(true);
	}

	public ISensorDataAlgorithm getAlgorithm() {
		return algorithm;
	}

}
