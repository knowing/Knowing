package de.lmu.ifi.dbs.utilities.io;

import java.io.File;
import java.util.Comparator;

/**
 * Sort Files lexicographically by their names regarding name of parent dir
 */
public class FilenameComparator implements Comparator<File> {
	@Override
	public int compare(File o1, File o2) {
		// same files: 0
		if (o1.equals(o2))
			return 0;

		// different directory
		int dir = o1.getParentFile().getName().compareToIgnoreCase(
				o2.getParentFile().getName());
		if (dir != 0)
			return dir;

		// same directory
		return o1.getName().compareToIgnoreCase(o2.getName());
	}
}