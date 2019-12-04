package network;

import java.util.Map;
import java.util.TreeMap;

import data.MnistImage;
import ecology.Population;
import ecology.Species;
import genetics.NodePhene;
import genetics.Transcriptome;
import staticUtils.CMUtils;
import utils.ConnTuple;

public class NeuralNetwork extends TreeMap<Integer, Layer> implements Runnable {
	private static final long serialVersionUID = -2513726838630426232L;
	private static int batchSize = Species.batchSize;
	private static MnistImage[][] images = Species.images;
	private static MnistImage[] currentImageSet = images[0];
	private static int currentBatchNum = 0;
	
	MnistImage currentImage;
	private int currentIndex = 0;
	
	private Organism org;
	private BottomLayer bottom;
	private TopLayer top;
	private double accuracy = 0.0;
	private double lossScalar = 1/(2.0*batchSize);
	private int size = 0;
	
	public boolean nanFound = false;
		
	public NeuralNetwork(Organism org) {
		super(Species.comparator);
		this.org = org;
		Transcriptome xscript = org.genome().transcribe();
		TreeMap<Integer,TreeMap<Integer,NodePhene>> laysAndNodes = xscript.getLaysAndNodes();
		TreeMap<ConnTuple,Connection> conns = getConns(xscript.getConnWeights());
		setBottom(conns);
		size = conns.size();
		laysAndNodes.forEach((layNum, nodePhenes) -> {
			size += nodePhenes.size();
			if (layNum != -1) {
				MidLayer layer = new MidLayer(nodePhenes, conns, this, layNum);
				put(layNum, layer);
			}
		});
		Map<Integer,NodePhene> nodePhenes = laysAndNodes.get(-1);
		TopLayer top = new TopLayer(nodePhenes, conns, this);
		setTop(top);
	}

	@Override
	public void run() {
		currentIndex = 0;
		top.reset();
		while (!nanFound && currentIndex < batchSize) {
			currentImage = currentImageSet[currentIndex];
			forEach((layNum, layer) -> {
				if (!nanFound) layer.run();	
			});
			currentIndex++;
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
		descendingKeySet().forEach((layNum) -> {
			if (layNum != 0) ((UpperLayer) get(layNum)).backProp();
		});
	}
	
	void setBottom(Map<ConnTuple,Connection> conns) {
		if (bottom == null) {
			Map<ConnTuple,Connection> bottomConns = CMUtils.subMap(conns, (tuple) -> tuple.iLay() == 0);
			bottom = new BottomLayer(this, bottomConns);
			put(0, bottom);
		}
	}
	
	void setTop(TopLayer top) {
		if (this.top == null) {
			this.top = top;
			put(-1, top);
		}
	}
	
	private TreeMap<ConnTuple,Connection> getConns(TreeMap<ConnTuple,Double> weights) {
		TreeMap<ConnTuple,Connection> conns = new TreeMap<>();
		weights.forEach((tuple,weight) -> conns.put(tuple, new Connection(weight)));
		return conns;
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
