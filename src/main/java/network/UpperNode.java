package network;

import java.util.Map;
import java.util.TreeMap;

import ecology.Species;
import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeTuple;

public abstract class UpperNode extends Node {
	protected static double learningRate = Species.learningRate;
	protected static int invBatchSize = Species.batchSize;
	
	protected UpperLayer layer;
	protected int nodeNum;
	protected double avgOutput = 0;
	protected double derivative = 0;
	Map<NodeTuple, Conn> downConns;
	
	UpperNode(UpperLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns){
		this.layer = layer;
		this.nodeNum = nodeNum;
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
		layer.nanCheck(output, "Node Output - Layer " + layer.layNum + ", Node " + nodeNum);
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
		updateDerivative();
		downConns.forEach((tuple, conn) -> {
			conn.updateWeight(learningRate*avgOutput*derivative);
		});
		avgOutput = 0;
	}
	
	public abstract void updateDerivative();

}
