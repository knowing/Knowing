package de.lmu.ifi.dbs.medmon.database.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="SENSOR")
public class Sensor {
	
	@Id
	private String id;
	
	@Column
	private String name;
	
	@Column
	private String version;
	
	@Column
	private int type;
	
	@Column
	private String defaultpath;
	
	@OneToMany(mappedBy="sensor")
	private Set<Data> data;
	
	protected Sensor() {
	}
	
	public Sensor(String name, String version, int type) {
		this.id = parseId(name, version);
		this.name = name;
		this.version = version;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	protected void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public String getDefaultpath() {
		return defaultpath;
	}

	public void setDefaultpath(String defaultpath) {
		this.defaultpath = defaultpath;
	}

	public Set<Data> getData() {
		return data;
	}

	public void setData(Set<Data> data) {
		this.data = data;
	}
	
	public static String parseId(String name, String version) {
		return name + ":" + version;
	}

	
	

}
