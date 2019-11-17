package genes;

import java.util.ArrayList;
import java.util.HashMap;

import soup.block.BlockType;
import soup.idvm.IdvmCell;
import soup.idvm.IdvmState;
import datatypes.Pos;

public class Genome {
	private static final int cMaxSequence = 48;
	private GeneInt mHunger = new GeneInt(0, 100, 50);
	// TODO IMPL make mutation rate a gene
	private Double mMutationRate = 0.1;
	// TODO REF make this private
	public ArrayList<IdvmCell> cellGrow = new ArrayList<IdvmCell>();
	// TODO 1 make this a gene
	public HashMap<IdvmState, ArrayList<MoveProbability>> movementSequences = new HashMap<IdvmState, ArrayList<MoveProbability>>();

	public void forceMutation() {
		for (int i = 0; i < 48; i++) {
			cellGrow.add(new IdvmCell(BlockType.NOTHING, new Pos(0, 0)));
		}
		mutate(1.0);
	}

	public void naturalMutation() {
		mutate(mMutationRate);
	}

	private void mutate(Double pMutationRate) {
		ArrayList<iGene> lGenes;
		lGenes = getGeneCollection();

		for (iGene iGene : lGenes)
			iGene.mutate(pMutationRate);

		setInitialCell(0, 0, 0);
		setInitialCell(1, 0, 1);
		setInitialCell(2, 1, 0);
		setInitialCell(3, 1, 1);
	}

	private void setInitialCell(int pIdx, int pX, int pY) {
		BlockType lCellType = cellGrow.get(pIdx).getBlockType();
		cellGrow.set(pIdx, new IdvmCell(lCellType, new Pos(pX, pY)));
	}

	private ArrayList<iGene> getGeneCollection() {
		ArrayList<iGene> lGenes = new ArrayList<iGene>();
		// TODO 1 add all Genes here
		lGenes.add(mHunger);
		for (iGene iGene : cellGrow) {
			lGenes.add(iGene);
		}
		return lGenes;
	}

	public int getHunger() {
		return mHunger.getValue();
	}

	public void setHunger(int pInt) {
		mHunger.setValue(pInt);
	}
}
