package de.lmu.ifi.dbs.medmon.sensor.provider;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.database.model.SensorData;


public class SensorLabelProvider extends LabelProvider implements
		ITableLabelProvider {

	@Override
	public String getText(Object element) {
		return getColumnText(element, 0);
	}
	
	@Override
	public Image getImage(Object element) {	
		return getColumnImage(element, 0);
	}
	
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		SensorData data = (SensorData)element;
		switch (columnIndex) {
		case 0: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_FILE);
		case 1: return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		case 2: return data.isAnalyzed() ? 
					PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ELCL_SYNCED) : 
					PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
		}

		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		SensorData data = (SensorData)element;
		switch (columnIndex) {
		case 0: return date2String(data.getRecorded());
		case 1: return date2String(data.getTimestamp());
		case 2: return data.isAnalyzed() ? date2String(data.getAnalyzedDate()) : "nein";
		}
		return element.toString();
	}
	
	private String date2String(Date date)  {
		SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd");
		if(date == null)
			return "";
		return df.format(date);
	}

}
