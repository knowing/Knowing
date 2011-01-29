package de.lmu.ifi.dbs.medmon.base.ui.filter;

import java.io.File;
import java.io.FileFilter;

public class XMLFileFilter implements FileFilter {

	@Override
	public boolean accept(File pathname) {
		return pathname.getName().endsWith(".xml");
	}


}
