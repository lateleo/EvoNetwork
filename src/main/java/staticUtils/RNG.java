package staticUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class RNG {

	
	public static double getShiftDouble(double scalar) {
		double out = ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		return scalar*out/3.0;
	}
	
	public static double getShiftDouble() {
		double out = ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		out += ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
		return out;
	}
	
	public static long getAnyLong() {
		return ThreadLocalRandom.current().nextLong(Long.MIN_VALUE+1, Long.MAX_VALUE);
	}
	
	public static double getFauxGauss() {
		double out = getAnyLong();
		out += getAnyLong();
		out += getAnyLong();
		return out/Long.MAX_VALUE;
	}
	
	public static double getDouble() {
		return ThreadLocalRandom.current().nextDouble(0.0, Math.nextUp(1.0));
	}
	
	public static double getExclusiveDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public static double getGauss() {
		return ThreadLocalRandom.current().nextGaussian();
	}
	
	public static double getGauss(double sigma) {
		return getGauss()*sigma;
	}
	
	public static double getGauss(double mean, double sigma) {
		return getGauss(sigma) + mean;
	}
	
	public static double getBoundGauss(double min, double max, double mean, double sigma) {
		double output;
		do {
			output = getGauss(mean, sigma);
		} while (output > max || output < min);
		return output;		
	}
	
	public static double getBoundGauss(double min, double max, double sigma) {
		return getBoundGauss(min, max, 0.0, sigma);
	}
	
	public static double getMinGauss(double min, double mean, double sigma) {
		return getBoundGauss(min, Double.POSITIVE_INFINITY, mean, sigma);
	}
	
	public static double getMinGauss(double min, double sigma) {
		return getMinGauss(min, 0.0, sigma);
	}
	
	public static double getMaxGauss(double max, double mean, double sigma) {
		return getBoundGauss(Double.NEGATIVE_INFINITY, max, mean, sigma);
	}
	
	public static double getMaxGauss(double max, double sigma) {
		return getMaxGauss(max, 0.0, sigma);
	}
	
	public static double getHalfGauss(double sigma) {
		return Math.abs(getGauss(0.0, sigma));
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
			while (indices.size() < sampleSize) indices.add(getIntMax(population.size()));
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
