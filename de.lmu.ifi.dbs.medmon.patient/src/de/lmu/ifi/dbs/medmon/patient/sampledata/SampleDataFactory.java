package de.lmu.ifi.dbs.medmon.patient.sampledata;

import java.util.HashSet;
import java.util.Set;

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
		set.add(new SensorData(2, 0, 1));
		set.add(new SensorData(1, 1, 0));
		set.add(new SensorData(3, 0, 0));
		set.add(new SensorData(0, 2, 3));
		set.add(new SensorData(0, 2, 2));
		set.add(new SensorData(1, 4, 2));
		set.add(new SensorData(2, 1, 1));
		return set;
	}
}
