package genetics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class Genome extends ArrayList<HomologPair> {
	private static final long serialVersionUID = -5681670623380224763L;  //This is just because ArrayLists are Serializable
	
	public Genome(Map<Integer,Chromosome> mapA, Map<Integer,Chromosome> mapB) {
		for (int i = 0; i < mapA.size(); i++) this.add(new HomologPair(mapA.get(i), mapB.get(i)));
	}
	
	public Genome(List<HomologPair> pairs) {
		this.addAll(pairs);
	}
	
	public Genome copy() {
		List<HomologPair> copies = new ArrayList<HomologPair>();
		for (HomologPair pair : this) copies.add(pair.copy());
		return new Genome(copies);
	}

	
	public Map<Integer,Chromosome> getHaploidSet() {
		Map<Integer,Chromosome> haploidSet = new Hashtable<Integer,Chromosome>();
		for (HomologPair pair : this) haploidSet.put(pair.chromNum, pair.getRecombinant());
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
