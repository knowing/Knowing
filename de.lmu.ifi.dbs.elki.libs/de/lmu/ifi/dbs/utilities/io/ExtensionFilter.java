package de.lmu.ifi.dbs.utilities.io;

import java.io.File;

public class ExtensionFilter implements java.io.FilenameFilter {

    private final String extension;

    public ExtensionFilter(String extension) {
        this.extension = extension;
    }

    @Override
    public boolean accept(File dir, String name) {
        return name.endsWith(extension);
    }
}
