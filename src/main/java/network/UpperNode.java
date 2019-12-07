package network;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ecology.Species;
import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeVector;

public abstract class UpperNode extends Node {
	protected static int invBatchSize = 1/Species.batchSize;
	
	protected UpperLayer layer;
	protected NodeVector vector;
	protected double avgOutput = 0;
	protected double derivative = 0;
	protected double learningRate;
	List<Connection> downConns;
	
	UpperNode(UpperLayer layer, NodeVector vector, NodePhene phene, Map<ConnTuple, Connection> conns){
		this.layer = layer;
		this.vector = vector;
		this.learningRate = phene.getLearnRate();
		getDownConns(conns, phene);
	}
	
	private void getDownConns(Map<ConnTuple,Connection> source, NodePhene phene) {
		downConns = new ArrayList<>();
		for (ConnTuple cTuple : phene.downConns) {
			Connection conn = source.get(cTuple);
			conn.setUpNode(this);
			downConns.add(conn);
		}
	}

	public void run() {
		for (Connection conn : downConns) output += conn.weightedOutput();
		output = Math.max(0.0, output);
		nanCheck();
		avgOutput += output;
	}
	
	public void backProp() {
		avgOutput *= invBatchSize;
		updateDerivative();
		downConns.forEach(conn -> conn.updateWeight(learningRate*avgOutput*derivative));
		avgOutput = 0;
	}
	
	public double getDerivative() {
		return derivative;
	}
	
	protected void nanCheck() {
		layer.nanCheck(output, "Node Output - Layer " + layer.layNum() + ", Node " + vector);
	}
	
	public abstract void updateDerivative();
	
}
