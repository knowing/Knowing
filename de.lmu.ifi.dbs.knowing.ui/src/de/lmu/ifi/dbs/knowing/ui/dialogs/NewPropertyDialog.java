package de.lmu.ifi.dbs.knowing.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import de.lmu.ifi.dbs.knowing.core.graph.xml.*;

public class NewPropertyDialog extends Dialog {
	private Text tKey;
	private Text tValue;
	
	private Property property;
	/**
	 * Create the dialog.
	 * 
	 * @param parentShell
	 */
	public NewPropertyDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * Create contents of the dialog.
	 * 
	 * @param parent
	 */
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		container.setLayout(new GridLayout(2, false));

		Label lKey = new Label(container, SWT.NONE);
		lKey.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lKey.setText("Key");

		tKey = new Text(container, SWT.BORDER);
		tKey.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lValue = new Label(container, SWT.NONE);
		lValue.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lValue.setText("Value");

		tValue = new Text(container, SWT.BORDER);
		tValue.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		return container;
	}

	/**
	 * Create contents of the button bar.
	 * 
	 * @param parent
	 */
	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
		createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
	}

	/**
	 * Return the initial size of the dialog.
	 */
	@Override
	protected Point getInitialSize() {
		return new Point(350, 150);
	}
	
	@Override
	protected void okPressed() {
		property = new Property(tKey.getText(), tValue.getText());
		super.okPressed();
	}
	
	public Property getProperty() {
		return property;
	}

}
