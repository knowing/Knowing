package de.lmu.ifi.dbs.medmon.base.ui.viewer.editing;

import javax.persistence.EntityManager;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Table;

import de.lmu.ifi.dbs.medmon.base.ui.adapter.PatientClusterAdapter;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class DefaultClusterEditingSupport extends EditingSupport {

	public DefaultClusterEditingSupport(TableViewer viewer) {
		super(viewer);
	}

	@Override
	protected CellEditor getCellEditor(Object element) {	
		return new CheckboxCellEditor((Table)getViewer().getControl());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		PatientClusterAdapter adapter = (PatientClusterAdapter) element;
		return adapter.isDefault();
	}

	@Override
	protected void setValue(Object element, Object value) {
		PatientClusterAdapter adapter = (PatientClusterAdapter) element;
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		Patient patient = adapter.getPatient();
		em.merge(patient);
		patient.setCluster(adapter.getCluster().getName());
		em.getTransaction().commit();
		em.close();
		validate(patient, adapter.getCluster().getName());
		getViewer().refresh();
	}
	
	private void validate(Patient patient, String cluster) {
		EntityManager em = JPAUtil.createEntityManager();
		em.getTransaction().begin();
		Patient entity = em.find(Patient.class, patient.getId());
		if(!cluster.equals(entity.getCluster())) {
			entity.setCluster(cluster);
		}
		em.getTransaction().commit();
		em.close();
	}

}
