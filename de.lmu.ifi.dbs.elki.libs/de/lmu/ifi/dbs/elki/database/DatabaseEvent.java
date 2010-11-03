package de.lmu.ifi.dbs.elki.database;

import java.util.EventObject;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.database.ids.DBIDs;

/**
 * Encapsulates information describing changes, i.e. updates, insertions, or
 * deletions to a database, and used to notify database listeners of the change.
 * 
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject as element of the database
 */
public class DatabaseEvent<O extends DatabaseObject> extends EventObject {
  /**
   * Serialization ID since Java EventObjects are expected to be serializable.
   */
  private static final long serialVersionUID = 1L;

  /**
   * The ids of the database object that have been changed, i.e. updated,
   * inserted or deleted.
   */
  private DBIDs objectIDs;

  /**
   * Used to create an event when database objects have been updated, inserted,
   * or removed.
   * 
   * @param source the database responsible for generating the event
   * @param objectIDs the ids of the database objects that have been changed
   */
  public DatabaseEvent(Database<O> source, DBIDs objectIDs) {
    super(source);
    this.objectIDs = objectIDs;
  }

  /**
   * Returns the ids of the database object that have been changed.
   * 
   * @return the ids of the database object that have been changed
   */
  public DBIDs getObjectIDs() {
    return objectIDs;
  }

  /**
   * Returns the database on which the changes have been occurred.
   * 
   * @return the database
   * @see #getSource()
   */
  @SuppressWarnings("unchecked")
  public Database<O> getDatabase() {
    return (Database<O>) super.getSource();
  }
}
