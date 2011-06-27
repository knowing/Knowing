package de.lmu.ifi.dbs.knowing.ui.editor.pages

import java.io.{IOException, InputStream}
import javax.xml.bind.{JAXBContext, JAXBException ,Unmarshaller }
import  scala.collection.mutable.{Map => mMap, HashMap}
import org.eclipse.core.resources.IFile
import org.eclipse.core.runtime.{CoreException,IProgressMonitor}
import org.eclipse.ui.{IEditorInput, IEditorSite}
import org.eclipse.ui.forms.IManagedForm
import org.eclipse.ui.forms.editor.{ FormEditor, FormPage }
import org.eclipse.ui.forms.widgets.{FormToolkit, ScrolledForm}
import org.eclipse.zest.core.widgets.{Graph, GraphNode, GraphConnection, ZestStyles}
import org.eclipse.zest.layouts.LayoutStyles
import org.eclipse.zest.layouts.algorithms.SpringLayoutAlgorithm
import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.SWT
import org.eclipse.swt.widgets.Composite;


class GraphicPage(editor: FormEditor) extends FormPage(editor, classOf[GraphicPage].getName, "Graphic") {

  var container: Composite = _
  var dpu: DataProcessingUnit = _

  override protected def createFormContent(managedForm: IManagedForm) {
    val toolkit = managedForm.getToolkit
    val form = managedForm.getForm
    form.setText("Data Processing Unit - Graph View");
    val body = form.getBody
    toolkit.decorateFormHeading(form.getForm)
    toolkit.paintBordersFor(body)
    body.setLayout(new FillLayout)

    container = toolkit.createComposite(body)
    container.setLayout(new FillLayout)
    managedForm.getToolkit().paintBordersFor(container);
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
    val file = input.getAdapter(classOf[IFile]).asInstanceOf[IFile]
    var in: InputStream = null
    try {
      val context = JAXBContext.newInstance(classOf[DataProcessingUnit])
      val um = context.createUnmarshaller
      in = file.getContents
      dpu = um.unmarshal(in).asInstanceOf[DataProcessingUnit]
      updateView(dpu)
    } catch {
      case e: CoreException => e.printStackTrace
      case e: JAXBException => e.printStackTrace
    } finally {
      if (in != null)
        try {
          in.close
        } catch {
          case e: IOException => e.printStackTrace
        }
    }
  }

  def updateView(dpu: DataProcessingUnit) {
    if (container == null)
      return
    val graph = new Graph(container, SWT.NONE)
    val nodesArray = for(node <- dpu.nodes) yield (node.id -> new GraphNode(graph,SWT.NONE, node.id))
    var nodes:mMap[String, GraphNode] = new HashMap
    nodesArray foreach {case (id, node) => nodes += (id -> node)}
    dpu.edges foreach(e => new GraphConnection(graph, ZestStyles.CONNECTIONS_DIRECTED, nodes(e.sourceId), nodes(e.targetId)))
    graph.setLayoutAlgorithm(new SpringLayoutAlgorithm(LayoutStyles.NO_LAYOUT_NODE_RESIZING), true)
  }
}