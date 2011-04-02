/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.swt.provider;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import weka.core.Attribute;
import weka.core.Instance;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public class InstanceLabelProvider extends LabelProvider implements ITableLabelProvider {

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage(java.lang.Object, int)
	 */
	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText(java.lang.Object, int)
	 */
	@Override
	public String getColumnText(Object element, int columnIndex) {
		Instance inst = (Instance) element;
		Attribute attribute = inst.attribute(columnIndex);
		double value = inst.value(columnIndex);
		if(attribute.isDate())
			return attribute.formatDate(value);
		if(attribute.isNumeric())
			return String.valueOf(value);
		return inst.stringValue(columnIndex);
	}

}
