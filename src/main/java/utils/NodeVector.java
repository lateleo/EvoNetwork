package utils;

import java.util.ArrayList;
import java.util.List;

import ecology.Species;
import staticUtils.ComparisonUtils;

public class NodeVector implements Comparable<NodeVector> {
	private static int topNodes = Species.topNodes;
	private static int bottomWidth = Species.bottomWidth;
	public static List<NodeVector> bottomVectors = setBottomVectors();
	public static List<NodeVector> unitVectors = setUnitVectors();
	private double x, y;
	
	private static List<NodeVector> setBottomVectors() {
		List<NodeVector> bottomVectors = new ArrayList<>();
		int start = bottomWidth/2;
		int stop = bottomWidth - start;
		for (int x = start; x < stop; x++) for (int y = start; y < stop; y++) bottomVectors.add(new NodeVector(x,y));
		return bottomVectors;
	}
	
	private static List<NodeVector> setUnitVectors() {
		List<NodeVector> unitVectors = new ArrayList<>();
		for (int i = 0; i < topNodes; i++) {
			double theta = 2*i*Math.PI/topNodes;
			unitVectors.add(new NodeVector(Math.cos(theta), Math.sin(theta)));
		}
		return unitVectors;
	}
		
	public static NodeVector fromMagTheta(double mag, double theta) {
		return new NodeVector(mag*Math.cos(theta), mag*Math.sin(theta));
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
		double theta = phi + getTheta();
		x = Math.cos(theta);
		y = Math.sin(theta);
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
		return new NodeVector(Math.cos(qTheta), Math.sin(qTheta));
	}
	
	public NodeVector snapToGrid() {
		return new NodeVector(round(x),round(y));
	}
	
	public NodeVector clone() {
		return new NodeVector(x,y);
	}
	
	private int round(double i) {
		return (int)((i % 1 == 0.5) ? Math.ceil(i) : Math.round(i));
	}
	
	public void add(double mag, double theta) {
		x += mag*Math.cos(theta);
		y += mag*Math.sin(theta);
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
