package de.lmu.ifi.dbs.utilities;

public interface PriorityObject<T> {

    public Comparable getKey();

    public T getValue();

    public double getPriority();
}
