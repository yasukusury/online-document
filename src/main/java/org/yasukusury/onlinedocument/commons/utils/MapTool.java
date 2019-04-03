package org.yasukusury.onlinedocument.commons.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * @author 30254
 * creadtedate: 2019/2/25
 */
public class MapTool {

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> mapByField(String getterName, Collection<V> collection) {
        Map<K, V> map = new HashMap<>(collection.size());
        if (collection.size() == 0) {
            return map;
        }
        try {
            Method m = collection.iterator().next().getClass().getMethod(getterName);
            for (V v : collection) {
                map.put((K) m.invoke(v), v);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return map;
    }
}
