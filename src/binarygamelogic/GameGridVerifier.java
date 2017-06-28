package binarygamelogic;

import java.util.Arrays;
import java.util.Random;

public class GameGridVerifier {
	
	
	/*
	 * Generate and return a randomly generated valid game grid.
	 * 
	 * @param gridsize	the size of the generated solution grid
	 * @return			a randomly generated valid solution grid
	*/
	public static int getNbrOfSolutions(byte[][] cellsValue) throws IllegalArgumentException{
		byte[][] cellsValueCopy = new byte[cellsValue.length][cellsValue.length];		
		for (int rowInd = 0 ; rowInd < cellsValue.length ; rowInd++)
			cellsValueCopy[rowInd] = cellsValue[rowInd].clone();
		
		
		if (!isValidGrid(cellsValueCopy))
			return 0;
		
		return getNbrOfSolutions(0, cellsValueCopy, 0);
	}
	
	
	
	/*	
	 * Generate a random valid solution grid from which we derive the starting game grid.
	 * This function call itself recursively to generate the grid row by row, using at each
	 * step the rows already generated to enforce the various constraint defined by the game.
	 * (this allow the method to narrow the possible valid rows that it can build at each step).
	 * 
	 * @param gridSize		the size of the solution grid that the method is building
	 * @param grid			an ArrayList containing the rows that the method has built until now
	 * @param colNumberZero	a byte array containing the number of times the number 0 appear in each column of the grid
	 * @param colNumberOne	a byte array containing the number of times the number 1 appear in each column of the grid
	 * @return				boolean value indicating if the method can generate the next row or not, considering the constraints enforced by the rows already built
	*/   
	private static int getNbrOfSolutions(int rowInd, byte[][] cellsValue, int nbrOfSolutions) {
		if (nbrOfSolutions >= 2)
			return 2;
		
		//If true, then the grid has been successfully built.
		if (rowInd == cellsValue.length) {
			nbrOfSolutions++;
			return nbrOfSolutions;
		}
		
		return randomlyFillRow(rowInd, 0, cellsValue, nbrOfSolutions);
	}
	

	
	/*
	 * Method that finish filling the row selecting randomly between 0 or 1. At each step the method find
	 * the next empty cell then decide randomly which value to try to put in it first, then call itself
	 * recursively to fill the next empty cell.
	 * 
	 * @param grid				the solution grid built until now
	 * @param newRowInd			the index of the new row that is being built
	 * @param freeCellInd		the index of the free cell to fill with a value
	 * @param colNumberZeros	the number of zeros that appear in each column (in the grid built until now)
	 * @param colNumberOnes		the number of ones that appear in each column (in the grid built until now)
	 * @return					true if we can complete the row and after that the whole grid from here, false otherwise
	 */
	private static int randomlyFillRow(int rowInd, int colInd, byte[][] cellsValue, int nbrOfSolutions) {
		//If true, then we have filled all the cells in the row and can then add it to the grid
		if (colInd == cellsValue[rowInd].length)
			return getNbrOfSolutions(rowInd+1, cellsValue, nbrOfSolutions);

		if (cellsValue[rowInd][colInd] == Game.CELLVALUE_EMPTY) {
			//Decide randomly which value to try first between 0 and 1
			byte[] randomValues = new byte[2];
			if (new Random().nextInt(2) == 0) {
				randomValues[0] = Game.CELLVALUE_ZERO;
				randomValues[1] = Game.CELLVALUE_ONE;
			}
			else {
				randomValues[0] = Game.CELLVALUE_ONE;
				randomValues[1] = Game.CELLVALUE_ZERO;
			}
			
			for (int i = 0 ; i < randomValues.length ; i++) {
				cellsValue[rowInd][colInd] = randomValues[i];
	
				if (GameGridVerifier.checkLocalCellConstraints(rowInd, colInd, cellsValue) &&
					GameGridVerifier.checkRowConstraints(rowInd, cellsValue) &&
					GameGridVerifier.checkColConstraints(colInd, cellsValue))
				{
					nbrOfSolutions = randomlyFillRow(rowInd, colInd+1, cellsValue, nbrOfSolutions);
					
					if (nbrOfSolutions >= 2)
						return 2;
				}
			}
			
			cellsValue[rowInd][colInd] = Game.CELLVALUE_EMPTY;
		}
		else
			nbrOfSolutions = randomlyFillRow(rowInd, colInd+1, cellsValue, nbrOfSolutions);
		
		//The method wasn't able to find a valid row considering the rows already added to 
		//the solution grid : must then backtrack in the grid creation process.
		return nbrOfSolutions;
	}
	
	
	
	public static boolean isValidGrid(byte[][] cellsValue) {
		for (int rowInd = 0 ; rowInd < cellsValue.length ; rowInd++)
			for (int colInd = 0 ; colInd < cellsValue.length ; colInd++) {
				if (cellsValue[rowInd][colInd] != Game.CELLVALUE_EMPTY)
					if (!checkLocalCellConstraints(rowInd, colInd, cellsValue))
						return false;
			}

		
		
		for (int rowInd = 0 ; rowInd < cellsValue.length ; rowInd++)
			if (!checkRowConstraints(rowInd, cellsValue))
				return false;
		
		for (int colInd = 0 ; colInd < cellsValue.length ; colInd++)
			if (!checkColConstraints(colInd, cellsValue))
				return false;
		
		return true;
	}
	
