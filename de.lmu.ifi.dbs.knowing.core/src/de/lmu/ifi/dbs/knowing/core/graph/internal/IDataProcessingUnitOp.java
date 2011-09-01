package de.lmu.ifi.dbs.knowing.core.graph.internal;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.ResourceStoreException;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

import de.lmu.ifi.dbs.knowing.core.graph.IDataProcessingUnit;
import de.lmu.ifi.dbs.knowing.core.graph.IFileDataProcessingUnit;

public final class IDataProcessingUnitOp {

	public static final Status execute(final IFileDataProcessingUnit context, final ProgressMonitor monitor) {
		// Do something here.
		System.out.println("Execute in IFileDataProcessingUnitOp");
		IDataProcessingUnit dpu = context.getDataProcessingUnit();
		try {
			System.out.println("(1) Try to store: " + dpu.getName().getContent());
			dpu.resource().save();
		} catch (ResourceStoreException e1) {
			e1.printStackTrace();
		}
		try {
			XmlResourceStore store = new XmlResourceStore(new File("/home/muki/sapphire.dpu.xml"));
			dpu.refresh(true);
			RootXmlResource rootSource = new RootXmlResource(store);
			rootSource.init(dpu);
			rootSource.save();
			System.out.println("(2) Try to store : " + dpu.getName().getContent());
		} catch (ResourceStoreException e) {
			e.printStackTrace();
		}

		return Status.createOkStatus();
	}

	public static byte[] getBytes(Object obj) throws java.io.IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(obj);
		oos.flush();
		oos.close();
		bos.close();
		return bos.toByteArray();
	}
}
