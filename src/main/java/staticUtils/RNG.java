package staticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class RNG {

	
	public static double getShiftDouble() {
		double out = ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		return out/3.0;
	}
	
	public static double getDouble() {
		return ThreadLocalRandom.current().nextDouble(0.0, Math.nextUp(1.0));
	}
	
	public static double getExclusiveDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public static double getPsuedoGauss(double bound) {
		return (getShiftDouble()+getShiftDouble()+getShiftDouble()+getShiftDouble())*bound/4.0;
	}
	
	public static double getGauss() {
		return ThreadLocalRandom.current().nextGaussian();
	}
	
	public static double getGauss(double sigma, double mean) {
		return ThreadLocalRandom.current().nextGaussian()*sigma + mean;
	}
	
	public static double getBoundGauss(double min, double max, double sigma, double mean) {
		double output;
		do {
			output = getGauss(sigma, mean);
		} while (output > max || output < min);
		return output;		
	}
	
	public static double getBoundGauss(double min, double max, double sigma) {
		return getBoundGauss(min, max, sigma, 0.0);
	}
	
	public static double getMinGauss(double min, double sigma, double mean) {
		return getBoundGauss(min, Double.POSITIVE_INFINITY, sigma, mean);
	}
	
	public static double getMinGauss(double min, double sigma) {
		return getMinGauss(min, sigma, 0.0);
	}
	
	public static double getMaxGauss(double max, double sigma, double mean) {
		return getBoundGauss(Double.NEGATIVE_INFINITY, max, sigma, mean);
	}
	
	public static double getMaxGauss(double max, double sigma) {
		return getMaxGauss(max, sigma, 0.0);
	}
	
	public static double getHalfGauss(double sigma) {
		return Math.abs(getGauss(sigma, 0.0));
	}
	
	public static double getHalfGauss() {
		return getHalfGauss(1);
	}
	
	public static int getBit() {
		return ThreadLocalRandom.current().nextInt(32);
	}
	
	public static int getIntRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max+1);
	}
	
	public static int getIntMax(int max) {
		return ThreadLocalRandom.current().nextInt(max);
	}
	
	public static int getIntLowBias(int max) {
		int a = ThreadLocalRandom.current().nextInt(max);
		int b = ThreadLocalRandom.current().nextInt(max);
		return Math.min(a, b);
	}
	
	public static int getIntLowBias(int min, int max) {
		int a = ThreadLocalRandom.current().nextInt(min, max);
		int b = ThreadLocalRandom.current().nextInt(min, max);
		return Math.min(a, b);
	}
	
	
	
	public static boolean getBoolean() {
		return ThreadLocalRandom.current().nextBoolean();
	}
	
	public static <E> List<E> getSample(List<E> population, int sampleSize) throws IndexOutOfBoundsException {
		if (sampleSize < population.size()) {
			List<E> output = new ArrayList<E>();
			Set<Integer> indices = new HashSet<Integer>();
			while (indices.size() < sampleSize) indices.add(ThreadLocalRandom.current().nextInt(population.size()));
			for (Integer index : indices) output.add(population.get(index));
			return output;
		} else if (sampleSize == population.size()) {
			return population;
		} else {
			throw new IndexOutOfBoundsException();
		}
		
	}
	
	public static <E> E sampleSet(Set<E> set) {
		return new ArrayList<E>(set).get(getIntMax(set.size()));
	}
	
	
	
}
