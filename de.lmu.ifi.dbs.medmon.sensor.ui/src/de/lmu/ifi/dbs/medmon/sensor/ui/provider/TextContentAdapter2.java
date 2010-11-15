package de.lmu.ifi.dbs.medmon.sensor.ui.provider;

import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.swt.widgets.Control;

public class TextContentAdapter2 extends TextContentAdapter {

	@Override
	public void insertControlContents(Control control, String text, int cursorPosition) {
		super.setControlContents(control, text, cursorPosition);
	}
	
	
}
