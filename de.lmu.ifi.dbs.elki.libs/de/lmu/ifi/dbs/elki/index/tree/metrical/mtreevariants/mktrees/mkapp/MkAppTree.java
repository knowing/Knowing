package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.mktrees.mkapp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.DistanceResultPair;
import de.lmu.ifi.dbs.elki.database.ids.ArrayModifiableDBIDs;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.ModifiableDBIDs;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.index.tree.LeafEntry;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.AbstractMTree;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.util.PQNode;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.math.statistics.PolynomialRegression;
import de.lmu.ifi.dbs.elki.utilities.ClassGenericsUtil;
import de.lmu.ifi.dbs.elki.utilities.datastructures.KNNHeap;
import de.lmu.ifi.dbs.elki.utilities.datastructures.KNNList;
import de.lmu.ifi.dbs.elki.utilities.heap.DefaultHeap;
import de.lmu.ifi.dbs.elki.utilities.heap.Heap;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.Flag;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;

/**
 * MkAppTree is a metrical index structure based on the concepts of the M-Tree
 * supporting efficient processing of reverse k nearest neighbor queries for
 * parameter k < kmax.
 * 
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject to be stored in the metrical index
 * @param <D> the type of NumberDistance used in the metrical index
 * @param <N> the type of Number used in the NumberDistance
 */
