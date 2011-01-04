package de.lmu.ifi.dbs.medmon.base.ui.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.crypto.Data;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.sensor.core.container.ISensorDataContainer;


public class SensorContainerLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public String getText(Object element) {
		if(element instanceof ISensorDataContainer)
			return ((ISensorDataContainer)element).getName();
		return element.toString();
	}
	
	@Override
	public Image getImage(Object element) {	
		if(element instanceof ISensorDataContainer)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FOLDER);
		return getColumnImage(element, 0);
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		case 1: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		case 2: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		}

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		switch (columnIndex) {
		case 0: return "N/A";
		case 1: return "N/A";
		case 2: return "N/A";
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
