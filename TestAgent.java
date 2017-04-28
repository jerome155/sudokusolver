/**
 * Sudoku Solver, uses recursive TreeSearch. For every iteration of Tree Search first fills in easy to compute
 * values (such as: only one value left to place). After, scores every cell based on a metric based upon two components, and continues the
 * tree search on the highest scoring item (=easiest to solve).
 * Uses class structure: Field for the complete sudoku field, Fielditem for a single cell. Cells do know their
 * neighboring cells by pointers, and are able to call them directly such that they update their score.
 * 
 * @author Jérôme Oesch
 * @version 1.0
 */

import java.util.LinkedList;


public class TestAgent implements SudokuAgent {

	public Field puzzle;

	private String name;

	
	/**
	 * Constructor, sets name.
	 */
	TestAgent()
	{
		name = "FatalitySudokuSmasher";

	}

	/**
	 * Entry point for Sudokusolver.
	 * Field: The complete Sudoku-field structure.
	 */
	public int[][] solve(int dimension, int[][] _puzzle){

		puzzle = new Field(_puzzle, dimension);
		
		Field output = SearchTree(puzzle);
		
		return output.export();
	}
	
	
	
	/**
	 * Recursive Search Tree Method. Is invoked 81 times at maximum.
	 * @param puzzle: The Sudoku to solve
	 * @return Null if unsolvable, Field if Sudoku is solved.
	 */
	public Field SearchTree(Field puzzle) {	
		//set cell values for all cells where only one value is still available
		boolean singlesExisting = true;
		while(singlesExisting) {
			if (singlesExisting) {
				singlesExisting = false;
				for (int i = 0; i < puzzle.list.length; i++) {
					if (!puzzle.list[i].valueFound && puzzle.list[i].valueRange.size() == 1) {
						puzzle.list[i].setValue(puzzle.list[i].valueRange.get(0));
						puzzle.calculateFrequencyScore();
						puzzle.list[i].notifyAdjacentCells();
						puzzle.calculateTotalScore();
						singlesExisting = true;
					}
				}
			} 
		}
		
		//check whether sudoku has errors, or if it is already solved.
		boolean puzzleComplete = true;
		boolean puzzleUnsolvable = false;
		for (int i = 0; i < puzzle.list.length; i++) {
			if (!puzzle.list[i].valueFound) {
				puzzleComplete = false;
			}
			if (puzzle.list[i].valueRange.isEmpty()) {
				puzzleUnsolvable = true;
			}
		}
		if (puzzleComplete) {
			return puzzle;
		}
		if (puzzleUnsolvable) {
			return null;
		}
		
		float highScore = 0;
		FieldItem workOnItem = null;
		//find item with highest score to work on:
		for (int i = 0; i < puzzle.list.length; i++) {
			if (puzzle.list[i].score > highScore && !puzzle.list[i].valueFound) {
				highScore = puzzle.list[i].score;
				workOnItem = puzzle.list[i];
			}
		}
		
		//for all values in value range: make new object and score it to find the best decision.
		LinkedList<Field> puzzleList = new LinkedList<Field>();
		for (int i = 0; i < workOnItem.valueRange.size(); i++) {
			Field newField = new Field(puzzle);
			FieldItem newWorkOnItem = newField.field[workOnItem.posX][workOnItem.posY];
			newWorkOnItem.setValue(newWorkOnItem.valueRange.get(i));
			newField.calculateFrequencyScore();
			newWorkOnItem.notifyAdjacentCells();
			newField.calculateTotalScore();
			
			if (puzzleList.isEmpty()) {
				puzzleList.add(newField);
			} else {
				boolean isAdded = false;
				for (int j = 0; j < puzzleList.size(); j++) {
					if (puzzleList.get(j).totalScore < newField.totalScore) {
						puzzleList.add(j, newField);
						isAdded = true;
						break;
					}
				}
				if (!isAdded) {
					puzzleList.add(newField);
				}
 			}
		}
		//recusively call SearchTree for all possible value ranges.
		for (int i = 0; i < puzzleList.size(); i++) {
			Field returnField = null;
			returnField = SearchTree(puzzleList.get(i));
			if (returnField != null) {
				return returnField;
			} else {
				continue;
			}
		}
		return null;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Prints the given Sudoku to the console.
	 * @param _puzzle: Sudoku to print
	 * @param message: Custom message
	 */
	public static void printValues(Field _puzzle, String message) {
		System.out.println(message);
		for (int i = 0; i < _puzzle.dimField; i++) {
			for (int j = 0; j < _puzzle.dimField; j++) {
				System.out.print(_puzzle.field[i][j].getValue() + " ");
			}
			System.out.println();
		}
	}

	/**
	 * Prints the given Sudoku field scores to the console.
	 * @param _puzzle: Sudoku for which cell scores should be printed.
	 * @param message: Custom message
	 */
	public static void printScores(Field _puzzle, String message) {
		System.out.println(message);
		for (int i = 0; i < _puzzle.dimField; i++) {
			for (int j = 0; j < _puzzle.dimField; j++) {
				System.out.print(Math.floor(_puzzle.field[i][j].score*10)/10 + " ");
			}
			System.out.println();
		}
	}
}
