/**
 * 
 */
package knowing.test.processor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.BlockingQueue;

import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instance;
import weka.core.Instances;
import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;
import de.lmu.ifi.dbs.knowing.core.processing.Processor;
import de.lmu.ifi.dbs.knowing.core.query.QueryResult;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;
import de.lmu.ifi.dbs.knowing.core.query.Results;

/**
 * @author Nepomuk Sieler
 * @version 0.1
 * @since 01.04.2011
 *
 */
public class NaiveBayesProcessor extends Processor implements IProcessorFactory {

	public static final String ID = "weka.classifiers.bayes.NaiveBayes";
	
	private Classifier naiveBayes;
	private Instances header;
	
	public NaiveBayesProcessor() {
		naiveBayes = new NaiveBayes();
	}
	
	@Override
	public void buildModel(ILoader loader) {
		try {
			fireQuery(QUERY_LOADER_DATASET, loader, false);
		} catch (IOException e) {
			log.error("Failed to query loader in NaiveBayes", e);
		}
	}

	@Override
	public void buildModel(IProcessor processor) { }

	@Override
	public void resetModel() {
		naiveBayes = new NaiveBayes();
	}

	@Override
	protected void result(BlockingQueue<QueryResult> results) {
		QueryResult result = results.poll();
		while(result != null) {
			QueryTicket ticket = result.getTicket();
			if(ticket.getName().equals(QUERY_LOADER_DATASET)) {
				Instances dataset = result.getResults()[0];
				try {
					int classIndex = Results.guessClassIndex(dataset);
					dataset.setClassIndex(classIndex);
					naiveBayes.buildClassifier(dataset);
					setReady(true);
					generateClassLabels(dataset);
					header = new Instances(dataset, 0);
					fireProcessorStateChanged();
					
				} catch (Exception e) {
					log.error("Unable to build classifier in NaiveBayes", e);
				}
			}
			result = results.poll();
		}
	}

	@Override
	protected void query(BlockingQueue<QueryTicket> tickets) {
		QueryTicket ticket = tickets.poll();
		while(ticket != null) {
			Instance query = ticket.getQuery();
			if(header.equalHeaders(query.dataset())) {
				try {
					double[] distribution = naiveBayes.distributionForInstance(query);
					Instances result = Results.classAndProbabilityResult(getClassLabels(), distribution);
					ticket.fireResult(result);
				} catch (Exception e) {
					log.error("Unable to classifiy instance in naive bayes", e);
				}
			}
			ticket = tickets.poll();
		}
	}
	
	@Override
	public void persistModel(OutputStream out) { }

	@Override
	public void loadModel(InputStream in) {	}

	@Override
	public Instances[] supportedQueries() {
		return new Instances[] { header };
	}

	@Override
	public String[] validate() {
		return new String[0];
	}

	/* ======================= */
	/* ======= Factory ======= */
	/* ======================= */
	
	@Override
	public String getId() {
		return ID;
	}

	@Override
	public String getName() {
		return "Naive Bayes";
	}

	@Override
	public Properties getDefault() {
		return new Properties();
	}

	@Override
	public IProcessor getInstance(Properties properties) {
		return new NaiveBayesProcessor();
	}

}
