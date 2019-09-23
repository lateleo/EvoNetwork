package utils;

import java.util.HashSet;
import java.util.Set;

//	Used for Orphan removal and but created/added to in earlier steps for efficiency
public class ConnSetPair {
	public Set<ConnTuple> upConns = new HashSet<>();
	public Set<ConnTuple> downConns = new HashSet<>();

	public void addUp(ConnTuple tuple) {
		upConns.add(tuple);
	}

	public void addDown(ConnTuple tuple) {
		downConns.add(tuple);
	}
}
