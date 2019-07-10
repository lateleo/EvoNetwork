package utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;
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
		Map<K, V> subMap = new Hashtable<>();
		source.forEach((key, value) ->{
			if (filter.test(key, value)) subMap.put(key, value);
		});
		return subMap;
	}
	
	/*
	 * Returns a subMap of source Map, containing all entries whose keys meet the given Predicate.
	 */
	public static <K, V> Map<K, V> subMap(Map<K, V> source, Predicate<K> filter) {
		Map<K, V> subMap = new Hashtable<>();
		source.forEach((key, value) -> {
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
	 * Returns a Map of ConnTuple/Doubles, representing all connections that the given layNum takes as inputs.
	 */
	public static Map<ConnTuple,Double> getConnsForLayer(Map<ConnTuple,Double> source, int layNum) {
		return subMap(source, tuple -> tuple.oLay() == layNum);
	}
	
	/*
	 * Returns a Map of NodeTuple/Doubles, representing all connections that the given layer takes as inputs.
	 * The layer number of the node is not needed, as this will only be used on the output of getConnsForLayer().
	 */
	public static Map<NodeTuple,Double> getConnsForNode(Map<ConnTuple,Double> source, int nodeNum) {
		Map<ConnTuple,Double> subMap = subMap(source, tuple -> tuple.oNode() == nodeNum);
		return transformMapKeys(subMap, connTuple -> new NodeTuple(connTuple));
	}
	
	
	

}
