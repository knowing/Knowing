package de.lmu.ifi.dbs.elki.algorithm.clustering.subspace;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import de.lmu.ifi.dbs.elki.algorithm.AbstractAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.ClusteringAlgorithm;
import de.lmu.ifi.dbs.elki.algorithm.clustering.OPTICS;
import de.lmu.ifi.dbs.elki.data.Clustering;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.data.Subspace;
import de.lmu.ifi.dbs.elki.data.cluster.Cluster;
import de.lmu.ifi.dbs.elki.data.cluster.SimpleHierarchy;
import de.lmu.ifi.dbs.elki.data.model.SubspaceModel;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.distance.distancefunction.PreprocessorBasedDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.ProxyDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancefunction.subspace.DiSHDistanceFunction;
import de.lmu.ifi.dbs.elki.distance.distancevalue.AbstractDistance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.PreferenceVectorBasedCorrelationDistance;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.logging.progress.FiniteProgress;
import de.lmu.ifi.dbs.elki.preprocessing.DiSHPreprocessor;
import de.lmu.ifi.dbs.elki.result.ClusterOrderEntry;
import de.lmu.ifi.dbs.elki.result.ClusterOrderResult;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.DatabaseUtil;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.documentation.Description;
import de.lmu.ifi.dbs.elki.utilities.documentation.Reference;
import de.lmu.ifi.dbs.elki.utilities.documentation.Title;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ChainedParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.ListParameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.TrackParameters;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.DoubleParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * <p>
 * Algorithm for detecting subspace hierarchies.
 * </p>
 * <p>
 * Reference: <br>
 * E. Achtert, C. Böhm, H.-P. Kriegel, P. Kröger, I. Müller-Gorman, A. Zimek:
 * Detection and Visualization of Subspace Cluster Hierarchies. <br>
 * In Proc. 12th International Conference on Database Systems for Advanced
 * Applications (DASFAA), Bangkok, Thailand, 2007.
 * </p>
 * 
 * @author Elke Achtert
 * @param <V> the type of NumberVector handled by this Algorithm
 */
