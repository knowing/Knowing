package de.lmu.ifi.dbs.medmon.medic.ui.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.medic.ui.Activator;
import de.lmu.ifi.dbs.medmon.medic.ui.handler.ClusterWizardHandler;
import de.lmu.ifi.dbs.medmon.medic.ui.handler.ImportWizardHandler;
import de.lmu.ifi.dbs.medmon.medic.ui.handler.NewPatientHandler;
import de.lmu.ifi.dbs.medmon.medic.ui.provider.ISharedImages;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.CommandUtil;

public class HomeView extends ViewPart {

	private FormToolkit toolkit;

	public HomeView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		// form.setBackgroundImage(Activator.getImageDescriptor("medmon.trans.banner.png").createImage());

		form.setText("Willkommen bei Medmon");
		form.getBody().setLayout(new GridLayout());

		GridLayout gridLayout = new GridLayout(2, false);
		gridLayout.horizontalSpacing = 50;
		gridLayout.verticalSpacing = 50;
		gridLayout.marginHeight = 15;
		gridLayout.marginWidth = 15;
		gridLayout.marginTop = 10;
		gridLayout.marginBottom = 10;
		gridLayout.marginRight = 50;
		gridLayout.marginLeft = 10;
		Composite container = toolkit.createComposite(form.getBody());
		container.setLayout(gridLayout);
		container.setLayoutData(new GridData(GridData.CENTER, GridData.CENTER, true, true));

		ImageHyperlink patient = toolkit.createImageHyperlink(container, SWT.NONE);
		patient.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		patient.setText("Neuer Patient");
		patient.setImage(Activator.getImageDescriptor(ISharedImages.IMG_ADD_PATIENT_48).createImage());
		patient.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
						IHandlerService.class);
				try {
					handlerService.executeCommand(NewPatientHandler.ID, null);
				} catch (Exception ex) {
					throw new RuntimeException(NewPatientHandler.ID, ex);
				}
			}
		});

		ImageHyperlink analyse = toolkit.createImageHyperlink(container, SWT.NONE);
		analyse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		analyse.setText("Analysieren");
		analyse.setImage(Activator.getImageDescriptor(ISharedImages.IMG_VISUALIZE_48).createImage());
		analyse.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				CommandUtil.openPerpsective("de.lmu.ifi.dbs.medmon.medic.ui.default");
			}
		});

		ImageHyperlink sensor = toolkit.createImageHyperlink(container, SWT.NONE);
		sensor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		sensor.setText("Daten importieren");
		sensor.setImage(Activator.getImageDescriptor(ISharedImages.IMG_IMPORT_48).createImage());
		sensor.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
						IHandlerService.class);
				try {
					handlerService.executeCommand(ImportWizardHandler.ID, null);
				} catch (Exception ex) {
					throw new RuntimeException(ImportWizardHandler.ID, ex);
				}
			}
		});

		ImageHyperlink cluster = toolkit.createImageHyperlink(container, SWT.NONE);
		cluster.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		cluster.setText("Training");
		cluster.setImage(Activator.getImageDescriptor(ISharedImages.IMG_CLUSTER_48).createImage());
		cluster.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
						IHandlerService.class);
				try {
					handlerService.executeCommand(ClusterWizardHandler.ID, null);
				} catch (Exception ex) {
					throw new RuntimeException(ClusterWizardHandler.ID, ex);
				}				
			}
		});

		Label label = toolkit.createLabel(form.getBody(), "");
		label.setImage(Activator.getImageDescriptor("icons/medmon.trans.png").createImage());
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));

	}

	@Override
	public void setFocus() {

	}

}
