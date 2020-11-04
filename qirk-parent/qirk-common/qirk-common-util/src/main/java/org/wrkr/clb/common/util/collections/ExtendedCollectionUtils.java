package org.wrkr.clb.common.util.collections;

import java.util.HashMap;
import java.util.Map;

public abstract class ExtendedCollectionUtils {

    public static <K extends Object, V extends Object> Map<K, V> buildMapFromKeyAndValue(K key, V value) {
        HashMap<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }
}
