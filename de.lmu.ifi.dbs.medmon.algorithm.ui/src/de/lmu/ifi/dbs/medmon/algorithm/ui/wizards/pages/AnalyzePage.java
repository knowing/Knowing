package de.lmu.ifi.dbs.medmon.algorithm.ui.wizards.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import de.lmu.ifi.dbs.medmon.algorithm.ui.editor.AlgorithmEditorBlock;
import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;

public class AnalyzePage extends WizardPage {

	private IAlgorithm algorithm;

	private ManagedForm m_managedForm;

	public AnalyzePage() {
		super("Analyse");
		setDescription("Konfigurieren Sie den Algorithmus zum Visualisieren der Daten");
		setTitle("Analyse und Visualisierung");
	}

	@Override
	public void createControl(final Composite parent) {
		m_managedForm = createManagedForm(parent);
		m_managedForm.getForm().getBody().setLayout(new FillLayout());

		parent.setBackground(m_managedForm.getToolkit().getColors()
				.getBackground());
		ScrolledForm scrolledForm = m_managedForm.getForm();
		setControl(scrolledForm);

		initialize(m_managedForm);
	}

	public void initialize(ManagedForm managedForm) {
		AlgorithmEditorBlock block = new AlgorithmEditorBlock();
		block.createContent(managedForm);
	}

	protected ManagedForm createManagedForm(final Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
		managedForm.setContainer(this);
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.verticalAlignment = SWT.FILL;
		managedForm.getForm().setLayoutData(gridData);
		return managedForm;
	}

	@Override
	public void dispose() {
		if (m_managedForm != null) {
			m_managedForm.dispose();
			m_managedForm = null;
		}
		super.dispose();
	}

	public boolean setFocus() {
		return m_managedForm.getForm().setFocus();
	}

	/**
	 * @return Returns the managedForm.
	 */
	protected ManagedForm getManagedForm() {
		return m_managedForm;
	}

	public IAlgorithm getAlgorithm() {
		return algorithm;
	}

}
