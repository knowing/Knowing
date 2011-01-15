package de.lmu.ifi.dbs.medmon.developer.ui.handler;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessingUnit;
import de.lmu.ifi.dbs.medmon.developer.ui.editor.ProcessorUnitEditorInput;

public class ExportDPUHandler extends AbstractHandler implements IHandler {

	public static final String ID = "de.lmu.ifi.dbs.medmon.developer.ui.exportDPU";
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		//TODO check if WorkbenchSelection || ActiveEditor
		IEditorPart editor = HandlerUtil.getActiveEditor(event);
		ProcessorUnitEditorInput input = (ProcessorUnitEditorInput) editor.getEditorInput();
		DataProcessingUnit dpu = input.getDpu();
		
		FileDialog saveDialog = new FileDialog(HandlerUtil.getActiveShell(event), SWT.SAVE);
		saveDialog.setFilterExtensions(new String[] { "*.xml" });
		saveDialog.setFilterNames(new String[] { "Data Processing Unit" });
		String filepath = saveDialog.open();
		if(!filepath.endsWith(".xml")) 
			filepath += ".xml";
		File file = new File(filepath);
		try {
			JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
			Marshaller marshaller = context.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(dpu, file);
			MessageDialog.openInformation(HandlerUtil.getActiveShell(event), "Export successful", dpu + " was successful exported");
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return null;
	}

}
