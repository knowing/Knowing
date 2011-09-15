package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

@Label(standard = "type", full = "node type")
public enum NodeType {

	@Label(standard = "processor")
	@EnumSerialization(primary = "processor")
	PROCESSOR,

	@Label(standard = "loader")
	@EnumSerialization(primary = "loader")
	LOADER,

	@Label(standard = "saver")
	@EnumSerialization(primary = "saver")
	SAVER,

	@Label(standard = "presenter")
	@EnumSerialization(primary = "presenter")
	PRESENTER,

	@Label(standard = "classifier")
	@EnumSerialization(primary = "classifier")
	CLASSIFIER,

	@Label(standard = "filter")
	@EnumSerialization(primary = "filter")
	FILTER
}
