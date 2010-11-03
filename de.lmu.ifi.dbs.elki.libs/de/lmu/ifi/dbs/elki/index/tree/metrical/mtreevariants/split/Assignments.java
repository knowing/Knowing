package de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.split;

import de.lmu.ifi.dbs.elki.database.ids.DBID;
import de.lmu.ifi.dbs.elki.distance.distancevalue.Distance;
import de.lmu.ifi.dbs.elki.index.tree.metrical.mtreevariants.MTreeEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Encapsulates the attributes of an assignment during a split.
 * 
 * @author Elke Achtert
 * @param <D> the type of Distance used in the M-Tree
 * @param <E> the type of MetricalEntry used in the M-Tree
 */
public class Assignments<D extends Distance<D>, E extends MTreeEntry<D>> {
  /**
   * The id of the first routing object.
   */
  private DBID id1;

  /**
   * The id of the second routing object.
   */
  private DBID id2;

  /**
   * The first covering radius.
   */
  private D firstCoveringRadius;

  /**
   * The second covering radius.
   */
  private D secondCoveringRadius;

  /**
   * The assignments to the first routing object.
   */
  private List<E> firstAssignments;

  /**
   * The assignments to the second routing object.
   */
  private List<E> secondAssignments;

  /**
   * Provides an assignment during a split of an MTree node.
   * 
   * @param id1 the first routing object
   * @param id2 the second routing object
   * @param firstCoveringRadius the first covering radius
   * @param secondCoveringRadius the second covering radius
   * @param firstAssignments the assignments to the first routing object
   * @param secondAssignments the assignments to the second routing object
   */
  public Assignments(DBID id1, DBID id2, D firstCoveringRadius, D secondCoveringRadius, Set<E> firstAssignments, Set<E> secondAssignments) {
    this.id1 = id1;
    this.id2 = id2;
    this.firstCoveringRadius = firstCoveringRadius;
    this.secondCoveringRadius = secondCoveringRadius;
    this.firstAssignments = new ArrayList<E>(firstAssignments);
    this.secondAssignments = new ArrayList<E>(secondAssignments);
  }

  /**
   * Returns the id of the first routing object.
   * 
   * @return the id of the first routing object
   */
  public DBID getFirstRoutingObject() {
    return id1;
  }

  /**
   * Returns the id of the second routing object.
   * 
   * @return the id of the second routing object
   */
  public DBID getSecondRoutingObject() {
    return id2;
  }

  /**
   * Returns the first covering radius.
   * 
   * @return the first covering radius
   */
  public D getFirstCoveringRadius() {
    return firstCoveringRadius;
  }

  /**
   * Returns the second covering radius.
   * 
   * @return the second covering radius
   */
  public D getSecondCoveringRadius() {
    return secondCoveringRadius;
  }

  /**
   * Returns the assignments to the first routing object.
   * 
   * @return the assignments to the first routing object
   */
  public List<E> getFirstAssignments() {
    return firstAssignments;
  }

  /**
   * Returns the assignments to the second routing object.
   * 
   * @return the assignments to the second routing object
   */
  public List<E> getSecondAssignments() {
    return secondAssignments;
  }
}
