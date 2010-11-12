package de.lmu.ifi.dbs.medmon.algorithm.ui.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.IAlgorithm;

/**
 * Simple LabelProvider for ListViewer and TableViewer.
 * TableViewer supports upto three columns: Name | Description | Version
 * 
 * 
 * @author muki
 * @version 1.0
 */
public class AlgorithmLabelProvider extends LabelProvider implements
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
		if(element instanceof IAlgorithm) {
			IAlgorithm alg = (IAlgorithm)element;
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}			
		return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_WARN_TSK);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if(element instanceof IAlgorithm) {
			IAlgorithm alg = (IAlgorithm)element;
			switch (columnIndex) {
			case 0: return alg.getName();
			case 1: return alg.getDescription();
			case 2: return String.valueOf(alg.getVersion());
			}
		}			
		return "[wrong input]";
	}

}
