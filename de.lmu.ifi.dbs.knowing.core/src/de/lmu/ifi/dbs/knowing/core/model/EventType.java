package de.lmu.ifi.dbs.knowing.core.model;

import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

@Label(standard = "type", full = "event type")
public enum EventType {
	
	@Label(standard = "All Events")
	@EnumSerialization(primary = "event")
	EVENT,

	@Label(standard = "Status")
	@EnumSerialization(primary = "status")
	STATUS,

	@Label(standard = "UIEvent")
	@EnumSerialization(primary = "uievent")
	UIEVENT,
	
	@Label(standard = "Results")
	@EnumSerialization(primary = "results")
	RESULTS,

	@Label(standard = "QueryResults")
	@EnumSerialization(primary = "queryresults")
	QUERYRESULTS,
	
	@Label(standard = "QueriesResults")
	@EnumSerialization(primary = "queriesresults")
	QUERIESRESULTS,

	@Label(standard = "Query")
	@EnumSerialization(primary = "query")
	QUERY,
	
	@Label(standard = "Queries")
	@EnumSerialization(primary = "queries")
	QUERIES
	
	
}
