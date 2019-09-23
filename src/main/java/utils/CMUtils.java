package utils;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.xml.crypto.dsig.TransformException;

/*
 * This class contains various static methods used for manipulating Collections and Maps.
 */
public class CMUtils {
	
	/*
	 * Returns a subMap of source Map, containing all entries whose keys and values meet the given BiPredicate.
	 */
	public static <K, V> Map<K, V> subMap(Map<K, V> source, BiPredicate<K, V> filter) {
		Map<K, V> subMap = new TreeMap<K, V>();
		source.forEach((key, value) ->{
			if (filter.test(key, value)) subMap.put(key, value);
		});
		return subMap;
	}
	
	/*
	 * Returns a subMap of source Map, containing all entries whose keys meet the given Predicate.
	 */
	public static <K, V> Map<K, V> subMap(Map<K, V> source, Predicate<K> filter) {
		Map<K, V> subMap = new TreeMap<K, V>();
		source.forEach((key, value) ->{
			if (filter.test(key)) subMap.put(key, value);
		});
		return subMap;
	}
	
	/*
	 * Returns a subMap of source Map, containing all entries whose key also exists in reference Map.
	 */
	public static <K, V, T> Map<K, V> subMapOnSharedKey(Map<K, V> source, Map<K, T> reference) {
		return subMap(source, key -> reference.containsKey(key));
	}
	
	/*
	 * Returns a Map with the same values, but different key Type as source Map, with new keys determined by
	 * calling the given Function on each key in source. Throws a TransformException if calling the given Function
	 * on a key produces a new key that is identical to that of a previous entry.
	 */
	public static <K, V, T> Map<T, V> transformMapKeys(Map<K, V> source, Function<K, T> function) {
		Map<T, V> newMap = new Hashtable<>();
		source.forEach((key, value) -> {
			try {
				T newKey = function.apply(key);
				if (newMap.containsKey(newKey)) throw new TransformException("Provided function produced duplicate keys");
				else newMap.put(newKey, value);
			} catch (TransformException e) {e.printStackTrace();};
		});
		return newMap;
	}
	
	/*
	 * Returns a Map with the same keys, but different value Type as source Map, with new values determined by
	 * calling the given Function on each value in source.
	 */
	public static <K, V, T> Map<K, T> transformMapValues(Map<K, V> source, Function<V, T> function) {
		Map<K, T> newMap = new Hashtable<>();
		source.forEach((key,value) -> newMap.put(key, function.apply(value)));
		return newMap;
	}
	
	/*
	 * Returns a Map of ConnTuple/Doubles, representing all connections that the given layNum takes as inputs.
	 */
	public static Map<ConnTuple,Double> getConnsForLayer(Map<ConnTuple,Double> source, Collection<ConnSetPair> pairs) {
		Map<ConnTuple,Double> weights = new TreeMap<>();
		for (ConnSetPair pair : pairs) {
			for (ConnTuple tuple : pair.downConns) {
				weights.put(tuple, source.get(tuple));
			}
		}
		return weights;
	}
	

	
	
	
	

}
