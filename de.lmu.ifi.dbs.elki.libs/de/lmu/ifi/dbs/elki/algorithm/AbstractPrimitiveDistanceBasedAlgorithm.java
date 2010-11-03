package de.lmu.ifi.dbs.elki.algorithm;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PrimitiveDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.result.AnyResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * Provides an abstract algorithm already setting the distance function.
 * 
 * This class only allows distances that are defined on arbitrary objects, not only database objects!
 * 
 * @author Arthur Zimek
 * @param <O> the type of DatabaseObjects handled by this Algorithm
 * @param <D> the type of Distance used by this Algorithm
 * @param <R> the type of result to retrieve from this Algorithm
 */
public abstract class AbstractPrimitiveDistanceBasedAlgorithm<O extends DatabaseObject, D extends Distance<D>, R extends AnyResult> extends AbstractAlgorithm<O, R> {
  /**
   * Parameter to specify the distance function to determine the distance
   * between database objects, must extend
   * {@link de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction}.
   * <p>
   * Key: {@code -algorithm.distancefunction}
   * </p>
   * <p>
   * Default value:
   * {@link de.lmu.ifi.dbs.elki.distance.distancefunction.EuclideanDistanceFunction}
   * </p>
   */
  protected final ObjectParameter<PrimitiveDistanceFunction<O,D>> DISTANCE_FUNCTION_PARAM = new ObjectParameter<PrimitiveDistanceFunction<O, D>>(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID, PrimitiveDistanceFunction.class, EuclideanDistanceFunction.class);

  /**
   * Holds the instance of the distance function specified by
   * {@link #DISTANCE_FUNCTION_PARAM}.
   */
  private PrimitiveDistanceFunction<? super O, D> distanceFunction;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  protected AbstractPrimitiveDistanceBasedAlgorithm(Parameterization config) {
    super();
    config = config.descend(this);
    distanceFunction = getParameterDistanceFunction(config, EuclideanDistanceFunction.class, PrimitiveDistanceFunction.class);
  }

  /**
   * Constructor.
   * 
   * @param distanceFunction Distance function
   */
  protected AbstractPrimitiveDistanceBasedAlgorithm(PrimitiveDistanceFunction<? super O, D> distanceFunction) {
    super();
    this.distanceFunction = distanceFunction;
  }

  /**
   * Returns the distanceFunction.
   * 
   * @return the distanceFunction
   */
  public PrimitiveDistanceFunction<? super O, D> getDistanceFunction() {
    return distanceFunction;
  }
}