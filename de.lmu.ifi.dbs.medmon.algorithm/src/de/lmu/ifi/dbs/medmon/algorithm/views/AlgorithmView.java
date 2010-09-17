package de.lmu.ifi.dbs.medmon.algorithm.views;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Scale;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.*;
import org.eclipse.jface.viewers.*;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.jface.action.*;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.*;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.SWT;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class AlgorithmView extends ViewPart {

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "de.lmu.ifi.dbs.medmon.algorithm.views.AlgorithmView";

	@Override
	public void createPartControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		toolkit.adapt(parent);
		parent.setLayout(new ColumnLayout());

		/* Algorithm Description Section */
		Section aSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR);
		aSection.setText("Algorithmusname");
		aSection.setDescription("Allgemeine Informationen zum Algorithmus");
		
		Composite aClient = toolkit.createComposite(aSection);
		GridLayout aLayout = new GridLayout(2, false);
		aClient.setLayout(aLayout);
		
		toolkit.createLabel(aClient, "Version ");
		Label version = toolkit.createLabel(aClient, "1.0", SWT.END);
		
		Text description = toolkit.createText(aClient, "Beschreibung des Algorithmus", SWT.MULTI | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 2;
		description.setLayoutData(data);
		
		
		toolkit.paintBordersFor(aClient);	
		aSection.setClient(aClient);
		
		/* Parameter Section*/
		Section pSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TWISTIE | Section.EXPANDED | Section.TITLE_BAR);
		pSection.setText("Parameter");
		
		Composite pClient = toolkit.createComposite(pSection);
		GridLayout pLayout = new GridLayout(2, false);
		pClient.setLayout(pLayout);
		
		// TODO Generic way to read Property file and create UI
		toolkit.createLabel(pClient, "Toleranz ");
		Scale scale = new Scale(pClient, SWT.NONE);
		scale.setMaximum(100);
		scale.setMaximum(0);
		scale.setSelection(50);
		scale.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.adapt(scale, false, false);
		
		toolkit.createLabel(pClient, "Darstellung");
		Combo combo = new Combo(pClient, SWT.READ_ONLY);
		combo.add("Pie Chart");
		combo.add("Bar Chart");
		combo.select(0);
		toolkit.adapt(combo);
		
		pSection.setClient(pClient);
	}

	public void setFocus() {
	}
}