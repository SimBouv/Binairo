package binarygamelogic;

import java.util.Arrays;
import java.util.Random;

/*
 * Class containing static methods used to check if a cell, a row/column or a whole grid is valid in respect to the game constraints.
 * Contains also a method to find if a given grid (which can be partially filled) has zero, one or multiple solutions.
 */


public class GameGridVerifier {
	
	
	/*
	 * Method that find if there is 0, 1 or multiple solutions for the given game grid.
	 * 
	 * @param cellsValue	the grid for which the method must find the number of possible solutions
	 * @return				0 if there is no possible solution for the given grid, 1 if there is exactly one solution or 2 if there are multiple solutions
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
	 * Method called recursively to find the number of solutions for the given grid, which try to solve the grid
	 * by filling one free cell at a time and backtracking when one of the game constraints is not respected. 
	 * 
	 * @param rowInd			row index of the cell to fill with a value
	 * @param cellsValue		the grid that the method is filling to find the number of possible solutions 
	 * @param nbrOfSolutions	the number of solutions found until now
	 * @return					0 if the grid violate one of the constraint, 1 if all the cells has been filled and the grid is valid, 2 as soon as the method find a second solution
	*/   
	private static int getNbrOfSolutions(int rowInd, byte[][] cellsValue, int nbrOfSolutions) {
		if (nbrOfSolutions >= 2)
			return 2;
		
		//If true, then the grid has been successfully filled and form a valid solution
		if (rowInd == cellsValue.length) {
			nbrOfSolutions++;
			return nbrOfSolutions;
		}
		
		return randomlyFillCells(rowInd, 0, cellsValue, nbrOfSolutions);
	}
	

	
	/*
	 * Method that try to randomly fill the given cell (if it is empty) of the grid,
	 * verifying after filling each one if the grid is still valid before continuing.
	 * 
	 * @param rowInd			row index of the cell to randomly fill
	 * @param colInd			column index of the cell to randomly fill
	 * @param cellsValue		the grid that the method is filling to find the number of possible solutions
	 * @param nbrOfSolutions	the number of solutions found until now
	 * @return					0 if the grid violate one of the constraint, 1 if all the cells has been filled and the grid is valid, 2 as soon as the method find a second solution
	 */
	private static int randomlyFillCells(int rowInd, int colInd, byte[][] cellsValue, int nbrOfSolutions) {
		//If true, then we have filled all the cells in the row and can go to the next one
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
	
				//Check if the grid is still valid after filling this cell with the random value.
				if (GameGridVerifier.checkLocalCellConstraints(rowInd, colInd, cellsValue) &&
					GameGridVerifier.checkRowConstraints(rowInd, cellsValue) &&
					GameGridVerifier.checkColConstraints(colInd, cellsValue))
				{
					nbrOfSolutions = randomlyFillCells(rowInd, colInd+1, cellsValue, nbrOfSolutions);
					
					//Return immediately the method has already found more than one solution.
					if (nbrOfSolutions >= 2)
						return 2;
				}
			}
			
