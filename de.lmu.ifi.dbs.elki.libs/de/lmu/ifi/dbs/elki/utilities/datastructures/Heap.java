package de.lmu.ifi.dbs.elki.utilities.datastructures;

import java.io.Serializable;
import java.util.AbstractQueue;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Basic in-memory heap structure. Closely related to a {@link java.util.PriorityQueue},
 * but here we can override methods to obtain e.g. a {@link TopBoundedHeap}
 * 
 * @author Erich Schubert
 * 
 * @param <E> Element type. Should be {@link java.lang.Comparable} or a
 *        {@link java.util.Comparator} needs to be given.
 */
public class Heap<E> extends AbstractQueue<E> implements Serializable {
  /**
   * Serial version
   */
  private static final long serialVersionUID = 1L;

  /**
   * Heap storage
   * 
   * Note: keep private; all write access should be done through
   * {@link #putInQueue} for subclasses to track!
   */
  private Object[] queue;

  /**
   * Current number of objects
   */
  protected int size = 0;

  /**
   * The comparator or {@code null}
   */
  private final Comparator<? super E> comparator;

  /**
   * (Structural) modification counter. Used to invalidate iterators.
   */
  public transient int modCount = 0;

  /**
   * Default initial capacity
   */
  private static final int DEFAULT_INITIAL_CAPACITY = 11;

  /**
   * Default constructor: default capacity, natural ordering.
   */
  public Heap() {
    this(DEFAULT_INITIAL_CAPACITY, null);
  }

  /**
   * Constructor with initial capacity, natural ordering.
   * 
   * @param size initial size
   */
  public Heap(int size) {
    this(size, null);
  }

  /**
   * Constructor with {@link Comparator}.
   * 
   * @param comparator Comparator
   */
  public Heap(Comparator<? super E> comparator) {
    this(DEFAULT_INITIAL_CAPACITY, comparator);
  }

  /**
   * Constructor with initial capacity and {@link Comparator}.
   * 
   * @param size initial capacity
   * @param comparator Comparator
   */
  public Heap(int size, Comparator<? super E> comparator) {
    super();
    this.size = 0;
    this.queue = new Object[size];
    this.comparator = comparator;
  }

  @Override
  public synchronized boolean offer(E e) {
    // resize when needed
    considerResize(size + 1);
    final int parent = parent(size);
    // append element
    modCount++;
    putInQueue(size, e);
    this.size = size + 1;
    heapifyUp(parent);
    // We have changed - return true according to {@link Collection#put}
    return true;
  }

  @Override
  public synchronized E peek() {
    if(size == 0) {
      return null;
    }
    return castQueueElement(0);
  }

  @Override
  public E poll() {
    return removeAt(0);
  }

  /**
   * Remove the element at the given position.
   * 
   * @param pos Element position.
   */
  protected synchronized E removeAt(int pos) {
    if(pos < 0 || pos >= size) {
      return null;
    }
    modCount++;
    E ret = castQueueElement(0);
    // remove!
    putInQueue(pos, queue[size - 1]);
    size = size - 1;
    // avoid dangling references!
    putInQueue(size, null);
    heapifyDown(pos);
    return ret;
  }

  /**
   * Compute parent index in heap array.
   * 
   * @param pos Element index
   * @return Parent index
   */
  private int parent(int pos) {
    return (pos - 1) / 2;
  }

  /**
   * Compute left child index in heap array.
   * 
   * @param pos Element index
   * @return left child index
   */
  private int leftChild(int pos) {
    return 2 * pos + 1;
  }

  /**
   * Compute right child index in heap array.
   * 
   * @param pos Element index
   * @return right child index
   */
  private int rightChild(int pos) {
    return 2 * pos + 2;
  }

  /**
   * Execute a "Heapify Upwards" aka "SiftUp". Used in insertions.
   * 
   * @param pos insertion position
   */
  protected void heapifyUp(int pos) {
    if(pos < 0 || pos >= size) {
      return;
    }
    // precondition: both child trees are already sorted.
    final int parent = parent(pos);
    final int lchild = leftChild(pos);
    final int rchild = rightChild(pos);

    int min = pos;
    if(lchild < size) {
      if(compare(min, lchild) > 0) {
        min = lchild;
      }
    }
    if(rchild < size) {
      if(compare(min, rchild) > 0) {
        min = rchild;
      }
    }
    if(min != pos) {
      swap(pos, min);
      heapifyUp(parent);
    }
  }
  
  /**
   * Start a heapify up at the parent of this node, since we've changed a child
   * 
   * @param pos Position to start the modification.
   */
  protected void heapifyUpParent(int pos) {
    heapifyUp(parent(pos));
  }

