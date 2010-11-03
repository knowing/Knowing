package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.List;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.AbstractNode;
import de.lmu.ifi.dbs.elki.index.tree.DistanceEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialDirectoryEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialLeafEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialNode;
import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;
import de.lmu.ifi.dbs.elki.persistent.PageFile;

/**
 * Abstract superclass for nodes in a R*-Tree.
 * 
 * @author Elke Achtert
 * @param <N> Node type
 * @param <E> Entry type
 */
public abstract class AbstractRStarTreeNode<N extends AbstractRStarTreeNode<N, E>, E extends SpatialEntry> extends AbstractNode<N, E> implements SpatialNode<N, E> {
  /**
   * Empty constructor for Externalizable interface.
   */
  public AbstractRStarTreeNode() {
    super();
  }

  /**
   * Creates a new AbstractRStarTreeNode with the specified parameters.
   * 
   * @param file the file storing the R*-Tree
   * @param capacity the capacity (maximum number of entries plus 1 for
   *        overflow) of this node
   * @param isLeaf indicates whether this node is a leaf node
   * @param eclass Entry class, to initialize array storage
   */
  public AbstractRStarTreeNode(PageFile<N> file, int capacity, boolean isLeaf, Class<? super E> eclass) {
    super(file, capacity, isLeaf, eclass);
  }

  @Override
  public double getMin(int dimension) {
    return mbr().getMin(dimension);
  }

  @Override
  public double getMax(int dimension) {
    return mbr().getMax(dimension);
  }

  @Override
  public HyperBoundingBox mbr() {
    E firstEntry = getEntry(0);
    if(firstEntry == null) {
      return null;
    }
    int dim = firstEntry.getMBR().getDimensionality();
    // Note: we deliberately get a cloned copy here, since we will modify it.
    double[] min = firstEntry.getMBR().getMin();
    double[] max = firstEntry.getMBR().getMax();

    for(int i = 1; i < getNumEntries(); i++) {
      HyperBoundingBox mbr = getEntry(i).getMBR();
      for(int d = 1; d <= dim; d++) {
        if(min[d - 1] > mbr.getMin(d)) {
          min[d - 1] = mbr.getMin(d);
        }
        if(max[d - 1] < mbr.getMax(d)) {
          max[d - 1] = mbr.getMax(d);
        }
      }
    }
    return new HyperBoundingBox(min, max);
  }

  @Override
  public int getDimensionality() {
    return getEntry(0).getMBR().getDimensionality();
  }

  /**
   * Adjusts the parameters of the entry representing this node.
   * 
   * @param entry the entry representing this node
   */
  public void adjustEntry(E entry) {
    entry.setMBR(mbr());
  }

  /**
   * Adjusts the parameters of the entry representing this node. Only applicable if one
   * object was inserted or the size of an existing node increased.
   * @param entry the entry representing this node
   * @param responsibleMBR the MBR of the object or node which is responsible for the call of the method
   * @return the MBR of the new Node
   */
  public E adjustEntryIncremental(E entry, HyperBoundingBox responsibleMBR){
      entry.setMBR(entry.getMBR().union(responsibleMBR));
      return entry;
  }

  /**   * Initializes a reinsert operation. Deletes all entries in this node and adds
   * all entries from start index on to this node's children.
   * 
   * @param start the start index of the entries that will be reinserted
   * @param reInsertEntries the array of entries to be reinserted
   */
  protected <D extends Distance<D>> void initReInsert(int start, DistanceEntry<D, E>[] reInsertEntries) {
    deleteAllEntries();

    if(isLeaf()) {
      for(int i = start; i < reInsertEntries.length; i++) {
        addLeafEntry(reInsertEntries[i].getEntry());
      }
    }
    else {
      for(int i = start; i < reInsertEntries.length; i++) {
        addDirectoryEntry(reInsertEntries[i].getEntry());
      }
    }
  }

