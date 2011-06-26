/**
 * 
 */
package de.lmu.ifi.dbs.knowing.ui.wizard.pages;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 25.06.2011
 *
 */
public class DPUPage extends WizardPage {

	/**
	 * Create the wizard.
	 */
	public DPUPage() {
		super("Create DPU Wizard");
		setTitle("Create DPU Wizard");
		setDescription("Create your Data Processing Unit");
	}

	/**
	 * Create contents of the wizard.
	 * @param parent
	 */
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);

		setControl(container);
	}

}
