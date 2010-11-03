package de.lmu.ifi.dbs.utilities;

public interface MutablePriorityObject<T> extends PriorityObject<T> {

    public void setPriority(double newPriority);
}