	public static boolean checkLocalCellConstraints(int rowInd, int colInd, byte[][] cellsValue) {
		byte[] cellTrio;
		
		if (rowInd > 1) {
			cellTrio = new byte[3];
			cellTrio[2] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd-1][colInd];
			cellTrio[0] = cellsValue[rowInd-2][colInd];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		if (rowInd < cellsValue.length-2) {
			cellTrio = new byte[3];
			cellTrio[0] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd+1][colInd];
			cellTrio[2] = cellsValue[rowInd+2][colInd];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		if (colInd > 1) {
			cellTrio = new byte[3];
			cellTrio[2] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd][colInd-1];
			cellTrio[0] = cellsValue[rowInd][colInd-2];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		if (colInd < cellsValue[rowInd].length-2) {
			cellTrio = new byte[3];
			cellTrio[0] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd][colInd+1];
			cellTrio[2] = cellsValue[rowInd][colInd+2];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		if (rowInd > 0 && rowInd < cellsValue.length-1) {
			cellTrio = new byte[3];
			cellTrio[1] = cellsValue[rowInd][colInd];
			cellTrio[0] = cellsValue[rowInd-1][colInd];
			cellTrio[2] = cellsValue[rowInd+1][colInd];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		if (colInd > 0 && colInd < cellsValue[rowInd].length-1) {
			cellTrio = new byte[3];
			cellTrio[1] = cellsValue[rowInd][colInd];
			cellTrio[0] = cellsValue[rowInd][colInd-1];
			cellTrio[2] = cellsValue[rowInd][colInd+1];
			
			if (identicalCellSerie(cellTrio))
				return false;
		}
		
		return true;		
	}
	
	private static boolean identicalCellSerie(byte[] cellSerie) {
		int currValue = cellSerie[0];
		for (int cellInd = 0 ; cellInd < cellSerie.length ; cellInd++) {
			if (cellSerie[cellInd] == Game.CELLVALUE_EMPTY)
				return false;
			else
				if (cellSerie[cellInd] != currValue)
					return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	public static boolean checkRowConstraints(int rowInd, byte[][] cellsValue) {
		int nbrOfZeros = 0, nbrOfOnes = 0;
		boolean rowIsComplete = true;
		for (int colInd = 0 ; colInd < cellsValue[rowInd].length ; colInd++) {
			if (cellsValue[rowInd][colInd] == Game.CELLVALUE_ZERO)
				nbrOfZeros++;
			else if (cellsValue[rowInd][colInd] == Game.CELLVALUE_ONE)
				nbrOfOnes++;
			else
				rowIsComplete = false;
		}
		
		int maxSameNumber = cellsValue[rowInd].length/2;
		
		if (nbrOfZeros > maxSameNumber || nbrOfOnes > maxSameNumber)
			return false;
		else if (rowIsComplete)
			return !checkForIdenticalRows(rowInd, cellsValue);
		else
			return true;
	}	
	
	/*
	 * Method that check if the new row to be added to the grid is identical to a row that was added before.
	 * 
	 * @param grid		the solution grid built until now
	 * @param newrowInd	the index in the grid of the new row is to be added to the grid
	 * @return			true if there is a row in the grid that is identical to the new row to be added, false otherwise
	 */
	public static boolean checkForIdenticalRows(int rowInd, byte[][] cellsValue) {	
		byte[] firstRow = cellsValue[rowInd];
		byte[] secondRow;
		for (int secondRowInd = 0 ; secondRowInd < cellsValue.length ; secondRowInd++) {
			if (secondRowInd != rowInd) {
				secondRow = cellsValue[secondRowInd];
			
				if (Arrays.equals(firstRow, secondRow))
					return true;
			}
		}
		
		return false;
	}
	
	
	
	public static boolean checkColConstraints(int colInd, byte[][] cellsValue) {
		int nbrOfZeros = 0, nbrOfOnes = 0;
		boolean colIsComplete = true;
		for (int rowInd = 0 ; rowInd < cellsValue.length ; rowInd++) {
			if (cellsValue[rowInd][colInd] == Game.CELLVALUE_ZERO)
				nbrOfZeros++;
			else if (cellsValue[rowInd][colInd] == Game.CELLVALUE_ONE)
				nbrOfOnes++;
			else
				colIsComplete = false;
		}
		
		int maxSameNumber = cellsValue.length/2;
		
		if (nbrOfZeros > maxSameNumber || nbrOfOnes > maxSameNumber)
			return false;
		else if (colIsComplete)
			return !checkForIdenticalColumns(colInd, cellsValue);
		else
			return true;
	}
	
	public static boolean checkForIdenticalColumns(int colInd, byte[][] cellsValue) {
		byte[] firstCol = getGridColumn(colInd, cellsValue);
		byte[] secondCol;
		for (int secondColInd = 0 ; secondColInd < cellsValue.length ; secondColInd++) {
			if (secondColInd != colInd) {
				secondCol = getGridColumn(secondColInd, cellsValue);
				
				if (Arrays.equals(firstCol, secondCol))
					return true;
			}
		}
		
		return false;
	}

	/*
	 * Method that return the specified column of the grid in array form.
	 * 
	 * @param grid		the solution grid built until now
	 * @param colInd	the index of the column to return
	 * @return			an array containing the value of the specified column
	 */
	private static byte[] getGridColumn(int colInd, byte[][] cellsValue) {
		byte[] column = new byte[cellsValue.length];
		
		for (int rowInd = 0 ; rowInd < cellsValue.length ; rowInd++)
			column[rowInd] = cellsValue[rowInd][colInd];
		
		return column;
	}
}
