package de.lmu.ifi.dbs.knowing.ui.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.lmu.ifi.dbs.knowing.core.graph.xml.DataProcessingUnit;

public class DPUUtil {

	public static InputStream createDPUInputStream(DataProcessingUnit dpu) throws JAXBException, IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		JAXBContext context = JAXBContext.newInstance(DataProcessingUnit.class);
		Marshaller marshaller = context.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		marshaller.marshal(dpu, bos);
		bos.close();
		return new ByteArrayInputStream(bos.toByteArray());
	}

	public static InputStream createDPUInputStream() throws JAXBException, IOException {
		return createDPUInputStream(new DataProcessingUnit());
	}
}