@Title("DiSH: Detecting Subspace cluster Hierarchies")
@Description("Algorithm to find hierarchical correlation clusters in subspaces.")
@Reference(authors = "E. Achtert, C. Böhm, H.-P. Kriegel, P. Kröger, I. Müller-Gorman, A. Zimek", title = "Detection and Visualization of Subspace Cluster Hierarchies", booktitle = "Proc. 12th International Conference on Database Systems for Advanced Applications (DASFAA), Bangkok, Thailand, 2007", url = "http://dx.doi.org/10.1007/978-3-540-71703-4_15")
public class DiSH<V extends NumberVector<V, ?>> extends AbstractAlgorithm<V, Clustering<SubspaceModel<V>>> implements ClusteringAlgorithm<Clustering<SubspaceModel<V>>, V> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(DiSH.class);

  /**
   * OptionID for {@link #EPSILON_PARAM}
   */
  public static final OptionID EPSILON_ID = OptionID.getOrCreateOptionID("dish.epsilon", "The maximum radius of the neighborhood " + "to be considered in each dimension for determination of " + "the preference vector.");

  /**
   * Parameter that specifies the maximum radius of the neighborhood to be
   * considered in each dimension for determination of the preference vector,
   * must be a double equal to or greater than 0.
   * <p>
   * Default value: {@code 0.001}
   * </p>
   * <p>
   * Key: {@code -dish.epsilon}
   * </p>
   */
  private final DoubleParameter EPSILON_PARAM = new DoubleParameter(EPSILON_ID, new GreaterEqualConstraint(0), 0.001);

  /**
   * Holds the value of {@link #EPSILON_PARAM}.
   */
  private double epsilon;

  /**
   * OptionID for
   * {@link de.lmu.ifi.dbs.elki.algorithm.clustering.subspace.DiSH#MU_PARAM}
   */
  public static final OptionID MU_ID = OptionID.getOrCreateOptionID("dish.mu", "The minimum number of points as a smoothing factor to avoid the single-link-effekt.");

  /**
   * Parameter that specifies the a minimum number of points as a smoothing
   * factor to avoid the single-link-effect, must be an integer greater than 0.
   * <p>
   * Default value: {@code 1}
   * </p>
   * <p>
   * Key: {@code -dish.mu}
   * </p>
   */
  private final IntParameter MU_PARAM = new IntParameter(MU_ID, new GreaterConstraint(0), 1);

  /**
   * The distance function we use
   */
  private DiSHDistanceFunction dishDistance;

  private Collection<Pair<OptionID, Object>> opticsAlgorithmParameters;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public DiSH(Parameterization config) {
    super();
    config = config.descend(this);
    if(config.grab(EPSILON_PARAM)) {
      epsilon = EPSILON_PARAM.getValue();
    }

    if(config.grab(MU_PARAM)) {
      int minpts = MU_PARAM.getValue();

      // DiSH distance
      ListParameterization dishParameters = new ListParameterization();
      dishParameters.addParameter(DiSHDistanceFunction.EPSILON_ID, Double.toString(epsilon));
      dishParameters.addParameter(PreprocessorBasedDistanceFunction.PREPROCESSOR_ID, DiSHPreprocessor.class);
      dishParameters.addParameter(DiSHPreprocessor.EPSILON_ID, Double.toString(epsilon));
      dishParameters.addParameter(DiSHPreprocessor.MINPTS_ID, minpts);
      ChainedParameterization dishchain = new ChainedParameterization(dishParameters, config);
      dishchain.errorsTo(config);

      dishDistance = new DiSHDistanceFunction(dishchain);

      // TODO: use TrackParameters!
      // Configure OPTICS. Tracked parameters
      ListParameterization opticsParameters = new ListParameterization();
      opticsParameters.addParameter(OPTICS.EPSILON_ID, AbstractDistance.INFINITY_PATTERN);
      opticsParameters.addParameter(OPTICS.MINPTS_ID, minpts);
      // Configure OPTICS. Untracked parameters
      ListParameterization opticsUntrackedParameters = new ListParameterization();
      opticsUntrackedParameters.addParameter(OPTICS.DISTANCE_FUNCTION_ID, dishDistance);
      // TODO: verbose, time?
      ChainedParameterization optchain = new ChainedParameterization(opticsParameters, config);
      TrackParameters trackpar = new TrackParameters(optchain);

      ChainedParameterization optchain2 = new ChainedParameterization(opticsUntrackedParameters, trackpar);
      optchain2.errorsTo(config);

      // Instantiate OPTICS for parameterization
      optchain2.tryInstantiate(OPTICS.class, OPTICS.class);
      // store parameters
      opticsAlgorithmParameters = trackpar.getGivenParameters();
    }
  }

  /**
   * Performs the DiSH algorithm on the given database.
   * 
   */
  @Override
  protected Clustering<SubspaceModel<V>> runInTime(Database<V> database) throws IllegalStateException {
    // Instantiate DiSH distance (and thus run the preprocessor)
    if(logger.isVerbose()) {
      logger.verbose("*** Run DiSH preprocessor.");
    }
    DiSHDistanceFunction.Instance<V> dishDistanceQuery = dishDistance.instantiate(database);
    // Configure and run OPTICS.
    if(logger.isVerbose()) {
      logger.verbose("*** Run OPTICS algorithm.");
    }
    ListParameterization opticsconfig = new ListParameterization(opticsAlgorithmParameters);
    opticsconfig.addParameter(OPTICS.DISTANCE_FUNCTION_ID, ProxyDistanceFunction.proxy(dishDistanceQuery));

    Class<OPTICS<V, PreferenceVectorBasedCorrelationDistance>> cls = ClassGenericsUtil.uglyCastIntoSubclass(OPTICS.class);
    OPTICS<V, PreferenceVectorBasedCorrelationDistance> optics = null;
    optics = opticsconfig.tryInstantiate(cls, cls);
    ClusterOrderResult<PreferenceVectorBasedCorrelationDistance> opticsResult = optics.run(database);

    if(logger.isVerbose()) {
      logger.verbose("*** Compute Clusters.");
    }
    return computeClusters(database, opticsResult, dishDistanceQuery);
  }

  /**
   * Computes the hierarchical clusters according to the cluster order.
   * 
   * @param database the database holding the objects
   * @param clusterOrder the cluster order
   * @param distFunc Distance function
   */
  private Clustering<SubspaceModel<V>> computeClusters(Database<V> database, ClusterOrderResult<PreferenceVectorBasedCorrelationDistance> clusterOrder, DiSHDistanceFunction.Instance<V> distFunc) {
    int dimensionality = database.dimensionality();
    int minpts = dishDistance.getMinpts();

    // extract clusters
    Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> clustersMap = extractClusters(database, distFunc, clusterOrder);

    if(logger.isVerbose()) {
      StringBuffer msg = new StringBuffer("Step 1: extract clusters");
      for(List<Pair<BitSet, ArrayModifiableDBIDs>> clusterList : clustersMap.values()) {
        for(Pair<BitSet, ArrayModifiableDBIDs> c : clusterList) {
          msg.append("\n").append(FormatUtil.format(dimensionality, c.first)).append(" ids ").append(c.second.size());
        }
      }
      logger.verbose(msg.toString());
    }

    // check if there are clusters < minpts
    checkClusters(database, distFunc, clustersMap, minpts);
    if(logger.isVerbose()) {
      StringBuffer msg = new StringBuffer("Step 2: check clusters");
      for(List<Pair<BitSet, ArrayModifiableDBIDs>> clusterList : clustersMap.values()) {
        for(Pair<BitSet, ArrayModifiableDBIDs> c : clusterList) {
          msg.append("\n").append(FormatUtil.format(dimensionality, c.first)).append(" ids ").append(c.second.size());
        }
      }
      logger.verbose(msg.toString());
    }

    // sort the clusters
    List<Cluster<SubspaceModel<V>>> clusters = sortClusters(database, clustersMap);
    if(logger.isVerbose()) {
      StringBuffer msg = new StringBuffer("Step 3: sort clusters");
      for(Cluster<SubspaceModel<V>> c : clusters) {
        msg.append("\n").append(FormatUtil.format(dimensionality, c.getModel().getSubspace().getDimensions())).append(" ids ").append(c.size());
      }
      logger.verbose(msg.toString());
    }

    // build the hierarchy
    buildHierarchy(database, distFunc, clusters, dimensionality);
    if(logger.isVerbose()) {
      StringBuffer msg = new StringBuffer("Step 4: build hierarchy");
      for(Cluster<SubspaceModel<V>> c : clusters) {
        msg.append("\n").append(FormatUtil.format(dimensionality, c.getModel().getDimensions())).append(" ids ").append(c.size());
        for(Cluster<SubspaceModel<V>> cluster : c.getParents()) {
          msg.append("\n   parent ").append(cluster);
        }
        for(Cluster<SubspaceModel<V>> cluster : c.getChildren()) {
          msg.append("\n   child ").append(cluster);
        }
      }
      logger.verbose(msg.toString());
    }

    // build result
    Clustering<SubspaceModel<V>> result = new Clustering<SubspaceModel<V>>("DiSH clustering", "dish-clustering");
    for(Cluster<SubspaceModel<V>> c : clusters) {
      result.addCluster(c);
    }
    return result;
  }

  /**
   * Extracts the clusters from the cluster order.
   * 
   * @param database the database storing the objects
   * @param distFunc the distance function
   * @param clusterOrder the cluster order to extract the clusters from
   * @return the extracted clusters
   */
  private Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> extractClusters(Database<V> database, DiSHDistanceFunction.Instance<V> distFunc, ClusterOrderResult<PreferenceVectorBasedCorrelationDistance> clusterOrder) {
    FiniteProgress progress = logger.isVerbose() ? new FiniteProgress("Extract Clusters", database.size(), logger) : null;
    int processed = 0;
    Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> clustersMap = new HashMap<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>>();
    Map<DBID, ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance>> entryMap = new HashMap<DBID, ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance>>();
    Map<DBID, Pair<BitSet, ArrayModifiableDBIDs>> entryToClusterMap = new HashMap<DBID, Pair<BitSet, ArrayModifiableDBIDs>>();
    for(Iterator<ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance>> it = clusterOrder.iterator(); it.hasNext();) {
      ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance> entry = it.next();
      entryMap.put(entry.getID(), entry);

      V object = database.get(entry.getID());
      BitSet preferenceVector = entry.getReachability().getCommonPreferenceVector();

      // get the list of (parallel) clusters for the preference vector
      List<Pair<BitSet, ArrayModifiableDBIDs>> parallelClusters = clustersMap.get(preferenceVector);
      if(parallelClusters == null) {
        parallelClusters = new ArrayList<Pair<BitSet, ArrayModifiableDBIDs>>();
        clustersMap.put(preferenceVector, parallelClusters);
      }

      // look for the proper cluster
      Pair<BitSet, ArrayModifiableDBIDs> cluster = null;
      for(Pair<BitSet, ArrayModifiableDBIDs> c : parallelClusters) {
        V c_centroid = DatabaseUtil.centroid(database, c.second, c.first);
        PreferenceVectorBasedCorrelationDistance dist = distFunc.correlationDistance(object, c_centroid, preferenceVector, preferenceVector);
        if(dist.getCorrelationValue() == entry.getReachability().getCorrelationValue()) {
          double d = distFunc.weightedDistance(object, c_centroid, dist.getCommonPreferenceVector());
          if(d <= 2 * epsilon) {
            cluster = c;
            break;
          }
        }
      }
      if(cluster == null) {
        cluster = new Pair<BitSet, ArrayModifiableDBIDs>(preferenceVector, DBIDUtil.newArray());
        parallelClusters.add(cluster);
      }
      cluster.second.add(entry.getID());
      entryToClusterMap.put(entry.getID(), cluster);

      if(progress != null) {
        progress.setProcessed(++processed, logger);
      }
    }
    if(progress != null) {
      progress.ensureCompleted(logger);
    }

    if(logger.isDebuggingFiner()) {
      StringBuffer msg = new StringBuffer("Step 0");
      for(List<Pair<BitSet, ArrayModifiableDBIDs>> clusterList : clustersMap.values()) {
        for(Pair<BitSet, ArrayModifiableDBIDs> c : clusterList) {
          msg.append("\n").append(FormatUtil.format(database.dimensionality(), c.first)).append(" ids ").append(c.second.size());
        }
      }
      logger.debugFiner(msg.toString());
    }

    // add the predecessor to the cluster
    for(BitSet pv : clustersMap.keySet()) {
      List<Pair<BitSet, ArrayModifiableDBIDs>> parallelClusters = clustersMap.get(pv);
      for(Pair<BitSet, ArrayModifiableDBIDs> cluster : parallelClusters) {
        if(cluster.second.isEmpty()) {
          continue;
        }
        DBID firstID = cluster.second.get(0);
        ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance> entry = entryMap.get(firstID);
        DBID predecessorID = entry.getPredecessorID();
        if(predecessorID == null) {
          continue;
        }
        ClusterOrderEntry<PreferenceVectorBasedCorrelationDistance> predecessor = entryMap.get(predecessorID);
        // parallel cluster
        if(predecessor.getReachability().getCommonPreferenceVector().equals(entry.getReachability().getCommonPreferenceVector())) {
          continue;
        }
        if(predecessor.getReachability().compareTo(entry.getReachability()) < 0) {
          continue;
        }

        Pair<BitSet, ArrayModifiableDBIDs> oldCluster = entryToClusterMap.get(predecessorID);
        oldCluster.second.remove(predecessorID);
        cluster.second.add(predecessorID);
        entryToClusterMap.remove(predecessorID);
        entryToClusterMap.put(predecessorID, cluster);
      }
    }

    return clustersMap;
  }

  /**
   * Returns a sorted list of the clusters w.r.t. the subspace dimensionality in
   * descending order.
   * 
   * @param database the database storing the objects
   * @param clustersMap the mapping of bits sets to clusters
   * @return a sorted list of the clusters
   */
  private List<Cluster<SubspaceModel<V>>> sortClusters(Database<V> database, Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> clustersMap) {
    int num = 1;
    List<Cluster<SubspaceModel<V>>> clusters = new ArrayList<Cluster<SubspaceModel<V>>>();
    for(BitSet pv : clustersMap.keySet()) {
      List<Pair<BitSet, ArrayModifiableDBIDs>> parallelClusters = clustersMap.get(pv);
      for(int i = 0; i < parallelClusters.size(); i++) {
        Pair<BitSet, ArrayModifiableDBIDs> c = parallelClusters.get(i);
        Cluster<SubspaceModel<V>> cluster = new Cluster<SubspaceModel<V>>(c.second);
        cluster.setModel(new SubspaceModel<V>(new Subspace<V>(c.first), DatabaseUtil.centroid(database, c.second)));
        cluster.setHierarchy(new SimpleHierarchy<Cluster<SubspaceModel<V>>>(cluster, new ArrayList<Cluster<SubspaceModel<V>>>(), new ArrayList<Cluster<SubspaceModel<V>>>()));
        cluster.setName("Cluster_" + num++);
        clusters.add(cluster);
      }
    }
    // sort the clusters w.r.t. lambda
    Comparator<Cluster<SubspaceModel<V>>> comparator = new Comparator<Cluster<SubspaceModel<V>>>() {
      @Override
      public int compare(Cluster<SubspaceModel<V>> c1, Cluster<SubspaceModel<V>> c2) {
        return c2.getModel().getSubspace().dimensionality() - c1.getModel().getSubspace().dimensionality();
      }

    };
    Collections.sort(clusters, comparator);
    return clusters;
  }

  /**
   * Removes the clusters with size < minpts from the cluster map and adds them
   * to their parents.
   * 
   * @param database the database storing the objects
   * @param distFunc the distance function
   * @param clustersMap the map containing the clusters
   * @param minpts MinPts
   */
  private void checkClusters(Database<V> database, DiSHDistanceFunction.Instance<V> distFunc, Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> clustersMap, int minpts) {

    // check if there are clusters < minpts
    // and add them to not assigned
    List<Pair<BitSet, ArrayModifiableDBIDs>> notAssigned = new ArrayList<Pair<BitSet, ArrayModifiableDBIDs>>();
    Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> newClustersMap = new HashMap<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>>();
    Pair<BitSet, ArrayModifiableDBIDs> noise = new Pair<BitSet, ArrayModifiableDBIDs>(new BitSet(), DBIDUtil.newArray());
    for(BitSet pv : clustersMap.keySet()) {
      // noise
      if(pv.cardinality() == 0) {
        List<Pair<BitSet, ArrayModifiableDBIDs>> parallelClusters = clustersMap.get(pv);
        for(Pair<BitSet, ArrayModifiableDBIDs> c : parallelClusters) {
          noise.second.addDBIDs(c.second);
        }
      }
      // clusters
      else {
        List<Pair<BitSet, ArrayModifiableDBIDs>> parallelClusters = clustersMap.get(pv);
        List<Pair<BitSet, ArrayModifiableDBIDs>> newParallelClusters = new ArrayList<Pair<BitSet, ArrayModifiableDBIDs>>(parallelClusters.size());
        for(Pair<BitSet, ArrayModifiableDBIDs> c : parallelClusters) {
          if(!pv.equals(new BitSet()) && c.second.size() < minpts) {
            notAssigned.add(c);
          }
          else {
            newParallelClusters.add(c);
          }
        }
        newClustersMap.put(pv, newParallelClusters);
      }
    }

    clustersMap.clear();
    clustersMap.putAll(newClustersMap);

    for(Pair<BitSet, ArrayModifiableDBIDs> c : notAssigned) {
      if(c.second.isEmpty()) {
        continue;
      }
      Pair<BitSet, ArrayModifiableDBIDs> parent = findParent(database, distFunc, c, clustersMap);
      if(parent != null) {
        parent.second.addDBIDs(c.second);
      }
      else {
        noise.second.addDBIDs(c.second);
      }
    }

    List<Pair<BitSet, ArrayModifiableDBIDs>> noiseList = new ArrayList<Pair<BitSet, ArrayModifiableDBIDs>>(1);
    noiseList.add(noise);
    clustersMap.put(noise.first, noiseList);
  }

  /**
   * Returns the parent of the specified cluster
   * 
   * @param database the database storing the objects
   * @param distFunc the distance function
   * @param child the child to search the parent for
   * @param clustersMap the map containing the clusters
   * @return the parent of the specified cluster
   */
  private Pair<BitSet, ArrayModifiableDBIDs> findParent(Database<V> database, DiSHDistanceFunction.Instance<V> distFunc, Pair<BitSet, ArrayModifiableDBIDs> child, Map<BitSet, List<Pair<BitSet, ArrayModifiableDBIDs>>> clustersMap) {
    V child_centroid = DatabaseUtil.centroid(database, child.second, child.first);

    Pair<BitSet, ArrayModifiableDBIDs> result = null;
    int resultCardinality = -1;

    BitSet childPV = child.first;
    int childCardinality = childPV.cardinality();
    for(BitSet parentPV : clustersMap.keySet()) {
      int parentCardinality = parentPV.cardinality();
      if(parentCardinality >= childCardinality) {
        continue;
      }
      if(resultCardinality != -1 && parentCardinality <= resultCardinality) {
        continue;
      }

      BitSet pv = (BitSet) childPV.clone();
      pv.and(parentPV);
      if(pv.equals(parentPV)) {
        List<Pair<BitSet, ArrayModifiableDBIDs>> parentList = clustersMap.get(parentPV);
        for(Pair<BitSet, ArrayModifiableDBIDs> parent : parentList) {
          V parent_centroid = DatabaseUtil.centroid(database, parent.second, parentPV);
          double d = distFunc.weightedDistance(child_centroid, parent_centroid, parentPV);
          if(d <= 2 * epsilon) {
            result = parent;
            resultCardinality = parentCardinality;
            break;
          }
        }
      }
    }

    return result;
  }

  /**
   * Builds the cluster hierarchy
   * 
   * @param distFunc the distance function
   * @param clusters the sorted list of clusters
   * @param dimensionality the dimensionality of the data
   * @param database the database containing the data objects
   */
  private void buildHierarchy(Database<V> database, DiSHDistanceFunction.Instance<V> distFunc, List<Cluster<SubspaceModel<V>>> clusters, int dimensionality) {
    StringBuffer msg = new StringBuffer();

    for(int i = 0; i < clusters.size() - 1; i++) {
      Cluster<SubspaceModel<V>> c_i = clusters.get(i);
      int subspaceDim_i = dimensionality - c_i.getModel().getSubspace().dimensionality();
      V ci_centroid = DatabaseUtil.centroid(database, c_i.getIDs(), c_i.getModel().getDimensions());

      for(int j = i + 1; j < clusters.size(); j++) {
        Cluster<SubspaceModel<V>> c_j = clusters.get(j);
        int subspaceDim_j = dimensionality - c_j.getModel().getSubspace().dimensionality();

        if(subspaceDim_i < subspaceDim_j) {
          if(logger.isDebugging()) {
            msg.append("\n l_i=").append(subspaceDim_i).append(" pv_i=[").append(FormatUtil.format(database.dimensionality(), c_i.getModel().getSubspace().getDimensions())).append("]");
            msg.append("\n l_j=").append(subspaceDim_j).append(" pv_j=[").append(FormatUtil.format(database.dimensionality(), c_j.getModel().getSubspace().getDimensions())).append("]");
          }

          // noise level reached
          if(c_j.getModel().getSubspace().dimensionality() == 0) {
            // no parents exists -> parent is noise
            if(c_i.getParents().isEmpty()) {
              c_j.getChildren().add(c_i);
              c_i.getParents().add(c_j);
              if(logger.isDebugging()) {
                msg.append("\n [").append(FormatUtil.format(database.dimensionality(), c_j.getModel().getSubspace().getDimensions()));
                msg.append("] is parent of [").append(FormatUtil.format(database.dimensionality(), c_i.getModel().getSubspace().getDimensions()));
                msg.append("]");
              }
            }
          }
          else {
            V cj_centroid = DatabaseUtil.centroid(database, c_j.getIDs(), c_j.getModel().getDimensions());
            PreferenceVectorBasedCorrelationDistance distance = distFunc.correlationDistance(ci_centroid, cj_centroid, c_i.getModel().getSubspace().getDimensions(), c_j.getModel().getSubspace().getDimensions());
            double d = distFunc.weightedDistance(ci_centroid, cj_centroid, distance.getCommonPreferenceVector());
            if(logger.isDebugging()) {
              msg.append("\n dist = ").append(distance.getCorrelationValue());
            }

            if(distance.getCorrelationValue() == subspaceDim_j) {
              if(logger.isDebugging()) {
                msg.append("\n d = ").append(d);
              }
              if(d <= 2 * epsilon) {
                // no parent exists or c_j is not a parent of the already
                // existing parents
                if(c_i.getParents().isEmpty() || !isParent(database, distFunc, c_j, c_i.getParents())) {
                  c_j.getChildren().add(c_i);
                  c_i.getParents().add(c_j);
                  if(logger.isDebugging()) {
                    msg.append("\n [").append(FormatUtil.format(database.dimensionality(), c_j.getModel().getSubspace().getDimensions()));
                    msg.append("] is parent of [");
                    msg.append(FormatUtil.format(database.dimensionality(), c_i.getModel().getSubspace().getDimensions()));
                    msg.append("]");
                  }
                }
              }
              else {
                throw new RuntimeException("Should never happen: d = " + d);
              }
            }
          }
        }
      }
    }
    if(logger.isDebugging()) {
      logger.debug(msg.toString());
    }
  }

  /**
   * Returns true, if the specified parent cluster is a parent of one child of
   * the children clusters.
   * 
   * @param database the database containing the objects
   * @param distFunc the distance function for distance computation between the
   *        clusters
   * @param parent the parent to be tested
   * @param children the list of children to be tested
   * @return true, if the specified parent cluster is a parent of one child of
   *         the children clusters, false otherwise
   */
  private boolean isParent(Database<V> database, DiSHDistanceFunction.Instance<V> distFunc, Cluster<SubspaceModel<V>> parent, List<Cluster<SubspaceModel<V>>> children) {
    V parent_centroid = DatabaseUtil.centroid(database, parent.getIDs(), parent.getModel().getDimensions());
    int dimensionality = database.dimensionality();
    int subspaceDim_parent = dimensionality - parent.getModel().getSubspace().dimensionality();

    for(Cluster<SubspaceModel<V>> child : children) {
      V child_centroid = DatabaseUtil.centroid(database, child.getIDs(), child.getModel().getDimensions());
      PreferenceVectorBasedCorrelationDistance distance = distFunc.correlationDistance(parent_centroid, child_centroid, parent.getModel().getSubspace().getDimensions(), child.getModel().getSubspace().getDimensions());
      if(distance.getCorrelationValue() == subspaceDim_parent) {
        return true;
      }
    }

    return false;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}