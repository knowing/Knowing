package de.lmu.ifi.dbs.medmon.developer.ui.dnd;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TransferData;

public class ProcessorTransfer extends ByteArrayTransfer {

	private static ProcessorTransfer _instance = new ProcessorTransfer();
	
	private static final String IDATAPROCESSOR = "IDATAPROCESSOR";
	private static final int IDATAPROCESSOR_ID = 0;
	
	public static ProcessorTransfer getInstance() {
		return _instance;
	}
	
	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		transferData.result = 0;
		if (!checkProcessor(object) || !isSupportedType(transferData)) {
			DND.error(DND.ERROR_INVALID_DATA);
		}
		super.javaToNative(object, transferData);
	}
	
	@Override
	protected Object nativeToJava(TransferData transferData) {
		
		return super.nativeToJava(transferData);
	}
	
	private boolean checkProcessor(Object object) {
		return object != null;
	}

	
	@Override
	protected String[] getTypeNames() {
		return new String[] { IDATAPROCESSOR };
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { IDATAPROCESSOR_ID };
	}

}
