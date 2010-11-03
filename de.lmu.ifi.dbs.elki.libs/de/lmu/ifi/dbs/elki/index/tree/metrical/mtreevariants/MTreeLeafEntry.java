package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.AbstractLeafEntry;

/**
 * Represents an entry in a leaf node of an M-Tree. A MTreeLeafEntry consists of
 * an id (representing the unique id of the underlying object in the database)
 * and the distance from the data object to its parent routing object in the
 * M-Tree.
 * 
 * @author Elke Achtert
 * @param <D> the type of Distance used in the M-Tree
 */
public class MTreeLeafEntry<D extends Distance<D>> extends AbstractLeafEntry implements MTreeEntry<D> {
  private static final long serialVersionUID = 1;

  /**
   * The distance from the underlying data object to its parent's routing
   * object.
   */
  private D parentDistance;

  /**
   * Empty constructor for serialization purposes.
   */
  public MTreeLeafEntry() {
    // empty
  }

  /**
   * Provides a new MTreeLeafEntry object with the given parameters.
   * 
   * @param objectID the id of the underlying data object
   * @param parentDistance the distance from the underlying data object to its
   *        parent's routing object
   */
  public MTreeLeafEntry(DBID objectID, D parentDistance) {
    super(objectID);
    this.parentDistance = parentDistance;
  }

  /**
   * Returns the id of the underlying data object of this entry.
   * 
   * @return the id of the underlying data object of this entry
   */
  @Override
  public final DBID getRoutingObjectID() {
    return getDBID();
  }

  /**
   * Sets the id of the underlying data object of this entry.
   * 
   * @param objectID the id to be set
   */
  @Override
  public final void setRoutingObjectID(DBID objectID) {
    throw new UnsupportedOperationException("Leaf entrys should not be assigned a routing object.");
    //super.setEntryID(objectID.getIntegerID());
  }

  /**
   * Returns the distance from the underlying data object to its parent's
   * routing object.
   * 
   * @return the distance from the underlying data object to its parent's
   *         routing object
   */
  @Override
  public final D getParentDistance() {
    return parentDistance;
  }

  /**
   * Sets the distance from the underlying data object to its parent's routing
   * object.
   * 
   * @param parentDistance the distance to be set
   */
  @Override
  public final void setParentDistance(D parentDistance) {
    this.parentDistance = parentDistance;
  }

  /**
   * Returns null, since a leaf entry has no covering radius.
   * 
   * @return null
   */
  @Override
  public D getCoveringRadius() {
    return null;
  }

  /**
   * Throws an UnsupportedOperationException, since a leaf entry has no covering
   * radius.
   * 
   * @throws UnsupportedOperationException thrown since a leaf has no covering radius
   */
  @Override
  public void setCoveringRadius(@SuppressWarnings("unused") D coveringRadius) {
    throw new UnsupportedOperationException("This entry is not a directory entry!");
  }

  /**
   * Calls the super method and writes the parentDistance of this entry to the
   * specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeObject(parentDistance);
  }

  /**
   * Calls the super method and reads the parentDistance of this entry from the
   * specified input stream.
   */
  @SuppressWarnings("unchecked")
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    this.parentDistance = (D) in.readObject();
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the object to be tested
   * @return true, if the super method returns true and o is an MTreeLeafEntry
   *         and has the same parentDistance as this entry.
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

    final MTreeLeafEntry<D> that = (MTreeLeafEntry<D>) o;

    return !(parentDistance != null ? !parentDistance.equals(that.parentDistance) : that.parentDistance != null);
  }
}