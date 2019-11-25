package core.soup.idvm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import core.datatypes.Decisions;
import core.datatypes.Direction;
import core.datatypes.Pos;
import core.exceptions.PosIsOutOfGrid;
import core.genes.Genome;
import core.genes.MoveDecisionsProbability;
import core.soup.block.Block;
import core.soup.block.BlockGrid;
import core.soup.block.BlockType;
import core.soup.block.Enemy;
import core.soup.block.Food;
import core.soup.block.IdvmCell;
import core.soup.block.iBlock;
import globals.Config;

public class Idvm extends Block implements iIdvm {
	Genome mGenomeOrigin;
	Genome mGenomeUsing;
	private IdvmCellGrid mCellGrid = new IdvmCellGrid();
	private HashMap<IdvmState, ArrayList<MoveDecisionsProbability>> mMovementSequences = new HashMap<IdvmState, ArrayList<MoveDecisionsProbability>>();
	private BlockGrid mBlockGrid;
	private int mStepCount;
	private int mEnergy = Config.cMaxEnergy / 3;
	private IdvmMoveCalculation mMoveCalculation;
	private IdvmSensor mIdvmSensor;

	public Idvm(Genome pGenome) {
		super(BlockType.IDVM);
		try {
			mPos = new Pos(0, 0);
			mGenomeOrigin = (Genome) pGenome.clone();
			mGenomeUsing = (Genome) pGenome.clone();

			grow();
			grow();
			grow();
			grow();

			for (IdvmState iState : pGenome.moveSequencesForState.keySet()) {
				@SuppressWarnings("unchecked")
				ArrayList<MoveDecisionsProbability> lMoveProbability = (ArrayList<MoveDecisionsProbability>) pGenome.moveSequencesForState
						.get(iState).clone();
				mMovementSequences.put(iState, lMoveProbability);
			}

		} catch (CloneNotSupportedException e) {
			throw new RuntimeException();
		}
	}

	private void grow() {
		if (mGenomeUsing.cellGrow.size() == 0)
			return;
		IdvmCell lCell = mGenomeUsing.cellGrow.get(0);
		mGenomeUsing.cellGrow.remove(0);
		mCellGrid.appendCell(lCell, mPos);
		popAllSequences();
	}

	public boolean isAlive() {
		if (mEnergy <= 0)
			return false;
		for (iBlock iCell : getUsedBlocks()) {
			if (iCell.getBlockType() == BlockType.LIFE) {
				return true;
			}
		}
		return false;
	}

	public Boolean isHungry() {
		return mEnergy < mGenomeUsing.getHunger().getValue();
	}

	//TODO 4 REF move to cell grid
	public iBlock setPosition(Pos pPos) {
		super.setPosition(pPos);
		for (int x = 0; x <= 3; x++) {
			for (int y = 0; y <= 3; y++) {
				mCellGrid.refreshCellPosOnGrid(x, y, mPos);
			}
		}
		return this;
	}

	// TODO 4 REF move to cell grid
	public ArrayList<iBlock> getUsedBlocks(BlockType pBlockType) {
		ArrayList<iBlock> lBlocks = new ArrayList<iBlock>();
		for (iBlock iBlock : mCellGrid.getGridBlocks())
			if (iBlock.getBlockType() == pBlockType)
				lBlocks.add(iBlock);
		return lBlocks;
	}


	public void step() {
		mStepCount++;
		for (iBlock iCount : getUsedBlocks(BlockType.LIFE)) {
			mEnergy--;
			mEnergy--;
		}
		move();
	}

	// TODO 4 IMPL turn
	@SuppressWarnings("unused")
	private void move() {
		for (iBlock iCount : getUsedBlocks(BlockType.MOVE)) {
			for (int i = 0; i < 10; i++) {
				try {
					Pos lNewPos = mMoveCalculation.getMovingPosition(this, mMovementSequences, mIdvmSensor);
					if (lNewPos != mPos)
						mEnergy--;
					setPosition(lNewPos);
					break;
				} catch (PosIsOutOfGrid e) {
				}
			}
		}
	}

	public void interactWithFood(Food pFood) {
		mEnergy = mEnergy + Config.cFoodEnergy;
		if (mEnergy > Config.cMaxEnergy)
			mEnergy = Config.cMaxEnergy;
		grow();
	}

	private void popAllSequences() {
		for (Entry<IdvmState, ArrayList<MoveDecisionsProbability>> iSequence : mMovementSequences.entrySet()) {
			try {
				iSequence.getValue().remove(0);
			} catch (RuntimeException e) {
				// Empty Sequence
			}
		}
	}

	// TODO 4 IMPL defence
	public void interactWithEnemy(Enemy pEnemy) {
		Pos lKillPos = new Pos(pEnemy.getPos().x - mPos.x + 1, pEnemy.getPos().y - mPos.y + 1);
		mCellGrid.removeCell(lKillPos);
	}

	// TODO 3 IMPL dynamic target order
	// TODO 4 IMPL sensor range
	// TODO 6 IMPL add hunger and blind
	// TODO 6 IMPL idle and blind hunger
	// if (pDetectedPos.size() == 0)
	// return IdvmState.BLIND;
	public IdvmState getState() {
		HashMap<Pos, Sensor> lDetectedPos = getDetectedPos();
		if (mIdvmSensor.detectSurroundingBlockType(BlockType.ENEMY, lDetectedPos))
			// if (pIsHungry) {
			// return IdvmState.ENEMY_HUNGER;
			// } else {
			return IdvmState.ENEMY;
		// }
		if (mIdvmSensor.detectSurroundingBlockType(BlockType.FOOD, lDetectedPos))
			// if (pIsHungry) {
			// return IdvmState.FOOD_HUNGER;
			// } else {
			return IdvmState.FOOD;
		// }
		return IdvmState.IDLE;

	}

	public HashMap<Pos, Sensor> getDetectedPos() {
		ArrayList<iBlock> lSensors = getUsedBlocks(BlockType.SENSOR);
		return mIdvmSensor.getDetectedPos(lSensors);
	}

	public void setBlockGrid(BlockGrid pBlockGrid) {
		mBlockGrid = pBlockGrid;
		mMoveCalculation = new IdvmMoveCalculation(mBlockGrid);
		mIdvmSensor = new IdvmSensor(mBlockGrid);
	}

	public int getStepCount() {
		return mStepCount;
	}

	public Direction getTargetDirection() {
		return mIdvmSensor.getTargetDirection(getState(), getUsedBlocks(BlockType.SENSOR));
	}

	public int getEnergyCount() {
		return mEnergy;
	}

	public Decisions getCalculatedDirection() {
		return mMoveCalculation.getCalculatedDirection();
	}

	public Genome getGenomeOrigin() {
		return mGenomeOrigin;
	}

	// TODO 7 REF delte
	public Double getMutationRate() {
		return mGenomeUsing.mMutationRate.getValue();
	}
	// TODO 3 IMPL cell type connection
	public ArrayList<iBlock> getUsedBlocks() {
		return mCellGrid.getGridBlocks();
	}
}
