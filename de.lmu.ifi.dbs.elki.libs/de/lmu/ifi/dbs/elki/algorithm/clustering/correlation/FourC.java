package de.lmu.ifi.dbs.elki.algorithm.clustering.correlation;

import de.lmu.ifi.dbs.elki.algorithm.clustering.AbstractProjectedDBSCAN;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.preprocessing.FourCPreprocessor;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * 4C identifies local subgroups of data objects sharing a uniform correlation.
 * The algorithm is based on a combination of PCA and density-based clustering
 * (DBSCAN).
 * <p>
 * Reference: Christian Böhm, Karin Kailing, Peer Kröger, Arthur
 * Zimek: Computing Clusters of Correlation Connected Objects. <br>
 * In Proc. ACM SIGMOD Int. Conf. on Management of Data, Paris, France, 2004.
 * </p>
 * 
 * @author Arthur Zimek
 * @param <O> type of NumberVector handled by this Algorithm
 */
@Title("4C: Computing Correlation Connected Clusters")
@Description("4C identifies local subgroups of data objects sharing a uniform correlation. " + "The algorithm is based on a combination of PCA and density-based clustering (DBSCAN).")
@Reference(authors = "C. Böhm, K. Kailing, P. Kröger, A. Zimek", title = "Computing Clusters of Correlation Connected Objects", booktitle = "Proc. ACM SIGMOD Int. Conf. on Management of Data, Paris, France, 2004, 455-466", url="http://dx.doi.org/10.1145/1007568.1007620")
public class FourC<O extends NumberVector<O, ?>> extends AbstractProjectedDBSCAN<O> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(FourC.class);
  
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public FourC(Parameterization config) {
    super(config);
    config = config.descend(this);
  }

  @Override
  public Class<?> preprocessorClass() {
    return FourCPreprocessor.class;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }

  @Override
  public String getLongResultName() {
    return "FourC Clustering";
  }

  @Override
  public String getShortResultName() {
    return "fourc-clustering";
  }
}