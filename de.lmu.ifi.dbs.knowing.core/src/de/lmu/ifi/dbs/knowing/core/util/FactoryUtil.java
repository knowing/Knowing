package de.lmu.ifi.dbs.knowing.core.util;

import java.util.Properties;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

import de.lmu.ifi.dbs.knowing.core.factory.IFactory;
import de.lmu.ifi.dbs.knowing.core.factory.ILoaderFactory;
import de.lmu.ifi.dbs.knowing.core.factory.IPresenterFactory;
import de.lmu.ifi.dbs.knowing.core.factory.IProcessorFactory;
import de.lmu.ifi.dbs.knowing.core.internal.Activator;
import de.lmu.ifi.dbs.knowing.core.processing.ILoader;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;
import de.lmu.ifi.dbs.knowing.core.processing.IProcessor;

/**
 * Utilclass for simple {@link IProcessor} and {@link ILoader} instantiation.
 * 
 * @author Nepomuk Seiler
 * @version 0.5
 * 
 */
public class FactoryUtil {

	/**
	 * 
	 * @param factoryId - the Id which the ServiceFactory declares via {@link IFactory#getId()}
	 * @param properties - can be null
	 * @return null if no factory for factoryId exists
	 */
	public static ILoader getLoaderService(String factoryId, Properties properties) {
		BundleContext context = Activator.getContext();
		ServiceReference[] references = null;
		try {
			references = context.getServiceReferences(ILoaderFactory.class.getName(), null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if(references == null || references.length == 0) 
			return null;
		for (ServiceReference reference : references) {
			ILoaderFactory loaderFactory = (ILoaderFactory) context.getService(reference);
			if(loaderFactory != null && loaderFactory.getId().equals(factoryId))
				return loaderFactory.getInstance(properties);
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param factoryId - the Id which the ServiceFactory declares via {@link IFactory#getId()}
	 * @param properties - can be null
	 * @return null if no factory for factoryId exists
	 */
	public static IProcessor getProcessorService(String factoryId, Properties properties) {
		BundleContext context = Activator.getContext();
		ServiceReference[] references = null;
		try {
			references = context.getServiceReferences(IProcessorFactory.class.getName(), null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if(references == null || references.length == 0) 
			return null;
		for (ServiceReference reference : references) {
			IProcessorFactory processorFactory = (IProcessorFactory) context.getService(reference);
			if(processorFactory != null && processorFactory.getId().equals(factoryId))
				return processorFactory.getInstance(properties);
		}
		return null;
	}
	
	/**
	 * 
	 * @param factoryId - the Id which the ServiceFactory declares via {@link IFactory#getId()}
	 * @param properties - must contain {@link IPresenterFactory#PROP_UI_CLASS}
	 * @return null if no factory for factoryId exists
	 */
	public static <T> IPresenter<T> getPresenterService(String factoryId, Properties properties) {
		BundleContext context = Activator.getContext();
		ServiceReference[] references = null;
		try {
			references = context.getServiceReferences(IPresenterFactory.class.getName(), null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if(references == null || references.length == 0) 
			return null;
		String className = properties.getProperty(IPresenterFactory.PROP_UI_CLASS);
	/*	try {
			if(className != null)
				Class.forName(className);
		} catch (ClassNotFoundException e) {
			//System is unable to handle UI component
			//TODO getPresenterService -> Handle ClassNotFoundException more proper
			e.printStackTrace();
		}*/
		for (ServiceReference reference : references) {
			IPresenterFactory presenterFactory = (IPresenterFactory) context.getService(reference);
			if(presenterFactory != null && presenterFactory.getId().equals(factoryId)) {
				IPresenter presenter = presenterFactory.getInstance(properties);
				if(presenter.getContainerClass().equals(className))
					return presenterFactory.getInstance(properties);
			}
		
		}
		return null;
	}
	

	public static <E extends IFactory> E getFactory(String factoryId, Class<E> factoryClass) {
		BundleContext context = Activator.getContext();
		ServiceReference[] references = null;
		try {
			references = context.getServiceReferences(factoryClass.getName(), null);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		if(references == null || references.length == 0) 
			return null;
		for (ServiceReference reference : references) {
			E factory = (E) context.getService(reference);
			if(factory != null && factory.getId().equals(factoryId))
				return factory;
		}
		return null;
	}
	
	/**
	 * Trys to register a service via the Knowing-Bundle. 
	 * 
	 * @param factory
	 * @param properties
	 * @return
	 */
	public static ServiceRegistration registerLoaderFactory(ILoaderFactory factory, Properties properties)  {
		return registerLoaderFactory(factory, properties, Activator.getContext());
	}
	
	/**
	 * Trys to register a service via the given context. 
	 * 
	 * @param factory
	 * @param properties
	 * @param context
	 * @return
	 */
	public static ServiceRegistration registerLoaderFactory(ILoaderFactory factory, Properties properties, BundleContext context)  {
		return context.registerService(ILoaderFactory.class.getName(), factory, properties);
	}
	
	/**
	 * Trys to register a service via the Knowing-Bundle. 
	 * 
	 * @param factory
	 * @param properties
	 * @return
	 */
	public static ServiceRegistration registerProcesorFactory(IProcessorFactory factory, Properties properties)  {
		return registerProcesorFactory(factory, properties, Activator.getContext());
	}
	
	/**
	 * Trys to register a service via the the given context. 
	 * 
	 * @param factory
	 * @param properties
	 * @param context
	 * @return
	 */
	public static ServiceRegistration registerProcesorFactory(IProcessorFactory factory, Properties properties, BundleContext context)  {
		return context.registerService(IProcessorFactory.class.getName(), factory, properties);
	}
	
	/**
	 * Trys to register a service via the Knowing-Bundle. 
	 * 
	 * @param factory
	 * @param properties
	 * @return
	 */
	public static ServiceRegistration registerPresenterFactory(IPresenterFactory factory, Properties properties)  {
		return registerPresenterFactory(factory, properties, Activator.getContext());
	}
	
	/**
	 * Trys to register a service via the given bundleContext
	 * 
	 * @param factory
	 * @param properties
	 * @param context
	 * @return
	 */
	public static ServiceRegistration registerPresenterFactory(IPresenterFactory factory, Properties properties, BundleContext context) {
		return context.registerService(IPresenterFactory.class.getName(), factory, properties);
	}
}
