import java.io.Serializable;
import java.util.HashMap;

public class Field implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1402077933342395199L;
	public FieldItem[][] field;
	public FieldItem[] list;
	public int dimField;
	public int dimSquare;
	public float totalScore;
	public HashMap<Integer, Integer> frequencyScore;
	
	@SuppressWarnings("unchecked")
	Field(Field oldField) {
		
		this.dimField = oldField.dimField;
		this.dimSquare = oldField.dimSquare;
		this.totalScore = oldField.totalScore;
		this.frequencyScore = (HashMap<Integer, Integer>) oldField.frequencyScore.clone();
		this.field = new FieldItem[this.dimField][this.dimField];
		this.list = new FieldItem[this.dimField*this.dimField];
		
		int countField = 0;
		for (int i = 0; i < oldField.dimField; i++) {
			for (int j = 0; j < oldField.dimField; j++) {
				this.field[i][j] = new FieldItem(oldField.field[i][j]);
				this.list[countField] = field[i][j];
				countField++;
			}
		}

		for (int i = 0; i < this.dimField; i++) {
			for (int j = 0; j < this.dimField; j++) {
				FieldItem fItem = this.field[i][j];
				for (int ij = 0; ij < this.dimField; ij++) {
					fItem.lineMembersX[ij] = this.field[ij][j];
				}
				for (int ji = 0; ji < this.dimField; ji++) {
					fItem.lineMembersY[ji] = this.field[i][ji];
				}
				int i2start = Math.round(fItem.posX/this.dimSquare)*this.dimSquare;
				int i2End = i2start+this.dimSquare;
				int j2start = Math.round(fItem.posY/this.dimSquare)*this.dimSquare;
				int j2End = j2start+this.dimSquare;
				int countSquare = 0;
				for (int i2 = i2start; i2 < i2End; i2++) {
					for (int j2 = j2start; j2 < j2End; j2++) {
						fItem.squareMembers[countSquare] = this.field[i2][j2];
						countSquare++;
					}
				}
			}
		}
		for (int i = 0; i < this.list.length; i++) {
			this.list[i].setFrequencyScore(this.frequencyScore);
		}
	}
	
	Field(int[][] puzzle, int _dimension) {
		dimSquare = _dimension;
		dimField = _dimension*_dimension;
		totalScore = 0.f;
		field = new FieldItem[dimField][dimField];
		list = new FieldItem[dimField*dimField];

		//field logic: field[x-->DOWN][y-->RIGHT]
		//create basic map
		int countField = 0;
		for (int i = 0; i < dimField; i++) {
			for (int j = 0; j < dimField; j++) {
				if (puzzle[i][j] != 0) {
					FieldItem newItem = new FieldItem(i, j, true, true, puzzle[i][j], dimSquare);
					field[i][j] = newItem;
					list[countField] = newItem;
				} else {
					FieldItem newItem = new FieldItem(i, j, false, false, puzzle[i][j], dimSquare);
					field[i][j] = newItem;
					list[countField] = newItem;
				}
				countField++;
			}
		}

		//for quicker access, add pointers with members of lines and squares
		for (int i = 0; i < dimField; i++) {
			for (int j = 0; j < dimField; j++) {
				FieldItem fItem = field[i][j];
				for (int ij = 0; ij < dimField; ij++) {
					fItem.lineMembersX[ij] = field[ij][j];
				}
				for (int ji = 0; ji < dimField; ji++) {
					fItem.lineMembersY[ji] = field[i][ji];
				}
				int i2start = Math.round(fItem.posX/dimSquare)*dimSquare;
				int i2End = i2start+dimSquare;
				int j2start = Math.round(fItem.posY/dimSquare)*dimSquare;
				int j2End = j2start+dimSquare;
				int countSquare = 0;
				for (int i2 = i2start; i2 < i2End; i2++) {
					for (int j2 = j2start; j2 < j2End; j2++) {
						fItem.squareMembers[countSquare] = field[i2][j2];
						countSquare++;
					}
				}
			}
		}
		calculateFrequencyScore();
		for (int i = 0; i < list.length; i++) {
			list[i].calculateValueRange();
			list[i].calculateScore();
		}
		calculateTotalScore();
	}

	//a field item has a score based on how many different elements can be placed in one cell, as well as how 
	//many members of a number are existing in the complete field.
	public void calculateFrequencyScore() {
		frequencyScore = new HashMap<Integer, Integer>();
		for (int i = 1; i < dimField+1; i++) {
			frequencyScore.put(i, 0);
		}
		for (int i = 0; i < dimField; i++) {
			for (int j = 0; j < dimField; j++) {
				if (field[i][j].getValue() != 0) {
					int temp = frequencyScore.get(field[i][j].getValue());
					frequencyScore.replace(field[i][j].getValue(), temp+1);
				}
			}
		}
		for (int i = 1; i < dimField+1; i++) {
			if (dimField-frequencyScore.get(i) != 0) {
				frequencyScore.replace(i, dimField/(dimField-frequencyScore.get(i)));
			}
		}
		//write frequency score into all subclasses
		for (int i = 0; i < list.length; i++) {
			list[i].setFrequencyScore(frequencyScore);
		}
	}
	
	public float calculateTotalScore() {
		totalScore = 0;
		for (int i = 0; i < dimField; i++) {
			for (int j = 0; j < dimField; j++) {
				//field[i][j].calculateValueRange();
				//field[i][j].calculateScore();
				totalScore += field[i][j].score;
			}
		}
		return totalScore;
	}

	public int[][] export() {
		int[][] output = new int[dimField][dimField];
		for (int i = 0; i < dimField; i++) {
			for (int j = 0; j < dimField; j++) {
				output[i][j] = field[i][j].getValue();
			}
		}
		return output;
	}
}
