package utils;

import genetics.ConnGene;
import staticUtils.ComparisonUtils;

/*
 * This class is used exclusively as a Key object for the 'conns' map that is produced by the Transcriptome.
 * It is essentially a Pair of NodeTuples, which are themselves Pairs of Integers, with shortcut methods for ease of use.
 * ConnTuples as a whole represent the starting point and ending point for a given connection.
 */
public class ConnTuple implements Comparable<ConnTuple> {
	private NodeTuple in, out;
	
	public ConnTuple(NodeTuple in, NodeTuple out) {
		this.in = in;
		this.out = out;
	}

	public ConnTuple(ConnGene gene) {
		in = new NodeTuple((int) gene.inLayNum, gene.inVector.snapToGrid());
		NodeVector outVector = (gene.outLayNum == -1) ? gene.outVector.getUnitVector() : gene.outVector.snapToGrid();
		out = new NodeTuple((int) Math.floor(gene.outLayNum), outVector);
	}
	
	public ConnTuple(int iLay, NodeVector iNode, int oLay, NodeVector oNode) {
		in = new NodeTuple(iLay, iNode);
		out = new NodeTuple(oLay, oNode);
	}
	
	public NodeTuple in() {
		return in;
	}
	
	public NodeTuple out() {
		return out;
	}
	
	public int iLay() {
		return in.layer();
	}

	public NodeVector iNode() {
		return in.node();
	}

	public int oLay() {
		return out.layer();
	}

	public NodeVector oNode() {
		return out.node();
	}

	public int compareTo(ConnTuple other) {
		return ComparisonUtils.compareConnTuples(this, other);
	}
	
	@Override
	public String toString() {
		return "[" + iLay() + ", " + iNode() + ", " + oLay() + ", " + oNode() + "]";
	}

}
