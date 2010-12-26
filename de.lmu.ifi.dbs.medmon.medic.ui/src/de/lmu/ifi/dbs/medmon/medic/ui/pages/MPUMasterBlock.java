package de.lmu.ifi.dbs.medmon.medic.ui.pages;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.medmon.medic.core.unit.MedicProcessingUnit;
import de.lmu.ifi.dbs.medmon.rcp.platform.IMedmonConstants;

public class MPUMasterBlock extends MasterDetailsBlock {

	private FormToolkit toolkit;
	
	private int orientation = SWT.VERTICAL;

	/**
	 * Create the master details block.
	 */
	public MPUMasterBlock() {
		// Create the master details block
	}	

	/**
	 * 
	 * @param orientation SWT.VERTICAL or SWT.HORIZONTAL
	 */
	public MPUMasterBlock(int orientation) {
		this.orientation = orientation;
	}



	/**
	 * Create contents of the master details block.
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		toolkit = managedForm.getToolkit();
		sashForm.setOrientation(orientation);
		//		
		Section section = toolkit.createSection(parent, ExpandableComposite.EXPANDED | ExpandableComposite.TITLE_BAR);
		section.setText("Analyseverfahren");
		//
		Composite composite = toolkit.createComposite(section, SWT.NONE);
		toolkit.paintBordersFor(composite);
		section.setClient(composite);
		composite.setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TableViewer mpuViewer = new TableViewer(composite, SWT.BORDER | SWT.FULL_SELECTION);
		mpuViewer.setContentProvider(new ArrayContentProvider());
		mpuViewer.setInput(createMPU());
		final SectionPart part = new SectionPart(section);
		managedForm.addPart(part);
		mpuViewer.addSelectionChangedListener(new ISelectionChangedListener() {		
			@Override
			public void selectionChanged(SelectionChangedEvent event) {	
				managedForm.fireSelectionChanged(part, event.getSelection());
			}
		});
	}
	
	private MedicProcessingUnit[] createMPU() {
		try {
			JAXBContext context = JAXBContext.newInstance(MedicProcessingUnit.class);
			Unmarshaller um = context.createUnmarshaller();
			File mpu_dir = new File(IMedmonConstants.DIR_MPU);
			String[] files = mpu_dir.list();
			MedicProcessingUnit[] returns = new MedicProcessingUnit[files.length];
			for (int i = 0; i < returns.length; i++) {
				returns[i] = (MedicProcessingUnit) um.unmarshal(new File(IMedmonConstants.DIR_MPU
						+ IMedmonConstants.DIR_SEPERATOR + files[i]));
			}
			return returns;
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return new MedicProcessingUnit[0];
	}

	/**
	 * Register the pages.
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.registerPage(MedicProcessingUnit.class, new MPUDetailsPage());
	}

	/**
	 * Create the toolbar actions.
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}

}
