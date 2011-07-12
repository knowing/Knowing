package de.lmu.ifi.dbs.knowing.ui.editor.pages

import java.io.{ IOException, InputStream }
import javax.xml.bind.{ JAXBContext, JAXBException, Unmarshaller }
import scala.collection.mutable.{ Map => mMap, HashMap }
import org.eclipse.core.resources.IFile
import org.eclipse.core.runtime.{ CoreException, IProgressMonitor }
import org.eclipse.ui.{ IEditorInput, IEditorSite }
import org.eclipse.ui.forms.IManagedForm
import org.eclipse.ui.forms.editor.{ FormEditor, FormPage }
import org.eclipse.ui.forms.widgets.{ FormToolkit, ScrolledForm }
import org.eclipse.zest.core.widgets.{ Graph, GraphNode, GraphConnection, ZestStyles }
import org.eclipse.zest.layouts.LayoutStyles
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite;
import de.lmu.ifi.dbs.knowing.ui.editor.DPUEditor

class GraphicPage(editor: FormEditor) extends FormPage(editor, classOf[GraphicPage].getName, "Graphic") {

  var container: Composite = _
  var mForm: IManagedForm = _
  var dpu: DataProcessingUnit = _

  override protected def createFormContent(managedForm: IManagedForm) {
    mForm = managedForm
    val toolkit = managedForm.getToolkit
    val form = managedForm.getForm
    form.setText("Data Processing Unit - Graph View");
    container = form.getBody
    toolkit.decorateFormHeading(form.getForm)
    toolkit.paintBordersFor(container)
    container.setLayout(new FillLayout)

    updateView(dpu)
  }

  override def init(site: IEditorSite, input: IEditorInput) {
    super.init(site, input)
    update(input)
  }

  override def doSave(monitor: IProgressMonitor) {
    super.doSave(monitor);
    println("DoSave in ConfigurationPage");
  }

  def update(input: IEditorInput) {
    try {
      dpu = DPUEditor.convert(input);
      updateView(dpu)
    } catch {
      case e: CoreException => e.printStackTrace
      case e: JAXBException => e.printStackTrace
      case e: IOException => e.printStackTrace
    }

  }

  def updateView(dpu: DataProcessingUnit) {
    GraphicPage.createGraph(dpu, container)
    if(mForm != null)
      mForm.reflow(true)
  }

}

object GraphicPage {
  def createGraph(dpu: DataProcessingUnit, container: Composite) {
    if (container == null)
      return
    container.getChildren foreach (c => c dispose)
    val graph = new Graph(container, SWT.NONE)
    val nodesArray = for (node <- dpu.nodes) yield (node.id -> new GraphNode(graph, SWT.NONE, node.id))
    var nodes: mMap[String, GraphNode] = new HashMap
    nodesArray foreach { case (id, node) => nodes += (id -> node) }
    dpu.edges foreach (e => new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes(e.sourceId.split(":")(0)), nodes(e.targetId)))
    graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true)
  }
}