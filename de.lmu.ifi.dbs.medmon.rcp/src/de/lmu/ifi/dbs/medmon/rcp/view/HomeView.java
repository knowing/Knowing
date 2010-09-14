package de.lmu.ifi.dbs.medmon.rcp.view;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.patient.perspective.PatientPerspectiveFactory;
import de.lmu.ifi.dbs.medmon.rcp.Activator;
import de.lmu.ifi.dbs.medmon.visualizer.perspectives.DefaultPerspectiveFactory;

public class HomeView extends ViewPart {

	private FormToolkit toolkit;

	public HomeView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		ScrolledForm form = toolkit.createScrolledForm(parent);
		form.setText("Willkommen bei Medmon");
		// form.getToolBarManager().add(new Action("This is the toolbar",
		// Activator.getImageDescriptor("icons/alt_window_16.gif")) { }); // NEW
		// LINE
		// form.getToolBarManager().update(true);
		ColumnLayout layout = new ColumnLayout();
		layout.maxNumColumns = 2;
		layout.leftMargin = 15;
		layout.rightMargin = 15;
		layout.topMargin = 15;
		layout.bottomMargin = 15;
		layout.verticalSpacing = 15;
		layout.horizontalSpacing = 15;
		form.getBody().setLayout(new ColumnLayout());
		// form.setBackgroundImage(Activator.getImageDescriptor("icons/medmon_logo.png").createImage());

		ImageHyperlink patient = toolkit.createImageHyperlink(form.getBody(),
				SWT.NONE);
		patient.setText("Patient verwalten");
		patient.setImage(Activator.getImageDescriptor("icons/gtk-open.png")
				.createImage());
		patient.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					PlatformUI.getWorkbench().showPerspective(
							PatientPerspectiveFactory.ID,
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow());
				} catch (WorkbenchException e1) {
					e1.printStackTrace();
				}
			}
		});

		ImageHyperlink sensor = toolkit.createImageHyperlink(form.getBody(),
				SWT.NONE);
		sensor.setText("Daten importieren");
		sensor.setImage(Activator.getImageDescriptor("icons/gtk-go-down.png")
				.createImage());

		ImageHyperlink analyse = toolkit.createImageHyperlink(form.getBody(),
				SWT.NONE);
		analyse.setText("Analysieren");
		analyse.setImage(Activator.getImageDescriptor("icons/gtk-zoom-100.png")
				.createImage());

		ImageHyperlink visualize = toolkit.createImageHyperlink(form.getBody(),
				SWT.NONE);
		visualize.setText("Visualisieren");
		visualize.setImage(Activator.getImageDescriptor("icons/gtk-chart.png")
				.createImage());
		visualize.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				try {
					PlatformUI.getWorkbench().showPerspective(
							DefaultPerspectiveFactory.ID,
							PlatformUI.getWorkbench()
									.getActiveWorkbenchWindow());
				} catch (WorkbenchException e1) {
					e1.printStackTrace();
				}
				;
			}
		});

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
