package de.lmu.ifi.dbs.elki.index.tree.spatial.rstarvariants;

import de.lmu.ifi.dbs.elki.data.HyperBoundingBox;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialComparator;
import de.lmu.ifi.dbs.elki.index.tree.spatial.SpatialEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Encapsulates the required parameters for a topological split of a R*-Tree.
 * 
 * @author Elke Achtert
 */
class TopologicalSplit<E extends SpatialEntry> {
  /**
   * The split axis.
   */
  private int splitAxis = 0;

  /**
   * The index of the split point.
   */
  private int splitPoint = -1;

  /**
   * Indicates whether the sorting according to maximal or to minimal value has
   * been used for choosing the split axis and split point.
   */
  private int bestSorting;

  /**
   * The entries sorted according to their max values of their MBRs.
   */
  private List<E> maxSorting;

  /**
   * The entries sorted according to their min values of their MBRs.
   */
  private List<E> minSorting;

  /**
   * Creates a new Split object.
   * 
   * @param entries the entries to be split
   * @param minEntries number of minimum entries in the node to be split
   */
  public TopologicalSplit(List<E> entries, int minEntries) {
    chooseSplitAxis(entries, minEntries);
    chooseSplitPoint(minEntries);
  }

  /**
   * Returns the split axis.
   * 
   * @return the split axis
   */
  public int getSplitAxis() {
    return splitAxis;
  }

  /**
   * Returns the split point.
   * 
   * @return the split point
   */
  public int getSplitPoint() {
    return splitPoint;
  }

  /**
   * Returns the entries to be split sorted according to their max values of
   * their MBRs.
   * 
   * @return the entries sorted according to their max values of their MBRs
   */
  public List<E> getMaxSorting() {
    return maxSorting;
  }

  /**
   * Returns the entries to be split sorted according to their min values of
   * their MBRs.
   * 
   * @return the entries sorted according to their min values of their MBRs
   */
  public List<E> getMinSorting() {
    return minSorting;
  }

  /**
   * Returns whether the sorting according to maximal or to minimal value has
   * been used for choosing the split axis and split point.
   * 
   * @return SpatialComparator.MIN if the sorting according to minimal value has
   *         been used, SpatialComparator.MAX otherwise
   */
  public int getBestSorting() {
    return bestSorting;
  }

  /**
   * Chooses a split axis.
   * 
   * @param entries the entries to be split
   * @param minEntries number of minimum entries in the node to be split
   */
  private void chooseSplitAxis(List<E> entries, int minEntries) {
    int dim = entries.get(0).getMBR().getDimensionality();

    maxSorting = new ArrayList<E>(entries);
    minSorting = new ArrayList<E>(entries);

    // best value for the surface
    double minSurface = Double.MAX_VALUE;
    // comparator used by sort method

    for(int i = 1; i <= dim; i++) {
      double currentPerimeter = 0;
      // sort the entries according to their minimal and according to their
      // maximal value
      final SpatialComparator compMin = new SpatialComparator(i, SpatialComparator.MIN);
      Collections.sort(minSorting, compMin);
      final SpatialComparator compMax = new SpatialComparator(i, SpatialComparator.MAX);
      Collections.sort(maxSorting, compMax);

      HyperBoundingBox mbr_min_left = minSorting.get(0).getMBR();
      HyperBoundingBox mbr_min_right = minSorting.get(minSorting.size() - 1).getMBR();
      HyperBoundingBox mbr_max_left = maxSorting.get(0).getMBR();
      HyperBoundingBox mbr_max_right = maxSorting.get(maxSorting.size() - 1).getMBR();

      for(int k = 1; k < entries.size() - minEntries; k++) {

        mbr_min_left = mbr_min_left.union(minSorting.get(k).getMBR());
        mbr_min_right = mbr_min_right.union(minSorting.get(minSorting.size() - 1 - k).getMBR());
        mbr_max_left = mbr_max_left.union(maxSorting.get(k).getMBR());
        mbr_max_right = mbr_max_right.union(maxSorting.get(maxSorting.size() - 1 - k).getMBR());
        if(k >= minEntries - 1) {
          currentPerimeter += mbr_min_left.perimeter() + mbr_min_right.perimeter() + mbr_max_left.perimeter() + mbr_max_right.perimeter();
        }
      }
      if(currentPerimeter < minSurface) {
        splitAxis = i;
        minSurface = currentPerimeter;
      }
    }
  }

  /**
   * Chooses a split axis.
   * 
   * @param minEntries number of minimum entries in the node to be split
   */
  private void chooseSplitPoint(int minEntries) {
    // numEntries
    int numEntries = maxSorting.size();
    // sort upper and lower in the right dimension
    final SpatialComparator compMin = new SpatialComparator(splitAxis, SpatialComparator.MIN);
    Collections.sort(minSorting, compMin);
    final SpatialComparator compMax = new SpatialComparator(splitAxis, SpatialComparator.MAX);
    Collections.sort(maxSorting, compMax);

    // the split point (first set to minimum entries in the node)
    splitPoint = minEntries;
    // best value for the overlap
    double minOverlap = Double.MAX_VALUE;
    // the volume of mbr1 and mbr2
    double volume = 0.0;
    // indicates whether the sorting according to maximal or to minimal value is
    // best for the split axis
    bestSorting = -1;

    for(int i = 0; i <= numEntries - 2 * minEntries; i++) {
      // test the sorting with respect to the minimal values
      HyperBoundingBox mbr1 = mbr(minSorting, 0, minEntries + i);
      HyperBoundingBox mbr2 = mbr(minSorting, minEntries + i, numEntries);
      double currentOverlap = mbr1.overlap(mbr2);
      if(currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
        minOverlap = currentOverlap;
        splitPoint = minEntries + i;
        bestSorting = SpatialComparator.MIN;
        volume = mbr1.volume() + mbr2.volume();
      }
      // test the sorting with respect to the maximal values
      mbr1 = mbr(maxSorting, 0, minEntries + i);
      mbr2 = mbr(maxSorting, minEntries + i, numEntries);
      currentOverlap = mbr1.overlap(mbr2);
      if(currentOverlap < minOverlap || (currentOverlap == minOverlap && (mbr1.volume() + mbr2.volume()) < volume)) {
        minOverlap = currentOverlap;
        splitPoint = minEntries + i;
        bestSorting = SpatialComparator.MAX;
        volume = mbr1.volume() + mbr2.volume();
      }
    }
  }

  /**
   * Computes and returns the mbr of the specified nodes, only the nodes between
   * from and to index are considered.
   * 
   * @param entries the array of nodes
   * @param from the start index
   * @param to the end index
   * @return the mbr of the specified nodes
   */
  private HyperBoundingBox mbr(final List<E> entries, final int from, final int to) {
    double[] min = new double[entries.get(from).getMBR().getDimensionality()];
    double[] max = new double[entries.get(from).getMBR().getDimensionality()];

    for(int d = 1; d <= min.length; d++) {
      min[d - 1] = entries.get(from).getMBR().getMin(d);
      max[d - 1] = entries.get(from).getMBR().getMax(d);
    }

    for(int i = from + 1; i < to; i++) {
      HyperBoundingBox currMBR = entries.get(i).getMBR();
      for(int d = 1; d <= min.length; d++) {
        if(min[d - 1] > currMBR.getMin(d)) {
          min[d - 1] = currMBR.getMin(d);
        }
        if(max[d - 1] < currMBR.getMax(d)) {
          max[d - 1] = currMBR.getMax(d);
        }
      }
    }
    return new HyperBoundingBox(min, max);
  }
}
