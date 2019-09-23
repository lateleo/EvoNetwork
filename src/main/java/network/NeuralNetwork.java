package network;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;
import ecology.Species;
import genetics.Transcriptome;
import utils.CMUtils;
import utils.ConnSetPair;
import utils.ConnTuple;

public class NeuralNetwork extends TreeMap<Integer, Layer> implements Runnable {
	private static final long serialVersionUID = -2513726838630426232L;
	private static int batchSize = Species.batchSize;
	private BottomLayer bottom;
	private TopLayer top;
	private double accuracy = 0.0;
	
	public ArrayList<double[]> allOutputs = new ArrayList<>();
	
	public NeuralNetwork(Transcriptome xscript) {
		super(Species.comparator);
		TreeMap<Integer,TreeMap<Integer,Double>> nodeBiasMap = xscript.getNodeBiasMap();
		TreeMap<Integer,TreeMap<Integer,ConnSetPair>> tupleSetMap = xscript.getTupleSetMap();
		TreeMap<ConnTuple,Double> connWeights = xscript.getConnWeights();
		setBottom();
		nodeBiasMap.forEach((layNum, biases) -> {
			if (layNum != -1) {
				Map<Integer,ConnSetPair> pairs = tupleSetMap.get(layNum);
				Map<ConnTuple,Double> weights = CMUtils.getConnsForLayer(connWeights, pairs.values());
				MidLayer layer = new MidLayer(biases, pairs, weights, this);
				put(layNum, layer);
			}
		});
		Map<Integer,ConnSetPair> pairs = tupleSetMap.get(-1);
		Map<ConnTuple,Double> weights = CMUtils.getConnsForLayer(connWeights, pairs.values());
		TopLayer top = new TopLayer(pairs, weights, this);
		setTop(top);
	}

	@Override
	public void run() {
		double loss = 0.0;
		while (!bottom.allImagesComplete()) {
			forEach((layNum, layer) -> layer.run());
			int label = bottom.currentImage.getLabel();
			
			double[] outputs = top.outputs;
			outputs[label] = 1 - outputs[label];
			for (int i = 0; i < outputs.length; i++) {
				outputs[i] *= outputs[i];
			}
			
			double sum = Arrays.stream(outputs).sum();
			
			loss += sum;

		}
		loss /= 2.0*batchSize;
		accuracy = 1 - loss;
		bottom.resetImageIndex();

	}
	
	public void backProp() {
		descendingKeySet().forEach((layNum) -> {
			if (layNum != 0) ((UpperLayer) get(layNum)).backProp();
		});
	}
	
	void setBottom() {
		if (bottom == null) {
			bottom = new BottomLayer();
			put(0, bottom);
		}
	}
	
	void setTop(TopLayer top) {
		if (this.top == null) {
			this.top = top;
			put(-1, top);
		}
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	
}
