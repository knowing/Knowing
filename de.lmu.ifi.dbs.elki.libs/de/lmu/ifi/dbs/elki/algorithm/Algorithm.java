package de.lmu.ifi.dbs.elki.algorithm;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.result.AnyResult;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;

/**
 * <p>
 * Specifies the requirements for any algorithm that is to be executable by the
 * main class.
 * </p>
 * <p/>
 * <p>
 * Any implementation needs not to take care of input nor output, parsing and so
 * on. Those tasks are performed by the framework. An algorithm simply needs to
 * ask for parameters that are algorithm specific.
 * </p>
 * <p/>
 * <p>
 * <b>Note:</b> Any implementation is supposed to provide a constructor without
 * parameters (default constructor).
 * </p>
 * 
 * @author Arthur Zimek
 * @param <O> the type of DatabaseObjects handled by this Algorithm
 * @param <R> the type of result to retrieve from this Algorithm
 * @see AbstractAlgorithm
 */
// TODO: does R need to be a Result? Why not an arbitrary object?
public interface Algorithm<O extends DatabaseObject, R extends AnyResult> extends Parameterizable {
  /**
   * Runs the algorithm.
   * 
   * @param database the database to run the algorithm on
   * @return the Result computed by this algorithm
   * @throws IllegalStateException if the algorithm has not been initialized
   *         properly (e.g. the setParameters(String[]) method has been failed
   *         to be called).
   */
  R run(Database<O> database) throws IllegalStateException;
}