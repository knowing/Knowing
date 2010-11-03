package de.lmu.ifi.dbs.utilities.io;

import java.io.File;

/**
 * Class for printing common debug information which are used in case of
 * IOExceptions.
 *
 * 
 * @author graf
 */
public class FileInfo {

    public static String getInfo(File f) {
        String nl = System.getProperty("line.separator");

        StringBuilder sb = new StringBuilder(150);
        sb.append(nl);
        sb.append("File:     '" + f + "'" + nl);
        sb.append("isNull:    " + (f == null) + nl);
        if (f != null) {
            sb.append("isFile:    " + (f.isFile()) + nl);
            sb.append("isDir:     " + (f.isDirectory()) + nl);
            sb.append("exists:    " + (f.exists()) + nl);
            sb.append("readable:  " + (f.canRead()) + nl);
            sb.append("writeable: " + (f.canWrite()) + nl);
            sb.append("exec:      " + (f.canExecute()) + nl);
            sb.append("size:      " + (f.length()) + nl);
        }
        return sb.toString();
    }
}
