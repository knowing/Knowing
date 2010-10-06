package de.lmu.ifi.dbs.medmon.database.sample;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.EntityManager;
import org.apache.derby.catalog.GetProcedureColumns;

import de.lmu.ifi.dbs.medmon.database.model.DataPK;
import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.database.util.JPAUtil;

public class SampleDataFactory {

	public static Patient[] getData() {
		Patient[] ps = new Patient[4];
		ps[0] = new Patient("Klara", "Fall");
		ps[0].setData(getSensorData());
		ps[1] = new Patient("Kurt", "Sichtig");
		ps[1].setData(getSensorData());
		ps[2] = new Patient("Olga", "Migram");
		ps[2].setData(getSensorData());
		ps[3] = new Patient("Hans", "Dampf");
		ps[3].setData(getSensorData());
		return ps;
	}

	public static Set<Data> getSensorData() {
		Set<Data> set = new HashSet<Data>();
		set.add(new Data(new DataPK(1, randomDate()),2, 0, 1));
		set.add(new Data(new DataPK(1, randomDate()),1, 1, 0));
		set.add(new Data(new DataPK(1, randomDate()),3, 0, 0));
		set.add(new Data(new DataPK(1, randomDate()),0, 2, 3));
		set.add(new Data(new DataPK(1, randomDate()),0, 2, 2));
		set.add(new Data(new DataPK(1, randomDate()),1, 4, 2));
		set.add(new Data(new DataPK(1, randomDate()),2, 1, 1));
		return set;
	}

	public static Data[] getSensorDataArray() {
		Set<Data> data = getSensorData();
		return data.toArray(new Data[data.size()]);
	}

	public static Data[] getSensorDataArray(Timestamp date) {
		Set<Data> set = new HashSet<Data>();
		set.add(new Data(new DataPK(1, randomDate()),2, 0, 1));
		set.add(new Data(new DataPK(1, randomDate()),1, 1, 0));
		set.add(new Data(new DataPK(1, randomDate()),3, 0, 0));
		set.add(new Data(new DataPK(1, randomDate()),0, 2, 3));
		set.add(new Data(new DataPK(1, randomDate()),0, 2, 2));
		set.add(new Data(new DataPK(1, randomDate()),1, 4, 2));
		set.add(new Data(new DataPK(1, randomDate()),2, 1, 1));
		return set.toArray(new Data[set.size()]);
	}

	public static Calendar randomDate() {
		Double d = Math.random();
		if (d == 0.0)
			d = 1.0;

		int year = 2010;
		int month = (int) (((d * 100) % 11) + 1);
		int day = (int) (((d * 100) % 28) + 1);

		return new GregorianCalendar(year, month, day);
	}

	public static void addPatientsToDB() {
		EntityManager em = JPAUtil.currentEntityManager();
		Patient[] patients = new Patient[4];
		patients[0] = new Patient("Klara", "Fall");
		patients[1] = new Patient("Kurt", "Sichtig");
		patients[2] = new Patient("Olga", "Migram");
		patients[3] = new Patient("Hans", "Dampf");
		em.getTransaction().begin();
		for (Patient each : patients)
			em.persist(each);
		em.getTransaction().commit();
	}
}
