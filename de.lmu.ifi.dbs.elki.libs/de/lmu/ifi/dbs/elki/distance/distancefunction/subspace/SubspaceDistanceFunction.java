package de.lmu.ifi.dbs.elki.distance.distancefunction.subspace;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractPreprocessorBasedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.LocalProjectionPreprocessorBasedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.WeightedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.SubspaceDistance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.PCAFilteredResult;
import de.lmu.ifi.dbs.elki.preprocessing.KNNQueryBasedLocalPCAPreprocessor;
import de.lmu.ifi.dbs.elki.preprocessing.AbstractLocalPCAPreprocessor;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * Provides a distance function to determine a kind of correlation distance
 * between two points, which is a pair consisting of the distance between the
 * two subspaces spanned by the strong eigenvectors of the two points and the
 * affine distance between the two subspaces.
 * 
 * @author Elke Achtert
 */
public class SubspaceDistanceFunction extends AbstractPreprocessorBasedDistanceFunction<NumberVector<?, ?>, AbstractLocalPCAPreprocessor, SubspaceDistance> implements LocalProjectionPreprocessorBasedDistanceFunction<NumberVector<?, ?>, AbstractLocalPCAPreprocessor, PCAFilteredResult, SubspaceDistance> {
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public SubspaceDistanceFunction(Parameterization config) {
    super(config);
    config = config.descend(this);
  }

  @Override
  public SubspaceDistance getDistanceFactory() {
    return SubspaceDistance.FACTORY;
  }

  /**
   * @return the name of the default preprocessor, which is
   *         {@link de.lmu.ifi.dbs.elki.preprocessing.KNNQueryBasedLocalPCAPreprocessor}
   */
  @Override
  public Class<?> getDefaultPreprocessorClass() {
    return KNNQueryBasedLocalPCAPreprocessor.class;
  }

  @Override
  public final String getPreprocessorDescription() {
    return "Preprocessor class to determine the correlation dimension of each object.";
  }

  @Override
  public Class<AbstractLocalPCAPreprocessor> getPreprocessorSuperClass() {
    return ClassGenericsUtil.uglyCastIntoSubclass(AbstractLocalPCAPreprocessor.class);
  }

  @Override
  public Class<? super NumberVector<?, ?>> getInputDatatype() {
    return NumberVector.class;
  }

  @Override
  public <V extends NumberVector<?, ?>> Instance<V> instantiate(Database<V> database) {
    return new Instance<V>(database, getPreprocessor().instantiate(database), this);
  }

  /**
   * The actual instance bound to a particular database.
   * 
   * @author Erich Schubert
   */
  public static class Instance<V extends NumberVector<?, ?>> extends AbstractPreprocessorBasedDistanceFunction.Instance<V, AbstractLocalPCAPreprocessor.Instance<V>, PCAFilteredResult, SubspaceDistance> {
    /**
     * @param database
     * @param preprocessor
     */
    public Instance(Database<V> database, AbstractLocalPCAPreprocessor.Instance<V> preprocessor, SubspaceDistanceFunction distanceFunction) {
      super(database, preprocessor, distanceFunction);
    }

    /**
     * Note, that the pca of o1 must have equal ore more strong eigenvectors
     * than the pca of o2.
     * 
     */
    @Override
    public SubspaceDistance distance(DBID id1, DBID id2) {
      PCAFilteredResult pca1 = preprocessor.get(id1);
      PCAFilteredResult pca2 = preprocessor.get(id2);
      V o1 = database.get(id1);
      V o2 = database.get(id2);
      return distance(o1, o2, pca1, pca2);
    }

    /**
     * Computes the distance between two given DatabaseObjects according to this
     * distance function. Note, that the first pca must have an equal number of
     * strong eigenvectors than the second pca.
     * 
     * @param o1 first DatabaseObject
     * @param o2 second DatabaseObject
     * @param pca1 first PCA
     * @param pca2 second PCA
     * @return the distance between two given DatabaseObjects according to this
     *         distance function
     */
    public SubspaceDistance distance(V o1, V o2, PCAFilteredResult pca1, PCAFilteredResult pca2) {
      if(pca1.getCorrelationDimension() != pca2.getCorrelationDimension()) {
        throw new IllegalStateException("pca1.getCorrelationDimension() != pca2.getCorrelationDimension()");
      }

      Matrix strong_ev1 = pca1.getStrongEigenvectors();
      Matrix weak_ev2 = pca2.getWeakEigenvectors();
      Matrix m1 = weak_ev2.getColumnDimensionality() == 0 ? strong_ev1.transpose() : strong_ev1.transposeTimes(weak_ev2);
      double d1 = m1.norm2();

      WeightedDistanceFunction df1 = new WeightedDistanceFunction(pca1.similarityMatrix());
      WeightedDistanceFunction df2 = new WeightedDistanceFunction(pca2.similarityMatrix());

      double affineDistance = Math.max(df1.distance(o1, o2).doubleValue(), df2.distance(o1, o2).doubleValue());

      return new SubspaceDistance(d1, affineDistance);
    }
  }
}