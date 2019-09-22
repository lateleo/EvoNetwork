package utils;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Predicate;

import org.apache.commons.collections4.CollectionUtils;

import data.MnistDataReader;
import data.MnistImage;
import ecology.Population;
import ecology.Species;
import genetics.Gene;
import network.BottomLayer;
import network.Layer;
import network.NeuralNetwork;

/*
 * This class is used solely as a sandbox to test the functionality of various parts of the application.
 * Ultimately, this will probably be replaced with a proper testing framework, but this will be used for the time being.
 */
@SuppressWarnings("unused")
public class FunctionalityTester {

	public static void main(String[] args) throws IOException {
		System.out.println("Start");
		
		double mRate = 0.05;
		double mMag = 1.25;
		double slip = 2;
		double lRate = 0.01;
		int haploidNum = 10;
		int layers = 2;
		int nodes = 25;
		int conns = 200;
		int popSize = 1;
		int simGens = 20;
		int batchSize = 100;
		double targetAccuracy = 0.9;
		
		Species.initialize("original", batchSize);
		Population pop = Species.createPopulation(mRate, mMag, slip, lRate, haploidNum, layers, nodes, conns, popSize, simGens);

		pop.runGeneration();
		pop.getMeanAccuracy();
		
	}



}