  /**
   * Splits the entries of this node into a new node at the specified splitPoint
   * and returns the newly created node.
   * 
   * @param sorting the sorted entries of this node
   * @param splitPoint the split point of the entries
   * @return the newly created split node
   */
  public N splitEntries(List<E> sorting, int splitPoint) {
    StringBuffer msg = new StringBuffer("\n");

    if(isLeaf()) {
      N newNode = createNewLeafNode(getCapacity());
      getFile().writePage(newNode);

      deleteAllEntries();

      for(int i = 0; i < splitPoint; i++) {
        addLeafEntry(sorting.get(i));
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(getPageID()).append(" ");
          msg.append(sorting.get(i)).append("\n");
        }
      }

      for(int i = 0; i < sorting.size() - splitPoint; i++) {
        newNode.addLeafEntry(sorting.get(splitPoint + i));
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(newNode.getPageID()).append(" ");
          msg.append(sorting.get(splitPoint + i)).append("\n");
        }
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

      for(int i = 0; i < splitPoint; i++) {
        addDirectoryEntry(sorting.get(i));
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(getPageID()).append(" ");
          msg.append(sorting.get(i)).append("\n");
        }
      }

      for(int i = 0; i < sorting.size() - splitPoint; i++) {
        newNode.addDirectoryEntry(sorting.get(splitPoint + i));
        if(LoggingConfiguration.DEBUG) {
          msg.append("n_").append(newNode.getPageID()).append(" ");
          msg.append(sorting.get(splitPoint + i)).append("\n");
        }
      }
      if(LoggingConfiguration.DEBUG) {
        Logger.getLogger(this.getClass().getName()).fine(msg.toString());
      }
      return newNode;
    }
  }

  /**
   * Tests this node (for debugging purposes).
   */
  @SuppressWarnings("unchecked")
  public final void integrityCheck() {
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

          node.integrityCheckParameters((N) this, i);
          node.integrityCheck();
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
   * @param parent the parent holding the entry representing this node
   * @param index the index of the entry in the parents child array
   */
  protected void integrityCheckParameters(N parent, int index) {
    // test if mbr is correctly set
    E entry = parent.getEntry(index);
    HyperBoundingBox mbr = mbr();

    if(entry.getMBR() == null && mbr == null) {
      return;
    }
    if(!entry.getMBR().equals(mbr)) {
      String soll = mbr.toString();
      String ist = entry.getMBR().toString();
      throw new RuntimeException("Wrong MBR in node " + parent.getPageID() + " at index " + index + " (child " + entry + ")" + "\nsoll: " + soll + ",\n ist: " + ist);
    }
  }

  /**
   * Calls the super method and writes the id of this node, the numEntries and
   * the entries array to the specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    // TODO: do we need to write/read the capacity?
    out.writeInt(entries.length);
    for(E entry : entries) {
      if(entry == null) {
        break;
      }
      entry.writeExternal(out);
    }
  }

  /**
   * Reads the id of this node, the numEntries and the entries array from the
   * specified stream.
   * 
   * @param in the stream to read data from in order to restore the object
   * @throws java.io.IOException if I/O errors occur
   * @throws ClassNotFoundException If the class for an object being restored
   *         cannot be found.
   */
  @Override
  @SuppressWarnings("unchecked")
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);

    // TODO: do we need to write/read the capacity?
    final int capacity = in.readInt();
    if(isLeaf) {
      entries = (E[]) new SpatialLeafEntry[capacity];
      for(int i = 0; i < numEntries; i++) {
        SpatialLeafEntry s = new SpatialLeafEntry();
        s.readExternal(in);
        entries[i] = (E) s;
      }
    }
    else {
      entries = (E[]) new SpatialDirectoryEntry[capacity];
      for(int i = 0; i < numEntries; i++) {
        SpatialDirectoryEntry s = new SpatialDirectoryEntry();
        s.readExternal(in);
        entries[i] = (E) s;
      }
    }

  }

  /**
   * Creates a new leaf node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  @Override
  abstract protected N createNewLeafNode(int capacity);

  /**
   * Creates a new directory node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  @Override
  abstract protected N createNewDirectoryNode(int capacity);
}