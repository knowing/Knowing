package knowing.test.factory;

import java.util.Properties;

import weka.core.converters.CSVLoader;
import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.SynchronizedLoader;

public class CSVLoaderFactory implements ILoaderFactory {

	public static final String ID = "de.lmu.ifi.dbs.medmon.datamining.core.weka.CSVLoader";
	
	/* == Properties ==*/
	public static final String SEPARATOR = "separator";
	public static final String RELATIVE_PATH = "relative";
	public static final String STRING_ATTRIBUTE = "string_attribute";
	public static final String NOMINAL_ATTRIBUTE = "nominal_attribute";
	public static final String MISSING_VALUE_PLACEHOLDER = "placeholder";

	private Properties properties;
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "CSV Loader";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public ILoader getInstance(Properties properties) {
		CSVLoader loader = new CSVLoader();
		if(properties == null || properties.isEmpty())
			return new SynchronizedLoader(loader);
		
		String sep = properties.getProperty(SEPARATOR, loader.getFieldSeparator());
		String rel = properties.getProperty(RELATIVE_PATH, String.valueOf(loader.getUseRelativePath()));
		String str = properties.getProperty(STRING_ATTRIBUTE, loader.getStringAttributes());
		String nom = properties.getProperty(NOMINAL_ATTRIBUTE, loader.getNominalAttributes());
		String mis = properties.getProperty(MISSING_VALUE_PLACEHOLDER, loader.getMissingValue());
		
		loader.setFieldSeparator(sep);
		loader.setUseRelativePath(Boolean.valueOf(rel));
		loader.setStringAttributes(str);
		loader.setNominalAttributes(nom);
		loader.setMissingValue(mis);
		return new SynchronizedLoader(loader);
	}

}
