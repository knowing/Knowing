package de.lmu.ifi.dbs.knowing.core.swt.view

import akka.actor.{ ActorRef, Actor, TypedActor }
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite
import org.eclipse.swt.graphics.Image
import org.eclipse.swt.custom.{ CTabFolder, CTabItem }
import org.eclipse.ui.part.ViewPart
import de.lmu.ifi.dbs.knowing.core.swt.dialog.ProgressDialog
import de.lmu.ifi.dbs.knowing.core.swt.factory.UIFactories.{ newTabUIFactoryInstance, newServiceProperties }
import de.lmu.ifi.dbs.knowing.core.factory.UIFactory
import de.lmu.ifi.dbs.knowing.core.events._
import de.lmu.ifi.dbs.knowing.core.model._
import de.lmu.ifi.dbs.knowing.core.swt.internal.Activator
import org.osgi.framework.ServiceRegistration
import com.typesafe.config.ConfigFactory
import akka.actor.ActorSystem

/**
 * @author Nepomuk Seiler
 * @version 0.3
 * @since 22.04.2011
 *
 */
class PresenterView extends ViewPart {

	var uifactory: UIFactory[Composite] = _

	private var uiFactoryReg: ServiceRegistration[UIFactory[Composite]] = _

	/**
	 * Creates UIFactory and register it as a service
	 */
	def createPartControl(parent: Composite) = {
		val asm = Activator.actorSystemManager
		val system = asm.getSystem("swt-local").
			getOrElse(asm.create("swt-local", ConfigFactory.defaultReference(classOf[ActorSystem].getClassLoader)))

		uifactory = newTabUIFactoryInstance(system, parent, PresenterView.ID)

		//Register UIFactory as a service
		val ctx = Activator.getDefault.getBundle.getBundleContext
		uiFactoryReg = ctx.registerService(classOf[UIFactory[Composite]], uifactory, newServiceProperties)
	}

	def setFocus() = {}

	override def dispose() {
		uiFactoryReg.unregister
		super.dispose()
	}

}

object PresenterView { val ID = "de.lmu.ifi.dbs.knowing.core.swt.presenterView" }

