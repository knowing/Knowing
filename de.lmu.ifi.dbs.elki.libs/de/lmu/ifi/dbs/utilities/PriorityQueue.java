package de.lmu.ifi.dbs.utilities;

/**
 * Implements a priority queue for objects.
 * 
 * @author Matthias Schubert(creation: 2002-5-11)
 */
public class PriorityQueue<T> extends AbstractPriorityQueue<T, DefaultPriorityObject<T>> {

    /** Symbolic constant indicating ascending sorting order. */
    public static final boolean ASCENDING = true;
    /** Symbolic constant indicating descending sorting order. */
    public static final boolean DESCENDING = false;

    /**
     * Standard constructor of the PriorityQueue class. It creates a priority
     * queue which is sorted with ascending priority.
     */
    public PriorityQueue() {
        super(true, 100);
    }

    /**
     * Standard constructor of the PriorityQueue class. It creates a priority
     * queue which is sorted with ascending priority.
     */
    public PriorityQueue(int initialSize) {
        super(true, initialSize);
    }

    /**
     * Creates a PriorityQueue with the given sorting order.
     *
     * @param ascending
     *            If this parameter is set 'true' the sorting order is
     *            ascending, otherwise descending.
     * @since 1.1
     */
    public PriorityQueue(boolean ascending) {
        super(ascending, 100);
    }

    /**
     * Creates a PriorityQueue with the given sorting order.
     *
     * @param ascending
     *            If this parameter is set 'true' the sorting order is
     *            ascending, otherwise descending.
     * @since 1.1
     */
    public PriorityQueue(boolean ascending, int initialSize) {
        super(ascending, initialSize);
    }

    @Override
    protected DefaultPriorityObject<T> getPriorityObject(double priority,
            T object) {
        return new DefaultPriorityObject<T>(priority, object);
    }

    @Override
    protected DefaultPriorityObject<T>[] initializeQueue(int initialSize) {
        return new DefaultPriorityObject[initialSize];
    }

    @Override
    public PriorityQueue<T> clone() {
        PriorityQueue<T> pq = new PriorityQueue<T>(this.asc, queue.length);
        System.arraycopy(queue, 0, pq.queue, 0, queue.length);
        pq.lastIndex = this.lastIndex;
        return pq;
    }

    /** @return The capacity of this queue */
    public int getCapacity() {
        return queue.length;
    }

    @Override
    public void ensureCapacity(int minCapacity) {
        super.ensureCapacity(minCapacity);
    }
}
