package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class RNG {

	
	public static double getShiftDouble() {
		return ThreadLocalRandom.current().nextDouble(-1.0, Math.nextUp(1.0));
	}
	
	public static double getDouble() {
		return ThreadLocalRandom.current().nextDouble();
	}
	
	public static double getPsuedoGauss(double bound) {
		return (getShiftDouble()+getShiftDouble()+getShiftDouble()+getShiftDouble())*bound/4.0;
	}
	
	public static double getBoundGauss(double min, double max, double sigma) {
		double output;
		do {
			output = ThreadLocalRandom.current().nextGaussian()/sigma;
		} while (output > max || output < min);
		return output;
	}
	
	public static int getLongBit() {
		return ThreadLocalRandom.current().nextInt(64);
	}
	
	public static int getIntRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max+1);
	}
	
	public static int getIntMax(int max) {
		return ThreadLocalRandom.current().nextInt(max+1);
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
}
