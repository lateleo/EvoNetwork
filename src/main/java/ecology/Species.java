package ecology;

import data.MnistImage;

/*
 * This class is purely a variable dump. It doesn't do anything, except hold various parameters
 * that we may want to tweak between simulations. These parameters will be defined when the simulation
 * first runs, and will be referenced here and there by various classes that need them.
 */
public abstract class Species {
	public static MnistImage[] images;
	public static int bottomNodes;
	public static int topNodes;
	public static int chromosomeCount;
	public static double mutationRate;
	public static double slipChance;
	public static int startingLayers;
	public static int startingNodesPerLayer;
	public static int startingConns;
	public static int populationSize;
	public static int prepGenerations;

}
