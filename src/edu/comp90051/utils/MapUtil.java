package edu.comp90051.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtil {

  /**
   * Sort the passed-in map in terms of its value's default comparing rule
   * <p>
   * This method does not modify the original map, and returns the sorted map with shallow copy
   *
   * @param map the map to be sorted
   * @param <K> type parameter of the map's key
   * @param <V> type parameter of the map's value
   * @return the sorted map
   */
  public static <K, V extends Comparable<? super V>> Map<K, V> sortByValue(Map<K, V> map) {
    List<Map.Entry<K, V>> list = new ArrayList<>(map.entrySet());
    list.sort(Map.Entry.comparingByValue());

    Map<K, V> result = new LinkedHashMap<>();
    for (Map.Entry<K, V> entry : list)
      result.put(entry.getKey(), entry.getValue());

    return result;
  }

}
