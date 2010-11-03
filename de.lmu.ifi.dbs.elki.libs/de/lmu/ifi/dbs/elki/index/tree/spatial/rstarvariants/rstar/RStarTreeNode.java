package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.rstar;

import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;
import de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants.AbstractRStarTreeNode;
import de.lmu.ifi.dbs.elki.persistent.PageFile;

/**
 * Represents a node in an R*-Tree.
 *
 * @author Elke Achtert 
 */
public class RStarTreeNode extends AbstractRStarTreeNode<RStarTreeNode, SpatialEntry> {
  private static final long serialVersionUID = 1;

  /**
   * Empty constructor for Externalizable interface.
   */
  public RStarTreeNode() {
	  // empty constructor
  }

  /**
   * Creates a new RStarTreeNode with the specified parameters.
   *
   * @param file     the file storing the R*-Tree
   * @param capacity the capacity (maximum number of entries plus 1 for overflow)
   *                 of this node
   * @param isLeaf   indicates whether this node is a leaf node
   */
  public RStarTreeNode(PageFile<RStarTreeNode> file, int capacity, boolean isLeaf) {
    super(file, capacity, isLeaf, SpatialEntry.class);
  }

  /**
   * Creates a new leaf node with the specified capacity.
   * Subclasses have to overwrite this method.
   *
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  @Override
  protected RStarTreeNode createNewLeafNode(int capacity) {
    return new RStarTreeNode(getFile(), capacity, true);
  }

  /**
   * Creates a new directory node with the specified capacity.
   * Subclasses have to overwrite this method.
   *
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  @Override
  protected RStarTreeNode createNewDirectoryNode(int capacity) {
    return new RStarTreeNode(getFile(), capacity, false);
  }
}
