package network;

import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import data.MnistImage;
import ecology.Population;
import ecology.Species;
import genetics.NodePhene;
import genetics.Transcriptome;
import staticUtils.CMUtils;
import staticUtils.ComparisonUtils;
import utils.ConnTuple;
import utils.NodeVector;

public class NeuralNetwork implements Runnable {
	private static int batchSize = Species.batchSize;
	private static MnistImage[][] images = Species.images;
	private static MnistImage[] currentImageSet = images[0];
	private static int currentBatchNum = 0;
	
	MnistImage currentImage;
		
	private Organism org;
	private BottomLayer bottom;
	private TopLayer top;
	private TreeSet<UpperLayer> upperLayers = new TreeSet<>(ComparisonUtils::compareUpperLayers);

	private double accuracy = 0.0;
	private double lossScalar = 1/(2.0*batchSize);
	private int size = 0;
	
	public boolean nanFound = false;
		
	public NeuralNetwork(Organism org) {
		this.org = org;
		Transcriptome xscript = org.genome().transcribe();
		TreeMap<Integer,TreeMap<NodeVector,NodePhene>> laysAndNodes = xscript.getLaysAndNodes();
		TreeMap<ConnTuple,Connection> conns = getConns(xscript.getConnWeights());
		this.bottom = new BottomLayer(this, CMUtils.subMap(conns, (tuple) -> tuple.iLay() == 0));
		size = conns.size();
		for (Map.Entry<Integer, TreeMap<NodeVector,NodePhene>> entry : laysAndNodes.entrySet()) {
			int layNum = entry.getKey();
			TreeMap<NodeVector,NodePhene> nodePhenes = entry.getValue();
			size += nodePhenes.size();
			if (layNum != -1) upperLayers.add(new MidLayer(nodePhenes, conns, this, layNum));
		}
		this.top = new TopLayer(laysAndNodes.get(-1), conns, this);
		upperLayers.add(top);
	}
	
	private TreeMap<ConnTuple,Connection> getConns(TreeMap<ConnTuple,Double> weights) {
		TreeMap<ConnTuple,Connection> conns = new TreeMap<>();
		for (Map.Entry<ConnTuple, Double> entry : weights.entrySet()) {
			conns.put(entry.getKey(), new Connection(entry.getValue()));
		}
		return conns;
	}

	@Override
	public void run() {
		for (int index = 0; !nanFound && index < batchSize; index++) {
			currentImage = currentImageSet[index];
			bottom.run();
			for (UpperLayer layer : upperLayers) if (!nanFound) layer.run();
		}
		if (!nanFound) {
			accuracy = 1 - top.getLoss()*lossScalar;
			if (!Double.isFinite(accuracy)) {
				nanFound = true;
				System.out.println("Non-Finite Accuracy: " + accuracy);
			}
		} else {
			Population.getInstance().remove(org);
		}
	}
	
	public void backProp() {
		top.backProp();
		for (UpperLayer layer : upperLayers.descendingSet()) layer.backProp();
	}

	
	public int size() {
		return size;
	}
	
	public double getAccuracy() {
		return accuracy;
	}
	
	public static void nextBatch() {
		currentBatchNum = (currentBatchNum + 1) % images.length;
		currentImageSet = images[currentBatchNum];
	}
	
	public static void testBatch() {
		currentImageSet = Species.testImages;
	}
	
}
