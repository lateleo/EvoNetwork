package staticUtils;

import network.UpperLayer;
import utils.ConnTuple;
import utils.NodeTuple;
import utils.NodeVector;

public class ComparisonUtils {
	
	public static int compareLayNums(int a, int b) {
		if (a == b) return 0;
		if (a == -1) return 1;
		if (b == -1) return -1;
		else return (int) Math.signum(a - b);
	}
	
	public static int compareUpperLayers(UpperLayer a, UpperLayer b) {
		return compareLayNums(a.layNum(), b.layNum());
	}
	
	public static int compareNodeTuples(NodeTuple a, NodeTuple b) {
		int layComp = compareLayNums(a.layer(), b.layer());
		return (int)Math.signum((layComp == 0) ? compareNodeVectors(a.node(), b.node()) : layComp);
	}
	
	public static int compareConnTuples(ConnTuple a, ConnTuple b) {
		if (a.in().equals(b.in())) return a.out().compareTo(b.out());
		else return a.in().compareTo(b.in());
	}
	
	public static int compareNodeVectors(NodeVector a, NodeVector b) {
		double magDif = a.getMagnitude() - b.getMagnitude();
		return (int)Math.signum((magDif == 0) ? a.getTheta() - b.getTheta() : magDif);
	}

}