			cellsValue[rowInd][colInd] = Game.CELLVALUE_EMPTY;
		}
		else
			nbrOfSolutions = randomlyFillCells(rowInd, colInd+1, cellsValue, nbrOfSolutions);
		
		//The method wasn't able to find a valid solution grid from this point : must backtrack to the previous cell
		//and try filling it with another value. 
		return nbrOfSolutions;
	}
	
	
	/*
	 * Method that verify if the given grid (which can be partially completed) is valid 
	 * (if it respect every constraints of the game).
	 * 
	 * @param cellsValue	the grid to verify, which can be only partially filled
	 * @return				true if the grid respect every constraints of the game, false otherwise
	 */
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
	
	
	/*
	 * Method that verify if a given cell is valid locally (the cell must not be part
	 * of a series of three consecutive non-empty cells with the same value).
	 * 
	 * @param rowInd		row index of the cell to check for validity
	 * @param colInd		column index of the cell to check for validity
	 * @param cellsValue	the grid containing the cells' value (can be partially filled)
	 * @return				true if the cell is not in a series of 3 identical cells
	 */
	public static boolean checkLocalCellConstraints(int rowInd, int colInd, byte[][] cellsValue) {
		byte[] cellTrio;
		
		//Check series : given cell + 2 cells on the left
		if (rowInd > 1) {
			cellTrio = new byte[3];
			cellTrio[2] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd-1][colInd];
			cellTrio[0] = cellsValue[rowInd-2][colInd];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Check series : given cell + 2 cells on the right
		if (rowInd < cellsValue.length-2) {
			cellTrio = new byte[3];
			cellTrio[0] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd+1][colInd];
			cellTrio[2] = cellsValue[rowInd+2][colInd];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Check series : given cell + 2 cells over
		if (colInd > 1) {
			cellTrio = new byte[3];
			cellTrio[2] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd][colInd-1];
			cellTrio[0] = cellsValue[rowInd][colInd-2];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Check series : given cell + 2 cells under
		if (colInd < cellsValue[rowInd].length-2) {
			cellTrio = new byte[3];
			cellTrio[0] = cellsValue[rowInd][colInd];
			cellTrio[1] = cellsValue[rowInd][colInd+1];
			cellTrio[2] = cellsValue[rowInd][colInd+2];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Check series : given cell + 1 cell on the left and 1 cell on the right
		if (rowInd > 0 && rowInd < cellsValue.length-1) {
			cellTrio = new byte[3];
			cellTrio[1] = cellsValue[rowInd][colInd];
			cellTrio[0] = cellsValue[rowInd-1][colInd];
			cellTrio[2] = cellsValue[rowInd+1][colInd];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Check series : given cell + 1 cell over and 1 cell under
		if (colInd > 0 && colInd < cellsValue[rowInd].length-1) {
			cellTrio = new byte[3];
			cellTrio[1] = cellsValue[rowInd][colInd];
			cellTrio[0] = cellsValue[rowInd][colInd-1];
			cellTrio[2] = cellsValue[rowInd][colInd+1];
			
			if (identicalCellSeries(cellTrio))
				return false;
		}
		
		//Found no identical series of 3 cells
		return true;		
	}
	
	
	/*
	 * Method used to check if the given cells are identical, which mean that they are not empty
	 * and all have the same value.
	 * 
	 * @param cellSeries	the series of cells to compare
	 * @return				true if all of the cells are identical, false otherwise
	 */
	private static boolean identicalCellSeries(byte[] cellSeries) {
		int currValue = cellSeries[0];
		for (int cellInd = 0 ; cellInd < cellSeries.length ; cellInd++) {
			if (cellSeries[cellInd] == Game.CELLVALUE_EMPTY)
				return false;
			else
				if (cellSeries[cellInd] != currValue)
					return false;
		}
		
		return true;
	}
	
	
	
	
	
	/*
	 * Method that check if the given row respect all of the game constraints.
	 * 
	 * @param rowInd		index of the row to check
	 * @param cellsValue	the grid containing the cells' value (can be partially filled)
	 * @return				true if the row is valid (respect all of the game constraints), false otherwise
	 */
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
		
		//Check if the given row doesn't have too many of the same value
		if (nbrOfZeros > maxSameNumber || nbrOfOnes > maxSameNumber)
			return false;
		//If the row is completely filled, check if there is an identical row in the grid
		else if (rowIsComplete)
			return !checkForIdenticalRows(rowInd, cellsValue);
		else
			return true;
	}	
	
	
	/*
	 * Method that check if a given row is identical to another row in the grid (if both rows are completely filled).
	 * 
	 * @param rowInd		index of to row to check
	 * @param cellsValue	the grid containing the cells' value (can be partially filled)
	 * @return				true if the row is completely filled and there is an identical row in the grid, false otherwise
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
	
	
	/*
	 * Method that check if the given column respect all of the game constraints.
	 * 
	 * @param colInd		index of the column to check
	 * @param cellsValue	the grid containing the cells' value (can be partially filled)
	 * @return				true if the column is valid (respect all of the game constraints), false otherwise
	 */
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
		
		//Check if the given column doesn't have too many of the same value
		if (nbrOfZeros > maxSameNumber || nbrOfOnes > maxSameNumber)
			return false;
		//If the column is completely filled, check if there is an identical column in the grid
		else if (colIsComplete)
			return !checkForIdenticalColumns(colInd, cellsValue);
		else
			return true;
	}
	
	
	/*
	 * Method that check if a given column is identical to another column in the grid (only if both columns are completely filled).
	 * 
	 * @param colInd		index of to column to check
	 * @param cellsValue	the grid containing the cells' value (can be partially filled)
	 * @return				true if the column is completely filled and there is an identical column in the grid, false otherwise
	 */
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
