package genetics;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import utils.RNG;

public class LayerGene extends Gene {
	double xprLevel, layerNum;
	Mutation xprMutation = (mutant) -> ((LayerGene) mutant).xprLevel += RNG.getShiftDouble();
	Mutation layNumMutation = (gene) -> {
		LayerGene mutant = (LayerGene) gene;
		mutant.layerNum = Math.max(1.0, mutant.layerNum + RNG.getShiftDouble());
	};
	
	public LayerGene(double xprLevel, double layerNum) {
		this.xprLevel = xprLevel;
		this.layerNum = layerNum;
	}
	
	public static ArrayList<Gene> generate(int layers, int diploidNum){
		ArrayList<Gene> genes = new ArrayList<>();
		int geneNum = 2*layers*diploidNum;
		double sigma = layers/2.0;
		double xprShift = 2/diploidNum;
		while (genes.size() < 4*geneNum) {
			double layNum = RNG.getMinGauss(1.0, sigma, 1+sigma);
			genes.add(new LayerGene(RNG.getGauss(0.5,xprShift), layNum));
		}
		return genes;
	};

	@Override
	protected Gene clone() {
		return new LayerGene(this.xprLevel, this.layerNum);
	}

	@Override
	public Gene mutate() {
		return mutate(Arrays.asList(xprMutation, layNumMutation));
	}
	
	public String toString() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(3);
		return "[" + format.format(xprLevel) + ", " + format.format(layerNum) + "]";
	}

}
