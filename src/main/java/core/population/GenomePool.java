package core.population;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import core.genes.GeneInt;
import core.genes.Genome;
import core.genes.iGene;

public class GenomePool {

	private List<PopulationGene> mGenomeList = new ArrayList<PopulationGene>();
	private HashMap<Integer, Integer> mHungerValueList = new HashMap<Integer, Integer>();;

	public void appenGenome(Genome pGenome) {
		for (iGene iGene : pGenome.getGeneCollection()) {
			PopulationGene lNewPopulationGene = new PopulationGene(iGene);
			addOrIncrease(lNewPopulationGene);
		}
		addHungerValueList(pGenome.getHunger());
	}

	public void addOrIncrease(PopulationGene lNewPopulationGene) {
		// TODO 0 Fix
		if (lNewPopulationGene.mSequenceIndex > 16)
			return;
		for (PopulationGene iPopGene : mGenomeList) {
			if (iPopGene.equals(lNewPopulationGene)) {
				iPopGene.increaseHostCounter();
				return;
			}
		}
		mGenomeList.add(lNewPopulationGene);
	}

	public List<PopulationGene> getGenes() {
		return mGenomeList;
	}

	public List<PopulationGene> getGenesSortedByRank() {
		mGenomeList.sort(new Comparator<PopulationGene>() {
			public int compare(PopulationGene pO1, PopulationGene pO2) {
				return pO2.getHostCounter() - pO1.getHostCounter();
			}
		});
		return mGenomeList;
	}

	// TODO 2 Test HungerList
	public void addHungerValueList(GeneInt pHungerGene) { 
		int lHungerValue = pHungerGene.getValue();
		if (mHungerValueList.containsKey(lHungerValue)) {
			mHungerValueList.replace(lHungerValue, getHungerValueList().get(lHungerValue) + 1);
		} else {
			mHungerValueList.put(lHungerValue, 1);
		}
	}

	public HashMap<Integer, Integer> getHungerValueList() {
		return mHungerValueList;
	}

}
