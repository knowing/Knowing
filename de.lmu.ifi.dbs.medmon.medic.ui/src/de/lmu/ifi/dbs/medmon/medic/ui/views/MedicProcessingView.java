package de.lmu.ifi.dbs.medmon.medic.ui.views;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Table;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Text;

public class MedicProcessingView extends ViewPart {

	public static final String ID = "de.lmu.ifi.dbs.medmon.medic.ui.views.MedicProcessingView"; //$NON-NLS-1$
	private final FormToolkit toolkit = new FormToolkit(Display.getCurrent());
	private Table table;
	private Text text;

	public MedicProcessingView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		toolkit.paintBordersFor(container);
		container.setLayout(new GridLayout(2, false));
		{
			Section sMPU = toolkit.createSection(container, Section.TITLE_BAR);
			sMPU.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 2));
			toolkit.paintBordersFor(sMPU);
			sMPU.setText("Analyseverfahren");
			{
				Composite composite = toolkit.createComposite(sMPU, SWT.NONE);
				toolkit.paintBordersFor(composite);
				sMPU.setClient(composite);
				composite.setLayout(new GridLayout(1, false));
				{
					TableViewer tableViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
					table = tableViewer.getTable();
					table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
					toolkit.paintBordersFor(table);
				}
			}
		}
		{
			Section sPatient = toolkit.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
			sPatient.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			toolkit.paintBordersFor(sPatient);
			sPatient.setText("Patient");
			sPatient.setExpanded(true);
			{
				Composite composite = toolkit.createComposite(sPatient, SWT.NONE);
				toolkit.paintBordersFor(composite);
				sPatient.setClient(composite);
				composite.setLayout(new GridLayout(2, false));
				{
					Label lblName = toolkit.createLabel(composite, "Name", SWT.NONE);
					lblName.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
				}
				{
					text = toolkit.createText(composite, "New Text", SWT.NONE);
					text.setText("");
					GridData gd_text = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					gd_text.widthHint = 150;
					text.setLayoutData(gd_text);
				}
			}
		}
		{
			Section sData = toolkit.createSection(container, Section.TWISTIE | Section.TITLE_BAR);
			sData.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
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
		// Set the focus
	}

}
