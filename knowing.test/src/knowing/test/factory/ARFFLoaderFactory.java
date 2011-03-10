package knowing.test.factory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import weka.core.converters.ArffLoader;
import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.SynchronizedLoader;

public class ARFFLoaderFactory implements ILoaderFactory {

	public static final String ID = "ARFFLoaderFactory";
	public static final String URL = "input.url";
	public static final String FILE = "input.file";
	
	private Properties properties = new Properties();
	
	public ARFFLoaderFactory() {
		properties.setProperty(URL, "http://dtai.cs.kuleuven.be/DataMiningInPractice11/DATASET/iris.arff");
		properties.setProperty(FILE, "/home/muki/iris.arff");
		//properties.setProperty(FILE, "/home/muki/result.arff");
	}
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "ARFF Loader";
	}

	@Override
	public Properties getDefault() {
		return properties;
	}

	@Override
	public ILoader getInstance(Properties properties) {
		String pathname = (String) this.properties.get(FILE);
		ArffLoader loader = new ArffLoader();		
		try {
			FileInputStream in = new FileInputStream(pathname);
			loader.setSource(in);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return new SynchronizedLoader(loader);
	}

}
