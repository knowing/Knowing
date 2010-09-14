package de.lmu.ifi.dbs.medmon.database.sample;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import de.lmu.ifi.dbs.medmon.database.model.Patient;
import de.lmu.ifi.dbs.medmon.database.model.Sensor;
import de.lmu.ifi.dbs.medmon.database.model.SensorData;

public class SampleDataFactory {

	public static Patient[] getData() {
		Patient[] ps = new Patient[4];
		ps[0] = new Patient("Klara", "Fall");
		ps[0].setSensorData(getSensorData());
		ps[1] = new Patient("Kurt", "Sichtig");
		ps[1].setSensorData(getSensorData());
		ps[2] = new Patient("Olga", "Migram");
		ps[2].setSensorData(getSensorData());
		ps[3] = new Patient("Hans", "Dampf");
		ps[3].setSensorData(getSensorData());
		return ps;
	}
	
	public static Sensor getSensor() {
		Sensor sensor = new Sensor("1.0A");
		sensor.setData(getSensorData());
		return sensor;
	}
	
	public static Set<SensorData> getSensorData() {
		Set<SensorData> set = new HashSet<SensorData>();
		set.add(new SensorData(2, 0, 1,randomDate()));
		set.add(new SensorData(1, 1, 0,randomDate()));
		set.add(new SensorData(3, 0, 0,randomDate()));
		set.add(new SensorData(0, 2, 3,randomDate()));
		set.add(new SensorData(0, 2, 2,randomDate()));
		set.add(new SensorData(1, 4, 2,randomDate()));
		set.add(new SensorData(2, 1, 1,randomDate()));
		return set;
	}
	
	public static Date randomDate() {
		Double d = Math.random();
		if(d == 0.0)
			d = 1.0;
		
		int year = 2010;
		int month = (int) (((d * 100) % 11) + 1);
		int day = (int) (((d * 100) % 28) + 1);
		GregorianCalendar date = new GregorianCalendar(year, month, day);
		
		return date.getTime();
	}
}
