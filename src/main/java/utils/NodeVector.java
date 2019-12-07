package utils;

import staticUtils.ComparisonUtils;

public class NodeVector implements Comparable<NodeVector> {
	private double x, y;
	
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
		return (round(x) == round(other.x) && round(y) == round(other.y));
	}


}
