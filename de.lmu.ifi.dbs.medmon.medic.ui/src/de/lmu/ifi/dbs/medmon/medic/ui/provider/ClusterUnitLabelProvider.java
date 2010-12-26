package de.lmu.ifi.dbs.medmon.medic.ui.provider;

import org.eclipse.jface.viewers.LabelProvider;

import de.lmu.ifi.dbs.medmon.datamining.core.cluster.ClusterUnit;

public class ClusterUnitLabelProvider extends LabelProvider {
	
	@Override
	public String getText(Object element) {
		if(element instanceof ClusterUnit)
			return ((ClusterUnit)element).getName();
		return super.getText(element);
	}

}
