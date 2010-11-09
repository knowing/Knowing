package de.lmu.ifi.dbs.medmon.developer.ui.provider;

public class ClusterFile {

	private String label;
	private String file;
	
	public ClusterFile() { }
	
	public ClusterFile(String label, String file) {
		this.label = label;
		this.file = file;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String toString() {
		return "ClusterFile [label=" + label + ", file=" + file + "]";
	}
	
	
	
}
