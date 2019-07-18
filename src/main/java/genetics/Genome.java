package genetics;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Genome extends ArrayList<HomologPair> {
	private static final long serialVersionUID = -1923510581355158985L;


	public Genome(Map<Integer,Chromosome> mapA, Map<Integer,Chromosome> mapB) {
		mapA.keySet().forEach(key -> {
			add(new HomologPair(mapA.get(key), mapB.get(key)));
		});
	}
	
	public Genome(List<HomologPair> pairs) {
		addAll(pairs);
	}
	
	public Genome copy() {
		List<HomologPair> copies = new ArrayList<HomologPair>();
		for (HomologPair pair : this) copies.add(pair.copy());
		return new Genome(copies);
	}

	
	public Map<Integer,Chromosome> getHaploidSet() {
		Map<Integer,Chromosome> haploidSet = new Hashtable<Integer,Chromosome>();
		for (HomologPair pair : this) {
			haploidSet.put(pair.chromNum, pair.getRecombinant());
		}
		return haploidSet;
	}
	
	public int size() {
		return stream().mapToInt(pair -> pair.size()).sum();
	}

//	IMPORTANT: Use this instead of calling the Transcriptome constructor directly.
	public Transcriptome trasncribe() {
		return new Transcriptome(this);
	}

}
