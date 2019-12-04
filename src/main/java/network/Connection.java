package network;

/*
 * This class probably seems unnecessary, but is used in place of the built-in Double class so that changes
 * made at one end of the connection will be visible at the other end.
 */
public class Connection {
	private UpperNode upNode = null;
	private Node downNode = null;
	private double weight;
	private double oldWeight = 0;
	
	
	public Connection(double weight) {
		this.weight = weight;
	}
	
	public void setUpNode(UpperNode node) {
		if (upNode == null) {
			upNode = node;
		}
	}
	
	public void setDownNode(Node node) {
		if (downNode == null) {
			downNode = node;
		}
	}
	
	public double weightedOutput() {
		return weight*downNode.getOutput();
	}
	
	public double weightedDerivative() {
		return oldWeight*upNode.getDerivative();
	}
	
	public void updateWeight(double increase) {
		if (increase < 0) System.out.println(increase);
		oldWeight = weight;
		weight += increase;
	}

}
