package de.lmu.ifi.dbs.elki.index.tree;

import java.io.File;

import de.lmu.ifi.dbs.elki.data.DatabaseObject;
import de.lmu.ifi.dbs.elki.index.Index;
import de.lmu.ifi.dbs.elki.logging.Logging;
import de.lmu.ifi.dbs.elki.persistent.LRUCache;
import de.lmu.ifi.dbs.elki.persistent.MemoryPageFile;
import de.lmu.ifi.dbs.elki.persistent.PageFile;
import de.lmu.ifi.dbs.elki.persistent.PersistentPageFile;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.OptionID;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.constraints.GreaterEqualConstraint;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameterization.Parameterization;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.FileParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.IntParameter;
import de.lmu.ifi.dbs.elki.utilities.optionhandling.parameters.LongParameter;

/**
 * Abstract super class for all tree based index classes.
 * 
 * @author Elke Achtert
 * @param <O> the type of DatabaseObject to be stored in the index
 * @param <N> the type of Node used in the index
 * @param <E> the type of Entry used in the index
 */
public abstract class TreeIndex<O extends DatabaseObject, N extends Node<N, E>, E extends Entry> implements Index<O> {
  /**
   * OptionID for {@link #FILE_PARAM}
   */
  public static final OptionID FILE_ID = OptionID.getOrCreateOptionID("treeindex.file", "The name of the file storing the index. " + "If this parameter is not set the index is hold in the main memory.");

  /**
   * Optional parameter that specifies the name of the file storing the index.
   * If this parameter is not set the index is hold in the main memory.
   * <p>
   * Key: {@code -treeindex.file}
   * </p>
   */
  private final FileParameter FILE_PARAM = new FileParameter(FILE_ID, FileParameter.FileType.OUTPUT_FILE, true);

  /**
   * Holds the name of the file storing the index specified by
   * {@link #FILE_PARAM}, null if {@link #FILE_PARAM} is not specified.
   */
  private String fileName = null;

  /**
   * OptionID for {@link #PAGE_SIZE_PARAM}
   */
  public static final OptionID PAGE_SIZE_ID = OptionID.getOrCreateOptionID("treeindex.pagesize", "The size of a page in bytes.");

  /**
   * Parameter to specify the size of a page in bytes, must be an integer
   * greater than 0.
   * <p>
   * Default value: {@code 4000}
   * </p>
   * <p>
   * Key: {@code -treeindex.pagesize}
   * </p>
   */
  private final IntParameter PAGE_SIZE_PARAM = new IntParameter(PAGE_SIZE_ID, new GreaterConstraint(0), 4000);

  /**
   * Holds the value of {@link #PAGE_SIZE_PARAM}.
   */
  protected int pageSize;

  /**
   * OptionID for {@link #CACHE_SIZE_PARAM}
   */
  public static final OptionID CACHE_SIZE_ID = OptionID.getOrCreateOptionID("treeindex.cachesize", "The size of the cache in bytes.");

  /**
   * Parameter to specify the size of the cache in bytes, must be an integer
   * equal to or greater than 0.
   * <p>
   * Default value: {@link Integer#MAX_VALUE}
   * </p>
   * <p>
   * Key: {@code -treeindex.cachesize}
   * </p>
   */
  private final LongParameter CACHE_SIZE_PARAM = new LongParameter(CACHE_SIZE_ID, new GreaterEqualConstraint(0), Integer.MAX_VALUE);

  /**
   * Holds the value of {@link #CACHE_SIZE_PARAM}.
   */
  protected long cacheSize;

  /**
   * The file storing the entries of this index.
   */
  protected PageFile<N> file;

  /**
   * True if this index is already initialized.
   */
  protected boolean initialized = false;

  /**
   * The capacity of a directory node (= 1 + maximum number of entries in a
   * directory node).
   */
  protected int dirCapacity;

  /**
   * The capacity of a leaf node (= 1 + maximum number of entries in a leaf
   * node).
   */
  protected int leafCapacity;

  /**
   * The minimum number of entries in a directory node.
   */
  protected int dirMinimum;

  /**
   * The minimum number of entries in a leaf node.
   */
  protected int leafMinimum;

  /**
   * The entry representing the root node.
   */
  private E rootEntry = createRootEntry();

  /**
   * Constructor, adhering to
   * {@link de.lmu.ifi.dbs.elki.utilities.optionhandling.Parameterizable}
   * 
   * @param config Parameterization
   */
  public TreeIndex(Parameterization config) {
    super();
    config = config.descend(this);

    // file
    if(config.grab(FILE_PARAM)) {
      fileName = FILE_PARAM.getValue().getPath();
    }
    else {
      fileName = null;
    }
    // page size
    if(config.grab(PAGE_SIZE_PARAM)) {
      pageSize = PAGE_SIZE_PARAM.getValue();
    }
    // cache size
    if(config.grab(CACHE_SIZE_PARAM)) {
      cacheSize = CACHE_SIZE_PARAM.getValue();
    }
  }

  /**
   * Get the (STATIC) logger for this class.
   * 
   * @return the static logger
   */
  abstract protected Logging getLogger();

  @Override
  public final long getPhysicalReadAccess() {
    return file.getPhysicalReadAccess();
  }

  @Override
  public final long getPhysicalWriteAccess() {
    return file.getPhysicalWriteAccess();
  }

