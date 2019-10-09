package network;

import java.util.ArrayList;
import java.util.Collection;
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
	
	public boolean nanFound;
	
	public ArrayList<double[]> allOutputs = new ArrayList<>();
	
	public NeuralNetwork(Organism org) {
		super(Species.comparator);
		this.org = org;
		Transcriptome xscript = org.genome().transcribe();
		TreeMap<Integer,TreeMap<Integer,NodePhene>> laysAndNodes = xscript.getLaysAndNodes();
		TreeMap<ConnTuple,Conn> conns = getConns(xscript.getConnWeights());
		setBottom();
		laysAndNodes.forEach((layNum, nodePhenes) -> {
			if (layNum != -1) {
				MidLayer layer = new MidLayer(nodePhenes, conns, this, layNum);
				put(layNum, layer);
			}
		});
		Map<Integer,NodePhene> nodePhenes = laysAndNodes.get(-1);
		TopLayer top = new TopLayer(nodePhenes, conns, this);
		setTop(top);
		setSize();
	}

	@Override
	public void run() {
		top.loss = 0.0;
		nanFound = false;
		while (!nanFound && currentIndex < batchSize) {
			currentImage = currentImageSet[currentIndex];
			forEach((layNum, layer) -> {
				if (!nanFound) layer.run();	
			});
			currentIndex++;
		}
		if (!nanFound) {
			top.setLoss();
			accuracy = 1 - top.loss*lossScalar;
			if (!Double.isFinite(accuracy)) {
				nanFound = true;
				System.out.println("Non-Finite Accuracy: " + accuracy);
			}
			currentIndex = 0;
			backProp();
		} else {
			org.age = -1;
			Population.getInstance().remove(org);
		}
	}
	
	public void backProp() {
		descendingKeySet().forEach((layNum) -> {
			if (layNum != 0) ((UpperLayer) get(layNum)).backProp();
		});
	}
	
	void setBottom() {
		if (bottom == null) {
			bottom = new BottomLayer(this);
			put(0, bottom);
		}
	}
	
	void setTop(TopLayer top) {
		if (this.top == null) {
			this.top = top;
			put(-1, top);
		}
	}
	
	private void setSize() {
		forEach((layNum,layer) -> {
			if (layNum != 0) {
				size += 1;
				layer.nodes.forEach((nodeNum,node) -> size += 1);
				((UpperLayer) layer).inputNodes.forEach((tuple,node) -> size += 3);
			}
		});
	}
	
	private TreeMap<ConnTuple,Conn> getConns(TreeMap<ConnTuple,Double> weights) {
		TreeMap<ConnTuple,Conn> conns = new TreeMap<>();
		weights.forEach((tuple,weight) -> conns.put(tuple, new Conn(weight)));
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
