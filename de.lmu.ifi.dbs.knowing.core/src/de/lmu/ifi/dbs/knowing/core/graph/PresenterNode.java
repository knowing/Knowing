/**
 * 
 */
package de.lmu.ifi.dbs.knowing.core.graph;

import java.io.IOException;
import java.util.Properties;

import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;
import de.lmu.ifi.dbs.knowing.core.processing.IResultProcessor;
import de.lmu.ifi.dbs.knowing.core.util.FactoryUtil;

/**
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public class PresenterNode extends Node {

	private IPresenter presenter;

	/**
	 * @param factoryName
	 * @param factoryId
	 * @param nodeId
	 */
	public PresenterNode(String factoryName, String factoryId, String nodeId) {
		super(factoryName, factoryId, nodeId);
	}

	/**
	 * @param factoryId
	 * @param nodeId
	 */
	public PresenterNode(String factoryId, String nodeId) {
		super(FactoryUtil.getFactory(factoryId, IPresenterFactory.class).getName(), factoryId, nodeId);
	}

	@Override
	public void run() throws Exception {

	}

	@Override
	public void initialize() {
		presenter = FactoryUtil.getPresenterService(getFactoryId(), properties);
	}

	@Override
	protected boolean validate(Properties properties) {
		String uiclass = properties.getProperty(IPresenterFactory.PROP_UI_CLASS);
		return uiclass != null;
	}

	@Override
	public void nodeChanged(NodeEvent event) {
		System.out.println("!!! [NodeEvent]: " + event.getSource() + " -> " + this);
		Object so = event.getServiceObject();
		switch (event.getType()) {
		case NodeEvent.LOADER_READY:
			createPresentation((ILoader) so);
			break;
		case NodeEvent.PROCESSOR_READY:
			createPresentation((IResultProcessor) so);
			break;
		}
	}

	/**
	 * @param loader
	 */
	private void createPresentation(final ILoader loader) {
		System.out.println("=== [RUN/BUILD]: " + presenter + " with " + loader);
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				try {
					presenter.buildPresentation(loader);
				} catch (IOException e) {
					// TODO createPresentation -> Handle Exception
					e.printStackTrace();
				}
			}
		};
		supervisor.execute(buildTask);
	}

	/**
	 * @param processor
	 */
	private void createPresentation(final IResultProcessor processor) {
		System.out.println("=== [RUN/BUILD]: " + presenter + " with " + processor);
		Runnable buildTask = new Runnable() {

			@Override
			public void run() {
				try {
					presenter.buildPresentation(processor);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		supervisor.execute(buildTask);
	}

	/**
	 * @return the presenter
	 */
	public IPresenter getPresenter() {
		return presenter;
	}
	
	@Override
	public INode clone() {
		PresenterNode clone = new PresenterNode(getName(), getFactoryId(), getNodeId());
		clone.setProperties(properties);
		clone.setSupervisor(supervisor);
		return clone;
	}

	@Override
	public String toString() {
		return getNodeId() + "[" + presenter + "]";
	}

}
