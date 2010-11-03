package de.lmu.ifi.dbs.elki.persistent;

import java.util.Stack;
import java.util.logging.Logger;

import de.lmu.ifi.dbs.elki.logging.LoggingConfiguration;

/**
 * Abstract class implementing general methods of a PageFile. A PageFile stores
 * objects that implement the <code>Page</code> interface.
 * 
 * @author Elke Achtert
 * @param <P> Page type
 */
public abstract class PageFile<P extends Page<P>> implements CachedFile<P> {

  /**
   * The cache of this file.
   */
  protected Cache<P> cache;

  /**
   * A stack holding the empty page ids.
   */
  protected Stack<Integer> emptyPages;

  /**
   * The last page ID.
   */
  protected int nextPageID;

  /**
   * The read I/O-Access of this file.
   */
  protected long readAccess;

  /**
   * The write I/O-Access of this file.
   */
  protected long writeAccess;

  /**
   * The size of a page in Bytes.
   */
  protected int pageSize;

  /**
   * Creates a new PageFile.
   */
  protected PageFile() {
    this.emptyPages = new Stack<Integer>();
    this.nextPageID = 0;
    this.readAccess = 0;
    this.writeAccess = 0;
  }

  /**
   * Returns the physical read I/O-Accesses of this file.
   * @return Number of physical read I/O accesses
   */
  public final long getPhysicalReadAccess() {
    return readAccess;
  }

  /**
   * Returns the physical write I/O-Accesses of this file.
   * @return Number of physical write I/O accesses
   */
  public final long getPhysicalWriteAccess() {
    return writeAccess;
  }

  /**
   * Returns the logical read I/O-Accesses of this file.
   * @return Number of logical I/O accesses
   */
  public final long getLogicalPageAccess() {
    return cache.getPageAccess();
  }

  /**
   * Resets the counters for page accesses of this file and flushes the cache.
   */
  public final void resetPageAccess() {
    cache.flush();
    this.readAccess = 0;
    this.writeAccess = 0;
    cache.resetPageAccess();
  }

  /**
   * Sets the id of the given page.
   * 
   * @param page the page to set the id
   */
  public void setPageID(P page) {
    if(page.getPageID() == null) {
      Integer pageID = getNextEmptyPageID();

      if(pageID == null) {
        page.setPageID(nextPageID++);
      }
      else {
        page.setPageID(pageID);
      }
    }
  }

  /**
   * Writes a page into this file. The method tests if the page has already an
   * id, otherwise a new id is assigned and returned.
   * 
   * @param page the page to be written
   * @return the id of the page
   */
  public synchronized final int writePage(P page) {
    // set page ID
    setPageID(page);
    // mark page as dirty
    page.setDirty(true);
    // put node into cache
    cache.put(page);
    return page.getPageID();
  }

  /**
   * Reads the page with the given id from this file.
   * 
   * @param pageID the id of the page to be returned
   * @return the page with the given pageId
   */
  public P readPage(int pageID) {
    // try to get from cache
    return cache.get(pageID);
  }

  /**
   * Deletes the node with the specified id from this file.
   * 
   * @param pageID the id of the node to be deleted
   */
  public void deletePage(int pageID) {
    // put id to empty nodes
    emptyPages.push(pageID);

    // delete from cache
    cache.remove(pageID);
  }

  /**
   * Closes this file.
   */
  public void close() {
    cache.flush();
  }

  /**
   * Clears this PageFile.
   */
  public void clear() {
    cache.clear();
  }

  /**
   * Sets the maximum size of the cache of this file.
   * 
   * @param cacheSize cache size
   */
  public void setCacheSize(int cacheSize) {
    cache.setCacheSize(cacheSize / pageSize);
  }

  /**
   * Initializes the cache.
   * 
   * @param pageSize the size of a page in Bytes
   * @param cacheSize the size of the cache in Byte
   * @param cache the class of the cache to be used
   */
  protected void initCache(int pageSize, long cacheSize, Cache<P> cache) {
    if(pageSize <= 0) {
      throw new IllegalStateException("pagesize <= 0!");
    }

    long pagesInCache = cacheSize / pageSize;
    if(LoggingConfiguration.DEBUG) {
      Logger.getLogger(this.getClass().getName()).fine("Number of pages in cache " + pagesInCache);
    }

    // if (pagesInCache <= 0)
    // throw new IllegalArgumentException("Cache size of " + cacheSize +
    // " Bytes is chosen too small: " +
    // cacheSize + "/" + pageSize + " = " + pagesInCache);

    this.pageSize = pageSize;
    this.cache = cache;
    this.cache.initialize(pagesInCache, this);
  }

  /**
   * Returns the next empty page id.
   * 
   * @return the next empty page id
   */
  private Integer getNextEmptyPageID() {
    if(!emptyPages.empty()) {
      return emptyPages.pop();
    }
    else {
      return null;
    }
  }

  /**
   * Returns the next page id.
   * 
   * @return the next page id
   */
  public int getNextPageID() {
    return nextPageID;
  }

  /**
   * Sets the next page id.
   * 
   * @param nextPageID the next page id to be set
   */
  public void setNextPageID(int nextPageID) {
    this.nextPageID = nextPageID;
  }

}
