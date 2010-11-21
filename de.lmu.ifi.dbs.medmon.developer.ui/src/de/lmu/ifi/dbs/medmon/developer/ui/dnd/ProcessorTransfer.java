package de.lmu.ifi.dbs.medmon.developer.ui.dnd;

import java.io.*;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

import de.lmu.ifi.dbs.medmon.datamining.core.processing.DataProcessor;

/**
 * DataProcessor serialization format is as follows:
 * (String)name of DataProcessor
 * (String)id of DataProcessor
 * (String)providedBy - The Bundle name which distribute this DataProcessor
 * @author Nepomuk Seiler
 *
 */
public class ProcessorTransfer extends ByteArrayTransfer {

	private static ProcessorTransfer instance = new ProcessorTransfer();
	
	private static final String TYPE_NAME = "Processor-Transfer-Format";
	private static final int TYPE_ID = registerType(TYPE_NAME);
	
	public static ProcessorTransfer getInstance() {
		return instance;
	}
	
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray((DataProcessor[])object);
		if(bytes != null)
			super.javaToNative(bytes, transferData);
	}
	
	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}
	
	protected DataProcessor[] fromByteArray(byte[] bytes) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
		
		try {
			//Read array size
			int n = in.readInt();
			//Read processors
			DataProcessor[] returns = new DataProcessor[n];
			for (int i = 0; i < n; i++) {
				DataProcessor processor = readProcessor(in);
				if(processor == null)
					return null;
				returns[i] = processor;
			}
			return returns;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private DataProcessor readProcessor(DataInputStream in) throws IOException  {
		String name = in.readUTF();
		String id = in.readUTF();
		String provider = in.readUTF();
		System.out.println("[Name " + name + "] [id " +  id +  "] [ provider " + provider + "]");
		return new DataProcessor(name, id, provider);
	}
	
	protected byte[] toByteArray(DataProcessor[] processors) {
	      ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	      DataOutputStream out = new DataOutputStream(byteOut);

	      byte[] bytes = null;

	      try {
	         /* write number of markers */
	         out.writeInt(processors.length);

	         /* write markers */
	         for (int i = 0; i < processors.length; i++) {
	            writeProcessor((DataProcessor)processors[i], out);
	         }
	         out.close();
	         bytes = byteOut.toByteArray();
	      } catch (IOException e) {
	         //when in doubt send nothing
	      }
	      return bytes;
	}
	
	private void writeProcessor(DataProcessor processor, DataOutputStream dataOut) throws IOException {
		dataOut.writeUTF(processor.getName());
		dataOut.writeUTF(processor.getId());
		dataOut.writeUTF(processor.getProvidedby());
	}
	
	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}

}