public class MkAppTree<O extends DatabaseObject, D extends NumberDistance<D, N>, N extends Number> extends AbstractMTree<O, D, MkAppTreeNode<O, D, N>, MkAppEntry<D, N>> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(MkAppTree.class);
  
  /**
   * OptionID for {@link #NOLOG_FLAG}
   */
  public static final OptionID NOLOG_ID = OptionID.getOrCreateOptionID("mkapp.nolog", "Flag to indicate that the approximation is done in " + "the ''normal'' space instead of the log-log space (which is default).");

  /**
   * Parameter for nolog
   */
  private final Flag NOLOG_FLAG = new Flag(NOLOG_ID);

  /**
   * OptionID for {@link #K_PARAM}
   */
  public static final OptionID K_ID = OptionID.getOrCreateOptionID("mkapp.k", "positive integer specifying the maximal number k of reverse" + "k nearest neighbors to be supported.");

  /**
   * Parameter for k
   */
  private final IntParameter K_PARAM = new IntParameter(K_ID, new GreaterConstraint(0));

  /**
   * OptionID for {@link #P_PARAM}
   */
  public static final OptionID P_ID = OptionID.getOrCreateOptionID("mkapp.p", "positive integer specifying the order of the polynomial approximation.");

  /**
   * Parameter for p
   */
  private final IntParameter P_PARAM = new IntParameter(P_ID, new GreaterConstraint(0));

  /**
   * Parameter k.
   */
  private int k_max;

  /**
   * Parameter p.
   */
  private int p;

  /**
   * Flag log.
   */
  private boolean log;

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public MkAppTree(Parameterization config) {
    super(config);
    config = config.descend(this);

    if(config.grab(K_PARAM)) {
      k_max = K_PARAM.getValue();
    }
    if(config.grab(P_PARAM)) {
      p = P_PARAM.getValue();
    }
    if(config.grab(NOLOG_FLAG)) {
      log = !NOLOG_FLAG.getValue();
    }
  }

  /**
   * Inserts the specified object into this MDkNNTree-Tree. This operation is
   * not supported.
   * 
   * @param object the object to be inserted
   */
  @Override
  public void insert(O object) {
    throw new UnsupportedOperationException("Insertion of single objects is not supported!");
  }

  /**
   * Performs necessary operations before inserting the specified entry.
   * 
   * @param entry the entry to be inserted
   */
  @Override
  protected void preInsert(MkAppEntry<D, N> entry) {
    throw new UnsupportedOperationException("Insertion of single objects is not supported!");
  }

  /**
   * Inserts the specified objects into this MkApp-Tree.
   * 
   * @param objects the object to be inserted
   */
  @Override
  public void insert(List<O> objects) {
    if(logger.isDebugging()) {
      logger.debugFine("insert " + objects + "\n");
    }

    if(!initialized) {
      initialize(objects.get(0));
    }

    ModifiableDBIDs ids = DBIDUtil.newArray();
    Map<DBID, KNNHeap<D>> knnHeaps = new HashMap<DBID, KNNHeap<D>>();

    // insert
    for(O object : objects) {
      // create knnList for the object
      ids.add(object.getID());
      knnHeaps.put(object.getID(), new KNNHeap<D>(k_max + 1, getDistanceQuery().infiniteDistance()));

      // insert the object
      super.insert(object, false);
    }

    // do batch nn
    batchNN(getRoot(), ids, knnHeaps);

    // finish KNN lists (sort them completely)
    Map<DBID, KNNList<D>> knnLists = new HashMap<DBID, KNNList<D>>();
    for (Entry<DBID, KNNHeap<D>> ent : knnHeaps.entrySet()) {
      knnLists.put(ent.getKey(), ent.getValue().toKNNList());
    }

    // adjust the knn distances
    adjustApproximatedKNNDistances(getRootEntry(), knnLists);

    if(extraIntegrityChecks) {
      getRoot().integrityCheck(this, getRootEntry());
    }
  }

  /**
   * Performs a reverse k-nearest neighbor query for the given object ID. The
   * query result is in ascending order to the distance to the query object.
   * 
   * @param object the query object
   * @param k the number of nearest neighbors to be returned
   * @return a List of the query results
   */
  @Override
  public List<DistanceResultPair<D>> reverseKNNQuery(O object, int k) {
    List<DistanceResultPair<D>> result = doReverseKNNQuery(k, object.getID());
    Collections.sort(result);
    return result;
  }

  /**
   * Returns the value of the k_max parameter.
   * 
   * @return the value of the k_max parameter
   */
  public int getK_max() {
    return k_max;
  }

  /**
   * Determines the maximum and minimum number of entries in a node.
   */
  @Override
  protected void initializeCapacities(@SuppressWarnings("unused") O object) {
    D dummyDistance = getDistanceQuery().nullDistance();
    int distanceSize = dummyDistance.externalizableSize();

    // overhead = index(4), numEntries(4), id(4), isLeaf(0.125)
    double overhead = 12.125;
    if(pageSize - overhead < 0) {
      throw new RuntimeException("Node size of " + pageSize + " Bytes is chosen too small!");
    }

    // dirCapacity = (pageSize - overhead) / (nodeID + objectID +
    // coveringRadius + parentDistance + approx) + 1
    dirCapacity = (int) (pageSize - overhead) / (4 + 4 + distanceSize + distanceSize + (p + 1) * 4 + 2) + 1;

    if(dirCapacity <= 1) {
      throw new RuntimeException("Node size of " + pageSize + " Bytes is chosen too small!");
    }

    if(dirCapacity < 10) {
      logger.warning("Page size is choosen too small! Maximum number of entries " + "in a directory node = " + (dirCapacity - 1));
    }

    // leafCapacity = (pageSize - overhead) / (objectID + parentDistance +
    // approx) + 1
    leafCapacity = (int) (pageSize - overhead) / (4 + distanceSize + (p + 1) * 4 + 2) + 1;

    if(leafCapacity <= 1) {
      throw new RuntimeException("Node size of " + pageSize + " Bytes is chosen too small!");
    }

    if(leafCapacity < 10) {
      logger.warning("Page size is choosen too small! Maximum number of entries " + "in a leaf node = " + (leafCapacity - 1));
    }

    initialized = true;

    if(logger.isVerbose()) {
      logger.verbose("Directory Capacity: " + (dirCapacity - 1) + "\nLeaf Capacity:    " + (leafCapacity - 1));
    }
  }

  /**
   * Performs a reverse knn query.
   * 
   * @param k the parameter k of the rknn query
   * @param q the id of the query object
   * @return the result of the reverse knn query
   */
  private List<DistanceResultPair<D>> doReverseKNNQuery(int k, DBID q) {
    List<DistanceResultPair<D>> result = new ArrayList<DistanceResultPair<D>>();
    final Heap<D, Integer> pq = new DefaultHeap<D, Integer>();

    // push root
    pq.addNode(new PQNode<D>(getDistanceQuery().nullDistance(), getRootEntry().getEntryID(), null));

    // search in tree
    while(!pq.isEmpty()) {
      PQNode<D> pqNode = (PQNode<D>) pq.getMinNode();

      MkAppTreeNode<O, D, N> node = getNode(pqNode.getValue());

      // directory node
      if(!node.isLeaf()) {
        for(int i = 0; i < node.getNumEntries(); i++) {
          MkAppEntry<D, N> entry = node.getEntry(i);
          D distance = getDistanceQuery().distance(entry.getRoutingObjectID(), q);
          D minDist = entry.getCoveringRadius().compareTo(distance) > 0 ? getDistanceQuery().nullDistance() : distance.minus(entry.getCoveringRadius());

          double approxValue = log ? Math.exp(entry.approximatedValueAt(k)) : entry.approximatedValueAt(k);
          if(approxValue < 0) {
            approxValue = 0;
          }
          D approximatedKnnDist = getDistanceQuery().getDistanceFactory().parseString(Double.toString(approxValue));

          if(minDist.compareTo(approximatedKnnDist) <= 0) {
            pq.addNode(new PQNode<D>(minDist, entry.getEntryID(), entry.getRoutingObjectID()));
          }
        }
      }
      // data node
      else {
        for(int i = 0; i < node.getNumEntries(); i++) {
          MkAppLeafEntry<D, N> entry = (MkAppLeafEntry<D, N>) node.getEntry(i);
          D distance = getDistanceQuery().distance(entry.getRoutingObjectID(), q);
          double approxValue = log ? StrictMath.exp(entry.approximatedValueAt(k)) : entry.approximatedValueAt(k);
          if(approxValue < 0) {
            approxValue = 0;
          }
          D approximatedKnnDist = getDistanceQuery().getDistanceFactory().parseString(Double.toString(approxValue));

          if(distance.compareTo(approximatedKnnDist) <= 0) {
            result.add(new DistanceResultPair<D>(distance, entry.getRoutingObjectID()));
          }
        }
      }
    }
    return result;
  }

  private List<D> getMeanKNNList(DBIDs ids, Map<DBID, KNNList<D>> knnLists) {
    double[] means = new double[k_max];
    for(DBID id : ids) {
      KNNList<D> knns = knnLists.get(id);
      List<D> knnDists = knns.asDistanceList();
      for(int k = 0; k < k_max; k++) {
        D knnDist = knnDists.get(k);
        means[k] += knnDist.doubleValue();
      }
    }

    List<D> result = new ArrayList<D>();
    for(int k = 0; k < k_max; k++) {
      means[k] /= ids.size();
      result.add(getDistanceQuery().getDistanceFactory().parseString(Double.toString(means[k])));
    }

    return result;
  }

  /**
   * Adjusts the knn distance in the subtree of the specified root entry.
   * 
   * @param entry the root entry of the current subtree
   * @param knnLists a map of knn lists for each leaf entry
   */
  private void adjustApproximatedKNNDistances(MkAppEntry<D, N> entry, Map<DBID, KNNList<D>> knnLists) {
    MkAppTreeNode<O, D, N> node = file.readPage(entry.getEntryID());

    if(node.isLeaf()) {
      for(int i = 0; i < node.getNumEntries(); i++) {
        MkAppLeafEntry<D, N> leafEntry = (MkAppLeafEntry<D, N>) node.getEntry(i);
        // approximateKnnDistances(leafEntry,
        // getKNNList(leafEntry.getRoutingObjectID(), knnLists));
        PolynomialApproximation approx = approximateKnnDistances(getMeanKNNList(leafEntry.getDBID(), knnLists));
        leafEntry.setKnnDistanceApproximation(approx);
      }
    }
    else {
      for(int i = 0; i < node.getNumEntries(); i++) {
        MkAppEntry<D, N> dirEntry = node.getEntry(i);
        adjustApproximatedKNNDistances(dirEntry, knnLists);
      }
    }

    // PolynomialApproximation approx1 = node.knnDistanceApproximation();
    ArrayModifiableDBIDs ids = DBIDUtil.newArray();
    leafEntryIDs(node, ids);
    PolynomialApproximation approx = approximateKnnDistances(getMeanKNNList(ids, knnLists));
    entry.setKnnDistanceApproximation(approx);
  }

  /**
   * Determines the ids of the leaf entries stored in the specified subtree.
   * 
   * @param node the root of the subtree
   * @param result the result list containing the ids of the leaf entries stored
   *        in the specified subtree
   */
  private void leafEntryIDs(MkAppTreeNode<O, D, N> node, ModifiableDBIDs result) {
    if(node.isLeaf()) {
      for(int i = 0; i < node.getNumEntries(); i++) {
        MkAppEntry<D, N> entry = node.getEntry(i);
        result.add(((LeafEntry)entry).getDBID());
      }
    }
    else {
      for(int i = 0; i < node.getNumEntries(); i++) {
        MkAppTreeNode<O, D, N> childNode = getNode(node.getEntry(i));
        leafEntryIDs(childNode, result);
      }
    }
  }

  /**
   * Computes the polynomial approximation of the specified knn-distances.
   * 
   * @param knnDistances the knn-distances of the leaf entry
   * @return the polynomial approximation of the specified knn-distances.
   */
  private PolynomialApproximation approximateKnnDistances(List<D> knnDistances) {
    StringBuffer msg = new StringBuffer();

    // count the zero distances (necessary of log-log space is used)
    int k_0 = 0;
    if(log) {
      for(int i = 0; i < k_max; i++) {
        double dist = knnDistances.get(i).doubleValue();
        if(dist == 0) {
          k_0++;
        }
        else {
          break;
        }
      }
    }

    de.lmu.ifi.dbs.elki.math.linearalgebra.Vector x = new de.lmu.ifi.dbs.elki.math.linearalgebra.Vector(k_max - k_0);
    de.lmu.ifi.dbs.elki.math.linearalgebra.Vector y = new de.lmu.ifi.dbs.elki.math.linearalgebra.Vector(k_max - k_0);

    for(int k = 0; k < k_max - k_0; k++) {
      if(log) {
        x.set(k, Math.log(k + k_0));
        y.set(k, Math.log(knnDistances.get(k + k_0).doubleValue()));
      }
      else {
        x.set(k, k + k_0);
        y.set(k, knnDistances.get(k + k_0).doubleValue());
      }
    }

    PolynomialRegression regression = new PolynomialRegression(y, x, p);
    PolynomialApproximation approximation = new PolynomialApproximation(regression.getEstimatedCoefficients().getArrayCopy());

    if(logger.isDebugging()) {
      msg.append("approximation ").append(approximation);
      logger.debugFine(msg.toString());
    }
    return approximation;

  }

  /**
   * Creates a new leaf node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  @Override
  protected MkAppTreeNode<O, D, N> createNewLeafNode(int capacity) {
    return new MkAppTreeNode<O, D, N>(file, capacity, true);
  }

  /**
   * Creates a new directory node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  @Override
  protected MkAppTreeNode<O, D, N> createNewDirectoryNode(int capacity) {
    return new MkAppTreeNode<O, D, N>(file, capacity, false);
  }

  /**
   * Creates a new leaf entry representing the specified data object in the
   * specified subtree.
   * 
   * @param object the data object to be represented by the new entry
   * @param parentDistance the distance from the object to the routing object of
   *        the parent node
   */
  @Override
  protected MkAppEntry<D, N> createNewLeafEntry(O object, D parentDistance) {
    return new MkAppLeafEntry<D, N>(object.getID(), parentDistance, null);
  }

  /**
   * Creates a new directory entry representing the specified node.
   * 
   * @param node the node to be represented by the new entry
   * @param routingObjectID the id of the routing object of the node
   * @param parentDistance the distance from the routing object of the node to
   *        the routing object of the parent node
   */
  @Override
  protected MkAppEntry<D, N> createNewDirectoryEntry(MkAppTreeNode<O, D, N> node, DBID routingObjectID, D parentDistance) {
    return new MkAppDirectoryEntry<D, N>(routingObjectID, parentDistance, node.getPageID(), node.coveringRadius(routingObjectID, this), null);
  }

  /**
   * Creates an entry representing the root node.
   * 
   * @return an entry representing the root node
   */
  @Override
  protected MkAppEntry<D, N> createRootEntry() {
    return new MkAppDirectoryEntry<D, N>(null, null, 0, null, null);
  }

  /**
   * Return the node base class.
   * 
   * @return node base class
   */
  @Override
  protected Class<MkAppTreeNode<O, D, N>> getNodeClass() {
    return ClassGenericsUtil.uglyCastIntoSubclass(MkAppTreeNode.class);
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}