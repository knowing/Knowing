package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants;

import java.util.List;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.DistanceUtil;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.distance.distancevalue.NumberDistance;
import de.lmu.ifi.dbs.elki.index.tree.AbstractNode;
import de.lmu.ifi.dbs.elki.index.tree.metrical.MetricalNode;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.persistent.PageFile;

/**
 * Abstract super class for nodes in M-Tree variants.
 * 
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject to be stored in the M-Tree
 * @param <D> the type of Distance used in the M-Tree
 * @param <N> the type of AbstractMTreeNode used in the M-Tree
 * @param <E> the type of MetricalEntry used in the M-Tree
 */
public abstract class AbstractMTreeNode<O extends DatabaseObject, D extends Distance<D>, N extends AbstractMTreeNode<O, D, N, E>, E extends MTreeEntry<D>> extends AbstractNode<N, E> implements MetricalNode<N, E> {
  /**
   * Empty constructor for Externalizable interface.
   */
  public AbstractMTreeNode() {
    // empty constructor
  }

  /**
   * Creates a new MTreeNode with the specified parameters.
   * 
   * @param file the file storing the M-Tree
   * @param capacity the capacity (maximum number of entries plus 1 for
   *        overflow) of this node
   * @param isLeaf indicates whether this node is a leaf node
   * @param eclass Entry class, to initialize array storage
   */
  public AbstractMTreeNode(PageFile<N> file, int capacity, boolean isLeaf, Class<? super E> eclass) {
    super(file, capacity, isLeaf, eclass);
  }

