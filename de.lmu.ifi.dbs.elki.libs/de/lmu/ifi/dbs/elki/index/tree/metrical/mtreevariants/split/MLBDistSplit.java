package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.split;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.database.query.DistanceQuery;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.DistanceEntry;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.AbstractMTreeNode;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.MTreeEntry;

/**
 * Encapsulates the required methods for a split of a node in an M-Tree. The
 * routing objects are chosen according to the M_LB_DIST strategy.
 * 
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject to be stored in the M-Tree
 * @param <D> the type of Distance used in the M-Tree
 * @param <N> the type of AbstractMTreeNode used in the M-Tree
 * @param <E> the type of MetricalEntry used in the M-Tree
 */
public class MLBDistSplit<O extends DatabaseObject, D extends Distance<D>, N extends AbstractMTreeNode<O, D, N, E>, E extends MTreeEntry<D>> extends MTreeSplit<O, D, N, E> {
  /**
   * Creates a new split object.
   * 
   * @param node the node to be split
   * @param distanceFunction the distance function
   */
  public MLBDistSplit(N node, DistanceQuery<O, D> distanceFunction) {
    super();
    promote(node, distanceFunction);
  }

  /**
   * Selects the second object of the specified node to be promoted and stored
   * into the parent node and partitions the entries according to the M_LB_DIST
   * strategy.
   * <p/>
   * This strategy considers all possible pairs of objects and chooses the pair
   * of objects for which the distance is maximum.
   * 
   * @param node the node to be split
   * @param distanceFunction the distance function
   */
  private void promote(N node, DistanceQuery<O, D> distanceFunction) {
    DBID firstPromoted = null;
    DBID secondPromoted = null;

    // choose first and second routing object
    D currentMaxDist = distanceFunction.nullDistance();
    for(int i = 0; i < node.getNumEntries(); i++) {
      DBID id1 = node.getEntry(i).getRoutingObjectID();
      for(int j = i + 1; j < node.getNumEntries(); j++) {
        DBID id2 = node.getEntry(j).getRoutingObjectID();

        D distance = distanceFunction.distance(id1, id2);
        if(distance.compareTo(currentMaxDist) >= 0) {
          firstPromoted = id1;
          secondPromoted = id2;
          currentMaxDist = distance;
        }
      }
    }

    // partition the entries
    List<DistanceEntry<D, E>> list1 = new ArrayList<DistanceEntry<D, E>>();
    List<DistanceEntry<D, E>> list2 = new ArrayList<DistanceEntry<D, E>>();
    for(int i = 0; i < node.getNumEntries(); i++) {
      DBID id = node.getEntry(i).getRoutingObjectID();
      D d1 = distanceFunction.distance(firstPromoted, id);
      D d2 = distanceFunction.distance(secondPromoted, id);

      list1.add(new DistanceEntry<D, E>(node.getEntry(i), d1, i));
      list2.add(new DistanceEntry<D, E>(node.getEntry(i), d2, i));
    }
    Collections.sort(list1);
    Collections.sort(list2);

    assignments = balancedPartition(node, firstPromoted, secondPromoted, distanceFunction);
  }
}
