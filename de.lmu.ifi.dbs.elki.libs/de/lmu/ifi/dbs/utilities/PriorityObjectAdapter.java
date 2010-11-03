
package de.lmu.ifi.dbs.utilities;

public abstract class PriorityObjectAdapter<T> implements MutablePriorityObject<T>{
    private final T value;
    private double priority = Double.NaN;

    public PriorityObjectAdapter(T t) {
        this.value = t;
    }

    public PriorityObjectAdapter(T t, double priority) {
        this.value = t;
        this.priority = priority;
    }

    @Override
    public void setPriority(double newPriority) {
        this.priority = newPriority;
    }

    @Override
    public T getValue() {
        return value;
    }

    @Override
    public double getPriority() {
        return this.priority;
    }

}
