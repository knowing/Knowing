package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.mktrees.mktab;

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.MTreeLeafEntry;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an entry in a leaf node of a MkTab-Tree. Additionally to a
 * MTreeLeafEntry a MkTabLeafEntry holds a list of its knn distances for
 * parameters k <= k_max.
 * 
 * @author Elke Achtert
 */
class MkTabLeafEntry<D extends Distance<D>> extends MTreeLeafEntry<D> implements MkTabEntry<D> {
  private static final long serialVersionUID = 1;

  /**
   * The maximal number of knn distances to be stored.
   */
  private int k_max;

  /**
   * The knn distances of the underlying data object.
   */
  private List<D> knnDistances;

  /**
   * Empty constructor for serialization purposes.
   */
  public MkTabLeafEntry() {
    // empty constructor
  }

  /**
   * Provides a new MkMaxLeafEntry with the given parameters.
   * 
   * @param objectID the id of the underlying data object
   * @param parentDistance the distance from the underlying data object to its
   *        parent's routing object
   * @param knnDistances the knn distances of the underlying data object
   */
  public MkTabLeafEntry(DBID objectID, D parentDistance, List<D> knnDistances) {
    super(objectID, parentDistance);
    this.knnDistances = knnDistances;
    this.k_max = knnDistances.size();
  }

  @Override
  public List<D> getKnnDistances() {
    return knnDistances;
  }

  @Override
  public void setKnnDistances(List<D> knnDistances) {
    if(knnDistances.size() != this.k_max) {
      throw new IllegalArgumentException("Wrong length of knn distances: " + knnDistances.size());
    }

    this.knnDistances = knnDistances;
  }

  @Override
  public D getKnnDistance(int k) {
    if(k > this.k_max) {
      throw new IllegalArgumentException("Parameter k = " + k + " is not supported!");
    }

    return knnDistances.get(k - 1);
  }

  @Override
  public int getK_max() {
    return k_max;
  }

  /**
   * Calls the super method and writes the parameter k_max and the knn distances
   * of this entry to the specified stream.
   * 
   * @param out the stream to write the object to
   * @throws java.io.IOException Includes any I/O exceptions that may occur
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(k_max);
    for(int i = 0; i < k_max; i++) {
      out.writeObject(knnDistances.get(i));
    }
  }

  /**
   * Calls the super method and reads the parameter k_max and knn distance of
   * this entry from the specified input stream.
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
    k_max = in.readInt();
    knnDistances = new ArrayList<D>();
    for(int i = 0; i < k_max; i++) {
      knnDistances.add((D) in.readObject());
    }
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the object to be tested
   * @return true, if the super method returns true and o is an MkTabLeafEntry
   *         and has the same parameter k_max and knnDistances as this entry.
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }
    if(!super.equals(o)) {
      return false;
    }

    final MkTabLeafEntry<D> that = (MkTabLeafEntry<D>) o;

    if(k_max != that.k_max) {
      return false;
    }
    return !(knnDistances != null ? !knnDistances.equals(that.knnDistances) : that.knnDistances != null);
  }
}