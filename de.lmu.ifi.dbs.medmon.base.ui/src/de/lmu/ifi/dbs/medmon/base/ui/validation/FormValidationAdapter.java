/**
 * FormValidationAdapter.java created on 28.03.2008
 * 
 * Copyright (c) 2008-2009 Stefan Reichert
 * All rights reserved. 
 * 
 * This program and the accompanying materials are proprietary information 
 * of Stefan Reichert. Use is subject to license terms.
 */
package de.lmu.ifi.dbs.medmon.base.ui.validation;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessage;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormText;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import de.lmu.ifi.dbs.medmon.base.ui.Activator;

/**
 * Adapter for a <code>ScrolledForm</code> that provides a validation and
 * message display. <br>
 * -----------------------------------------------------------------------------<br>
 * This code is basicly adapted from the corresponding snipplet provided by IBM.<br>
 * -----------------------------------------------------------------------------<br>
 * <br>
 * 
 * @author Stefan Reichert
 */
public class FormValidationAdapter {

	/** The <code>ManagedForm</code> that handles the validation. */
	private IManagedForm managedForm;

	/** The <code>IMessageManager</code> that handles the validation. */
	private IMessageManager messageManager;

	/** The factory for specific <code>IValidator</code>s. */
	private ValidatorFactory validatorFactory;

	private UpdateValueStrategyFactory updateValueStrategyFactory;

	/** The current error shell. */
	private Shell currentErrorShell;

	private ScrolledForm scrolledForm;

	/**
	 * Adapts the given <code>ScrolledForm</code> with the given
	 * <code>FormToolkit</code>.
	 * 
	 * @param formToolkit
	 *            The <code>FormToolkit</code> to use
	 * @param scrolledForm
	 *            The <code>ScrolledForm</code> to adapt
	 * @return The adapter
	 */
	public static FormValidationAdapter adapt(FormToolkit formToolkit,
			ScrolledForm scrolledForm) {
		FormValidationAdapter formValidationAdapter = new FormValidationAdapter(
				formToolkit, scrolledForm);
		return formValidationAdapter;
	}

