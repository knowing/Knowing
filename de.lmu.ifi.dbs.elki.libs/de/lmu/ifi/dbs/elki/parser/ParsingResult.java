package de.lmu.ifi.dbs.elki.parser;

import java.util.List;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.utilities.pairs.Pair;

/**
 * Provides a list of database objects and labels associated with these objects.
 * 
 * @author Elke Achtert
 * @param <O> object type
 */
public class ParsingResult<O extends DatabaseObject> {
  /**
   * The list of database objects and labels associated with these objects.
   */
  private final List<Pair<O, List<String>>> objectAndLabelList;

  /**
   * The object factory for this kind of objects.
   */
  private O prototype;

  /**
   * Provides a list of database objects and labels associated with these
   * objects.
   * 
   * @param objectAndLabelList the list of database objects and labels
   *        associated with these objects
   * @param prototype Object prototype / factory
   */
  public ParsingResult(List<Pair<O, List<String>>> objectAndLabelList, O prototype) {
    this.objectAndLabelList = objectAndLabelList;
    this.prototype = prototype;
  }

  /**
   * Returns the list of database objects and labels associated with these
   * objects.
   * 
   * @return the list of database objects and labels associated with these
   *         objects
   */
  public List<Pair<O, List<String>>> getObjectAndLabelList() {
    return objectAndLabelList;
  }

  /**
   * Returns a string representation of the object.
   * 
   * @return a string representation of the object.
   */
  @Override
  public String toString() {
    return objectAndLabelList.toString();
  }

  /**
   * Provides the number of objects listed in this parsing result.
   * 
   * @return the number of objects listed in this parsing result
   */
  public int size() {
    return this.objectAndLabelList.size();
  }

  /**
   * Get the object factory for this data type.
   * 
   * @return object factory
   */
  public O getObjectFactory() {
    return prototype;
  }
}