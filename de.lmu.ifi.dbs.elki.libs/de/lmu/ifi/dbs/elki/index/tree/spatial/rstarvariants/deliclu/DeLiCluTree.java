package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.deliclu;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.data.NumberVector;
import de.lmu.ifi.dbs.elki.index.tree.BreadthFirstEnumeration;
import de.lmu.ifi.dbs.elki.index.tree.Entry;
import de.lmu.ifi.dbs.elki.index.tree.TreeIndexPath;
import de.lmu.ifi.dbs.elki.index.tree.TreeIndexPathComponent;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.NonFlatRStarTree;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;

/**
 * DeLiCluTree is a spatial index structure based on an R-TRee. DeLiCluTree is
 * designed for the DeLiClu algorithm, having in each node a boolean array which
 * indicates whether the child nodes are already handled by the DeLiClu
 * algorithm.
 * 
 * @author Elke Achtert
 * @param <O> object type
 */
public class DeLiCluTree<O extends NumberVector<O, ?>> extends NonFlatRStarTree<O, DeLiCluNode, DeLiCluEntry> {
  /**
   * The logger for this class.
   */
  private static final Logging logger = Logging.getLogger(DeLiCluTree.class);
  
  /**
   * Holds the ids of the expanded nodes.
   */
  private HashMap<Integer, HashSet<Integer>> expanded = new HashMap<Integer, HashSet<Integer>>();

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public DeLiCluTree(Parameterization config) {
    super(config);
    config = config.descend(this);
    // this.debug = true;
  }

  /**
   * Marks the specified object as handled and returns the path of node ids from
   * the root to the objects's parent.
   * 
   * @param o the object to be marked as handled
   * @return the path of node ids from the root to the objects's parent
   */
  public synchronized List<TreeIndexPathComponent<DeLiCluEntry>> setHandled(O o) {
    if(logger.isDebugging()) {
      logger.debugFine("setHandled " + o.getID() + o + "\n");
    }

    // find the leaf node containing o
    double[] values = getValues(o);
    HyperBoundingBox mbr = new HyperBoundingBox(values, values);
    TreeIndexPath<DeLiCluEntry> pathToObject = findPathToObject(getRootPath(), mbr, o.getID());

    if(pathToObject == null) {
      return null;
    }

    // set o handled
    DeLiCluEntry entry = pathToObject.getLastPathComponent().getEntry();
    entry.setHasHandled(true);
    entry.setHasUnhandled(false);

    for(TreeIndexPath<DeLiCluEntry> path = pathToObject; path.getParentPath() != null; path = path.getParentPath()) {
      DeLiCluEntry parentEntry = path.getParentPath().getLastPathComponent().getEntry();
      DeLiCluNode node = getNode(parentEntry);
      boolean hasHandled = false;
      boolean hasUnhandled = false;
      for(int i = 0; i < node.getNumEntries(); i++) {
        final DeLiCluEntry nodeEntry = node.getEntry(i);
        hasHandled = hasHandled || nodeEntry.hasHandled();
        hasUnhandled = hasUnhandled || nodeEntry.hasUnhandled();
      }
      parentEntry.setHasUnhandled(hasUnhandled);
      parentEntry.setHasHandled(hasHandled);
    }

    return pathToObject.getPath();
  }

  /**
   * Marks the nodes with the specified ids as expanded.
   * 
   * @param entry1 the first node
   * @param entry2 the second node
   */
  public void setExpanded(SpatialEntry entry1, SpatialEntry entry2) {
    HashSet<Integer> exp1 = expanded.get(entry1.getEntryID());
    if(exp1 == null) {
      exp1 = new HashSet<Integer>();
      expanded.put(entry1.getEntryID(), exp1);
    }
    exp1.add(entry2.getEntryID());
  }

  /**
   * Returns the nodes which are already expanded with the specified node.
   * 
   * @param entry the id of the node for which the expansions should be returned
   * @return the nodes which are already expanded with the specified node
   */
  public Set<Integer> getExpanded(SpatialEntry entry) {
    HashSet<Integer> exp = expanded.get(entry.getEntryID());
    if(exp != null) {
      return exp;
    }
    return new HashSet<Integer>();
  }

  /**
   * Returns the nodes which are already expanded with the specified node.
   * 
   * @param entry the id of the node for which the expansions should be returned
   * @return the nodes which are already expanded with the specified node
   */
  public Set<Integer> getExpanded(DeLiCluNode entry) {
    HashSet<Integer> exp = expanded.get(entry.getPageID());
    if(exp != null) {
      return exp;
    }
    return new HashSet<Integer>();
  }

  /**
   * Determines and returns the number of nodes in this index.
   * 
   * @return the number of nodes in this index
   */
  public int numNodes() {
    int numNodes = 0;

    BreadthFirstEnumeration<O, DeLiCluNode, DeLiCluEntry> bfs = new BreadthFirstEnumeration<O, DeLiCluNode, DeLiCluEntry>(this, getRootPath());
    while(bfs.hasMoreElements()) {
      Entry entry = bfs.nextElement().getLastPathComponent().getEntry();
      if(!entry.isLeafEntry()) {
        numNodes++;
      }
    }

    return numNodes;
  }

  /**
   * Creates a new leaf node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  @Override
  protected DeLiCluNode createNewLeafNode(int capacity) {
    return new DeLiCluNode(file, capacity, true);
  }

  /**
   * Creates a new directory node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  @Override
  protected DeLiCluNode createNewDirectoryNode(int capacity) {
    return new DeLiCluNode(file, capacity, false);
  }

  /**
   * Creates a new leaf entry representing the specified data object.
   * 
   * @param object the data object to be represented by the new entry
   */
  @Override
  protected DeLiCluEntry createNewLeafEntry(O object) {
    return new DeLiCluLeafEntry(object.getID(), getValues(object));
  }

  /**
   * Creates a new directory entry representing the specified node.
   * 
   * @param node the node to be represented by the new entry
   */
  @Override
  protected DeLiCluEntry createNewDirectoryEntry(DeLiCluNode node) {
    return new DeLiCluDirectoryEntry(node.getPageID(), node.mbr(), node.hasHandled(), node.hasUnhandled());
  }

  /**
   * Creates an entry representing the root node.
   * 
   * @return an entry representing the root node
   */
  @Override
  protected DeLiCluEntry createRootEntry() {
    return new DeLiCluDirectoryEntry(0, null, false, true);
  }

  /**
   * Performs necessary operations before inserting the specified entry.
   * 
   * @param entry the entry to be inserted
   */
  @Override
  protected void preInsert(DeLiCluEntry entry) {
    // do nothing
  }

  /**
   * Performs necessary operations after deleting the specified object.
   * 
   * @param o the object to be deleted
   */
  @Override
  protected void postDelete(O o) {
    // do nothing
  }

  /**
   * Return the node base class.
   * 
   * @return node base class
   */
  @Override
  protected Class<DeLiCluNode> getNodeClass() {
    return DeLiCluNode.class;
  }

  @Override
  protected Logging getLogger() {
    return logger;
  }
}