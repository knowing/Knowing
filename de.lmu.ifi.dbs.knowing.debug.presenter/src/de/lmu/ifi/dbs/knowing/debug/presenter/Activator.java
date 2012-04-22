package de.lmu.ifi.dbs.knowing.debug.presenter;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import akka.actor.TypedActor;
import akka.actor.TypedActorFactory;

import de.lmu.ifi.dbs.knowing.core.factory.UIFactory;

public class Activator implements BundleActivator {

	private static BundleContext context;

	private ServiceRegistration<UIFactory>	uiFactory;
	
	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		DebugPresenterFactories.registerAll(bundleContext);
		UIFactory<Path> uiFactoryActor = TypedActor.newInstance(UIFactory.class, new TypedActorFactory() {
			@Override
			public TypedActor create() {
				return new DebugUIFactory(Paths.get(System.getProperty("user.home")));
			}
		});
		//TODO add services properties
		uiFactory = context.registerService(UIFactory.class, uiFactoryActor, null);
		
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		DebugPresenterFactories.unregisterAll();
		uiFactory.unregister();
		Activator.context = null;
	}

}
