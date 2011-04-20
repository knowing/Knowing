package de.lmu.ifi.dbs.knowing.core.swt.handler;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import scala.collection.immutable.List;

import de.lmu.ifi.dbs.knowing.core.graph.GraphSupervisor;
import de.lmu.ifi.dbs.knowing.core.graph.PresenterNode;
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.processing.IPresenter;
import de.lmu.ifi.dbs.knowing.core.swt.view.PresenterView;

/**
 * 
 * @author Nepomuk Seiler
 * @version 0.1
 * @since 21.03.2011
 */
public class EvaluateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String pathname = openDPU(event);	
		DataProcessingUnit dpu = unmarshallDPU(pathname);
		GraphSupervisor supervisor = new GraphSupervisor(dpu);
		try {
			IViewPart view = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().showView(PresenterView.ID);
			PresenterView pView = (PresenterView) view;
			pView.clearTabs();
			List actors = supervisor.getPresenterActors();
//			for (IPresenter presenter : supervisor.getPresenterActors()) {
//				createPresenterContainer(pView, presenter);
//			}
			
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		
		supervisor.initialize();
		try {
			supervisor.evaluate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * @param pView
	 * @param presenter
	 */
	private void createPresenterContainer(PresenterView pView, IPresenter presenter) {
		String clazz = presenter.getContainerClass();
		if(Composite.class.getName().equals(clazz)) {
			IPresenter<Composite> swtPresenter = presenter;
			Composite control = pView.createTab(swtPresenter.getName());
			swtPresenter.createContainer(control);
		}
	}

	/**
	 * @param pathname
	 */
	private DataProcessingUnit unmarshallDPU(String pathname) {
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			return (DataProcessingUnit) um.unmarshal(new File(pathname));
		} catch (JAXBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param event
	 * @return
	 */
	private String openDPU(ExecutionEvent event) {
		FileDialog dialog = new FileDialog(HandlerUtil.getActiveShell(event));
		dialog.setFilterExtensions(new String[] {"*.xml", "*.dpu"});
		dialog.setFilterNames(new String[] {"XML DataProcessingUnit", "DPU DataProcessingUnit"});
		String pathname = dialog.open();
		return pathname;
	}

}
