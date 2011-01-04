package de.lmu.ifi.dbs.medmon.base.ui.cluster;

public abstract class ClusterTableItem<E> {

	private String label;
	private final E source;

	public ClusterTableItem(String label, E source) {
		this.label = label;
		this.source = source;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public E getSource() {
		return source;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("ClusterTableItem [label=");
		builder.append(label);
		builder.append(", source=");
		builder.append(source);
		builder.append("]");
		return builder.toString();
	}

}
