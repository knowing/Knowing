package de.lmu.ifi.dbs.medmon.sensor.core.container;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

public class BlockDescriptor {

	public static final String NAME = "name";
	public static final String DESCRIPTION = "description";
	
	public static final String FILE = "file";
	public static final String ENTITY_MANAGER = "entitymanager";
	
	public static final String STARTDATE = "startdate";
	public static final String ENDDATE = "enddate";
	public static final String CALENDAR = "calendar";
	
	private Map<String, Object> attributes = new HashMap<String, Object>();

	public BlockDescriptor() {
		
	}
	
	public BlockDescriptor(String name) {
		setAttribute(NAME, name);
	}
		
	public BlockDescriptor(String file, Date startDate, Date endDate) {
		setAttribute(NAME, startDate);
		setAttribute(FILE, file);
		setAttribute(STARTDATE, startDate);
		setAttribute(ENDDATE, endDate);
	}
	
	public BlockDescriptor(EntityManager entityManager, Date startDate, Date endDate) {
		setAttribute(NAME, startDate);
		setAttribute(ENTITY_MANAGER, entityManager);
		setAttribute(STARTDATE, startDate);
		setAttribute(ENDDATE, endDate);
	}

	public Object setAttribute(String key, Object value) {
		return attributes.put(key, value);
	}
	
	public Object getAttribute(String key) {
		return attributes.get(key);
	}
	
	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public String getName() {
		return attributes.get(NAME).toString();
	}
	
	public void setName(String name) {
		setAttribute(NAME, name);
	}
	
	public Date getStartDate() {
		Object date = getAttribute(STARTDATE);
		if(date == null || !(date instanceof Date))
			return null;
		return (Date) date;
	}
	
	public void setStartDate(Date date) {
		setAttribute(STARTDATE, date);
	}
	
	public Date getEndDate() {
		Object date = getAttribute(ENDDATE);
		if(date == null || !(date instanceof Date))
			return null;
		return (Date) date;
	}
	
	
}
