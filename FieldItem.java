import java.util.ArrayList;
import java.util.HashMap;

/**
 * A fieldItem is a single cell in a sudoku game. Every cell calculates its score and its possible range of values
 * itself. In addition, a cell knows its neighboring cells by a pointer array, and can notify those cells to perform
 * an action.
 * @author jeromeoesch
 * @version 1.0
 */

public class FieldItem {

	private float SCORE_FOUND_FREQ;
	private boolean preDefined;
	
	private int value;
	public int posX;
	public int posY;
	public ArrayList<Integer> valueRange;
	public float score;
	public boolean valueFound;
	public FieldItem[] squareMembers;
	public FieldItem[] lineMembersX;
	public FieldItem[] lineMembersY;
	public HashMap<Integer, Integer> frequencyScore;
	
	private int dimSquare;
	private int dimField;

	/**
	 * Constructor.
	 * @param _posX: Coordinate X of cell in sudoku game
	 * @param _posY: Coordinate Y of cell in sudoku game
	 * @param _preDefined: Whether the value is known from the beginning of the game
	 * @param _correct: True if cell approved to be correct (can be revoked)
	 * @param _value: The value of the cell
	 * @param _dimSquare: The dimension of the elements in a square (=dimension^2)
	 */
	FieldItem(int _posX, int _posY, boolean _preDefined, boolean _correct, int _value, int _dimSquare) {
		posX = _posX;
		posY = _posY;
		preDefined = _preDefined;
		valueFound = preDefined ? true : false;
		dimSquare = _dimSquare;
		dimField = dimSquare*dimSquare;
		value = _value;
		SCORE_FOUND_FREQ = dimField + 1;
		squareMembers = new FieldItem[_dimSquare*_dimSquare];
		lineMembersX = new FieldItem[_dimSquare*_dimSquare];
		lineMembersY = new FieldItem[_dimSquare*_dimSquare];
		frequencyScore = new HashMap<Integer, Integer>();

	}
	
	/**
	 * Copy Constructor of FieldItem
	 * @param oldFieldItem: the old fieldItem to be copied.
	 */
	@SuppressWarnings("unchecked")
	FieldItem(FieldItem oldFieldItem) {
		this.posX = oldFieldItem.posX;
		this.posY = oldFieldItem.posY;
		this.preDefined = oldFieldItem.preDefined;
		this.valueFound = oldFieldItem.valueFound;
		this.dimSquare = oldFieldItem.dimSquare;
		this.dimField = oldFieldItem.dimField;
		this.value = oldFieldItem.value;
		this.SCORE_FOUND_FREQ = oldFieldItem.SCORE_FOUND_FREQ;
		this.score = oldFieldItem.score;
		this.valueRange = (ArrayList<Integer>) oldFieldItem.valueRange.clone();
		squareMembers = new FieldItem[this.dimField];
		lineMembersX = new FieldItem[this.dimField];
		lineMembersY = new FieldItem[this.dimField];
		frequencyScore = new HashMap<Integer, Integer>();
	}

	/**
	 * Calculates the range of possible values that can still be set for a certain cell.
	 */
	public void calculateValueRange() {
		valueRange = new ArrayList<Integer>();
		if (valueFound) {
			valueRange.add(value);
		} else {
			for (int i = 1; i < dimField+1; i++) {
				valueRange.add(new Integer(i));
			}
			for (int i = 0; i < dimField; i++) {
				if (valueRange.contains(lineMembersX[i].value)) {
					valueRange.remove(new Integer(lineMembersX[i].value));
				}
				if (valueRange.contains(lineMembersY[i].value)) {
					valueRange.remove(new Integer(lineMembersY[i].value));
				}
				if (valueRange.contains(squareMembers[i].value)) {
					valueRange.remove(new Integer(squareMembers[i].value));
				}
			}
		}
	}
	
	/**
	 * Calculates a score for every cell. Fielditem has a score based on how many different elements can be placed 
	 * in one cell (=valueRange), as well as how many members of a number are still missing in the complete field.
	 * The smaller the valueRange of a cell, and the more elements of a certain number have been detected, the
	 * higher the score.
	 */
	public void calculateScore() {
		score = 0;
		if (valueFound) {
			score = SCORE_FOUND_FREQ;
		} else {
			for (int i = 0; i < valueRange.size(); i++) {
				score += 1.f*frequencyScore.get(valueRange.get(i))/valueRange.size();
			}
		}
	}

	/**
	 * Is called when a neighboring cell updates its value. It causes the adjacient cells to recalculate their
	 * valueRange (possible set of values to set into cell), as well as their score.
	 */
	public void notifyAdjacentCells() {
		for (int i = 0; i < dimField; i++) {
			if (lineMembersX[i] != this && !lineMembersX[i].valueFound) {
				lineMembersX[i].calculateValueRange();
				lineMembersX[i].calculateScore();
			}
			if (lineMembersY[i] != this && !lineMembersY[i].valueFound) {
				lineMembersY[i].calculateValueRange();
				lineMembersY[i].calculateScore();
			}
			//Prevent scoring items that have been processed above.
			int startx = Math.round(this.posX/dimSquare)*dimSquare;
			int endx = startx+dimSquare;
			int starty = Math.round(this.posY/dimSquare)*dimSquare;
			int endy = starty+dimSquare;
			if (!((squareMembers[i].posX > startx && squareMembers[i].posX < endx) &&
					(squareMembers[i].posY > starty && squareMembers[i].posY < endy)) && 
					!squareMembers[i].valueFound) {
				squareMembers[i].calculateValueRange();
				squareMembers[i].calculateScore();
			}
		}
	}
	
	/**
	 * Used to set the value of a cell.
	 * @param _value: The value to be set for a cell.
	 */
	public void setValue(int _value) {
		if (valueRange.contains(new Integer(_value))) {
			value = _value;
			valueFound = true;
		}
	}
	
	public void setFrequencyScore(HashMap<Integer, Integer> _frequencyScore) {
		frequencyScore= _frequencyScore;
	}
	
	public int getValue() {
		return value;
	}
}