  /**
   * Splits the entries of this node into a new node at the specified splitPoint
   * and returns the newly created node.
   * 
   * @param assignmentsToFirst the assignment to this node
   * @param assignmentsToSecond the assignment to the new node
   * @return the newly created split node
   */
  public N splitEntries(List<E> assignmentsToFirst, List<E> assignmentsToSecond) {
    StringBuffer msg = new StringBuffer();

    if(isLeaf()) {
      N newNode = createNewLeafNode(getCapacity());
      getFile().writePage(newNode);

      deleteAllEntries();

      // assignments to this node
      for(E entry : assignmentsToFirst) {
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(getPageID()).append(" ").append(entry).append("\n");
        }
        addLeafEntry(entry);
      }

      // assignments to the new node
      for(E entry : assignmentsToSecond) {
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(newNode.getPageID()).append(" ").append(entry).append("\n");
        }
        newNode.addLeafEntry(entry);
      }
      if(LoggingConfiguration.DEBUG) {
        Logger.getLogger(this.getClass().getName()).fine(msg.toString());
      }
      return newNode;
    }
    else {
      N newNode = createNewDirectoryNode(getCapacity());
      getFile().writePage(newNode);

      deleteAllEntries();

      for(E entry : assignmentsToFirst) {
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(getPageID()).append(" ").append(entry).append("\n");
        }
        addDirectoryEntry(entry);
      }

      for(E entry : assignmentsToSecond) {
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(newNode.getPageID()).append(" ").append(entry).append("\n");
        }
        newNode.addDirectoryEntry(entry);
      }
      if(LoggingConfiguration.DEBUG) {
        Logger.getLogger(this.getClass().getName()).fine(msg.toString());
      }
      return newNode;
    }
  }

  /**
   * Adjusts the parameters of the entry representing this node (e.g. after
   * insertion of new objects). Subclasses may need to overwrite this method.
   * 
   * @param entry the entry representing this node
   * @param routingObjectID the id of the (new) routing object of this node
   * @param parentDistance the distance from the routing object of this node to
   *        the routing object of the parent node
   * @param mTree the M-Tree object holding this node
   */
  public void adjustEntry(E entry, DBID routingObjectID, D parentDistance, AbstractMTree<O, D, N, E> mTree) {
    entry.setRoutingObjectID(routingObjectID);
    entry.setParentDistance(parentDistance);
    entry.setCoveringRadius(coveringRadius(entry.getRoutingObjectID(), mTree));

    for(int i = 0; i < getNumEntries(); i++) {
      E childEntry = getEntry(i);
      D dist = mTree.distance(routingObjectID, childEntry.getRoutingObjectID());
      childEntry.setParentDistance(dist);
    }
  }

  /**
   * Determines and returns the covering radius of this node.
   * 
   * @param routingObjectID the object id of the routing object of this node
   * @param mTree the M-Tree
   * @return the covering radius of this node
   */
  public D coveringRadius(DBID routingObjectID, AbstractMTree<O, D, N, E> mTree) {
    D coveringRadius = mTree.getDistanceFactory().nullDistance();
    for(int i = 0; i < getNumEntries(); i++) {
      E entry = getEntry(i);
      D distance = mTree.distance(entry.getRoutingObjectID(), routingObjectID);
      // extend by the other objects covering radius, if non-null
      D d2 = entry.getCoveringRadius();
      if(d2 != null) {
        distance = distance.plus(d2);
      }
      coveringRadius = DistanceUtil.max(coveringRadius, distance);
    }
    return coveringRadius;
  }

  /**
   * Tests this node (for debugging purposes).
   * 
   * @param mTree the M-Tree holding this node
   * @param entry the entry representing this node
   */
  @SuppressWarnings("unchecked")
  public final void integrityCheck(AbstractMTree<O, D, N, E> mTree, E entry) {
    // leaf node
    if(isLeaf()) {
      for(int i = 0; i < getCapacity(); i++) {
        E e = getEntry(i);
        if(i < getNumEntries() && e == null) {
          throw new RuntimeException("i < numEntries && entry == null");
        }
        if(i >= getNumEntries() && e != null) {
          throw new RuntimeException("i >= numEntries && entry != null");
        }
      }
    }

    // dir node
    else {
      N tmp = getFile().readPage(getEntry(0).getEntryID());
      boolean childIsLeaf = tmp.isLeaf();

      for(int i = 0; i < getCapacity(); i++) {
        E e = getEntry(i);

        if(i < getNumEntries() && e == null) {
          throw new RuntimeException("i < numEntries && entry == null");
        }

        if(i >= getNumEntries() && e != null) {
          throw new RuntimeException("i >= numEntries && entry != null");
        }

        if(e != null) {
          N node = getFile().readPage(e.getEntryID());

          if(childIsLeaf && !node.isLeaf()) {
            for(int k = 0; k < getNumEntries(); k++) {
              getFile().readPage(getEntry(k).getEntryID());
            }

            throw new RuntimeException("Wrong Child in " + this + " at " + i);
          }

          if(!childIsLeaf && node.isLeaf()) {
            throw new RuntimeException("Wrong Child: child id no leaf, but node is leaf!");
          }

          // noinspection unchecked
          node.integrityCheckParameters(entry, (N) this, i, mTree);
          node.integrityCheck(mTree, e);
        }
      }

      if(LoggingConfiguration.DEBUG) {
        Logger.getLogger(this.getClass().getName()).fine("DirNode " + getPageID() + " ok!");
      }
    }
  }

  /**
   * Tests, if the parameters of the entry representing this node, are correctly
   * set. Subclasses may need to overwrite this method.
   * 
   * @param parentEntry the entry representing the parent
   * @param parent the parent holding the entry representing this node
   * @param index the index of the entry in the parents child arry
   * @param mTree the M-Tree holding this node
   */
  protected void integrityCheckParameters(E parentEntry, N parent, int index, AbstractMTree<O, D, N, E> mTree) {
    // test if parent distance is correctly set
    E entry = parent.getEntry(index);
    D parentDistance = mTree.distance(entry.getRoutingObjectID(), parentEntry.getRoutingObjectID());
    if(!entry.getParentDistance().equals(parentDistance)) {
      String soll = parentDistance.toString();
      String ist = entry.getParentDistance().toString();
      throw new RuntimeException("Wrong parent distance in node " + parent.getPageID() + " at index " + index + " (child " + entry.getEntryID() + ")" + "\nsoll: " + soll + ",\n ist: " + ist);
    }

    // test if covering radius is correctly set
    D mincover = parentDistance.plus(entry.getCoveringRadius());
    if(parentEntry.getCoveringRadius().compareTo(mincover) < 0) {
      String msg = "pcr < pd + cr \n" + parentEntry.getCoveringRadius() + " < " + parentDistance + " + " + entry.getCoveringRadius() + "in node " + parent.getPageID() + " at index " + index + " (child " + entry.getEntryID() + "):\n" + "dist(" + entry.getRoutingObjectID() + " - " + parentEntry.getRoutingObjectID() + ")" + " >  cr(" + entry + ")";

      // throw new RuntimeException(msg);
      if(parentDistance instanceof NumberDistance<?, ?>) {
        double d1 = Double.parseDouble(parentDistance.toString());
        double d2 = Double.parseDouble(entry.getCoveringRadius().toString());
        if(Math.abs(d1 - d2) > 0.000000001) {
          throw new RuntimeException(msg);
        }
      }
      else {
        throw new RuntimeException(msg);
      }
    }
  }
}