  @Override
  public final long getLogicalPageAccess() {
    return file.getLogicalPageAccess();
  }

  @Override
  public final void resetPageAccess() {
    file.resetPageAccess();
  }

  @Override
  public final void close() {
    file.close();
  }

  /**
   * Returns the entry representing the root if this index.
   * 
   * @return the entry representing the root if this index
   */
  public final E getRootEntry() {
    return rootEntry;
  }

  /**
   * Returns the node with the specified id.
   * 
   * @param nodeID the page id of the node to be returned
   * @return the node with the specified id
   */
  public N getNode(Integer nodeID) {
    if(nodeID == rootEntry.getEntryID()) {
      return getRoot();
    }
    else {
      return file.readPage(nodeID);
    }
  }

  /**
   * Returns the node that is represented by the specified entry.
   * 
   * @param entry the entry representing the node to be returned
   * @return the node that is represented by the specified entry
   */
  public final N getNode(E entry) {
    return getNode(entry.getEntryID());
  }

  /**
   * Creates a header for this index structure which is an instance of
   * {@link TreeIndexHeader}. Subclasses may need to overwrite this method if
   * they need a more specialized header.
   * 
   * @return a new header for this index structure
   */
  protected TreeIndexHeader createHeader() {
    return new TreeIndexHeader(pageSize, dirCapacity, leafCapacity, dirMinimum, leafMinimum);
  }

  /**
   * Initializes this index from an existing persistent file.
   */
  public void initializeFromFile() {
    if(fileName == null) {
      throw new IllegalArgumentException("Parameter file name is not specified.");
    }

    // init the file
    TreeIndexHeader header = createHeader();
    this.file = new PersistentPageFile<N>(header, cacheSize, new LRUCache<N>(), fileName, getNodeClass());

    this.dirCapacity = header.getDirCapacity();
    this.leafCapacity = header.getLeafCapacity();
    this.dirMinimum = header.getDirMinimum();
    this.leafMinimum = header.getLeafMinimum();

    if(getLogger().isDebugging()) {
      StringBuffer msg = new StringBuffer();
      msg.append(getClass());
      msg.append("\n file = ").append(file.getClass());
      getLogger().debugFine(msg.toString());
    }

    this.initialized = true;
  }

  /**
   * Initializes the index.
   * 
   * @param object an object that will be stored in the index
   */
  protected final void initialize(O object) {
    // determine minimum and maximum entries in a node
    // todo verbose flag as parameter
    // initializeCapacities(object, true);
    initializeCapacities(object);

    // init the file
    if(fileName == null) {
      this.file = new MemoryPageFile<N>(pageSize, cacheSize, new LRUCache<N>());
    }
    else {
      if(new File(fileName).exists()) {
        initializeFromFile();
      }
      else {
        this.file = new PersistentPageFile<N>(createHeader(), cacheSize, new LRUCache<N>(), fileName, getNodeClass());
      }
    }

    // create empty root
    createEmptyRoot(object);

    if(getLogger().isDebugging()) {
      StringBuffer msg = new StringBuffer();
      msg.append(getClass()).append("\n");
      msg.append(" file    = ").append(file.getClass()).append("\n");
      msg.append(" maximum number of dir entries = ").append((dirCapacity - 1)).append("\n");
      msg.append(" minimum number of dir entries = ").append(dirMinimum).append("\n");
      msg.append(" maximum number of leaf entries = ").append((leafCapacity - 1)).append("\n");
      msg.append(" minimum number of leaf entries = ").append(leafMinimum).append("\n");
      msg.append(" root    = ").append(getRoot());
      getLogger().debugFine(msg.toString());
    }

    initialized = true;
  }

  /**
   * Returns the path to the root of this tree.
   * 
   * @return the path to the root of this tree
   */
  protected final TreeIndexPath<E> getRootPath() {
    return new TreeIndexPath<E>(new TreeIndexPathComponent<E>(rootEntry, null));
  }

  /**
   * Reads the root node of this index from the file.
   * 
   * @return the root node of this index
   */
  protected N getRoot() {
    return file.readPage(rootEntry.getEntryID());
  }

  /**
   * Determines the maximum and minimum number of entries in a node.
   * 
   * @param object an object that will be stored in the index
   */
  abstract protected void initializeCapacities(O object);

  /**
   * Creates an empty root node and writes it to file.
   * 
   * @param object an object that will be stored in the index
   */
  abstract protected void createEmptyRoot(O object);

  /**
   * Creates an entry representing the root node.
   * 
   * @return an entry representing the root node
   */
  abstract protected E createRootEntry();

  /**
   * Creates a new leaf node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new leaf node
   */
  abstract protected N createNewLeafNode(int capacity);

  /**
   * Creates a new directory node with the specified capacity.
   * 
   * @param capacity the capacity of the new node
   * @return a new directory node
   */
  abstract protected N createNewDirectoryNode(int capacity);

  /**
   * Performs necessary operations before inserting the specified entry.
   * 
   * @param entry the entry to be inserted
   */
  abstract protected void preInsert(E entry);

  /**
   * Performs necessary operations after deleting the specified object.
   * 
   * @param o the object to be deleted
   */
  abstract protected void postDelete(O o);

  /**
   * Get the node class of this index
   * 
   * @return node class
   */
  abstract protected Class<N> getNodeClass();

  /**
   * @return filename of the underlying persistence layer
   */
  public String getFileName() {
    return fileName;
  }
}