package de.lmu.ifi.dbs.elki.math.linearalgebra.pca;

import java.util.Collection;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.math.linearalgebra.EigenvalueDecomposition;
import de.lmu.ifi.dbs.elki.math.linearalgebra.Matrix;
import de.lmu.ifi.dbs.elki.math.linearalgebra.SortedEigenPairs;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;

/**
 * Class to run PCA on given data.
 * 
 * The various methods will start PCA at different places (e.g. with database
 * IDs, database query results, a precomputed covariance matrix or eigenvalue
 * decomposition).
 * 
 * The runner can be parameterized by setting a covariance matrix builder (e.g.
 * to a weighted covariance matrix builder)
 * 
 * @author Erich Schubert
 * 
 * @param <V> Vector type
 * @param <D> Distance type
 */
public class PCARunner<V extends NumberVector<?, ?>, D extends NumberDistance<D, ?>> implements Parameterizable {
  /**
   * OptionID for {@link #COVARIANCE_PARAM}
   */
  public static final OptionID PCA_COVARIANCE_MATRIX = OptionID.getOrCreateOptionID("pca.covariance", "Class used to compute the covariance matrix.");

  /**
   * Parameter to specify the class to compute the covariance matrix, must be a
   * subclass of {@link AbstractCovarianceMatrixBuilder}.
   * <p>
   * Default value: {@link AbstractCovarianceMatrixBuilder}
   * </p>
   * <p>
   * Key: {@code -pca.covariance}
   * </p>
   */
  private ObjectParameter<AbstractCovarianceMatrixBuilder<V, D>> COVARIANCE_PARAM = new ObjectParameter<AbstractCovarianceMatrixBuilder<V, D>>(PCA_COVARIANCE_MATRIX, AbstractCovarianceMatrixBuilder.class, StandardCovarianceMatrixBuilder.class);

  /**
   * The covariance computation class.
   */
  protected AbstractCovarianceMatrixBuilder<V, D> abstractCovarianceMatrixBuilder;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public PCARunner(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(COVARIANCE_PARAM)) {
      abstractCovarianceMatrixBuilder = COVARIANCE_PARAM.instantiateClass(config);
    }
  }

  /**
   * Run PCA on the complete database
   * 
   * @param database the database used
   * @return PCA result
   */
  public PCAResult processDatabase(Database<? extends V> database) {
    return processCovarMatrix(abstractCovarianceMatrixBuilder.processDatabase(database));
  }

  /**
   * Run PCA on a collection of database IDs
   * 
   * @param ids a collection of ids
   * @param database the database used
   * @return PCA result
   */
  public PCAResult processIds(DBIDs ids, Database<? extends V> database) {
    return processCovarMatrix(abstractCovarianceMatrixBuilder.processIds(ids, database));
  }

  /**
   * Run PCA on a QueryResult Collection
   * 
   * @param results a collection of QueryResults
   * @param database the database used
   * @return PCA result
   */
  public PCAResult processQueryResult(Collection<DistanceResultPair<D>> results, Database<? extends V> database) {
    return processCovarMatrix(abstractCovarianceMatrixBuilder.processQueryResults(results, database));
  }

  /**
   * Process an existing covariance Matrix
   * 
   * @param covarMatrix the matrix used for performing pca
   * @return PCA result
   */
  public PCAResult processCovarMatrix(Matrix covarMatrix) {
    // TODO: add support for a different implementation to do EVD?
    EigenvalueDecomposition evd = covarMatrix.eig();
    return processEVD(evd);
  }

  /**
   * Process an existing eigenvalue decomposition
   * 
   * @param evd eigenvalue decomposition to use
   * @return PCA result
   */
  public PCAResult processEVD(EigenvalueDecomposition evd) {
    SortedEigenPairs eigenPairs = new SortedEigenPairs(evd, false);
    return new PCAResult(eigenPairs);
  }

  /**
   * Get covariance matrix builder
   * 
   * @return covariance matrix builder in use
   */
  public AbstractCovarianceMatrixBuilder<V, D> getCovarianceMatrixBuilder() {
    return abstractCovarianceMatrixBuilder;
  }

  /**
   * Set covariance matrix builder.
   * 
   * @param covarianceBuilder New covariance matrix builder.
   */
  public void setCovarianceMatrixBuilder(AbstractCovarianceMatrixBuilder<V, D> covarianceBuilder) {
    this.abstractCovarianceMatrixBuilder = covarianceBuilder;
  }
}
