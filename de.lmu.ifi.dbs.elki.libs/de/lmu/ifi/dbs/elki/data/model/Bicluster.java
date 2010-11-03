package de.lmu.ifi.dbs.elki.data.model;

import java.util.Arrays;
import java.util.Iterator;

import de.lmu.ifi.dbs.elki.data.FeatureVector;
import de.lmu.ifi.dbs.elki.database.Database;
import de.lmu.ifi.dbs.elki.database.ids.DBIDUtil;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;
import de.lmu.ifi.dbs.elki.database.ids.integer.ArrayStaticDBIDs;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriteable;
import de.lmu.ifi.dbs.elki.result.textwriter.TextWriterStream;
import de.lmu.ifi.dbs.elki.utilities.FormatUtil;
import de.lmu.ifi.dbs.elki.utilities.exceptions.ExceptionMessages;

/**
 * Wrapper class to provide the basic properties of a bicluster.
 * 
 * @author Arthur Zimek
 * @param <V> the type of NumberVector handled by this Result
 */
public class Bicluster<V extends FeatureVector<V, ?>> implements TextWriteable, Model {
  /**
   * The ids of the rows included in the bicluster.
   */
  private int[] rowIDs;

  /**
   * The ids of the rows included in the bicluster.
   */
  private int[] colIDs;

  /**
   * The database this bicluster is defined for.
   */
  private Database<V> database;

  /**
   * Defines a new bicluster for given parameters.
   * 
   * @param rowIDs the ids of the rows included in the bicluster
   * @param colIDs the ids of the columns included in the bicluster
   * @param database the database this bicluster is defined for
   */
  public Bicluster(int[] rowIDs, int[] colIDs, Database<V> database) {
    this.rowIDs = rowIDs;
    this.colIDs = colIDs;
    this.database = database;
  }

  /**
   * Sorts the row and column ids in ascending order.
   */
  public void sortIDs() {
    Arrays.sort(this.rowIDs);
    Arrays.sort(this.colIDs);
  }

  /**
   * The size of the cluster.
   * <p/>
   * The size of a bicluster is the number of included rows.
   * 
   * @return the size of the bicluster, i.e., the number or rows included in the
   *         bicluster
   */
  public int size() {
    return rowIDs.length;
  }

  /**
   * Provides an iterator for the row ids.
   * <p/>
   * Note that the iterator is not guaranteed to touch all elements if the
   * {@link #sortIDs()} is called during the lifetime of the iterator.
   * 
   * @return an iterator for the row ids
   */
  public Iterator<V> rowIterator() {
    return new Iterator<V>() {
      private int index = -1;

      @Override
      public boolean hasNext() {
        return index + 1 < size();
      }

      @Override
      @SuppressWarnings("synthetic-access")
      public V next() {
        return database.get(DBIDUtil.importInteger(rowIDs[++index]));
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException(ExceptionMessages.UNSUPPORTED_REMOVE);
      }

    };
  }

  /**
   * Creates a DBIDs for the row IDs included in this Bicluster.
   * 
   * 
   * @return a DBIDs for the row IDs included in this Bicluster
   */
  public DBIDs getDatabaseObjectGroup() {
    return new ArrayStaticDBIDs(this.rowIDs);
  }

  /**
   * Getter to retrieve the database
   * 
   * @return Database
   */
  public Database<V> getDatabase() {
    return database;
  }

  /**
   * Provides a copy of the column IDs contributing to the bicluster.
   * 
   * @return a copy of the columnsIDs
   */
  public int[] getColumnIDs() {
    int[] columnIDs = new int[colIDs.length];
    System.arraycopy(colIDs, 0, columnIDs, 0, colIDs.length);
    return columnIDs;
  }

  /**
   * Implementation of {@link TextWriteable} interface.
   */
  @Override
  public void writeToText(TextWriterStream out, String label) {
    if(label != null) {
      out.commentPrintLn(label);
    }
    out.commentPrintLn("Serialization class: " + this.getClass().getName());
    out.commentPrintLn("Cluster size: " + size());
    out.commentPrintLn("Cluster dimensions: " + colIDs.length);
    out.commentPrintLn("Included row IDs: " + FormatUtil.format(rowIDs));
    out.commentPrintLn("Included column IDs: " + FormatUtil.format(colIDs));
  }
}
