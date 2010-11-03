package de.lmu.ifi.dbs.elki.algorithm.outlier;

import java.util.ArrayList;
import java.util.List;

import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.OPTICS;
import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.AssociationID;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.datastore.WritableDataStore;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.math.MinMax;
import de.lmu.ifi.dbs.elki.result.AnnotationFromDataStore;
import de.lmu.ifi.dbs.elki.result.AnnotationResult;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierResult;
import de.lmu.ifi.dbs.elki.result.outlier.OutlierScoreMeta;
import de.lmu.ifi.dbs.elki.result.outlier.QuotientOutlierScoreMeta;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * OPTICSOF provides the Optics-of algorithm, an algorithm to find Local
 * Outliers in a database.
 * <p>
 * Reference:<br>
 * Markus M. Breunig, Hans-Peter Kriegel, Raymond T. N, Jörg Sander:<br />
 * OPTICS-OF: Identifying Local Outliers<br />
 * In Proc. of the 3rd European Conference on Principles of Knowledge Discovery
 * and Data Mining (PKDD), Prague, Czech Republic
 * 
 * @author Ahmed Hettab
 * 
 * @param <O> DatabaseObject
 */
@Title("OPTICS-OF: Identifying Local Outliers")
@Description("Algorithm to compute density-based local outlier factors in a database based on the neighborhood size parameter 'minpts'")
@Reference(authors = "M. M. Breunig, H.-P. Kriegel, R. Ng, and J. Sander", title = "OPTICS-OF: Identifying Local Outliers", booktitle = "Proc. of the 3rd European Conference on Principles of Knowledge Discovery and Data Mining (PKDD), Prague, Czech Republic", url = "http://springerlink.metapress.com/content/76bx6413gqb4tvta/")
public class OPTICSOF<O extends DatabaseObject, D extends NumberDistance<D, ?>> extends AbstractDistanceBasedAlgorithm<O, D, OutlierResult> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(OPTICSOF.class);
  
  /**
   * Parameter to specify the threshold MinPts.
   */
  private int minpts;

  /**
   * The association id to associate the OPTICS_OF_SCORE of an object for the OF
   * algorithm.
   */
  public static final AssociationID<Double> OPTICS_OF_SCORE = AssociationID.getOrCreateAssociationID("optics-of", Double.class);

  /**
   * Constructor with parameters.
   * @param distanceFunction distance function
   * @param minpts minPts parameter
   */
  public OPTICSOF(DistanceFunction<? super O, D> distanceFunction, int minpts) {
    super(distanceFunction);
    this.minpts = minpts;
  }

  @Override
  protected OutlierResult runInTime(Database<O> database) throws IllegalStateException {
    DistanceQuery<O, D> distQuery = getDistanceFunction().instantiate(database);
    DBIDs ids = database.getIDs();

    WritableDataStore<List<DistanceResultPair<D>>> nMinPts = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, List.class);
    WritableDataStore<Double> coreDistance = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, Double.class);
    WritableDataStore<Integer> minPtsNeighborhoodSize = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, Integer.class);

    // Pass 1
    // N_minpts(id) and core-distance(id)

    for(DBID id : database) {
      List<DistanceResultPair<D>> minptsNegibours = database.kNNQueryForID(id, minpts, distQuery);
      Double d = minptsNegibours.get(minptsNegibours.size() - 1).getDistance().doubleValue();
      nMinPts.put(id, minptsNegibours);
      coreDistance.put(id, d);
      minPtsNeighborhoodSize.put(id, database.rangeQuery(id, d.toString(), distQuery).size());
    }

    // Pass 2
    WritableDataStore<List<Double>> reachDistance = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, List.class);
    WritableDataStore<Double> lrds = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_HOT | DataStoreFactory.HINT_TEMP, Double.class);
    for(DBID id : database) {
      List<Double> core = new ArrayList<Double>();
      double lrd = 0;
      for(DistanceResultPair<D> neighPair : nMinPts.get(id)) {
        DBID idN = neighPair.getID();
        double coreDist = coreDistance.get(idN);
        double dist = distQuery.distance(id, idN).doubleValue();
        Double rd = Math.max(coreDist, dist);
        lrd = rd + lrd;
        core.add(rd);
      }
      lrd = (minPtsNeighborhoodSize.get(id) / lrd);
      reachDistance.put(id, core);
      lrds.put(id, lrd);
    }

    // Pass 3
    MinMax<Double> ofminmax = new MinMax<Double>();
    WritableDataStore<Double> ofs = DataStoreUtil.makeStorage(ids, DataStoreFactory.HINT_STATIC, Double.class);
    for(DBID id : database) {
      double of = 0;
      for(DistanceResultPair<D> pair : nMinPts.get(id)) {
        DBID idN = pair.getID();
        double lrd = lrds.get(id);
        double lrdN = lrds.get(idN);
        of = of + lrdN / lrd;
      }
      of = of / minPtsNeighborhoodSize.get(id);
      ofs.put(id, of);
      // update minimum and maximum
      ofminmax.put(of);
    }
    // Build result representation.
    AnnotationResult<Double> scoreResult = new AnnotationFromDataStore<Double>("OPTICS Outlier Scores", "optics-outlier", OPTICS_OF_SCORE, ofs);
    OutlierScoreMeta scoreMeta = new QuotientOutlierScoreMeta(ofminmax.getMin(), ofminmax.getMax(), 0.0, Double.POSITIVE_INFINITY, 1.0);
    return new OutlierResult(scoreMeta, scoreResult);
  }

  /**
   * Factory method for {@link Parameterizable}
   * 
   * @param config Parameterization
   * @return KNN outlier detection algorithm
   */
  public static <O extends DatabaseObject, D extends NumberDistance<D, ?>> OPTICSOF<O, D> parameterize(Parameterization config) {
    int minpts = getParameterMinPts(config);
    DistanceFunction<O, D> distanceFunction = getParameterDistanceFunction(config);
    return new OPTICSOF<O, D>(distanceFunction, minpts);
  }

  /**
   * Get the minPts parameter for the algorithm
   * 
   * @param config Parameterization
   * @return minPts parameter
   */
  protected static int getParameterMinPts(Parameterization config) {
    final IntParameter param = new IntParameter(OPTICS.MINPTS_ID, new GreaterConstraint(1));
    if(config.grab(param)) {
      return param.getValue();
    }
    return -1;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}