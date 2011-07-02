package de.lmu.ifi.dbs.knowing.ui.editor.pages;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.graph.*;
import de.lmu.ifi.dbs.knowing.ui.viewer.EdgeTableViewer;
import de.lmu.ifi.dbs.knowing.ui.viewer.NodeTableViewer;

public class ConfigurationMasterDetailBlock extends MasterDetailsBlock implements PropertyChangeListener {

	private NodeTableViewer nodeTableViewer;
	private EdgeTableViewer edgeTableViewer;

	private DataProcessingUnit dpu;

	/**
	 * Create the master details block.
	 */
	public ConfigurationMasterDetailBlock() {
		// Create the master details block
	}

	/**
	 * Create contents of the master details block.
	 * 
	 * @param managedForm
	 * @param parent
	 */
	@Override
	protected void createMasterPart(final IManagedForm managedForm, Composite parent) {
		FormToolkit toolkit = managedForm.getToolkit();
		Composite container = toolkit.createComposite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		//
		Section sectionNode = toolkit.createSection(container, Section.EXPANDED | Section.TITLE_BAR);
		sectionNode.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sectionNode.setText("Node Configuration");
		//
		Composite cNode = toolkit.createComposite(sectionNode, SWT.NONE);
		toolkit.paintBordersFor(cNode);
		sectionNode.setClient(cNode);
		cNode.setLayout(new GridLayout(2, false));

		final SectionPart spart = new SectionPart(sectionNode);
		Table nodeTable = toolkit.createTable(cNode, SWT.NONE);
		nodeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.paintBordersFor(nodeTable);
		nodeTable.setHeaderVisible(true);
		nodeTable.setLinesVisible(true);
		nodeTableViewer = new NodeTableViewer(nodeTable);
		nodeTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				managedForm.fireSelectionChanged(spart, event.getSelection());
			}
		});

		Button bRemoveNode = toolkit.createButton(cNode, "remove", SWT.PUSH);
		bRemoveNode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		bRemoveNode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) nodeTableViewer.getSelection();
				if(selection.isEmpty())
					return;
				PersistentNode node = (PersistentNode) selection.getFirstElement();
				dpu.removeNode(node);
				nodeTableViewer.setInput(dpu.nodes());
			}
		});
		
		Button bAddNode = toolkit.createButton(cNode, "add", SWT.NONE);
		bAddNode.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bAddNode.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				PersistentNode node = new PersistentNode("newId", "class","processor");
				dpu.addNode(node);
				nodeTableViewer.setInput(dpu.nodes());
			}
		});

		Section sectionEdge = toolkit.createSection(container, Section.EXPANDED | Section.TWISTIE | Section.TITLE_BAR);
		sectionEdge.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		sectionEdge.setText("Edge Configuration");
		Composite cEdge = toolkit.createComposite(sectionEdge, SWT.NONE);
		sectionEdge.setClient(cEdge);
		cEdge.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		toolkit.adapt(cEdge);
		toolkit.paintBordersFor(cEdge);
		cEdge.setLayout(new GridLayout(2, false));

		Table edgeTable = toolkit.createTable(cEdge, SWT.NONE);
		edgeTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		toolkit.paintBordersFor(edgeTable);
		edgeTable.setHeaderVisible(true);
		edgeTable.setLinesVisible(true);
		edgeTableViewer = new EdgeTableViewer(edgeTable);

		Button bRemoveEdge = toolkit.createButton(cEdge, "remove", SWT.PUSH);
		bRemoveEdge.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false, 1, 1));
		bRemoveEdge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) edgeTableViewer.getSelection();
				if(selection.isEmpty())
					return;
				Edge edge = (Edge) selection.getFirstElement();
				dpu.removeEdge(edge);
				edgeTableViewer.setInput(dpu.edges());
			}
		});
		
		Button bAddEdge = toolkit.createButton(cEdge, "add", SWT.NONE);
		bAddEdge.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		bAddEdge.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				Edge edge = new Edge("newId", "newSource", "newTarget", 1);
				dpu.addEdge(edge);
				edgeTableViewer.setInput(dpu.edges());
			}
		});

		if (dpu != null)
			setInput(dpu);

	}

	/**
	 * Register the pages.
	 * 
	 * @param part
	 */
	@Override
	protected void registerPages(DetailsPart part) {
		part.registerPage(PersistentNode.class, new PersistentNodeDetailsPage().addPropertyChangeListener(this));
	}

	/**
	 * Create the toolbar actions.
	 * 
	 * @param managedForm
	 */
	@Override
	protected void createToolBarActions(IManagedForm managedForm) {
		// Create the toolbar actions
	}

	public void setInput(DataProcessingUnit dpu) {
		this.dpu = dpu;
		if (nodeTableViewer != null) 
			nodeTableViewer.setInput(dpu.nodes());
			
		if (edgeTableViewer != null) 
			edgeTableViewer.setInput(dpu.edges());
		refresh();
	}
	
	public void refresh() {
		if (nodeTableViewer != null) 
			nodeTableViewer.refresh();
			
		if (edgeTableViewer != null) 
			edgeTableViewer.refresh();
	}
	
	public NodeTableViewer getNodeTableViewer() {
		return nodeTableViewer;
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if(dpu != null)
			setInput(dpu);
	}

}
