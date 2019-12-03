package network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecology.Species;
import genetics.NodePhene;
import utils.ConnTuple;

public abstract class UpperNode extends Node {
	protected static int invBatchSize = 1/Species.batchSize;
	
	protected UpperLayer layer;
	protected int nodeNum;
	protected double avgOutput = 0;
	protected double derivative = 0;
	protected double learningRate;
	List<Conn> downConns;
	
	UpperNode(UpperLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Conn> conns){
		this.layer = layer;
		this.nodeNum = nodeNum;
		this.learningRate = phene.getLearnRate();
		getDownConns(conns, phene);
	}
	
	private void getDownConns(Map<ConnTuple,Conn> source, NodePhene phene) {
		downConns = new ArrayList<>();
		for (ConnTuple cTuple : phene.downConns) {
			Conn conn = source.get(cTuple);
			conn.setUpNode(this);
			downConns.add(conn);
		}
	}

	public void run() {
		for (Conn conn : downConns) output += conn.weight()*conn.downNode().output;
		output = Math.max(0.0, output);
		layer.nanCheck(output, "Node Output - Layer " + layer.layNum + ", Node " + nodeNum);
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
		updateDerivative();
		downConns.forEach(conn -> conn.updateWeight(learningRate*avgOutput*derivative));
		avgOutput = 0;
	}
	
	public abstract void updateDerivative();

}
