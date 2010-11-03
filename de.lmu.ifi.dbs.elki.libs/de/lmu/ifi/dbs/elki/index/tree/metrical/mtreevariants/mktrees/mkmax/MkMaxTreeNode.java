package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.mktrees.mkmax;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.DistanceUtil;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.AbstractMTree;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.AbstractMTreeNode;
import de.lmu.ifi.dbs.elki.persistent.PageFile;

/**
 * Represents a node in an {@link MkMaxTree}.
 *
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject to be stored in the MkMaxTree
 * @param <D> the type of Distance used in the MkMaxTree
 */
class MkMaxTreeNode<O extends DatabaseObject, D extends Distance<D>>
    extends AbstractMTreeNode<O, D, MkMaxTreeNode<O, D>, MkMaxEntry<D>> {
    private static final long serialVersionUID = 1;

    /**
     * Empty constructor for Externalizable interface.
     */
    public MkMaxTreeNode() {
        // empty constructor
    }

    /**
     * Creates a new MkMaxTreeNode object.
     *
     * @param file     the file storing the MkMaxTree
     * @param capacity the capacity (maximum number of entries plus 1 for overflow)
     *                 of this node
     * @param isLeaf   indicates whether this node is a leaf node
     */
    public MkMaxTreeNode(PageFile<MkMaxTreeNode<O, D>> file, int capacity, boolean isLeaf) {
        super(file, capacity, isLeaf, MkMaxEntry.class);
    }

    /**
     * @return a new MkMaxTreeNode which is a leaf node
     */
    @Override
    protected MkMaxTreeNode<O, D> createNewLeafNode(int capacity) {
        return new MkMaxTreeNode<O, D>(getFile(), capacity, true);
    }

    /**
     * @return a new MkMaxTreeNode which is a directory node
     */
    @Override
    protected MkMaxTreeNode<O, D> createNewDirectoryNode(int capacity) {
        return new MkMaxTreeNode<O, D>(getFile(), capacity, false);
    }

    /**
     * Determines and returns the k-nearest neighbor distance of this node as the maximum
     * of the k-nearest neighbor distances of all entries.
     *
     * @param distanceFunction the distance function
     * @return the knn distance of this node
     */
    protected D kNNDistance(DistanceQuery<O, D> distanceFunction) {
        D knnDist = distanceFunction.nullDistance();
        for (int i = 0; i < getNumEntries(); i++) {
            MkMaxEntry<D> entry = getEntry(i);
            knnDist = DistanceUtil.max(knnDist, entry.getKnnDistance());
        }
        return knnDist;
    }

    /**
     * Calls the super method
     * and adjust additionally the k-nearest neighbor distance of this node
     * as the maximum of the k-nearest neighbor distances of all its entries.
     */
    @Override
    public void adjustEntry(MkMaxEntry<D> entry, DBID routingObjectID, D parentDistance, AbstractMTree<O, D, MkMaxTreeNode<O, D>, MkMaxEntry<D>> mTree) {
        super.adjustEntry(entry, routingObjectID, parentDistance, mTree);
        // adjust knn distance
        entry.setKnnDistance(kNNDistance(mTree.getDistanceQuery()));
    }

    /**
     * Calls the super method and tests if
     * the k-nearest neighbor distance of this node is correctly set.
     */
    @Override
    protected void integrityCheckParameters(MkMaxEntry<D> parentEntry, MkMaxTreeNode<O, D> parent, int index, AbstractMTree<O, D, MkMaxTreeNode<O, D>, MkMaxEntry<D>> mTree) {
        super.integrityCheckParameters(parentEntry, parent, index, mTree);
        // test if knn distance is correctly set
        MkMaxEntry<D> entry = parent.getEntry(index);
        D knnDistance = kNNDistance(mTree.getDistanceQuery());
        if (!entry.getKnnDistance().equals(knnDistance)) {
            String soll = knnDistance.toString();
            String ist = entry.getKnnDistance().toString();
            throw new RuntimeException("Wrong knnDistance in node "
                + parent.getPageID() + " at index " + index + " (child "
                + entry + ")" + "\nsoll: " + soll
                + ",\n ist: " + ist);
        }
    }
}
