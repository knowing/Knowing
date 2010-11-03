package de.lmu.ifi.dbs.elki.preprocessing;

import java.util.HashMap;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.SpatialIndexDatabase;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreFactory;
import de.lmu.ifi.dbs.elki.database.datastore.DataStoreUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDPair;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.DistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.LeafEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialIndex;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialNode;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.math.MeanVariance;
import de.lmu.ifi.dbs.elki.utilities.datastructures.KNNHeap;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * A preprocessor for annotation of the k nearest neighbors (and their
 * distances) to each database object.
 * 
 * Used for example by {@link de.lmu.ifi.dbs.elki.algorithm.outlier.LOF}.
 * 
 * @author Erich Schubert
 * @param <D> the type of distance the used distance function will return
 * @param <N> the type of spatial nodes in the spatial index
 * @param <E> the type of spatial entries in the spatial index
 */
@Title("Spatial Approximation Materialize kNN Preprocessor")
@Description("Caterializes the (approximate) k nearest neighbors of objects of a database using a spatial approximation.")
public class SpatialApproximationMaterializeKNNPreprocessor<D extends Distance<D>, N extends SpatialNode<N, E>, E extends SpatialEntry> extends MaterializeKNNPreprocessor<NumberVector<?, ?>, D> {
  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public SpatialApproximationMaterializeKNNPreprocessor(Parameterization config) {
    super(config);
    config = config.descend(this);
  }

  @Override
  public <T extends NumberVector<?, ?>> Instance<T, D, N, E> instantiate(Database<T> database) {
    return new Instance<T, D, N, E>(database, distanceFunction, k);
  }

  /**
   * The actual preprocessor instance.
   * 
   * @author Erich Schubert
   */
  public static class Instance<O extends NumberVector<?, ?>, D extends Distance<D>, N extends SpatialNode<N, E>, E extends SpatialEntry> extends MaterializeKNNPreprocessor.Instance<O, D> {
    /**
     * Logger to use
     */
    private static final Logging logger = Logging.getLogger(SpatialApproximationMaterializeKNNPreprocessor.class);

    /**
     * Constructor
     * 
     * @param database Database to preprocess
     * @param distanceFunction The distance function to use.
     * @param k query k
     */
    public Instance(Database<O> database, DistanceFunction<? super O, D> distanceFunction, int k) {
      super(database, distanceFunction, k);
    }

    @Override
    protected void preprocess(Database<O> database, DistanceFunction<? super O, D> distanceFunction) {
      DistanceQuery<O, D> distanceQuery = database.getDistanceQuery(distanceFunction);

      SpatialIndexDatabase<O, N, E> db = getSpatialDatabase(database);
      SpatialIndex<O, N, E> index = db.getIndex();

      materialized = DataStoreUtil.makeStorage(database.getIDs(), DataStoreFactory.HINT_STATIC, List.class);
      MeanVariance pagesize = new MeanVariance();
      MeanVariance ksize = new MeanVariance();
      if(logger.isVerbose()) {
        logger.verbose("Approximating nearest neighbor lists to database objects");
      }

      List<E> leaves = index.getLeaves();
      FiniteProgress progress = logger.isVerbose() ? new FiniteProgress("Processing leaf nodes.", leaves.size(), logger) : null;
      for(E leaf : leaves) {
        N node = index.getNode(leaf);
        int size = node.getNumEntries();
        pagesize.put(size);
        if(logger.isDebuggingFinest()) {
          logger.debugFinest("NumEntires = " + size);
        }
        // Collect the ids in this node.
        DBID[] ids = new DBID[size];
        for(int i = 0; i < size; i++) {
          ids[i] = ((LeafEntry) node.getEntry(i)).getDBID();
        }
        HashMap<DBIDPair, D> cache = new HashMap<DBIDPair, D>(size * size * 3 / 8);
        for(DBID id : ids) {
          KNNHeap<D> kNN = new KNNHeap<D>(k, distanceQuery.infiniteDistance());
          for(DBID id2 : ids) {
            DBIDPair key = DBIDUtil.newPair(id, id2);
              D d = cache.remove(key);
              if(d != null) {
                // consume the previous result.
                kNN.add(new DistanceResultPair<D>(d, id2));
              }
              else {
                // compute new and store the previous result.
                d = distanceQuery.distance(id, id2);
                kNN.add(new DistanceResultPair<D>(d, id2));
                // put it into the cache, but with the keys reversed
                key = DBIDUtil.newPair(id2, id);
                cache.put(key, d);
              }
          }
          ksize.put(kNN.size());
          materialized.put(id, kNN.toSortedArrayList());
        }
        if(logger.isDebugging()) {
          if(cache.size() > 0) {
            logger.warning("Cache should be empty after each run, but still has " + cache.size() + " elements.");
          }
        }
        if(progress != null) {
          progress.incrementProcessed(logger);
        }
      }
      if(progress != null) {
        progress.ensureCompleted(logger);
      }
      if(logger.isVerbose()) {
        logger.verbose("Average page size = " + pagesize.getMean() + " +- " + pagesize.getStddev());
        logger.verbose("On average, " + ksize.getMean() + " +- " + ksize.getStddev() + " neighbors returned.");
      }
    }

    @Override
    public List<DistanceResultPair<D>> get(DBID id) {
      return materialized.get(id);
    }

    /**
     * Do some (limited) type checking, then cast the database into a spatial
     * database.
     * 
     * @param database Database
     * @return Spatial database.
     * @throws IllegalStateException when the cast fails.
     */
    @SuppressWarnings("unchecked")
    private SpatialIndexDatabase<O, N, E> getSpatialDatabase(Database<O> database) throws IllegalStateException {
      if(!(database instanceof SpatialIndexDatabase)) {
        throw new IllegalStateException("Database must be an instance of " + SpatialIndexDatabase.class.getName());
      }
      SpatialIndexDatabase<O, N, E> db = (SpatialIndexDatabase<O, N, E>) database;
      return db;
    }
  }
}