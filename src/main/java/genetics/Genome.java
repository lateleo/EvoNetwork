package genetics;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import network.Organism;

/*
 *A Child Class of ArrayList that represents the entire genome of a single organism. 
 */
public class Genome extends ArrayList<HomologPair> {
	private static final long serialVersionUID = -1923510581355158985L;
	
	private Organism org;
	private Transcriptome transcriptome;

	/*
	 * Constructor that takes two haploid sets (in the form of Map objects) as inputs.
	 * This is the Constructor used regularly during replication.
	 */
	public Genome(HaploidSet mapA, HaploidSet mapB, Organism org) {
		this.org = org;
		mapA.keySet().forEach(key -> {
			add(new HomologPair(mapA.get(key), mapB.get(key)));
		});
	}
	/*
	 * Constructor that takes a list of homologs as inputs, only to be used in initial genome creation and genome copying.
	 */
	public Genome(List<HomologPair> pairs) {
		addAll(pairs);
	}
	
	public void setOrg(Organism org) {
		if (this.org == null) this.org = org;
	}
	
	/*
	 * Creates a semi-deep copy of itself, creating duplicate HomologPair and Chromosome objects, but with references
	 * to the same gene objects to save on memory. Not currently used, but maintained for future use if needed.
	 */
	public Genome copy() {
		List<HomologPair> copies = new ArrayList<HomologPair>();
		for (HomologPair pair : this) copies.add(pair.copy());
		return new Genome(copies);
	}

	/*
	 * Used in reproduction to create a haploid set, consisting of recombinant chromosomes from each homolog pair.
	 */
	public HaploidSet getHaploidSet(boolean forcedMutation) {
		HaploidSet haploidSet = new HaploidSet();
		for (HomologPair pair : this) {
			haploidSet.put(pair.chromNum, pair.getRecombinant(forcedMutation, org));
		}
		return haploidSet;
	}
	
	/*
	 * Overrides the ArrayList.size() method, to instead give the number of genes in the genome.
	 */
	public int size() {
		return stream().mapToInt(pair -> pair.size()).sum();
	}


	public Transcriptome transcriptome() {
		if (transcriptome == null) transcriptome = new Transcriptome(this);
		return transcriptome;
	}

}
