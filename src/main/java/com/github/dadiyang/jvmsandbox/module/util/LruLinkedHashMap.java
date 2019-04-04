package com.github.dadiyang.jvmsandbox.module.util;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 实现LRU算法的 LinkedHashMap
 *
 * @author dadiyang
 * date2018/11/23
 */
public class LruLinkedHashMap<K, V> extends LinkedHashMap<K, V> {
    private int capacity;

    public LruLinkedHashMap(int capacity) {
        super(capacity);
        this.capacity = capacity;
    }

    @Override
    public boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

}
