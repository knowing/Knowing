package de.lmu.ifi.dbs.utilities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Collections2 {

    /**
     * returns a random sample from the input list
     * @param <T>
     * @param in source list
     * @param size size of sample
     * @return UNSORTED random sample from the list
     * @throws NullPointerException if in = null
     * @throws IllegalArgumentException if size > in.size()
     */
    public static <T> List<T> randomSample(final Collection<T> in, int size) {
        if (in == null) {
            throw new NullPointerException("in must not be null");
        }
        return randomSample(in, size, new Random());
    }

    public static <T> List<T> randomSample(final Collection<T> in, double ratio) {
        if (in == null) {
            throw new NullPointerException("in must not be null");
        }
        return randomSample(in, ratio, new Random());
    }

    /**
     * returns a random sample from the input list
     * @param <T>
     * @param in source list
     * @param ratio percentage of returned elements = Math.round(in.size() * ratio
     * @param rnd random seed
     * @return UNSORTED random sample from the list
     * @throws NullPointerException if in = null
     * @throws IllegalArgumentException if ratio <= 0 || ratio > 1
     */
    public static <T> List<T> randomSample(final Collection<T> in, double ratio, Random rnd) {
        if (in == null) {
            throw new NullPointerException("in must not be null");
        }
        if (ratio <= 0 || ratio > 1) {
            throw new IllegalArgumentException("ratio must be in ]0,1] but was " + ratio);
        }
        return randomSample(in, (int) Math.round(in.size() * ratio), rnd);
    }

    /**
     * returns a random sample from the input list
     * @param <T>
     * @param in source list
     * @param size size of sample
     * @return rnd Random seed
     * @return UNSORTED random sample from the list
     * @throws NullPointerException if in = null
     * @throws IllegalArgumentException if size > in.size()
     */
    public static <T> List<T> randomSample(final Collection<T> in, int size, Random rnd) {
        if (in == null) {
            throw new NullPointerException("in must not be null");
        }
        if (!Math2.isIn(0, size, in.size())) {
            throw new IllegalArgumentException("size must be in [0, in.size()]");
        }
        // sampling = 0, nothing
        if (size == 0) {
            return new ArrayList<T>(0);
        }
        // sampling = 1 -> all
        ArrayList<T> out = new ArrayList<T>(in);
        if (size == in.size()) {
            return out;
        }

        Collections.shuffle(out, rnd);
        return out.subList(0, size);
    }

    public static void trimToSize(Collection in, int size) {
        while (in.size() > size) {
            in.remove(in.size() - 1);
        }
    }

    /**
     * Returns a new List with unique elements from the source list.
     * @param src
     * @return list with unique elements from src
     */
    public static <T> List<T> unique(Collection<T> src) {
        List<T> dst = new ArrayList<T>(src.size());
        for (T elem : src) {
            if (!dst.contains(elem)) {
                dst.add(elem);
            }
        }
        return dst;
    }

    public static int indexOf(List list, Object o) {
        for (int i = 0; i < list.size(); i++) {
            Object lo = list.get(i);
            if (o == null && lo == null) {
                return i;
            } else if (o != null && o.equals(lo)) {
                return i;
            }
        }
        return -1;
    }

    public static String joinToString(List in, String glue) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.size(); i++) {
            sb.append(in.get(i));
            if (i < in.size() - 1) {
                sb.append(glue);
            }
        }
        return sb.toString();
    }

    /**
     * Find and return the index with the greatest value. If there are >1 maxima
     * with the same value, only the first index will be returned. 
     *
     * @param in the collection of objects
     * @return max array index of max value 
     */
    public static int max(Collection<? extends Comparable> in) {
        int pos = -1;
        Comparable o = null;
        int i = 0;
        for (Comparable cand : in) {
            if (o == null || o.compareTo(cand) < 0) {
                pos = i;
                o = cand;
            }
            i++;
        }
        return pos;
    }

    /**
     * Find and return the index with the greatest value. If there are >1 maxima
     * with the same value, only the first index will be returned.
     *
     * @param in the collection of objects
     * @param comparator which does the comparisons
     * @return max array index of max value
     */
    public static <T> int max(Collection<T> in, Comparator<T> comparator) {
        int pos = -1;
        T o = null;
        int i = 0;
        for (T cand : in) {
            if (o == null || comparator.compare(o, cand) < 0) {
                pos = i;
                o = cand;
            }
            i++;
        }
        return pos;
    }

    /**
     * Find and return the index with the smallest value. If there are >1 maxima
     * with the same value, only the first index will be returned.
     *
     * @param a
     * @return max array index of max value
     */
    public static int min(Collection<? extends Comparable> in) {
        int pos = -1;
        Comparable o = null;
        int i = 0;
        for (Comparable cand : in) {
            if (o == null || o.compareTo(cand) > 0) {
                pos = i;
                o = cand;
            }
            i++;
        }
        return pos;
    }

    /**
     * Find and return the index with the greatest value. If there are >1 maxima
     * with the same value, only the first index will be returned.
     *
     * @param in the collection of objects
     * @param comparator which does the comparisons
     * @return max array index of max value
     */
    public static <T> int min(Collection<T> in, Comparator<T> comparator) {
        int pos = -1;
        T o = null;
        int i = 0;
        for (T cand : in) {
            if (o == null || comparator.compare(o, cand) > 0) {
                pos = i;
                o = cand;
            }
            i++;
        }
        return pos;
    }

    public static double sum(Collection<? extends Number> list) {
        double sum = 0;
        for (Number d : list) {
            sum += d.doubleValue();
        }
        return sum;
    }

    public static double mean(Collection<? extends Number> list) {
        return sum(list) / list.size();
    }

    public static double variance(Collection<? extends Number> list) {
        double mean = mean(list);
        double sum = 0;
        double t;
        for (Number d : list) {
            t = d.doubleValue() - mean;
            sum += t * t;
        }
        return sum;
    }
}
