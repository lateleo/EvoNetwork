package staticUtils;

import network.UpperLayer;
import utils.ConnTuple;
import utils.NodeTuple;

public class ComparisonUtils {
	
	public static int compareLayNums(int a, int b) {
		if (a == b) return 0;
		if (a == -1) return 1;
		if (b == -1) return -1;
		else return a - b;
	}
	
	public static int compareUpperLayers(UpperLayer a, UpperLayer b) {
		return compareLayNums(a.layNum(), b.layNum());
	}
	
	public static int compareNodeTuples(NodeTuple a, NodeTuple b) {
		int layComp = compareLayNums(a.layer(), b.layer());
		if (layComp == 0) return a.node() - b.node();
		else return layComp;
	}
	
	public static int compareConnTuples(ConnTuple a, ConnTuple b) {
		if (a.getKey().equals(b.getKey())) return a.getValue().compareTo(b.getValue());
		else return a.getKey().compareTo(b.getKey());
	}

}
