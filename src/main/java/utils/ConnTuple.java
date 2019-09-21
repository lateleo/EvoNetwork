package utils;

import org.apache.commons.math3.util.Pair;

import genetics.ConnGene;

/*
 * This class is used exclusively as a Key object for the 'conns' map that is produced by the Transcriptome.
 * It is essentially a Pair of NodeTuples, which are themselves Pairs of Integers, with shortcut methods for ease of use.
 * ConnTuples as a whole represent the starting point and ending point for a given connection.
 */
public class ConnTuple extends Pair<NodeTuple, NodeTuple> implements Comparable<ConnTuple> {
	
	public ConnTuple(NodeTuple in, NodeTuple out) {
		super(in,out);
	}

	public ConnTuple(ConnGene gene) {
		super(new NodeTuple((int) gene.inLayNum, (int) gene.inNodeNum),
				new NodeTuple((int) Math.floor(gene.outLayNum), (int) gene.outNodeNum));
	}
	
	public int iLay() {
		return getKey().layer();
	}

	public int iNode() {
		return getKey().node();
	}

	public int oLay() {
		return getValue().layer();
	}

	public int oNode() {
		return getValue().node();
	}

	public int compareTo(ConnTuple other) {
		if (!getKey().equals(other.getKey())) return getKey().compareTo(other.getKey());
		else return getValue().compareTo(other.getValue());
	}

}
