package org.wrkr.clb.common.util.collections;

import java.util.HashMap;
import java.util.Map;

public class MapBuilder<K extends Object, V extends Object> {

    private Map<K, V> map;

    public MapBuilder() {
        map = new HashMap<K, V>();
    }
    
    public MapBuilder(Map<K, V> map) {
        this.map = new HashMap<K, V>(map);
    }

    public MapBuilder<K, V> put(K key, V value) {
        map.put(key, value);
        return this;
    }

    public Map<K, V> build() {
        return map;
    }
}
