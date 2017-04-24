import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class FieldItem implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1219829686281456494L;
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
