package de.lmu.ifi.dbs.knowing.core.processing;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

import weka.core.Instances;
import weka.core.converters.Loader;
import weka.core.converters.URLSourcedLoader;
import de.lmu.ifi.dbs.knowing.core.query.QueryTicket;

/**
 * <p>Wraps an {@link Loader} to cache and synchronize the access<br>
 * on the dataset. This is really important for concurrent access,<br>
 * because the underlying {@link Reader} or {@link InputStream}s <br>
 * won't work.</p>
 * @author Nepomuk Seiler
 * @version 0.1
 */
public class SynchronizedLoader implements ILoader {

	private final Loader loader;
	private Instances dataset;
	private Instances structure;

	public SynchronizedLoader(Loader loader) {
		this.loader = loader;
	}

	@Override
	public synchronized void reset() throws Exception {
		loader.reset();
		dataset = null;
	}

	@Override
	public synchronized void getStructure(QueryTicket ticket) throws IOException {
		if (dataset == null) {
			structure = loader.getStructure();
		}

		try {
			ticket.fireResult(structure);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// TODO listener.result(new QueryResult(dataset, null, listener));
			// try more than once
		}
	}

	@Override
	public synchronized void getDataSet(QueryTicket ticket) throws IOException {

		if (dataset == null) {
			structure = loader.getStructure();
			dataset = loader.getDataSet();
		}

		try {
			ticket.fireResult(dataset);
		} catch (InterruptedException e) {
			e.printStackTrace();
			// TODO listener.result(new QueryResult(dataset, null, listener));
			// try more than once
		}
	}

	@Override
	public void getNextInstance(Instances structure) throws IOException {
		//TODO SynchronizedLoader.getNextInstance(Instances structure) throws IOException
	}

	@Override
	public synchronized void setSource(InputStream input) throws IOException {
		loader.setSource(input);
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void setSource(File file) throws IOException {
		loader.setSource(file);
		try {
			reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public synchronized void setURL(String url) throws Exception {
		if (loader instanceof URLSourcedLoader)
			((URLSourcedLoader) loader).setURL(url);
		else
			throw new UnsupportedOperationException(loader + " is no URLSourcedLoader!");

	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SynchronizedLoader [loader=");
		builder.append(loader.getClass().getSimpleName());
		builder.append("]");
		return builder.toString();
	}

}
