package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.AbstractDirectoryEntry;

/**
 * Represents an entry in a directory node of an M-Tree. A MTreeDirectoryEntry
 * consists of an id (representing the unique id of the underlying node), the id
 * of the routing object, the covering radius of the entry and the distance from
 * the routing object of the entry to its parent's routing object in the M-Tree.
 * 
 * @author Elke Achtert
 * @param <D> the type of Distance used in the M-Tree
 */
public class MTreeDirectoryEntry<D extends Distance<D>> extends AbstractDirectoryEntry implements MTreeEntry<D> {
  private static final long serialVersionUID = 1;

  /**
   * The id of routing object of this entry.
   */
  private DBID routingObjectID;

  /**
   * The distance from the routing object of this entry to its parent's routing
   * object.
   */
  private D parentDistance;

  /**
   * The covering radius of the entry.
   */
  private D coveringRadius;

  /**
   * Empty constructor for serialization purposes.
   */
  public MTreeDirectoryEntry() {
    // empty constructor
  }

  /**
   * Provides a new MTreeDirectoryEntry with the given parameters.
   * 
   * @param objectID the id of the routing object
   * @param parentDistance the distance from the routing object of this entry to
   *        its parent's routing object
   * @param nodeID the id of the underlying node
   * @param coveringRadius the covering radius of the entry
   */
  public MTreeDirectoryEntry(DBID objectID, D parentDistance, Integer nodeID, D coveringRadius) {
    super(nodeID);
    this.routingObjectID = objectID;
    this.parentDistance = parentDistance;
    this.coveringRadius = coveringRadius;
  }

  /**
   * Returns the covering radius of this entry.
   * 
   * @return the covering radius of this entry
   */
  @Override
  public final D getCoveringRadius() {
    return coveringRadius;
  }

  /**
   * Sets the covering radius of this entry.
   * 
   * @param coveringRadius the covering radius to be set
   */
  @Override
  public final void setCoveringRadius(D coveringRadius) {
    this.coveringRadius = coveringRadius;
  }

  /**
   * Returns the id of the routing object of this entry.
   * 
   * @return the id of the routing object
   */
  @Override
  public final DBID getRoutingObjectID() {
    return routingObjectID;
  }

  /**
   * Sets the id of the routing object of this entry.
   * 
   * @param objectID the id to be set
   */
  @Override
  public final void setRoutingObjectID(DBID objectID) {
    this.routingObjectID = objectID;
  }

  /**
   * Returns the distance from the routing object of this entry to its parent's
   * routing object.
   * 
   * @return the distance from the routing object of this entry to its parent's
   *         routing object.
   */
  @Override
  public final D getParentDistance() {
    return parentDistance;
  }

  /**
   * Sets the distance from the object to its parent object.
   * 
   * @param parentDistance the distance to be set
   */
  @Override
  public final void setParentDistance(D parentDistance) {
    this.parentDistance = parentDistance;
  }

  /**
   * Calls the super method and writes the routingObjectID, the parentDistance
   * and the coveringRadius of this entry to the specified stream.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    out.writeInt(routingObjectID.getIntegerID());
    out.writeObject(parentDistance);
    out.writeObject(coveringRadius);
  }

  /**
   * Calls the super method and reads the routingObjectID, the parentDistance
   * and the coveringRadius of this entry from the specified input stream.
   */
  @Override
  @SuppressWarnings( { "unchecked" })
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    this.routingObjectID = DBIDUtil.importInteger(in.readInt());
    this.parentDistance = (D) in.readObject();
    this.coveringRadius = (D) in.readObject();
  }

  /**
   * Returns the id as a string representation of this entry.
   * 
   * @return a string representation of this entry
   */
  @Override
  public String toString() {
    return super.toString() + " (o.id = " + getRoutingObjectID() + ")";
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the object to be tested
   * @return true, if the super method returns true and o is an
   *         MTreeDirectoryEntry and has the same coveringRadius, parentDistance
   *         and routingObjectID as this entry.
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

    final MTreeDirectoryEntry<D> that = (MTreeDirectoryEntry<D>) o;

    if(coveringRadius != null ? !coveringRadius.equals(that.coveringRadius) : that.coveringRadius != null) {
      return false;
    }
    if(parentDistance != null ? !parentDistance.equals(that.parentDistance) : that.parentDistance != null) {
      return false;
    }
    return !(routingObjectID != null ? !routingObjectID.equals(that.routingObjectID) : that.routingObjectID != null);
  }
}