  /**
   * Execute a "Heapify Downwards" aka "SiftDown". Used in deletions.
   * 
   * @param pos re-insertion position
   */
  protected void heapifyDown(int pos) {
    if(pos < 0 || pos >= size) {
      return;
    }
    final int lchild = leftChild(pos);
    final int rchild = rightChild(pos);

    int min = pos;
    if(lchild < size) {
      if(compare(min, lchild) > 0) {
        min = lchild;
      }
    }
    if(rchild < size) {
      if(compare(min, rchild) > 0) {
        min = rchild;
      }
    }
    if(min != pos) {
      // swap with minimal element
      swap(pos, min);
      // recruse down
      heapifyDown(min);
    }
  }

  /**
   * Put an element into the queue at a given position. This allows subclasses
   * to index the queue.
   * 
   * @param index Index
   * @param e Element
   */
  protected void putInQueue(int index, Object e) {
    queue[index] = e;
  }

  /**
   * Swap two elements in the heap.
   * 
   * @param a Element
   * @param b Element
   */
  protected void swap(int a, int b) {
    Object oa = queue[a];
    Object ob = queue[b];
    putInQueue(a, ob);
    putInQueue(b, oa);
    modCount++;
  }

  @SuppressWarnings("unchecked")
  protected int compare(int pos1, int pos2) {
    if(comparator != null) {
      return comparator.compare(castQueueElement(pos1), castQueueElement(pos2));
    }
    try {
      Comparable<E> c = (Comparable<E>) castQueueElement(pos1);
      return c.compareTo(castQueueElement(pos2));
    }
    catch(ClassCastException e) {
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  protected int compareExternal(E o1, int pos2) {
    if(comparator != null) {
      return comparator.compare(o1, castQueueElement(pos2));
    }
    try {
      Comparable<E> c = (Comparable<E>) o1;
      return c.compareTo(castQueueElement(pos2));
    }
    catch(ClassCastException e) {
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  protected int compareExternalExternal(E o1, E o2) {
    if(comparator != null) {
      return comparator.compare(o1, o2);
    }
    try {
      Comparable<E> c = (Comparable<E>) o1;
      return c.compareTo(o2);
    }
    catch(ClassCastException e) {
      throw e;
    }
  }

  @SuppressWarnings("unchecked")
  protected E castQueueElement(int n) {
    return (E) queue[n];
  }

  @Override
  public int size() {
    return this.size;
  }

  /**
   * Test whether we need to resize to have the requested capacity.
   * 
   * @param requiredSize required capacity
   */
  private void considerResize(int requiredSize) {
    if(requiredSize > queue.length) {
      // Double until 64, then increase by 50% each time.
      int newCapacity = ((queue.length < 64) ? ((queue.length + 1) * 2) : ((queue.length / 2) * 3));
      // overflow?
      if(newCapacity < 0) {
        newCapacity = Integer.MAX_VALUE;
      }
      if(requiredSize > newCapacity) {
        newCapacity = requiredSize;
      }
      grow(newCapacity);
    }
  }

  /**
   * Execute the actual resize operation.
   * 
   * @param newsize New size
   */
  private void grow(int newsize) {
    // check for overflows
    if(newsize < 0) {
      throw new OutOfMemoryError();
    }
    if(newsize == queue.length) {
      return;
    }
    modCount++;
    queue = Arrays.copyOf(queue, newsize);
  }

  @Override
  public void clear() {
    modCount++;
    // clean up references in the array for memory management
    for(int i = 0; i < size; i++) {
      queue[i] = null;
    }
    this.size = 0;
  }

  @Override
  public boolean contains(Object o) {
    if(o != null) {
      for(int i = 0; i < size; i++) {
        if(o.equals(queue[i])) {
          return true;
        }
      }
    }
    return false;
  }

  // TODO: bulk add implementation of addAll?

  @Override
  public Iterator<E> iterator() {
    return new Itr();
  }

  /**
   * Iterator over queue elements. No particular order (i.e. heap order!)
   * 
   * @author Erich Schubert
   * 
   */
  protected final class Itr implements Iterator<E> {
    /**
     * Cursor position
     */
    private int cursor = 0;

    /**
     * Modification counter this iterator is valid for.
     */
    private int expectedModCount = modCount;

    @Override
    public boolean hasNext() {
      return cursor < size;
    }

    @Override
    public E next() {
      if(expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      if(cursor < size) {
        return castQueueElement(cursor++);
      }
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      if(expectedModCount != modCount) {
        throw new ConcurrentModificationException();
      }
      if(cursor > 0) {
        cursor--;
      }
      else {
        throw new IllegalStateException();
      }
      expectedModCount = modCount;
    }
  }
  
  /**
   * Return the heap as a sorted array list, by repeated polling.
   * This will empty the heap!
   * 
   * @return new array list
   */
  public ArrayList<E> toSortedArrayList() {
    ArrayList<E> ret = new ArrayList<E>(size());
    while(!isEmpty()) {
      ret.add(poll());
    }
    return ret;
  }
}