package de.lmu.ifi.dbs.medmon.base.ui.viewer.editing;

import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;

public class PathDialogCellEditor extends DialogCellEditor {
	
	public PathDialogCellEditor(Composite parent) {
		super(parent);
	}
	
	@Override
	protected Object openDialogBox(Control cellEditorWindow) {
		DirectoryDialog dialog = new DirectoryDialog(cellEditorWindow.getShell());
		return dialog.open();
	}
	

}
