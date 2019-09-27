package ecology;

public class Environment {

	public static void main(String[] args) {
		System.out.println("Start");
		
		double mRate = 0.05;
		double mMag = 1.25;
		double slip = 2;
		double lRate = 0.01;
		int haploidNum = 10;
		int layers = 3;
		int nodes = 25;
		int conns = 10;
		int topRedundancy = 5;
		int bottomRedundancy = 3;
		int popSize = 20;
		int simGens = 40;
		int batchSize = 500;
		double targetAccuracy = 0.9;
		
		long startTime = System.nanoTime();
		Species.initialize("original", batchSize);
		Population pop = Species.createPopulation(mRate,  mMag,  slip,  lRate,  haploidNum,  layers,  nodes, conns,
				topRedundancy, bottomRedundancy, popSize,  simGens);
		System.out.println("Simulating First Few Generations...");
		pop.simulateGenerations();
		pop.iterate(targetAccuracy);
		
		long endTime = System.nanoTime();
		long seconds = (endTime - startTime)/1000000000;
		int minutes = 0;
		int hours = 0;
		while (seconds > 60) {
			minutes++;
			seconds -= 60;
		}
		while (minutes > 60) {
			hours++;
			minutes -= 60;
		}
		System.out.println("Training Done: " + hours + "h" + minutes + "m" + seconds + "s");
//		pop.testGeneration();
		System.out.println("Done.");
	}

}
