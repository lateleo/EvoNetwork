package genetics;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import ecology.Species;
import utils.RNG;

public class Genome extends ArrayList<HomologPair> {
	private static final long serialVersionUID = -5681670623380224763L;  //This is just because ArrayLists are Serializable
	
	public Genome(List<Chromosome> listA, List<Chromosome> listB) {
		Comparator<Chromosome> sorter = (x, y) -> x.chromNum() - y.chromNum();
		listA.sort(sorter);
		listB.sort(sorter);
		for (int i = 0; i < listA.size(); i++) this.add(new HomologPair(listA.get(i), listB.get(i)));
	}
	
	public Genome(List<HomologPair> pairs) {
		this.addAll(pairs);
	}
	
	public Genome copy() {
		List<HomologPair> copies = new ArrayList<HomologPair>();
		for (HomologPair pair : this) copies.add(pair.copy());
		return new Genome(copies);
	}

	
	public List<Chromosome> getHaploidSet() {
		List<Chromosome> haploidSet = new ArrayList<Chromosome>();
		for (HomologPair pair : this) haploidSet.add(pair.getRecombinant());
		return haploidSet;
	}
	
	
//	IMPORTANT: Use this instead of calling the Transcriptome constructor directly.
	public Transcriptome trasncribe() {
		return new Transcriptome(this);
	}

}
