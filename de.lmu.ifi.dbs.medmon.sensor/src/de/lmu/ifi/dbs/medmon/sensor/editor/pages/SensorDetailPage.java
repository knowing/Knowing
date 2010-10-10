package de.lmu.ifi.dbs.medmon.sensor.editor.pages;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.database.model.Data;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.CommandUtil;
import de.lmu.ifi.dbs.medmon.rcp.platform.util.ResourceManager;

import org.eclipse.swt.widgets.Label;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.ui.forms.widgets.ColumnLayoutData;

public class SensorDetailPage implements IDetailsPage {
	
	private DataBindingContext bindingContext;
	
	public SensorDetailPage() {
	}

	private IManagedForm managedForm;
	private Data data;
	private Text tImport;
	private Text tRecord;

	@Override
	public void initialize(IManagedForm managedForm) {
		this.managedForm = managedForm;
	}

	@Override
	public void selectionChanged(IFormPart part, ISelection selection) {
		if(!selection.isEmpty() && selection instanceof IStructuredSelection) {
			if(bindingContext != null) bindingContext.dispose();
			data = (Data)((IStructuredSelection)selection).getFirstElement();
			//TODO set ImportText
			//tImport.setText(date2String(data.getImported().getTime()));
			//tRecord.setText(date2String(data.getId().getRecord().getTime()));
			bindingContext = initDataBindings();
		}

	}

	@Override
	public void createContents(Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();	
		ColumnLayout layout_parent = new ColumnLayout();
		layout_parent.maxNumColumns = 2;
		parent.setLayout(layout_parent);
		
		/* Comments */
		Section cSection = toolkit.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED | Section.TWISTIE);
		ColumnLayoutData cld_cSection = new ColumnLayoutData();
		cld_cSection.widthHint = 300;
		cSection.setLayoutData(cld_cSection);
		cSection.setText("Allgemein");

		Composite cClient = toolkit.createComposite(cSection, SWT.WRAP);
		GridLayout cLayout = new GridLayout(2, false);
		cLayout.marginWidth = 5;
		cLayout.marginHeight = 5;
		cLayout.horizontalSpacing = 10;
		cLayout.verticalSpacing = 10;
		cClient.setLayout(cLayout);
	
		Label label = toolkit.createLabel(cClient, "Importiert");
		label.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		
		tImport = toolkit.createText(cClient, "", SWT.READ_ONLY);
		tImport.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		toolkit.adapt(tImport, true, true);
		toolkit.createLabel(cClient, "Aufgezeichnet");
		tRecord = toolkit.createText(cClient, "", SWT.READ_ONLY);	
		
		Text comments = toolkit.createText(cClient, "Kommentare", SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 100;
		data.horizontalSpan = 2;
		//TODO Listener um automatisch groesser zu machen
		comments.setLayoutData(data);
		
		cSection.setClient(cClient);
		
		/* Analyse */
		
		Section bSection = toolkit.createSection(parent, Section.NO_TITLE);	
		Composite bClient = toolkit.createComposite(bSection);
		bClient.setLayout(new FillLayout());
		
		ImageHyperlink sensorLink = toolkit.createImageHyperlink(bClient, SWT.NONE);
		sensorLink.setImage(ResourceManager.getPluginImage(IMedmonConstants.RCP_PLUGIN, IMedmonConstants.IMG_DIRECTORY_48));
		sensorLink.setText("Datenanalyse");
		
		sensorLink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent event) {
				CommandUtil.openView(IMedmonConstants.THERAPY_MANAGEMENT_VIEW);
			}
		});
		
		toolkit.paintBordersFor(bClient);
		bSection.setClient(bClient);
		

	}

	protected DataBindingContext initDataBindings() {
		DataBindingContext bindingContext = new DataBindingContext();
		//

		//
		return bindingContext;
	}
	
	@Override
	public void dispose() {

	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public void commit(boolean onSave) {
	}

	@Override
	public boolean setFormInput(Object input) {
		return false;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public boolean isStale() {
		return false;
	}

	@Override
	public void refresh() {

	}
	
	private String date2String(Date date) {
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyy");
		if(date == null)
			return "";
		return df.format(date);
	}
}
