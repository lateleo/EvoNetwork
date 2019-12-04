package network;

import java.util.Map;

import genetics.NodePhene;
import utils.ConnTuple;

public class TopNode extends UpperNode {
	double error = 0;
	double loss = 0;
	

	TopNode(TopLayer layer, int nodeNum, NodePhene phene, Map<ConnTuple, Connection> conns) {
		super(layer, nodeNum, phene, conns);
	}
	
	public void updateError() {
		error += output;
		double sqrOut = output*output;
		layer.nanCheck(sqrOut, "Top Node Output Squared: ");
		loss += sqrOut;
	}
	
	public void reset() {
		error = 0;
		loss = 0;
	}
	
	@Override
	public void run() {
		output = 0;
		super.run();
	}

	@Override
	public void updateDerivative() {
		derivative = 2*error*invBatchSize;			
	}
	
}
