package network;

import java.util.Map;
import java.util.TreeMap;

import ecology.Species;
import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeTuple;

public abstract class UpperNode extends Node {
	protected static int invBatchSize = 1/Species.batchSize;
	
	protected UpperLayer layer;
	protected int nodeNum;
	protected double avgOutput = 0;
	protected double derivative = 0;
	protected double learningRate;
	Map<NodeTuple, Conn> downConns;
	
	UpperNode(UpperLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns){
		this.layer = layer;
		this.nodeNum = nodeNum;
		this.learningRate = phene.getLearnRate();
		this.downConns = getDownConns(conns, phene);
	}
	
	protected Map<NodeTuple,Conn> getDownConns(Map<ConnTuple,Conn> source, NodePhene phene) {
		Map<NodeTuple,Conn> conns = new TreeMap<>();
		for (ConnTuple cTuple : phene.downConns) {
			Conn conn = source.get(cTuple);
			conn.setUpNode(this);
			conns.put(cTuple.getFirst(), conn);
		}
		return conns;
	}

	public void run() {
		downConns.forEach((tuple, conn) -> output += conn.weight()*conn.downNode().output);
		output = Math.max(0.0, output);
		if (layer.nanCheck(output, "Node Output - Layer " + layer.layNum + ", Node " + nodeNum)) {
//			downConns.forEach((tuple, conn) -> {
//				System.out.println(conn.weight() + " (W); " + conn.initWeight() + " (IW)");
//			});
		}
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
		updateDerivative();
//		if (derivative < 0)	System.out.println(derivative);
		downConns.forEach((tuple, conn) -> {
			conn.updateWeight(learningRate*avgOutput*derivative);
		});
		avgOutput = 0;
	}
	
	public abstract void updateDerivative();

}
