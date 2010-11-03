package de.lmu.ifi.dbs.elki.visualization.opticsplot;

import de.lmu.ifi.dbs.elki.distance.distancevalue.CorrelationDistance;
import de.lmu.ifi.dbs.elki.result.ClusterOrderEntry;

/**
 * Adapter that will map a correlation distance to its dimensionality.
 * 
 * @author Erich Schubert
 */
public class OPTICSCorrelationDimensionalityDistance<D extends CorrelationDistance<D>> implements OPTICSDistanceAdapter<D> {
  /**
   * Default constructor.
   */
  public OPTICSCorrelationDimensionalityDistance() {
    super();
  }

  @Override
  public double getDoubleForEntry(ClusterOrderEntry<D> coe) {
    final D reachability = coe.getReachability();
    if (reachability == null) {
      return Double.POSITIVE_INFINITY;
    }
    if (reachability.isInfiniteDistance() || reachability.isUndefinedDistance()) {
      return Double.POSITIVE_INFINITY;
    }
    return reachability.getCorrelationValue();
  }
}