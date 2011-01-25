package de.lmu.ifi.dbs.medmon.base.ui.viewer.editing;

import javax.persistence.EntityManager;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;
import de.lmu.ifi.dbs.medmon.sensor.core.util.SensorAdapter;

public class SensorPathEditingSupport extends EditingSupport {


	public SensorPathEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new PathDialogCellEditor(((TableViewer)getViewer()).getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		String defaultPath = ((SensorAdapter)element).getDefaultPath();
		if(defaultPath == null)
			return "";
		return defaultPath;
	}

	@Override
	protected void setValue(Object element, Object value) {
		SensorAdapter adapter = (SensorAdapter)element;
		adapter.setDefaultPath((String) value);
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		em.merge(adapter.getSensorEntity());
		em.getTransaction().commit();
		em.close();
		getViewer().refresh();
		//TODO something changed!
	}

}
