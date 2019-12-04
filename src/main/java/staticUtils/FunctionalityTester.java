package staticUtils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.math3.stat.regression.SimpleRegression;

import data.MnistDataReader;
import data.MnistImage;
import ecology.Population;
import ecology.Species;
import genetics.Gene;
import network.BottomLayer;
import network.MidLayer;
import network.NeuralNetwork;

/*
 * This class is used solely as a sandbox to test the functionality of various parts of the application.
 * Ultimately, this will probably be replaced with a proper testing framework, but this will be used for the time being.
 */
@SuppressWarnings("unused")
public class FunctionalityTester {
	static DecimalFormat f1 = new DecimalFormat("#,##0.0###");
	static DecimalFormat f2 = new DecimalFormat("#,##0.00#");


	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		ExecutorService exec = Executors.newSingleThreadExecutor();
		Callable<Integer> caller = ()-> {
			TimeUnit.SECONDS.sleep(1);
			return 0;
		};
		FutureTask<Integer> task = new FutureTask<>(caller);
		System.out.println("Start");
		exec.execute(task);
		System.out.println("Ran");
		int result = task.get();
		System.out.println("Done");
		exec.execute(task);
		System.out.println("Ran");
		result = task.get();
		System.out.println("Done");
		
		
	}
	
	
	public static void newRands() {
		System.out.println("Start...");
		int count = 5000;
		
		List<List<Double>> allRands = new ArrayList<>(count);
		long start1 = System.nanoTime();
		for (int i = 0; i < count; i++) {
			List<Double> rands = new ArrayList<Double>(count);
			for (int j = 0; j < count; j++) rands.add(RNG.getFauxGauss());
			allRands.add(rands);
		}
		long end1 = System.nanoTime();
		
		List<List<Double>> allShifts = new ArrayList<>(count);
		long start2 = System.nanoTime();
		for (int i = 0; i < count; i++) {
			List<Double> rands = new ArrayList<Double>(count);
			for (int j = 0; j < count; j++) rands.add(RNG.getShiftDouble());
			allShifts.add(rands);
		}
		long end2 = System.nanoTime();
		
		List<List<Double>> allGauss = new ArrayList<>(count);
		long start3 = System.nanoTime();
		for (int i = 0; i < count; i++) {
			List<Double> rands = new ArrayList<Double>(count);
			for (int j = 0; j < count; j++) rands.add(RNG.getGauss());
			allGauss.add(rands);
		}
		long end3 = System.nanoTime();
		
		double newLong = (end1 - start1)/1e9;
		double newShift = (end2 - start2)/1e9;
		double gauss = (end3 - start3)/1e9;
		
		System.out.println("NewLong: " + newLong);
		System.out.println("NewShift: " + newShift);
		System.out.println("Gauss: " + gauss);
		
		double longMean = 0;
		double longSigma = 0;
		for (List<Double> rands : allRands) {
			for (Double rand : rands) {
				longMean += rand;
				longSigma += rand*rand;
			}
		}
		longMean /= count*count;
		longSigma = Math.sqrt(longSigma/(count*count) - longMean*longMean);
		double shiftMean = 0;
		double shiftSigma = 0;
		for (List<Double> rands : allShifts) {
			for (Double rand : rands) {
				shiftMean += rand;
				shiftSigma += rand*rand;
			}
		}
		shiftMean /= count*count;
		shiftSigma = Math.sqrt(shiftSigma/(count*count) - shiftMean*shiftMean);
		double gaussMean = 0;
		double gaussSigma = 0;
		for (List<Double> rands : allGauss) {
			for (Double rand : rands) {
				gaussMean += rand;
				gaussSigma += rand*rand;
			}
		}
		gaussMean /= count*count;
		gaussSigma = Math.sqrt(gaussSigma/(count*count) - gaussMean*gaussMean);
		
		System.out.println("longMean: " + longMean);
		System.out.println("longSigma: " + longSigma);
		System.out.println("shiftMean: " + shiftMean);
		System.out.println("shiftSigma: " + shiftSigma);
		System.out.println("gaussMean: " + gaussMean);
		System.out.println("gaussSigma: " + gaussSigma);
	}
	

	

}
