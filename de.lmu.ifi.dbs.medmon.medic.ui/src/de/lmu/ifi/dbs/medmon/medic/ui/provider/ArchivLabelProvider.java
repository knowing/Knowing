package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import java.text.DateFormat;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import de.lmu.ifi.dbs.medmon.database.model.Archiv;

public class ArchivLabelProvider extends LabelProvider implements ITableLabelProvider {

	private static final DateFormat df = DateFormat.getDateInstance(DateFormat.SHORT);

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		Archiv archiv = (Archiv) element;
		if (columnIndex == 2) {
			String path = archiv.getPath();
			if (path != null && !path.isEmpty())
				return null;
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Archiv archiv = (Archiv) element;
		switch (columnIndex) {
		case 0:
			return df.format(archiv.getTimestamp());
		case 1:
			return archiv.getTitle();
		default:
			return "";

		}
	}

}
