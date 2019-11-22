package core.genes;

import java.util.concurrent.ThreadLocalRandom;

public class GeneInt implements iGene {

	private int pMax;
	private int pMin;
	private int mValue;

	public void mutate() {
		int lRnd = ThreadLocalRandom.current().nextInt(pMin, pMax + 1);
		mValue = lRnd;
	}

	public GeneInt(int pPMin, int pPMax, int pStartValue) {
		super();
		pMax = pPMax;
		pMin = pPMin;
		mValue = pStartValue;
	}

	public int getValue() {
		return mValue;
	}

	public void setValue(int pInt) {
		mValue = pInt;
	}

	@Override
	public iGene clone() throws CloneNotSupportedException {
		return new GeneInt(pMin, pMax, mValue);
	}

}
