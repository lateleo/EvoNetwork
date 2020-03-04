package utils;

import java.util.ArrayList;
import java.util.List;

import ecology.Species;
import genetics.ConnGene;
import genetics.NodeGene;
import staticUtils.ComparisonUtils;
import staticUtils.MathUtils;

public class NodeVector implements Comparable<NodeVector> {
	private static int topNodes = Species.topNodes;
	private static int minBottom = Species.minBottom;
	private static int maxBottom = Species.maxBottom;
	public static List<NodeVector> bottomVectors = setBottomVectors();
	public static List<NodeVector> unitVectors = setUnitVectors();
	private double x, y;
	
	private static List<NodeVector> setBottomVectors() {
		List<NodeVector> bottomVectors = new ArrayList<>();
		for (int x = minBottom; x < maxBottom; x++) {
			for (int y = minBottom; y < maxBottom; y++) {
				bottomVectors.add(new NodeVector(x,y));
			}
		}
		return bottomVectors;
	}
	
	private static List<NodeVector> setUnitVectors() {
		List<NodeVector> unitVectors = new ArrayList<>();
		double halfTop = topNodes/2.0;
		for (int i = 0; i < topNodes; i++) {
			double theta = (i/halfTop)*Math.PI;
			unitVectors.add(new NodeVector(MathUtils.cos(theta), MathUtils.sin(theta)));
		}
		return unitVectors;
	}
		
	public static NodeVector fromMagTheta(double mag, double theta) {
		return new NodeVector(mag*MathUtils.cos(theta), mag*MathUtils.sin(theta));
	}
	
	public static NodeVector fromNodeGene(NodeGene gene) {
		return fromLayXY((int) Math.floor(gene.layerNum), gene.nodeX, gene.nodeY);
	}
	
	public static NodeVector inFromConnGene(ConnGene gene) {
		return fromLayXY((int) Math.floor(gene.inLayNum), gene.inNodeX, gene.inNodeY);
	}
	
	public static NodeVector outFromConnGene(ConnGene gene) {
		return fromLayXY((int) Math.floor(gene.outLayNum), gene.outNodeX, gene.outNodeY);
	}
	
	public static NodeVector fromLayXY(int layNum, double geneX, double geneY) {
		double x, y;
		if (layNum != -1) {
			x = round(geneX);
			y = round(geneY);
		} else {
			double theta = Math.atan2(geneY, geneX);
			if (theta < 0) theta += 2*Math.PI;
			double floor = Math.floor(theta*topNodes/(2*Math.PI));
			double qTheta = floor*2*Math.PI/topNodes;
			x = MathUtils.cos(qTheta);
			y = MathUtils.sin(qTheta);
		}
		return new NodeVector(x,y);
	}
	
	public NodeVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
		
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getMagnitude() {
		return Math.sqrt(x*x + y*y);
	}
	
	public double getTheta() {
		double theta = Math.atan2(y, x);
		return (theta < 0) ? theta + 2*Math.PI : theta;
	}
	
	public NodeVector addTheta(double phi) {
		double magnitude = getMagnitude();
		double theta = phi + getTheta();
		x = magnitude*MathUtils.cos(theta);
		y = magnitude*MathUtils.sin(theta);
		return this;
	}
	
	public NodeVector addMagnitude(double mag) {
		x *= mag;
		y *= mag;
		return this;
	}
	
	public int getThetaFloor() {
		return (int)(getTheta()*topNodes/(2*Math.PI));
	}
	
	public NodeVector getUnitVector() {
		double qTheta = 2*getThetaFloor()*Math.PI/topNodes;
		return new NodeVector(MathUtils.cos(qTheta), MathUtils.sin(qTheta));
	}
	
//	public NodeVector snapToGrid() {
//		return new NodeVector(round(x),round(y));
//	}
	
	public NodeVector clone() {
		return new NodeVector(x,y);
	}
	
	private static int round(double i) {
		return (int)((i % 1 == 0.5) ? Math.ceil(i) : Math.round(i));
	}
	
	public int compareTo(NodeVector other) {
		return ComparisonUtils.compareNodeVectors(this, other);
	}
	
	public boolean equals(NodeVector other) {
		return (x == other.x && y == other.y);
	}
	
	public String toString() {
		return "(" + x + ", " + y + ")";
	}


}
