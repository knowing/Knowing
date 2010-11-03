package de.lmu.ifi.dbs.utilities;

/**
 * This pair type is used to store the objects together with their priority
 * in one structure.
 */
public class DefaultPriorityObject<T1> implements PriorityObject<T1> {

    private double priority;
    private final T1 obj;

    public DefaultPriorityObject(double priority, T1 t) {
        this.priority = priority;
        this.obj = t;
    }

    @Override
    public double getPriority() {
        return priority;
    }

    @Override
    public T1 getValue() {
        return obj;
    }

    @Override
    public Comparable getKey() {
        return obj.hashCode();
    }

    @Override
    public String toString() {
        return "priority=" + priority + "obj=" + obj;
    }
}
