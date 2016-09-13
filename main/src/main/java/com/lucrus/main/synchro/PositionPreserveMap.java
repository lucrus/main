/**
 *
 */
package com.lucrus.main.synchro;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author l.russo
 */
public class PositionPreserveMap<K, V> extends HashMap<K, V> {
    private SortedMap<Integer, K> positions = new TreeMap<Integer, K>();

    /**
     *
     */
    public PositionPreserveMap() {
        super();
    }

    /**
     * @param initialCapacity
     */
    public PositionPreserveMap(int initialCapacity) {
        super(initialCapacity);
    }

    /**
     * @param m
     */
    public PositionPreserveMap(Map<K, V> m) {
        super(m);
    }

    /**
     * @param initialCapacity
     * @param loadFactor
     */
    public PositionPreserveMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }

    @Override
    public Set<K> keySet() {
        Set<K> res = new PositionPreserveSet<K>();
        for (Integer i : this.positions.keySet()) {
            res.add(this.positions.get(i));
        }
        return res;
    }

    @Override
    public V put(K key, V value) {
        this.positions.put(this.positions.size() + 1, key);
        return super.put(key, value);
    }

}
