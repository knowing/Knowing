package de.lmu.ifi.dbs.medmon.rcp.view;

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

import de.lmu.ifi.dbs.medmon.rcp.Activator;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.CommandUtil;

public class HomeView extends ViewPart {

	private FormToolkit toolkit;

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		//form.setBackgroundImage(Activator.getImageDescriptor("medmon.trans.banner.png").createImage());
		
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
		patient.setText("Patient verwalten");
		patient.setImage(Activator.getImageDescriptor("icons/gtk-open.png").createImage());
		patient.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				CommandUtil.openPerpsective(IMedmonConstants.MANAGEMENT_PERSPECTIVE);
			}
		});

		ImageHyperlink analyse = toolkit.createImageHyperlink(container, SWT.NONE);
		analyse.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		analyse.setText("Analysieren");
		analyse.setImage(Activator.getImageDescriptor("icons/gtk-zoom-100.png").createImage());
		
				ImageHyperlink sensor = toolkit.createImageHyperlink(container, SWT.NONE);
				sensor.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
				sensor.setText("Daten importieren");
				sensor.setImage(Activator.getImageDescriptor(IMedmonConstants.IMG_ARROW_DOWN_BIG).createImage());
				sensor.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						IHandlerService handlerService = (IHandlerService) PlatformUI.getWorkbench().getService(
								IHandlerService.class);
						try {
							handlerService.executeCommand(IMedmonConstants.CALL_IMPORT_WIZARD, null);
						} catch (Exception ex) {
							ex.printStackTrace();
							throw new RuntimeException(IMedmonConstants.CALL_IMPORT_WIZARD);
						}
					}
				});

		ImageHyperlink visualize = toolkit.createImageHyperlink(container, SWT.NONE);
		visualize.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		visualize.setText("Visualisieren");
		visualize.setImage(Activator.getImageDescriptor("icons/gtk-chart.png").createImage());
		
		Label label = toolkit.createLabel(form.getBody(), "");
		label.setImage(Activator.getImageDescriptor("medmon.trans.png").createImage());
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.BOTTOM, false, false, 1, 1));
		visualize.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					PlatformUI.getWorkbench().showPerspective(IMedmonConstants.VISUALIZE_PERSPECTIVE_DEFAULT,
							PlatformUI.getWorkbench().getActiveWorkbenchWindow());
				} catch (WorkbenchException e1) {
					e1.printStackTrace();
				}
			}
		});

	}

	@Override
	public void setFocus() {
		
	}

}
