package ecology;

public class Environment {

	public static void main(String[] args) {
		System.out.println("Start");
		
		double mRate = 0.05;
		double mMag = 1.25;
		double slip = 2;
		int diploidNum = 10;
		int layers = 3;
		int nodes = 25;
		int conns = 800;
		int signBits = 5;
		int fams = 0;
		int popSize = 10;
		int simGens = 20;
		int epochs = 6;
		double targetAccuracy = 0.9;
		
		long startTime = System.nanoTime();
		Species.initialize("fashion", epochs);
		Population pop = Species.createPopulation(mRate, mMag, slip, diploidNum, layers, nodes, conns, signBits, fams, popSize, simGens);
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
		System.out.println("Done.");
		System.out.println(hours + "h" + minutes + "m" + seconds + "s");
	}

}
