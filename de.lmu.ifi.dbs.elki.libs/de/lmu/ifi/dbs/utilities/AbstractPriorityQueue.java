package de.lmu.ifi.dbs.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Implements a priority queue for objects.
 * 
 * @author Matthias Schubert(creation: 2002-5-11)
 */
public abstract class AbstractPriorityQueue<T, O extends PriorityObject<T>>
        implements Cloneable {

    /** The list of objects. */
    protected O[] queue;
    /**
     * Indicates the sorting order. The queue is sorted in ascending order if
     * asc is true and with descending priority otherwise.
     */
    protected final boolean asc;
    protected int lastIndex;
    protected int maxSize = -1;

    /**
     * Standard constructor of the PriorityQueue class. It creates a priority
     * queue which is sorted with ascending priority.
     */
    public AbstractPriorityQueue() {
        this(true, 100);
    }

    /**
     * Standard constructor of the PriorityQueue class. It creates a priority
     * queue which is sorted with ascending priority.
     */
    public AbstractPriorityQueue(int initialSize) {
        this(true, initialSize);
    }

    /**
     * Creates a PriorityQueue with the given sorting order.
     *
     * @param ascending
     *            If this parameter is set 'true' the sorting order is
     *            ascending, otherwise descending.
     * @since 1.1
     */
    public AbstractPriorityQueue(boolean ascending) {
        this(ascending, 100);
    }

    /**
     * Creates a PriorityQueue with the given sorting order.
     *
     * @param ascending
     *            If this parameter is set 'true' the sorting order is
     *            ascending, otherwise descending.
     * @since 1.1
     */
    public AbstractPriorityQueue(boolean ascending, int initialSize) {
        if (initialSize <= 0) {
            throw new IllegalArgumentException("initial size must be > 0");
        }
        queue = initializeQueue(initialSize);
        lastIndex = 0;
        asc = ascending;
    }

    /**
     * Creates the array needed for this priority queue.
     *
     * @param initialSize
     * @return The future priority queue
     */
    protected abstract O[] initializeQueue(int initialSize);

    /**
     * Generates a priority object with priority <code>priority</code> for the
     * object <code>object</code>.
     *
     * @param priority
     * @param object
     * @return A priority object of the used {@link PriorityObject} type with
     *         priority <code>priority</code> for <code>object</code>
     */
    protected abstract O getPriorityObject(double priority, T object);

    /**
     * Performs a copy of <code>this</code>, using references to the elements in
     * {@link #queue}.
     *
     * @return copy of this queue
     */
    @Override
    public abstract AbstractPriorityQueue<T, O> clone();

    /**
     * Adds an object to the queue at the appropriate position.
     *
     * @param priority
     *            The priority of the object.
     * @param obj
     *            The object that is added.
     */
    public void add(double priority, T obj) {
        add(getPriorityObject(priority, obj));
    }

    public void add(O p) {
        // keep size if maxSize > 0
        if (maxSize > 0 && size() >= maxSize) {
            if (asc && p.getPriority() > firstPriority()) {
                removeFirst();
            } else if (!asc && p.getPriority() < firstPriority()) {
                removeFirst();
            }
        }

        queue[lastIndex] = p;
        lastIndex++;
        ensureCapacity(lastIndex + 1);
        if (asc) {
            sift_up();
        } else {
            sift_up_rev();
        }
    }

    /**
     * Adds an object and ensures that maxSize of the queue is not exceeded.
     * This means, that the element is only added, if its priority is better
     * than the worst one.
     *
     * @param priority
     *            The priority/weight of the object.
     * @param obj
     *            The object that is added.
     * @param maxSize
     *            maximum size of the queue
     * @return If the capacity of the queue was exceeded and an element was
     *         removed, the entry of this element, else <code>null</code>.
     */
    public PriorityObject<T> addIfBetter(double priority, T obj, int maxSize) {
        if (size() >= maxSize) {
            if ((asc && priority > firstPriority())
                    || (!asc && priority < firstPriority())) {
                PriorityObject<T> o = getFirstEntry();
                removeFirst();
                add(priority, obj);
                return o;
            }
            return null;
        } else {
            add(priority, obj);
            return null;
        }
    }

    /**
     * copied from ArrayList to automatically increase the array size
     */
    protected void ensureCapacity(int minCapacity) {
        int oldCapacity = queue.length;
        if (minCapacity > oldCapacity) {
            int newCapacity = (oldCapacity * 3) / 2 + 1;
            if (newCapacity < minCapacity) {
                newCapacity = minCapacity;
            }
            // minCapacity is usually close to size, so this is a win:
            queue = Arrays.copyOf(queue, newCapacity);
        }
    }

    protected double val(int x) {
        return queue[x].getPriority();
    }

    /**
     * Lowers a key from the top
     */
    protected void sift_up() {
        int akt = lastIndex;
        int comp = akt / 2;
        O x = queue[akt - 1];
        while (comp > 0 && val(comp - 1) > x.getPriority()) {
            queue[akt - 1] = queue[comp - 1];
            akt = comp;
            comp = akt / 2;
        }
        queue[akt - 1] = x;
    }

    /**
     * Lowers a key from the top
     */
    protected void sift_down() {
        int akt = 1;
        int comp = 2 * akt;
        O x = queue[akt - 1];
        if (comp < lastIndex && (val(comp) < val(comp - 1))) {
            comp++;
        }
        while (comp <= lastIndex && val(comp - 1) < x.getPriority()) {
            queue[akt - 1] = queue[comp - 1];
            akt = comp;
            comp = 2 * akt;
            if (comp < lastIndex && (val(comp) < val(comp - 1))) {
                comp++;
            }
        }
        queue[akt - 1] = x;
    }

    /**
     * Lowers a key from the top
     */
    protected void sift_up_rev() {
        int akt = lastIndex;
        int comp = akt / 2;
        O x = queue[akt - 1];
        while (comp > 0 && val(comp - 1) < x.getPriority()) {
            queue[akt - 1] = queue[comp - 1];
            akt = comp;
            comp = akt / 2;
        }
        queue[akt - 1] = x;
    }

    /**
     * Lowers a key from the top
     */
    protected void sift_down_rev() {
        int akt = 1;
        int comp = 2 * akt;
        O x = queue[akt - 1];
        if ((comp < lastIndex) && (val(comp) > val(comp - 1))) {
            comp++;
        }
        while (comp <= lastIndex && val(comp - 1) > x.getPriority()) {
            queue[akt - 1] = queue[comp - 1];
            akt = comp;
            comp = 2 * akt;
            if ((comp < lastIndex) && (val(comp) > val(comp - 1))) {
                comp++;
            }
        }
        queue[akt - 1] = x;
    }

    /**
     * Returns the size of the priority queue.
     *
     * @return the number of elements in the priority queue.
     */
    public int size() {
        return lastIndex;
    }

    /**
     * Returns the priority of the first object in the queue.
     */
    public double firstPriority() {
        return queue[0].getPriority();
    }

    public boolean isEmpty() {
        return lastIndex == 0;
    }

    /**
     * Indicates if the sorting order is ascending or descending.
     *
     * @return 'true' if the sorting order for the priority queue is ascending,
     *         'false if the sorting order is descending.
     */
    public final boolean isAscending() {
        return asc;
    }

    public O getFirstEntry() {
        return queue[0];
    }

    /**
     * returns the first value of the queue. DOES NOT remove the value
     * @return
     */
    public T getFirst() {
        return queue[0].getValue();
    }

    /**
     * returns AND removes the first value of the queue.
     * @return
     */
    public O removeFirstEntry() {
        O p = getFirstEntry();
        removeFirst();
        return p;
    }

    /**
     * Removes the first object from the queue and returns that object to the
     * caller.
     *
     * @return the first object in the queue.
     */
    public T removeFirst() {
        T obj = queue[0].getValue();
        lastIndex--;
        queue[0] = queue[lastIndex];
        if (asc) {
            sift_down();
        } else {
            sift_down_rev();
        }
        return obj;
    }

    /**
     * Return sorted list of all objects
     *
     * @return A list of the objects in this queue, sorted in ascending order if
     *         {@link #isAscending()} is <code>true</code>, else sorted in
     *         descending order
     */
    public List<T> asList() {
        AbstractPriorityQueue<T, O> pq = clone();
        List<T> list = new ArrayList<T>(size());
        while (!pq.isEmpty()) {
            list.add(pq.removeFirst());
        }
        return list;
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }
}
