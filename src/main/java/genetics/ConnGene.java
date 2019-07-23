package genetics;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ecology.Species;
import utils.RNG;

public class ConnGene extends Gene {
	private static int bottomNodes = Species.bottomNodes;
	private static int topNodes = Species.topNodes;
	private static double mMag = Species.mutationMagnitude;
	public double xprLevel, inLayNum, inNodeNum, outLayNum, outNodeNum, weight;
	int signature;
	
	Mutation xprMutation = (gene) -> ((ConnGene) gene).xprLevel += RNG.getShiftDouble()*mMag;
	Mutation layNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inLayNum = Math.max(0.0, mutant.inLayNum + RNG.getShiftDouble()*mMag);
		else mutant.outLayNum = Math.max(-1.0, mutant.outLayNum + RNG.getShiftDouble()*mMag);
	};
	Mutation nodeNumMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		if (RNG.getBoolean()) mutant.inNodeNum = Math.max(0.0, mutant.inNodeNum + RNG.getShiftDouble()*mMag);
		else mutant.outNodeNum = Math.max(0.0, mutant.outNodeNum + RNG.getShiftDouble()*mMag);		
	};
	Mutation weightMutation = (gene) -> ((ConnGene) gene).weight *= 1 + RNG.getGauss()*mMag;
	Mutation signMutation = (gene) -> {
		ConnGene mutant = (ConnGene) gene;
		mutant.signature = mutant.signature ^ (int) Math.pow(2, RNG.getBit());
	};
	
	public ConnGene(double xprLevel, double inLayNum, double inNodeNum,
			double outLayNum, double outNodeNum, double weight, int signature) {
		this.xprLevel = xprLevel;
		this.inLayNum = inLayNum;
		this.inNodeNum = inNodeNum;
		this.outLayNum = outLayNum;
		this.outNodeNum = outNodeNum;
		this.weight = weight;
		this.signature = signature;
	}
	
	public static ArrayList<Gene> generate(int layers, int nodes, int conns, int diploidNum, int signBits) {
		ArrayList<Gene> genes = new ArrayList<>();
		int geneNum = conns;
		double laySigma = layers/2.5;
		double nodeSigma = nodes/2.0;
		double xprShift = 1/diploidNum;
		for (int i = 0; i<bottomNodes; i++) {
			double oLay = RNG.getMinGauss(-1.0, 1+laySigma, 2+laySigma);
			double oNode = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			int[] bits = new int[signBits];
			for (int b = 0; b < bits.length; b++) bits[b] = RNG.getBit();
			int sign = Arrays.stream(bits).sum();
			genes.add(new ConnGene(RNG.getGauss(0.5,xprShift), 0.5, i+0.5, oLay, oNode, RNG.getGauss(), sign));			
		}
		for (int i = 0; i<topNodes; i++) {
			double iLay = RNG.getMinGauss(0.0, laySigma, 1+laySigma);
			double iNode = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			int[] bits = new int[signBits];
			for (int b = 0; b < bits.length; b++) bits[b] = RNG.getBit();
			int sign = Arrays.stream(bits).sum();
			genes.add(new ConnGene(RNG.getGauss(0.5,xprShift), iLay, iNode, -0.5, i+0.5, RNG.getGauss(), sign));
			iLay = RNG.getMinGauss(0.0, laySigma, 1+laySigma);
			iNode = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			bits = new int[signBits];
			for (int b = 0; b < bits.length; b++) bits[b] = RNG.getBit();
			sign = Arrays.stream(bits).sum();
			genes.add(new ConnGene(RNG.getGauss(0.5,xprShift), iLay, iNode, -0.5, i+0.5, RNG.getGauss(), sign));	
		}
		while (genes.size() < 2*geneNum) {
			double iLay = RNG.getMinGauss(0.0, laySigma, 1+laySigma);
			double iNode = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			double oLay = RNG.getMinGauss(-1.0, 1+laySigma, 2+laySigma);
			double oNode = RNG.getMinGauss(0.0, nodeSigma, 1+nodeSigma);
			int[] bits = new int[signBits];
			for (int i = 0; i < bits.length; i++) bits[i] = RNG.getBit();
			int sign = Arrays.stream(bits).sum();
			genes.add(new ConnGene(RNG.getGauss(0.5,xprShift), iLay, iNode, oLay, oNode, RNG.getGauss(), sign));
		}
		return genes;	
	}

	@Override
	protected Gene clone() {
		return new ConnGene(this.xprLevel, this.inLayNum, this.inNodeNum, 
				this.outLayNum, this.outNodeNum, this.weight, this.signature);
	}

	@Override
	public Gene mutate(double rand) {
		return mutate(new Mutation[] {xprMutation, layNumMutation, nodeNumMutation, weightMutation}, rand);
	}

}
