/**
 * IValidationPage.java created on 24.04.2008
 * 
 * Copyright (c) 2008-2009 Stefan Reichert
 * All rights reserved. 
 * 
 * This program and the accompanying materials are proprietary information 
 * of Stefan Reichert. Use is subject to license terms.
 */
package de.lmu.ifi.dbs.medmon.base.ui.wizard;

/**
 * Interface for a validatable page.
 * 
 * @author Stefan Reichert
 */
public interface IValidationPage {
	/**
	 * Checks the content and updates the <code>List</code> of errors.
	 */
	void checkContents();
}
