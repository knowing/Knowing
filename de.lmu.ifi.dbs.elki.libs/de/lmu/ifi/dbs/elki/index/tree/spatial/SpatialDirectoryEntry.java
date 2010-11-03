package de.lmu.ifi.dbs.elki.index.tree.spatial;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.index.tree.AbstractDirectoryEntry;

/**
 * Represents an entry in a directory node of a spatial index.
 * 
 * A SpatialDirectoryEntry consists of an id (representing the unique id of the
 * underlying spatial node) and the minimum bounding rectangle of the underlying
 * spatial node.
 * 
 * @author Elke Achtert
 */
public class SpatialDirectoryEntry extends AbstractDirectoryEntry implements SpatialEntry {
  private static final long serialVersionUID = 1;

  /**
   * The minimum bounding rectangle of the underlying spatial node.
   */
  private HyperBoundingBox mbr;

  /**
   * Empty constructor for serialization purposes.
   */
  public SpatialDirectoryEntry() {
    // empty constructor
  }

  /**
   * Constructs a new SpatialDirectoryEntry object with the given parameters.
   * 
   * @param id the unique id of the underlying spatial node
   * @param mbr the minimum bounding rectangle of the underlying spatial node
   */
  public SpatialDirectoryEntry(int id, HyperBoundingBox mbr) {
    super(id);
    this.mbr = mbr;
  }

  @Override
  public HyperBoundingBox getMBR() {
    return mbr;
  }

  @Override
  public int getDimensionality() {
    return mbr.getDimensionality();
  }

  /**
   * @return the coordinate at the specified dimension of the minimum hyper
   *         point of the MBR of the underlying node
   */
  @Override
  public double getMin(int dimension) {
    return mbr.getMin(dimension);
  }

  /**
   * @return the coordinate at the specified dimension of the maximum hyper
   *         point of the MBR of the underlying node
   */
  @Override
  public double getMax(int dimension) {
    return mbr.getMax(dimension);
  }

  /**
   * Sets the MBR of this entry.
   * 
   * @param mbr the MBR to be set
   */
  @Override
  public void setMBR(HyperBoundingBox mbr) {
    this.mbr = mbr;
  }

  /**
   * Calls the super method and writes the MBR object of this entry to the
   * specified output stream.
   * 
   * @param out the stream to write the object to
   * @throws java.io.IOException Includes any I/O exceptions that may occur
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    super.writeExternal(out);
    mbr.writeExternal(out);
  }

  /**
   * Calls the super method and reads the MBR object of this entry from the
   * specified input stream.
   * 
   * @param in the stream to read data from in order to restore the object
   * @throws java.io.IOException if I/O errors occur
   * @throws ClassNotFoundException If the class for an object being restored
   *         cannot be found.
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    super.readExternal(in);
    this.mbr = new HyperBoundingBox();
    this.mbr.readExternal(in);
  }
}
