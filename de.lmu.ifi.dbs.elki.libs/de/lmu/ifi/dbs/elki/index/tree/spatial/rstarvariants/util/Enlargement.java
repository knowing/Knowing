package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.util;

import de.lmu.ifi.dbs.elki.index.tree.TreeIndexPathComponent;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;

/**
 * Encapsulates the parameters for enlargement of nodes after insertion of new
 * objects.
 * 
 * @author Elke Achtert
 * @param <E> Entry type
 */
public class Enlargement<E extends SpatialEntry> implements Comparable<Enlargement<E>> {
  /**
   * The path information of the entry representing the node.
   */
  private TreeIndexPathComponent<E> pathComponent;

  /**
   * The volume of the node's MBR.
   */
  private double volume;

  /**
   * The increment of the volume.
   */
  private double volInc;

  /**
   * The increment of the overlap.
   */
  private double overlapInc;

  /**
   * Creates an new Enlargement object with the specified parameters.
   * 
   * @param pathComponent the path information of the entry representing the
   *        node
   * @param volume the volume of the node's MBR
   * @param volInc the increment of the volume
   * @param overlapInc the increment of the overlap
   */
  public Enlargement(TreeIndexPathComponent<E> pathComponent, double volume, double volInc, double overlapInc) {
    this.pathComponent = pathComponent;
    this.volume = volume;
    this.volInc = volInc;
    this.overlapInc = overlapInc;
  }

  /**
   * Compares this Enlargement with the specified Enlargement. First the
   * increment of the overlap will be compared. If both are equal the increment
   * of the volume will be compared. If also both are equal the volumes of both
   * nodes will be compared. If both are equal the ids of the nodes will be
   * compared.
   * 
   * @param other the Enlargement to be compared.
   * @return a negative integer, zero, or a positive integer as this Enlargement
   *         is less than, equal to, or greater than the specified Enlargement.
   */
  @Override
  public int compareTo(Enlargement<E> other) {
    if(this.overlapInc < other.overlapInc) {
      return -1;
    }
    if(this.overlapInc > other.overlapInc) {
      return +1;
    }

    if(this.volInc < other.volInc) {
      return -1;
    }
    if(this.volInc > other.volInc) {
      return +1;
    }

    if(this.volume < other.volume) {
      return -1;
    }
    if(this.volume > other.volume) {
      return +1;
    }

    return this.pathComponent.getEntry().getEntryID() - other.pathComponent.getEntry().getEntryID();
  }

  /**
   * Returns the path information of the entry representing the node.
   * 
   * @return the path information of the entry representing the node
   */
  public TreeIndexPathComponent<E> getPathComponent() {
    return pathComponent;
  }
}