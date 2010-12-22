package de.lmu.ifi.dbs.medmon.medic.ui.views;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.ViewPart;

import de.lmu.ifi.dbs.medmon.medic.core.unit.MedicProcessingUnit;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class MedicProcessingView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.views.MedicProcessingView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	private Text text;
	private Text text_1;

	public MedicProcessingView() {
	}

	/**
	 * Create contents of the view part.
	 * 
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(2, false));
		{
			Section sMPU = toolkit.createSection(container, Section.TITLE_BAR);
			sMPU.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
					2));
			toolkit.paintBordersFor(sMPU);
			sMPU.setText("Analyseverfahren");
			{
				Composite composite = toolkit.createComposite(sMPU, SWT.NONE);
				toolkit.paintBordersFor(composite);
				sMPU.setClient(composite);
				composite.setLayout(new GridLayout(1, false));
				{
					TableViewer tableViewer = new TableViewer(composite,
							SWT.BORDER | SWT.FULL_SELECTION);
					tableViewer.setContentProvider(new ArrayContentProvider());
					tableViewer.setInput(createMPU());
					table = tableViewer.getTable();
					table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
							true, 1, 1));
					toolkit.paintBordersFor(table);
				}
			}
		}
		{
			Section sPatient = toolkit.createSection(container, Section.TWISTIE
					| Section.TITLE_BAR);
			sPatient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false,
					true, 1, 1));
			toolkit.paintBordersFor(sPatient);
			sPatient.setText("Patient");
			sPatient.setExpanded(true);
			{
				Composite composite = toolkit.createComposite(sPatient,
						SWT.NONE);
				toolkit.paintBordersFor(composite);
				sPatient.setClient(composite);
				composite.setLayout(new GridLayout(3, false));
				{
					Label lblName = toolkit.createLabel(composite, "Name",
							SWT.NONE);
					lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER,
							false, false, 1, 1));
				}
				{
					text = toolkit.createText(composite, "New Text", SWT.NONE);
					text.setText("");
					GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER,
							false, false, 1, 1);
					gd_text.widthHint = 150;
					text.setLayoutData(gd_text);
				}
				{
					Button btnAuswaehlen = toolkit.createButton(composite,
							"Auswaehlen", SWT.NONE);
				}
				{
					Label lblVergleichsdaten = toolkit.createLabel(composite,
							"Vergleichsdaten", SWT.NONE);
					lblVergleichsdaten.setLayoutData(new GridData(SWT.RIGHT,
							SWT.CENTER, false, false, 1, 1));
				}
				{
					text_1 = toolkit
							.createText(composite, "New Text", SWT.NONE);
					text_1.setText("");
					text_1.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
							true, false, 1, 1));
				}
				{
					Button btnAuswaehlen_1 = toolkit.createButton(composite,
							"Auswaehlen", SWT.NONE);
				}
			}
		}
		{
			Section sData = toolkit.createSection(container, Section.TWISTIE
					| Section.TITLE_BAR);
			sData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true,
					1, 1));
			toolkit.paintBordersFor(sData);
			sData.setText("Daten");
			sData.setExpanded(true);
			{
				Composite composite = toolkit.createComposite(sData, SWT.NONE);
				toolkit.paintBordersFor(composite);
				sData.setClient(composite);
				composite.setLayout(new GridLayout(1, false));
			}
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	public void dispose() {
		toolkit.dispose();
		super.dispose();
	}

	private MedicProcessingUnit[] createMPU() {
		try {
			JAXBContext context = JAXBContext
					.newInstance(MedicProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			File mpu_dir = new File(IMedmonConstants.DIR_MPU);
			String[] files = mpu_dir.list();
			MedicProcessingUnit[] returns = new MedicProcessingUnit[files.length];
			for (int i = 0; i < returns.length; i++) {
				returns[i] = (MedicProcessingUnit) um.unmarshal(new File(
						IMedmonConstants.DIR_MPU
								+ IMedmonConstants.DIR_SEPERATOR + files[i]));
			}
			return returns;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return new MedicProcessingUnit[0];
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		IMenuManager manager = getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		table.setFocus();
	}

}
