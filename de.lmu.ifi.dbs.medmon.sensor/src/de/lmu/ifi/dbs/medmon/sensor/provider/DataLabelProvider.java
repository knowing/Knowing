package de.lmu.ifi.dbs.medmon.sensor.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.sensor.data.ISensorDataContainer;


public class DataLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof Data)
			return getColumnText(element, 0);
		else if(element instanceof ISensorDataContainer)
			return ((ISensorDataContainer)element).getName();
		return element.toString();
	}
	
	@Override
	public Image getImage(Object element) {	
		if(element instanceof Data)
			return getColumnImage(element, 0);
		else if(element instanceof ISensorDataContainer)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		return null;
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		Data data = (Data)element;
		switch (columnIndex) {
		case 0: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		case 1: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		case 2: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		}

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		Data data = (Data)element;
		switch (columnIndex) {
		case 0: return date2String(data.getId().getRecord().getTime());
		case 1: return date2String(data.getId().getRecord().getTime());
		case 2: return "nicht verfügbar";
		}
		return element.toString();
	}
	
	private String date2String(Date date)  {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		if(date == null)
			return "";
		//return df.format(date);
		return date.toString();
	}

}
