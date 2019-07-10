package utils;

public class NodeTuple implements Comparable<NodeTuple> {
	private int layer, node;
	
	public NodeTuple(ConnTuple connTuple) {
		this.layer = connTuple.iLay();
		this.node = connTuple.iNode();
	}
	
	public int layer() {
		return layer;
	}
	
	public int node() {
		return node;
	}

	boolean equals(NodeTuple other) {
		return this.layer == other.layer &&
				this.node == other.node;
	}
	
	public int hashCode() {
		return (Integer.toString(layer) + "." +
				Integer.toString(node)).hashCode();
	}

	public int compareTo(NodeTuple other) {
		if (this.layer != other.layer) return this.layer - other.layer;
		else return this.node - other.node;
	}

}
