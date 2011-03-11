package de.lmu.ifi.dbs.medmon.datamining.core.processing.internal;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.DateAttribute;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.Instance;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.NominalAttribute;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.NumericAttribute;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorData;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataClass;
import de.lmu.ifi.dbs.medmon.datamining.core.annotations.SensorDataTimestamp;
import de.lmu.ifi.dbs.medmon.datamining.core.container.RawData;
import de.lmu.ifi.dbs.medmon.datamining.core.util.AnnotationUtil;

public class DataConverter {

	private static final Logger logger = Logger.getLogger(DataConverter.class.getName());

	public static RawData convert(Object[] data) {
		if (data == null || data[0] == null) {
			logger.severe("data == null OR data[0] == null");
			return null;
		}

		SensorDataClass dataClass = data[0].getClass().getAnnotation(SensorDataClass.class);
		if (dataClass == null) {
			logger.severe("DataClass == null");
			return null;
		}

		Field[] fields = data[0].getClass().getDeclaredFields();
		List<String> getters = new LinkedList<String>();
		String dateGetter = null;
		for (Field field : fields) {
			SensorData annotation = field.getAnnotation(SensorData.class);
			if (annotation != null) {
				String getter = AnnotationUtil.getGetterMethod(field.getName());
				getters.add(getter);
				continue;
			}

			SensorDataTimestamp dateAnnotation = field.getAnnotation(SensorDataTimestamp.class);
			if (dateAnnotation != null) {
				dateGetter = AnnotationUtil.getGetterMethod(field.getName());
			}
		}

		if (dataClass.dimension() != -1 && dataClass.dimension() != getters.size()) {
			logger.severe("Dimension of Class and SensorData fields doesn't match");
			return null;
		}

		RawData rawData = new RawData(getters.size());
		int dimension_index = 0;
		// Run through all getters
		for (String getter : getters) {
			double[] dimension = new double[data.length];
			int data_index = 0;
			// Get value from every single field in every data Object

			try {
				for (Object d : data) {
					Object getObject = d.getClass().getMethod(getter, null).invoke(d, null);
					dimension[data_index++] = (Double) getObject;
				}
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (ClassCastException e) {
				e.printStackTrace();
			}

			rawData.setDimension(dimension_index++, dimension);
		}

		if (dateGetter == null)
			return rawData;

		long[] timestamp = new long[data.length];
		int data_index = 0;
		for (Object d : data) {
			try {
				Object getObject = d.getClass().getMethod(dateGetter, null).invoke(d, null);
				timestamp[data_index++] = (Long) getObject;
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
		rawData.setTimestamp(timestamp);
		return rawData;

	}

	public static Instances convertInstance(Object[] data) throws Exception {
		if (data == null || data.length == 0) {
			logger.severe("data == null OR data[0] == null");
			return new Instances("EMPTY", new ArrayList<Attribute>(), 0);
		}
		Instances inst = getHeader(data[0]);
		for (Object each : data) {
			
		}
		return inst;
	}

	public static Instances getHeader(Object data) {
		Instance dataClass = data.getClass().getAnnotation(Instance.class);
		if (dataClass == null) {
			logger.severe("DataClass == null");
			return null;
		}

		Field[] fields = data.getClass().getDeclaredFields();
		ArrayList<Attribute> attr = new ArrayList<Attribute>();
		for (Field field : fields) {
			NumericAttribute numAn = field.getAnnotation(NumericAttribute.class);
			if (numAn != null) {
				attr.add(new Attribute(numAn.name(), numAn.index()));
				continue;
			}

			DateAttribute dateAn = field.getAnnotation(DateAttribute.class);
			if (dateAn != null) {
				attr.add(new Attribute(dateAn.name(), dateAn.dateFormat(), dateAn.index()));
				continue;
			}

			NominalAttribute nomAn = field.getAnnotation(NominalAttribute.class);
			if (nomAn != null) {
				String[] values = nomAn.attributeValues();
				attr.add(new Attribute(nomAn.name(), Arrays.asList(values), nomAn.index()));
			}

		}

		return new Instances(dataClass.name(), attr, 0);
	}
	
	public static weka.core.Instance getInstance(Object o, Instances dataset) {
		DenseInstance instance = new DenseInstance(dataset.numAttributes());
		
		instance.setDataset(dataset);
		return instance;
	}
	

}
