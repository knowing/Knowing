package de.lmu.ifi.dbs.elki.persistent;

import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

/**
 * Abstract superclass for pages.
 * 
 * @author Elke Achtert
 * @param <P> Self reference
 */
// todo elke revise comments
public abstract class AbstractPage<P extends AbstractPage<P>> implements Page<P> {
  private static final long serialVersionUID = 1;

  /**
   * The unique id if this page.
   */
  private Integer id;

  /**
   * The dirty flag of this page.
   */
  private boolean dirty;

  /**
   * The file that stores the pages.
   */
  private PageFile<P> file;

  /**
   * Empty constructor for Externalizable interface.
   */
  public AbstractPage() {
    this(null);
  }

  /**
   * Provides a new page object.
   * 
   * @param file the page file that stores the pages.
   */
  public AbstractPage(PageFile<P> file) {
    this.file = file;
  }

  /**
   * Returns the unique id of this Page.
   * 
   * @return the unique id of this Page
   */
  @Override
  public final Integer getPageID() {
    return id;
  }

  /**
   * Sets the unique id of this Page.
   * 
   * @param id the id to be set
   */
  @Override
  public final void setPageID(int id) {
    this.id = id;
  }

  /**
   * Sets the page file of this page.
   * 
   * @param file the page file to be set
   */
  @Override
  public final void setFile(PageFile<P> file) {
    this.file = file;
  }

  /**
   * Returns true if this page is dirty, false otherwise.
   * 
   * @return true if this page is dirty, false otherwise
   */
  @Override
  public final boolean isDirty() {
    return dirty;
  }

  /**
   * Sets the dirty flag of this page.
   * 
   * @param dirty the dirty flag to be set
   */
  @Override
  public final void setDirty(boolean dirty) {
    this.dirty = dirty;
  }

  /**
   * The object implements the writeExternal method to save its contents by
   * calling the methods of DataOutput for its primitive values or calling the
   * writeObject method of ObjectOutput for objects, strings, and arrays.
   * 
   * @param out the stream to write the object to
   * @throws java.io.IOException Includes any I/O exceptions that may occur
   * @serialData Overriding methods should use this tag to describe the data
   *             layout of this Externalizable object. List the sequence of
   *             element types and, if possible, relate the element to a
   *             public/protected field and/or method of this Externalizable
   *             class.
   */
  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    out.writeInt(id);
  }

  /**
   * The object implements the readExternal method to restore its contents by
   * calling the methods of DataInput for primitive types and readObject for
   * objects, strings and arrays. The readExternal method must read the values
   * in the same sequence and with the same types as were written by
   * writeExternal.
   * 
   * @param in the stream to read data from in order to restore the object
   * @throws java.io.IOException if I/O errors occur
   * @throws ClassNotFoundException If the class for an object being restored
   *         cannot be found.
   */
  @Override
  public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
    id = in.readInt();
  }

  /**
   * Returns a string representation of the object.
   * 
   * @return a string representation of the object
   */
  @Override
  public String toString() {
    if(id != null) {
      return Integer.toString(id);
    }
    else {
      return "null";
    }
  }

  /**
   * Returns the file that stores the pages.
   * 
   * @return the file that stores the pages
   */
  public final PageFile<P> getFile() {
    return file;
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   * 
   * @param o the object to be tested
   * @return true, if o is an AbstractNode and has the same id and the same
   *         entries as this node.
   */
  @Override
  @SuppressWarnings("unchecked")
  public boolean equals(Object o) {
    if(this == o) {
      return true;
    }
    if(o == null || getClass() != o.getClass()) {
      return false;
    }

    final P that = (P) o;

    return id.equals(that.getPageID());
  }

  /**
   * Returns as hash code value for this node the id of this node.
   * 
   * @return the id of this node
   */
  @Override
  public int hashCode() {
    return id.hashCode();
  }
}
