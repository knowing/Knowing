package de.lmu.ifi.dbs.medmon.base.ui.widgets;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;

public class DBSourceWidget extends Composite {
	private Text tURL;
	private Text tUser;
	private Text tPassword;
	private Text tDatabase;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public DBSourceWidget(Composite parent, int style) {
		super(parent, style);
		setLayout(new GridLayout(2, false));
		
		Label lURL = new Label(this, SWT.NONE);
		lURL.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lURL.setText("URL");
		
		tURL = new Text(this, SWT.BORDER);
		tURL.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lDatabase = new Label(this, SWT.NONE);
		lDatabase.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lDatabase.setText("Database");
		
		tDatabase = new Text(this, SWT.BORDER);
		tDatabase.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblUser = new Label(this, SWT.NONE);
		lblUser.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblUser.setText("User");
		
		tUser = new Text(this, SWT.BORDER);
		tUser.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		Label lblPassword = new Label(this, SWT.NONE);
		lblPassword.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPassword.setText("Password");
		
		tPassword = new Text(this, SWT.BORDER | SWT.PASSWORD);
		tPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(this, SWT.NONE);
		
		Button bConnect = new Button(this, SWT.NONE);
		bConnect.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bConnect.setText("verbinden");

	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
