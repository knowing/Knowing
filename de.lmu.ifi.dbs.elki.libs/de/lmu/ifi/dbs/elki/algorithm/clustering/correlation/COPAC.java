package de.lmu.ifi.dbs.elki.algorithm.clustering.correlation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.AbstractDistanceBasedAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.model.ClusterModel;
import de.lmu.ifi.dbs.elki.data.model.DimensionModel;
import de.lmu.ifi.dbs.elki.data.model.Model;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancefunction.LocalProjectionPreprocessorBasedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.LocallyWeightedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PreprocessorBasedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.ProxyDistanceFunction;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.math.linearalgebra.pca.PCAFilteredResult;
import de.lmu.ifi.dbs.elki.preprocessing.AbstractLocalPCAPreprocessor;
import de.lmu.ifi.dbs.elki.preprocessing.LocalProjectionPreprocessor;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.exceptions.UnableToComplyException;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ChainedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.TrackParameters;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ClassParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.ObjectParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Provides the COPAC algorithm, an algorithm to partition a database according
 * to the correlation dimension of its objects and to then perform an arbitrary
 * clustering algorithm over the partitions.
 * <p>
 * Reference: Achtert E., Böhm C., Kriegel H.-P., Kröger P., Zimek A.: Robust,
 * Complete, and Efficient Correlation Clustering. <br>
 * In Proc. 7th SIAM International Conference on Data Mining (SDM'07),
 * Minneapolis, MN, 2007
 * </p>
 * 
 * @author Arthur Zimek
 * @param <V> the type of NumberVector handled by this Algorithm
 */
@Title("COPAC: COrrelation PArtition Clustering")
@Description("Partitions a database according to the correlation dimension of its objects and performs " + "a clustering algorithm over the partitions.")
@Reference(authors = "E. Achtert, C. Böhm, H.-P. Kriegel, P. Kröger P., A. Zimek", title = "Robust, Complete, and Efficient Correlation Clustering", booktitle = "Proc. 7th SIAM International Conference on Data Mining (SDM'07), Minneapolis, MN, 2007", url = "http://www.siam.org/proceedings/datamining/2007/dm07_037achtert.pdf")
public class COPAC<V extends NumberVector<V, ?>> extends AbstractAlgorithm<V, Clustering<Model>> implements ClusteringAlgorithm<Clustering<Model>, V> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(COPAC.class);
  
  /**
   * OptionID for {@link #PREPROCESSOR_PARAM}
   */
  public static final OptionID PREPROCESSOR_ID = OptionID.getOrCreateOptionID("copac.preprocessor", "Local PCA Preprocessor to derive partition criterion.");

  /**
   * Parameter to specify the local PCA preprocessor to derive partition
   * criterion, must extend
   * {@link de.lmu.ifi.dbs.elki.preprocessing.AbstractLocalPCAPreprocessor}.
   * <p>
   * Key: {@code -copac.preprocessor}
   * </p>
   * 
   */
  private final ClassParameter<AbstractLocalPCAPreprocessor> PREPROCESSOR_PARAM = new ClassParameter<AbstractLocalPCAPreprocessor>(PREPROCESSOR_ID, AbstractLocalPCAPreprocessor.class);

  /**
   * Holds the instance of preprocessor specified by {@link #PREPROCESSOR_PARAM}
   * .
   */
  private AbstractLocalPCAPreprocessor preprocessor;

  /**
   * OptionID for {@link #PARTITION_DISTANCE_PARAM}
   */
  public static final OptionID PARTITION_DISTANCE_ID = OptionID.getOrCreateOptionID("copac.partitionDistance", "Distance to use for the inner algorithms.");

  /**
   * Parameter to specify the distance function to use inside the partitions
   * {@link de.lmu.ifi.dbs.elki.distance.distancefunction.AbstractPreprocessorBasedDistanceFunction}
   * .
   * <p>
   * Key: {@code -copac.partitionDistance}
   * </p>
   */
  protected final ObjectParameter<LocalProjectionPreprocessorBasedDistanceFunction<V, AbstractLocalPCAPreprocessor, PCAFilteredResult, ?>> PARTITION_DISTANCE_PARAM = new ObjectParameter<LocalProjectionPreprocessorBasedDistanceFunction<V, AbstractLocalPCAPreprocessor, PCAFilteredResult, ?>>(PARTITION_DISTANCE_ID, LocalProjectionPreprocessorBasedDistanceFunction.class, LocallyWeightedDistanceFunction.class);

  /**
   * Holds the instance of the preprocessed distance function
   * {@link #PARTITION_DISTANCE_PARAM}.
   */
  private LocalProjectionPreprocessorBasedDistanceFunction<V, AbstractLocalPCAPreprocessor, PCAFilteredResult, ?> partitionDistanceFunction;

  /**
   * OptionID for {@link #PARTITION_ALGORITHM_PARAM}
   */
  public static final OptionID PARTITION_ALGORITHM_ID = OptionID.getOrCreateOptionID("copac.partitionAlgorithm", "Clustering algorithm to apply to each partition.");

  /**
   * Parameter to specify the clustering algorithm to apply to each partition,
   * must extend
   * {@link de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithm}.
   * <p>
   * Key: {@code -copac.partitionAlgorithm}
   * </p>
   */
  protected final ClassParameter<ClusteringAlgorithm<Clustering<Model>, V>> PARTITION_ALGORITHM_PARAM = new ClassParameter<ClusteringAlgorithm<Clustering<Model>, V>>(PARTITION_ALGORITHM_ID, ClusteringAlgorithm.class);

  /**
   * Holds the parameters of the algorithm to run on each partition.
   */
  private Collection<Pair<OptionID, Object>> partitionAlgorithmParameters;

  /**
   * OptionID for {#PARTITION_DB_PARAM}
   */
  public static final OptionID PARTITION_DB_ID = OptionID.getOrCreateOptionID("copac.partitionDB", "Database class for each partition. " + "If this parameter is not set, the databases of the partitions have " + "the same class as the original database.");

  /**
   * Parameter to specify the database class for each partition, must extend
   * {@link de.lmu.ifi.dbs.elki.database.Database}.
   * <p>
   * Key: {@code -copac.partitionDB}
   * </p>
   */
  private final ClassParameter<Database<V>> PARTITION_DB_PARAM = new ClassParameter<Database<V>>(PARTITION_DB_ID, Database.class, true);

  /**
   * Holds the instance of the partition database specified by
   * {@link #PARTITION_DB_PARAM}.
   */
  private Class<? extends Database<V>> partitionDatabase;

  /**
   * Holds the parameters of the partition databases.
   */
  private Collection<Pair<OptionID, Object>> partitionDatabaseParameters;

  /**
   * The last used distance query
   */
  // FIXME: remove this when migrating to a full Factory pattern! This is
  // non-reentrant!
  private LocalProjectionPreprocessorBasedDistanceFunction.Instance<V, ? extends LocalProjectionPreprocessor.Instance<PCAFilteredResult>, ?> partitionDistanceQuery;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}.
   * 
   * @param config Parameterization
   */
  public COPAC(Parameterization config) {
    super();
    config = config.descend(this);
    // parameter preprocessor
    if(config.grab(PREPROCESSOR_PARAM)) {
      preprocessor = PREPROCESSOR_PARAM.instantiateClass(config);
    }
    if(config.grab(PARTITION_DISTANCE_PARAM)) {
      ListParameterization predefinedDist = new ListParameterization();
      predefinedDist.addParameter(PreprocessorBasedDistanceFunction.PREPROCESSOR_ID, preprocessor);
      ChainedParameterization chainDist = new ChainedParameterization(predefinedDist, config);
      chainDist.errorsTo(config);
      partitionDistanceFunction = PARTITION_DISTANCE_PARAM.instantiateClass(chainDist);
      predefinedDist.reportInternalParameterizationErrors(config);
    }
    // parameter partition algorithm
    if(config.grab(PARTITION_ALGORITHM_PARAM)) {
      ListParameterization predefined = new ListParameterization();
      predefined.addParameter(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID, partitionDistanceFunction);
      TrackParameters trackpar = new TrackParameters(config);
      ChainedParameterization chain = new ChainedParameterization(predefined, trackpar);
      chain.errorsTo(config);
      PARTITION_ALGORITHM_PARAM.instantiateClass(chain);
      partitionAlgorithmParameters = trackpar.getGivenParameters();
      predefined.reportInternalParameterizationErrors(chain);
    }
    // parameter partition database class
    if(config.grab(PARTITION_DB_PARAM)) {
      TrackParameters trackpar = new TrackParameters(config);
      Database<V> tmpDB = PARTITION_DB_PARAM.instantiateClass(trackpar);
      partitionDatabaseParameters = trackpar.getGivenParameters();
      partitionDatabase = ClassGenericsUtil.uglyCrossCast(tmpDB.getClass(), Database.class);
    }
  }

  /**
   * Performs the COPAC algorithm on the given database.
   */
  @Override
  protected Clustering<Model> runInTime(Database<V> database) throws IllegalStateException {
    if(logger.isVerbose()) {
      logger.verbose("Running COPAC on db size = " + database.size() + " with dimensionality = " + database.dimensionality());
    }

    // Get a proxy distance for the query
    partitionDistanceQuery = partitionDistanceFunction.instantiate(database);
    LocalProjectionPreprocessor.Instance<PCAFilteredResult> preprocin = partitionDistanceQuery.getPreprocessorInstance();

    // partitioning
    Map<Integer, ModifiableDBIDs> partitionMap = new HashMap<Integer, ModifiableDBIDs>();
    FiniteProgress partitionProgress = logger.isVerbose() ? new FiniteProgress("Partitioning", database.size(), logger) : null;
    int processed = 1;

    for(DBID id : database) {
      Integer corrdim = preprocin.get(id).getCorrelationDimension();

      if(!partitionMap.containsKey(corrdim)) {
        partitionMap.put(corrdim, DBIDUtil.newArray());
      }

      partitionMap.get(corrdim).add(id);
      if(partitionProgress != null) {
        partitionProgress.setProcessed(processed++, logger);
      }
    }

    if(partitionProgress != null) {
      partitionProgress.ensureCompleted(logger);
    }
    if(logger.isVerbose()) {
      for(Integer corrDim : partitionMap.keySet()) {
        ModifiableDBIDs list = partitionMap.get(corrDim);
        logger.verbose("Partition [corrDim = " + corrDim + "]: " + list.size() + " objects.");
      }
    }

    // convert for partition algorithm.
    // TODO: do this with DynamicDBIDs instead
    Map<Integer, DBIDs> pmap = new HashMap<Integer, DBIDs>();
    for(Entry<Integer, ModifiableDBIDs> ent : partitionMap.entrySet()) {
      pmap.put(ent.getKey(), ent.getValue());
    }
    // running partition algorithm
    return runPartitionAlgorithm(database, pmap, partitionDistanceQuery);
  }

  /**
   * Runs the partition algorithm and creates the result.
   * 
   * @param database the database to run this algorithm on
   * @param partitionMap the map of partition IDs to object ids
   * @param query The preprocessor based query function
   */
  private Clustering<Model> runPartitionAlgorithm(Database<V> database, Map<Integer, DBIDs> partitionMap, DistanceQuery<V, ?> query) {
    try {
      Map<Integer, Database<V>> databasePartitions = database.partition(partitionMap, partitionDatabase, partitionDatabaseParameters);

      Clustering<Model> result = new Clustering<Model>("COPAC clustering", "copac-clustering");

      // TODO: use an extra finite progress for the partitions?
      for(Integer partitionID : databasePartitions.keySet()) {
        // noise partition
        if(partitionID == database.dimensionality()) {
          Database<V> noiseDB = databasePartitions.get(partitionID);
          // Make a Noise cluster
          result.addCluster(new Cluster<Model>(noiseDB.getIDs(), true, ClusterModel.CLUSTER));
        }
        else {
          ClusteringAlgorithm<Clustering<Model>, V> partitionAlgorithm = getPartitionAlgorithm(query);

          if(logger.isVerbose()) {
            logger.verbose("Running " + partitionAlgorithm.getClass().getName() + " on partition [corrDim = " + partitionID + "]...");
          }
          Clustering<Model> p = partitionAlgorithm.run(databasePartitions.get(partitionID));
          // Re-Wrap resulting Clusters as DimensionModel clusters.
          for(Cluster<Model> clus : p.getAllClusters()) {
            if(clus.isNoise()) {
              result.addCluster(new Cluster<Model>(clus.getIDs(), true, ClusterModel.CLUSTER));
            }
            else {
              result.addCluster(new Cluster<Model>(clus.getIDs(), new DimensionModel(partitionID)));
            }
          }
        }
      }
      return result;
    }
    catch(UnableToComplyException e) {
      throw new IllegalStateException(e);
    }
  }

  /**
   * Returns the partition algorithm.
   * 
   * @return the specified partition algorithm
   */
  public ClusteringAlgorithm<Clustering<Model>, V> getPartitionAlgorithm(DistanceQuery<V, ?> query) {
    ListParameterization reconfig = new ListParameterization(partitionAlgorithmParameters);
    ProxyDistanceFunction<V, ?> dist = ProxyDistanceFunction.proxy(query);
    reconfig.addParameter(AbstractDistanceBasedAlgorithm.DISTANCE_FUNCTION_ID, dist);
    ClusteringAlgorithm<Clustering<Model>, V> partitionAlgorithm = PARTITION_ALGORITHM_PARAM.instantiateClass(reconfig);
    reconfig.failOnErrors();
    return partitionAlgorithm;
  }

  /**
   * Get the last used distance query (to expose access to the preprocessor)
   * 
   * Used by ERiC. TODO: migrate to factory pattern!
   * 
   * @return distance query
   */
  public LocalProjectionPreprocessorBasedDistanceFunction.Instance<V, ? extends de.lmu.ifi.dbs.elki.preprocessing.LocalProjectionPreprocessor.Instance<PCAFilteredResult>, ?> getPartitionDistanceQuery() {
    return partitionDistanceQuery;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}