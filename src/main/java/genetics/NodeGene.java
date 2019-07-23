package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecology.Species;
import utils.RNG;

public class NodeGene extends Gene {
	private static int topNodes = Species.topNodes;
	private static double mMag = Species.mutationMagnitude;
	double xprLevel, layerNum, nodeNum, bias;
	Mutation xprMutation = (mutant) -> ((NodeGene) mutant).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation layNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.layerNum = Math.max(-1.0, mutant.layerNum + RNG.getShiftDouble()*mMag);
	};
	Mutation nodeNumMutation = (gene) -> {
		NodeGene mutant = (NodeGene) gene;
		mutant.nodeNum = Math.max(0.0, mutant.nodeNum + RNG.getShiftDouble()*mMag);
	};
	Mutation biasMutation = (mutant) -> ((NodeGene) mutant).bias += RNG.getGauss()*mMag;
	 
	
	public NodeGene(double xprLevel, double layerNum, double nodeNum, double bias) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
		this.nodeNum = nodeNum;
		this.bias = bias;
	}
	
	public static ArrayList<Gene> generate(int layers, int nodes, int diploidNum) {
		ArrayList<Gene> genes = new ArrayList<>();
		int geneNum = layers*nodes*diploidNum/2;
		double laySigma = layers/2.5;
		double nodeSigma = nodes/2.0;
		double xprShift = 2/diploidNum;
		for (int i = 0; i < topNodes; i++) {
			genes.add(new NodeGene(RNG.getGauss(), -1.0, i+0.5, RNG.getGauss()));
		}
		while (genes.size() < 2*geneNum) {
			double layNum = RNG.getMinGauss(-1.0, laySigma, 1+laySigma);
			double nodeNum = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			genes.add(new NodeGene(RNG.getGauss(0.5, xprShift), layNum, nodeNum, RNG.getGauss()));
		}
		return genes;		
	}
	
	@Override
	protected Gene clone() {
		return new NodeGene(this.xprLevel, this.layerNum, this.nodeNum, this.bias);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, layNumMutation, nodeNumMutation, biasMutation}, rand);
	}

}
