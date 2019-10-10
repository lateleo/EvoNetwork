package network;

/*
 * This class probably seems unnecessary, but is used in place of the built-in Double class so that changes
 * made at one end of the connection will be visible at the other end.
 */
public class Conn {
	private UpperNode upNode = null;
	private Node downNode = null;
	private double weight;
	private double oldWeight = 0;
	
	
	public Conn(double weight) {
		this.weight = weight;
	}
	
	public void setUpNode(UpperNode node) {
		if (upNode == null) {
			upNode = node;
		}
	}
	
	public UpperNode upNode() {
		return upNode;
	}
	
	public void setDownNode(Node node) {
		if (downNode == null) {
			downNode = node;
		}
	}
	
	public Node downNode() {
		return downNode;
	}
	
	public double weight() {
		return weight;
	}
	
	public double oldWeight() {
		return oldWeight;
	}
	
	public void updateWeight(double increase) {
		oldWeight = weight;
		weight += increase;
	}

}