	/**
	 * Constructor for <class>FormValidationAdapter</class>.
	 */
	private FormValidationAdapter(final FormToolkit formToolkit,
			final ScrolledForm scrolledForm) {
		this.scrolledForm = scrolledForm;
		managedForm = new ManagedForm(formToolkit, scrolledForm);
		messageManager = managedForm.getMessageManager();
		scrolledForm.getForm().addMessageHyperlinkListener(
				new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent event) {
						if (currentErrorShell != null) {
							currentErrorShell.dispose();
							currentErrorShell = null;
						}
						String title = event.getLabel();
						Object href = event.getHref();
						Point hl = ((Control) event.widget).toDisplay(0, 0);
						hl.x += 10;
						hl.y += 10;
						currentErrorShell = new Shell(scrolledForm.getForm()
								.getShell(), SWT.ON_TOP | SWT.TOOL);
						currentErrorShell.setImage(getImage(scrolledForm
								.getForm().getMessageType()));
						currentErrorShell.setText(title);
						FillLayout fillLayout = new FillLayout();
						fillLayout.marginHeight = 4;
						fillLayout.marginWidth = 1;
						currentErrorShell.setLayout(fillLayout);
						//currentErrorShell.setBackground(ResourceManager.getColor(SWT.COLOR_WHITE));
						FormText text = formToolkit.createFormText(
								currentErrorShell, true);
						configureFormText(scrolledForm.getForm(), text);
						if (href instanceof IMessage[])
							text.setText(
									createErrorShellContent((IMessage[]) href),
									true, false);
						currentErrorShell.setLocation(hl);
						currentErrorShell.pack();
						currentErrorShell.open();
					}
				});
		// we have to ensure that our error shell is disposed when the parent
		// form is diposed
		scrolledForm.addDisposeListener(new DisposeListener() {
			/**
			 * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
			 */
			public void widgetDisposed(DisposeEvent event) {
				if (currentErrorShell != null
						&& !currentErrorShell.isDisposed()) {
					currentErrorShell.dispose();
					currentErrorShell = null;
				}
			}
		});
	}

	/**
	 * Configures the <code>FormText</code> to display the errors.
	 * 
	 * @param form
	 *            The parent <code>Form</code>
	 * @param formText
	 *            The <code>FormText</code> to be configured
	 */
	private void configureFormText(final Form form, FormText formText) {
		formText.addHyperlinkListener(new HyperlinkAdapter() {
			public void linkActivated(HyperlinkEvent e) {
				String is = (String) e.getHref();
				try {
					int index = Integer.parseInt(is);
					IMessage[] messages = form.getChildrenMessages();
					IMessage message = messages[index];
					Control c = message.getControl();
					((FormText) e.widget).getShell().dispose();
					if (c != null && !c.isDisposed())
						c.setFocus();
				}
				catch (NumberFormatException ex) {
				}
			}
		});
		formText.setImage("error", getImage(IMessageProvider.ERROR)); //$NON-NLS-1$
		formText.setImage("warning", getImage(IMessageProvider.WARNING)); //$NON-NLS-1$
		formText.setImage("info", getImage(IMessageProvider.INFORMATION)); //$NON-NLS-1$
	}

	/**
	 * Creates the content for the error shell.
	 * 
	 * @param messages
	 *            The messages to be displayed
	 * @return the messages as <code>String</code>
	 */
	private String createErrorShellContent(IMessage[] messages) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		pw.println("<form>"); //$NON-NLS-1$
		for (int i = 0; i < messages.length; i++) {
			IMessage message = messages[i];
			pw
					.print("<li vspace=\"false\" style=\"image\" indent=\"16\" value=\""); //$NON-NLS-1$
			switch (message.getMessageType()) {
				case IMessageProvider.ERROR:
					pw.print("error"); //$NON-NLS-1$
					break;
				case IMessageProvider.WARNING:
					pw.print("warning"); //$NON-NLS-1$
					break;
				case IMessageProvider.INFORMATION:
					pw.print("info"); //$NON-NLS-1$
					break;
			}
			pw.print("\"> <a href=\""); //$NON-NLS-1$
			pw.print(i + ""); //$NON-NLS-1$
			pw.print("\">"); //$NON-NLS-1$
			if (message.getPrefix() != null)
				pw.print(message.getPrefix());
			pw.print(message.getMessage());
			pw.println("</a></li>"); //$NON-NLS-1$
		}
		pw.println("</form>"); //$NON-NLS-1$
		pw.flush();
		return sw.toString();
	}

	/**
	 * Returns the <code>Image</code> for the given message type.
	 * 
	 * @param type
	 *            The given message typ
	 * @return the <code>Image</code>
	 */
	private Image getImage(int type) {
		switch (type) {
			case IMessageProvider.ERROR:
				return null; //$NON-NLS-1$
			case IMessageProvider.WARNING:
				return null; //$NON-NLS-1$
			case IMessageProvider.INFORMATION:
				return null; //$NON-NLS-1$
		}
		return null;
	}

	/**
	 * @return the <code>ValidatorFactory</code> to get
	 */
	public ValidatorFactory getValidatorFactory() {
		if (validatorFactory == null) {
			validatorFactory = new ValidatorFactory();
		}
		return validatorFactory;
	}
	
	/**
	 * Returns whether there are currently eerors listed
	 */
	public boolean containsErrors(){
		return scrolledForm.getMessageType() == IMessageProvider.ERROR;
	}

	/**
	 * @return the <code>UpdateValueStrategyFactory</code> to get
	 */
	public UpdateValueStrategyFactory getUpdateValueStrategyFactory() {
		if (updateValueStrategyFactory == null) {
			updateValueStrategyFactory = new UpdateValueStrategyFactory();
		}
		return updateValueStrategyFactory;
	}

	/**
	 * Factory for common <code>UpdateValueStrategy</code>
	 * 
	 * @author Stefan Reichert
	 */
	public class UpdateValueStrategyFactory {

		/**
		 * Constructor for <class>UpdateValueStrategyFactory</class>.
		 */
		private UpdateValueStrategyFactory() {
			// private constructor to avoid instantiation
		}

		/**
		 * An <code>UpdateValueStrategy</code> that ensures a not null / not
		 * empty value.
		 * 
		 * @param validatorId
		 *            The unique ID
		 * @param control
		 *            The hosting control
		 * @return the <code>UpdateValueStrategy</code> that ensures a not
		 *         null / not empty value
		 */
		public UpdateValueStrategy getUpdateValueStrategy(
				final Object validatorId, final Control control,
				int validatorBits) {
			UpdateValueStrategy updateValueStrategy = new UpdateValueStrategy();
			updateValueStrategy.setAfterConvertValidator(getValidatorFactory()
					.getMandatoryValidator(validatorId, control));
			return updateValueStrategy;
		}
	}

	/**
	 * Factory for common <code>IValidators</code>
	 * 
	 * @author Stefan Reichert
	 */
	public class ValidatorFactory {

		/** Constant for <i>mandatory</i> validator. */
		public static final int MANDATORY_VALIDATOR = 1 << 1;

		/**
		 * Constructor for <class>ValidatorFactory</class>.
		 */
		private ValidatorFactory() {
			// private constructor to avoid instantiation
		}

		/**
		 * A validator that ensures a not null / not empty value.
		 * 
		 * @param validatorId
		 *            The unique ID
		 * @param control
		 *            The hosting control
		 * @return the validator that ensures a not null / not empty value
		 */
		public IValidator getMandatoryValidator(final Object validatorId,
				final Control control) {
			return new IValidator() {
				/**
				 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
				 */
				public IStatus validate(Object value) {
					boolean valid = false;
					if (value instanceof String) {
						String valueString = (String) value;
						valid = (value != null) && (valueString.trim().length() > 0);
					}
					else {
						valid = value != null;
					}
					if (valid) {
						messageManager.removeMessage(validatorId, control);
						return Status.OK_STATUS;
					}
					else {
						messageManager.addMessage(validatorId, "Message", null, //$NON-NLS-1$
								IMessageProvider.ERROR, control);
						return new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Field"); //$NON-NLS-1$
					}
				}
			};
		}

		/**
		 * A validator that ensures a not null / not empty value.
		 * 
		 * @param validatorId
		 *            The unique ID
		 * @param control
		 *            The hosting control
		 * @return the validator that ensures a not null / not empty value
		 */
		public IValidator getValidator(final Object validatorId,
				final Control control, int validatorBits) {
			final List<IValidator> validators = new ArrayList<IValidator>();
			if ((validatorBits | MANDATORY_VALIDATOR) == 0) {
				validators.add(getMandatoryValidator(validatorId, control));
			}
			return new IValidator() {
				/**
				 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
				 */
				public IStatus validate(Object value) {
					MultiStatus multiStatus = new MultiStatus(
							Activator.PLUGIN_ID, SWT.OK, "Error Message", //$NON-NLS-1$
							null);
					for (IValidator validator : validators) {
						multiStatus.add(validator.validate(value));
					}
					if (multiStatus.isOK()) {
						messageManager.removeMessage(validatorId, control);
					}
					else {
						messageManager
								.addMessage(
										validatorId,
										"Error Message", //$NON-NLS-1$
										null, IMessageProvider.ERROR, control);
					}
					return multiStatus;
				}
			};
		}

		/**
		 * Adapts an existing validator to display the result.
		 * 
		 * @param validator
		 *            The validator to adapt
		 * @param validatorId
		 *            The unique ID
		 * @param control
		 *            The hosting control
		 * @param message
		 *            The message to display
		 * @return the adapted validator
		 */
		public IValidator adapt(final IValidator validator,
				final Object validatorId, final Control control,
				final String message) {
			return new IValidator() {
				/**
				 * @see org.eclipse.core.databinding.validation.IValidator#validate(java.lang.Object)
				 */
				public IStatus validate(Object value) {
					IStatus status = validator.validate(value);
					if (status.isOK()) {
						messageManager.removeMessage(validatorId, control);
					}
					else {
						messageManager.addMessage(validatorId, message, null,
								IMessageProvider.ERROR, control);
					}
					return status;
				}
			};
		}
	}
}
