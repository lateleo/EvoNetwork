package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;
import utils.NodeVector;

public class TopNode extends UpperNode {
	private double error = 0;
	private double loss = 0;
	

	TopNode(TopLayer layer, NodeVector vector, NodePhene phene, Map<ConnTuple, Connection> conns) {
		super(layer, vector, phene, conns);
	}
	
	public void updateError(double sum, int label) {
		double result = (label == vector.getThetaFloor()) ? output/sum - 1 : output/sum;
		layer.nanCheck(result, "Top Node Post-SoftMax");
		error += result;
		double sqrOut = result*result;
		layer.nanCheck(sqrOut, "Top Node Output Squared");
		loss += sqrOut;
	}
	
	public double getLoss() {
		return loss;
	}
	
	@Override
	public void run() {
		output = 0;
		super.run();
	}

	@Override
	public void updateDerivative() {
		derivative = 2*error*invBatchSize;
		error = 0;
		loss = 0;
	}
	
	@Override
	protected void nanCheck() {
		layer.nanCheck(output, "Top Node Output, Node " + vector);
	}
	
}
