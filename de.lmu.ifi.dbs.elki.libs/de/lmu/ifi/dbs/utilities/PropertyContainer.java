package de.lmu.ifi.dbs.utilities;

import java.awt.Dimension;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

import java.awt.Color;
import java.awt.Point;
import java.util.Collections;

/**
 * Class for storing and fetching properties to/from a Properties-file.
 */
public class PropertyContainer {

    protected final Logger log = Logger.getLogger(PropertyContainer.class.getName());
    protected final Properties properties = new Properties();
    protected final String SEPARATOR = ";";
    protected final File configfile;
    protected String comment = null;

    /**
     * uses the specified file as onfig file
     * 
     * @param file
     */
    public PropertyContainer(File file) throws IOException {
        if (file == null) {
            throw new NullPointerException("Filename mustn't be null");
        }
        this.configfile = file;
        this.initialize();
    }

    /**
     * Create a new PropertyContainer in the specified directory. If the
     * directoriy or configfile doesn't exist, it'll be created
     * 
     * @param dir
     * @param filename
     * @throws IOException
     */
    public PropertyContainer(File dir, String filename) throws IOException {
        if (filename == null) {
            throw new NullPointerException("Filename ain't be null");
        }
        if (dir == null) {
            throw new NullPointerException("Directory must not be null");
        }

        // check directory
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("could not create config dir: " + dir.toString());
        }

        this.configfile = new File(dir, filename);
        this.initialize();
    }

    private void initialize() throws IOException {
        properties.load(new FileInputStream(configfile));
    }

    /**
     * returns list of keys in sorted order
     * @return
     */
    public List<String> getKeys() {
        ArrayList<String> l = new ArrayList<String>();
        for (Object k : properties.keySet()) {
            l.add((String) k);
        }
        Collections.sort(l);
        return l;
    }

    @Override
    public String toString() {
        return toSplitString(" = ");
    }

    public String toSplitString(String splitSeparator) {
        StringBuilder sb = new StringBuilder(properties.keySet().size() * 15);
        ArrayList l = new ArrayList(properties.keySet());
        Collections.sort(l);
        for (Object keyObject : l) {
            String key = (String) keyObject;
            String val = (String) properties.get(key);
            sb.append(key + splitSeparator + val + "\n");
        }
        return sb.toString();
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public String getProperty(String string, String string0) {
        return properties.getProperty(string, string0);
    }

    public synchronized void save() throws IOException {
        if (!configfile.exists()) {
            configfile.getParentFile().mkdirs();
            configfile.createNewFile();
        }
        properties.store(new FileOutputStream(configfile), comment);
    }

    public void removeProperty(String key) {
        properties.remove(key);
    }

    public String setProperty(String key, String value) {
        String old = getProperty(key);
        properties.setProperty(key, value);
//        firePropertyChange(key, old, value);
        return old;
    }

    public String getString(String key) {
        return getProperty(key);
    }

    /**
     * Simple delegate to {@link #setProperty(java.lang.String, java.lang.String)}
     */
    public String setString(String key, String value) {
        return setProperty(key, value);
    }

    public Integer getInteger(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return Integer.parseInt(value);
    }

    public Integer getInteger(String key, int def) {
        Integer i = getInteger(key);
        if (i == null) {
            return def;
        }
        return i;
    }

    public Integer setProperty(String key, int i) {
        Integer old = getInteger(key);
        setProperty(key, Integer.toString(i));
//        firePropertyChange(key, old, getInteger(key));
        return old;
    }

    public Double getDouble(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return Double.parseDouble(value);
    }

    public Double setProperty(String key, double i) {
        Double old = getDouble(key);
        setProperty(key, Double.toString(i));
//        firePropertyChange(key, old, getInteger(key));
        return old;
    }

    public Boolean getBoolean(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        return new Boolean(value);
    }

    public Boolean getBoolean(String key, Boolean default_value) {
        Boolean val = this.getBoolean(key);
        return (val == null) ? default_value : val;
    }

    public boolean setProperty(String key, boolean b) {
        Boolean old = getBoolean(key);
        setProperty(key, Boolean.toString(b));
//        firePropertyChange(key, old, getBoolean(key));
        return old;
    }

    public File getFile(String key) {
        String value = getProperty(key);
        if (value == null || value.length() == 0) {
            return null;
        }
        return new File(value);
    }

    public File getFile(String key, File def) {
        File f = getFile(key);
        if (f == null) {
            return def;
        }
        return f;
    }

    public File getFile(File parent, String key) {
        String s = getString(key);
        if (s == null) {
            return null;
        }
        return new File(parent, s);
    }

    public File setProperty(String key, File f) {
        File old = getFile(key);
        if (f == null) {
            removeProperty(key);
        } else {
            setProperty(key, f.getAbsolutePath());
        }
//        firePropertyChange(key, old, getFile(key));
        return old;
    }

    public List<String> getStringArray(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }

        ArrayList<String> list = new ArrayList<String>(0);
        String[] p = value.split(SEPARATOR);
        for (String o : p) {
            list.add(o);
        }
        return list;
    }

    public List<String> setProperty(String key, String[] tags) {
        List<String> old = getStringArray(key);
        setProperty(key, Arrays2.join(tags, SEPARATOR));
//        firePropertyChange(key, old, getStringArray(key));
        return old;
    }

    public List<String> setProperty(String key, List<String> tags) {
        String[] s = new String[tags.size()];
        tags.toArray(s);
        return setProperty(key, s);
    }

    public Dimension getDimension(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }

        Dimension d = null;
        String[] s = value.split(SEPARATOR);
        if (s.length == 2) {
            d = new Dimension();
            d.width = Integer.parseInt(s[0]);
            d.height = Integer.parseInt(s[1]);
        }
        return d;
    }

    public Dimension setProperty(String key, Dimension d) {
        Dimension old = getDimension(key);
        setProperty(key, d.width + SEPARATOR + d.height);
//        firePropertyChange(key, old, getDimension(key));
        return old;
    }

    public Color getColor(String key) {
        List<String> c = getStringArray(key);
        if (c == null || c.size() < 3) {
            return null;
        }
        int r = Integer.parseInt(c.get(0));
        int g = Integer.parseInt(c.get(1));
        int b = Integer.parseInt(c.get(2));

        if (c.size() == 3) {
            return new Color(r, g, b);
        } else {
            int a = Integer.parseInt(c.get(3));
            return new Color(r, g, b, a);
        }
    }

    public Color setProperty(String key, Color c) {
        Color old = getColor(key);
        String s = c.getRed() + SEPARATOR + c.getGreen() + SEPARATOR + c.getBlue() + SEPARATOR + c.getAlpha();
        setProperty(key, s);
//        firePropertyChange(key, old, getColor(key));
        return old;
    }

    public Point getPoint(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }

        Point p = new Point();
        String[] s = value.split(SEPARATOR);
        if (s.length == 2) {
            p.x = Integer.parseInt(s[0]);
            p.y = Integer.parseInt(s[1]);
        }
        return p;
    }

    public Point setProperty(String key, Point p) {
        Point old = getPoint(key);
        setProperty(key, p.x + SEPARATOR + p.y);
//        firePropertyChange(key, old, getPoint(key));
        return old;
    }
}
