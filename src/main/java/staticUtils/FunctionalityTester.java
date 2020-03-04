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
import java.util.concurrent.ThreadLocalRandom;
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
import utils.NodeVector;

/*
 * This class is used solely as a sandbox to test the functionality of various parts of the application.
 * Ultimately, this will probably be replaced with a proper testing framework, but this will be used for the time being.
 */
@SuppressWarnings("unused")
public class FunctionalityTester {
	static DecimalFormat f1 = new DecimalFormat("#,##0.0###");
	static DecimalFormat f2 = new DecimalFormat("#,##0.00#");


	public static void main(String[] args) throws InterruptedException, ExecutionException {
		int i = 0;
		while (true) {
			System.out.println(i);
			i++;
		}

	}
	
	public static boolean check(int val) {
		return val < 1024;
	}
	
	
	

	

	

}
