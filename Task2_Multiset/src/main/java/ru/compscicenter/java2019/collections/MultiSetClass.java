package ru.compscicenter.java2019.collections;

import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Arrays;
import java.util.AbstractCollection;

public class MultiSetClass<E> extends AbstractCollection<E> implements MultiSet<E> {

    private int multiSetSize;
    private HashMap<E, Integer> multiSet;

    public MultiSetClass() {
        multiSet = new HashMap<>();
        multiSetSize = 0;
    }

    public MultiSetClass(final Collection<? extends E> collection) {
        this();
        this.addAll(collection);
        multiSetSize = collection.size();
    }

    @Override
    public int size() {
        return multiSetSize;
    }

    @Override
    public Iterator<E> iterator() {
        return new MultiSetIterator();
    }

    @Override
    public boolean add(final Object o) {
        this.add(o, 1);
        return true;
    }

    @Override
    public int add(final Object o, final int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException();
        }
        int amountWas = multiSet.getOrDefault(o, 0);
        multiSet.put((E) o, multiSet.getOrDefault(o, 0) + occurrences);
        multiSetSize += occurrences;
        return amountWas;
    }

    @Override
    public boolean remove(final Object e) {
        return this.remove(e, 1) != 0;
    }

    @Override
    public int remove(final Object e, final int occurrences) {
        if (occurrences < 0) {
            throw new IllegalArgumentException();
        }
        int amountWas = multiSet.getOrDefault(e, 0);
        multiSetSize -= Math.min(amountWas, occurrences);
        if (occurrences >= amountWas) {
            multiSet.remove(e);
            return amountWas;
        }
        multiSet.put((E) e, Math.max(multiSet.getOrDefault(e, 0) - occurrences, 0));
        return amountWas;
    }

    @Override
    public boolean addAll(final Collection c) {
        for (Object value : c) {
            this.add(value);
        }
        return true;
    }

    @Override
    public int count(final Object e) {
        return multiSet.getOrDefault(e, 0);
    }

    @Override
    public void clear() {
        multiSet = new HashMap<>();
        multiSetSize = 0;
    }

    @Override
    public boolean retainAll(final Collection c) {
        HashMap<E, Integer> newHashSet = new HashMap<>();
        boolean wasRemoved = false;
        multiSetSize = 0;
        for (Object value : c) {
            if (multiSet.getOrDefault(value, 0) != 0) {
                newHashSet.put((E) value, multiSet.get(value));
                multiSetSize += multiSet.get(value);
            } else {
                wasRemoved = true;
            }
        }
        multiSet = newHashSet;
        return wasRemoved;
    }

    @Override
    public boolean removeAll(final Collection c) {
        boolean wasRemoved = false;
        for (Object k : c) {
            if (multiSet.containsKey(k)) {
                multiSetSize -= multiSet.get(k);
                multiSet.remove(k);
                wasRemoved = true;
            }
        }
        return wasRemoved;
    }

    @Override
    public Object[] toArray() {
        Object[] multisetArray = new Object[multiSetSize];
        int i = 0;
        for (E key : multiSet.keySet()) {
            for (int j = 0; j < multiSet.getOrDefault(key, 0); ++j) {
                multisetArray[i] = key;
                ++i;
            }
        }
        return multisetArray;
    }

    @Override
    public E[] toArray(final Object[] a) {
        if (a.length < multiSetSize) {
            Object[] copyArray = new Object[multiSetSize];
            copyArray = Arrays.copyOf(copyArray, multiSetSize, a.getClass());
            int i = 0;
            for (E key : multiSet.keySet()) {
                for (int j = 0; j < multiSet.getOrDefault(key, 0); ++j) {
                    copyArray[i] = key;
                    ++i;
                }
            }
            return (E[]) copyArray;
        }
        int i = 0;
        for (Object key : multiSet.keySet()) {
            for (int j = 0; j < multiSet.getOrDefault(key, 0); ++j) {
                a[i] = key;
                ++i;
            }
        }
        if (a.length > multiSetSize) {
            a[multiSetSize] = null;
        }
        return (E[]) a;
    }

    @Override
    public int hashCode() {
        return multiSet.hashCode();
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof MultiSetClass)) {
            return false;
        }
        return this.multiSet.equals(((MultiSetClass) o).multiSet);
    }

    private class MultiSetIterator implements Iterator {

        private Set<E> keys = new HashSet<>();
        private Iterator it;
        private E keyNow = null;
        private int amountNow = 1;

        MultiSetIterator() {
            keys.addAll(multiSet.keySet());
            it = keys.iterator();
        }

        @Override
        public void remove() {
            MultiSetClass.this.remove(keyNow);
        }

        @Override
        public boolean hasNext() {
            if (MultiSetClass.this.size() == 0) {
                return false;
            }
            if (keyNow == null || multiSet.getOrDefault(keyNow, 0) > amountNow) {
                return true;
            }
            return it.hasNext();
        }

        @Override
        public Object next() {
            if (keyNow == null) {
                keyNow = (E) it.next();
                return keyNow;
            } else {
                if (multiSet.containsKey(keyNow) && multiSet.get(keyNow) > amountNow) {
                    ++amountNow;
                    return keyNow;
                } else {
                    amountNow = 1;
                    keyNow = (E) it.next();
                    return keyNow;
                }
            }
        }
    }
}
