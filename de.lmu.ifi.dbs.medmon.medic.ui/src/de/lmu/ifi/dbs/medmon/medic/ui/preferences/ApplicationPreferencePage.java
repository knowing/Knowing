package de.lmu.ifi.dbs.medmon.medic.ui.preferences;

import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import de.lmu.ifi.dbs.medmon.medic.core.preferences.IMedicPreferences;
import de.lmu.ifi.dbs.medmon.medic.core.util.ApplicationConfigurationUtil;

public class ApplicationPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Create the preference page.
	 */
	public ApplicationPreferencePage() {
		super(GRID);
	}

	/**
	 * Create contents of the preference page.
	 */
	@Override
	protected void createFieldEditors() {
		addField(new DirectoryFieldEditor(IMedicPreferences.DIR_MEDMON_ID, "Programmordner", getFieldEditorParent()));
	}
	
	@Override
	protected void checkState() {
		super.checkState();
		String defaultValue = getPreferenceStore().getDefaultString(IMedicPreferences.DIR_MEDMON_ID);
		String value = getPreferenceStore().getString(IMedicPreferences.DIR_MEDMON_ID);
		if(!defaultValue.equals(value)) {
			setErrorMessage("Sie duerfen das Programmverzeichnis nicht aendern");
			setValid(false);
		}
			
	}
	
	/**
	 * Initialize the preference page.
	 */
	public void init(IWorkbench workbench) {
		setPreferenceStore(ApplicationConfigurationUtil.getPreferenceStore());
		setDescription("Programmeinstellungen");
	}

}
