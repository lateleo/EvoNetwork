package genetics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import staticUtils.RNG;

public class HaploidSet extends TreeMap<Integer,Chromosome> {
	private static final long serialVersionUID = 3763122064518512584L;
		
	public HaploidSet() {
		super();
	}
	
	public HaploidSet(int haploidNum) {
		super();
		for (int i = 1; i <= haploidNum; i++) put(i, new Chromosome(i));
	}
	
	public HaploidSet copy() {
		HaploidSet copy = new HaploidSet();
		for (Map.Entry<Integer, Chromosome> entry : entrySet()) {
			copy.put(entry.getKey(), entry.getValue().copy());
		}
		return copy;
	}
	
//	public void add(Gene gene) {
//		List<Chromosome> chroms = new ArrayList<>(values());
//	}
	
	public void insertAll(List<Gene> genes) {
		for (Gene gene : genes) get(RNG.sampleSet(keySet())).insert(gene);
	}

}
