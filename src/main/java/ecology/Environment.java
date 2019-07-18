package ecology;

public class Environment {

	public static void main(String[] args) {
		System.out.println("Start");
		
		double mRate = 0.1;
		double slip = 1;
		int diploidNum = 10;
		int layers = 3;
		int nodes = 25;
		int conns = 800;
		int signBits = 5;
		int fams = 200;
		int popSize = 20;
		int simGens = 5;
		
		Species.initialize("fashion");
		Population pop = Species.createPopulation(mRate, slip, diploidNum, layers, nodes, conns, signBits, fams, popSize, simGens);
		System.out.println("Simulation First Few Generations...");
		pop.simulateGenerations();
		System.out.println("Generating Networks...");
		pop.iterate(0.7);
		System.out.println("Done.");
	}

}
