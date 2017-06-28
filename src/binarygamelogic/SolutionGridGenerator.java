package binarygamelogic;

import java.util.*;

/*
 * Class containing static methods used to randomly generate a valid solution grid, from which
 * we can then derive a starting game grid. The only public method is generatedGrid(...), which
 * we call return in matrix form a randomly generated solution grid.
 */

public class SolutionGridGenerator {	
	/*
	 * Generate and return a randomly generated valid game grid.
	 * 
	 * @param gridsize	the size of the generated solution grid
	 * @return			a randomly generated valid solution grid
	*/
	public static byte[][] generateSolutionGrid(int gridSize) throws IllegalArgumentException{
		//Check if we received a valid size for the grid (must be a positive even number)
		if (gridSize <= 1 || (gridSize & 1) != 0) {
			throw new IllegalArgumentException("Invalid size for the grid : must be greater than 1 and be an even number.");
		}
		
		byte[][] solutionGrid = new byte[gridSize][gridSize];
		for (int rowInd = 0 ; rowInd < solutionGrid.length ; rowInd++)
			for (int colInd = 0 ; colInd < solutionGrid[rowInd].length ; colInd++)
				solutionGrid[rowInd][colInd] = Game.CELLVALUE_EMPTY;
		
		if (generateSolutionGrid(0, solutionGrid))
			return solutionGrid;
		else
			return null;
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
	private static boolean generateSolutionGrid(int rowInd, byte[][] solutionGrid) {
		//If true, then the grid has been successfully built.
		if (rowInd == solutionGrid.length)
			return true;
		
		return randomlyFillRow(rowInd, 0, solutionGrid);
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
	private static boolean randomlyFillRow(int rowInd, int colInd, byte[][] solutionGrid) {
		//If true, then we have filled all the cells in the row and can then add it to the grid
		if (colInd == solutionGrid[rowInd].length)
			return generateSolutionGrid(rowInd+1, solutionGrid);

		
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
			solutionGrid[rowInd][colInd] = randomValues[i];

			if (GameGridVerifier.checkLocalCellConstraints(rowInd, colInd, solutionGrid) &&
				GameGridVerifier.checkRowConstraints(rowInd, solutionGrid) &&
				GameGridVerifier.checkColConstraints(colInd, solutionGrid))
			{				
				if (randomlyFillRow(rowInd, colInd+1, solutionGrid))
					return true;
			}
		}
		
		solutionGrid[rowInd][colInd] = Game.CELLVALUE_EMPTY;
		
		//The method wasn't able to find a valid row considering the rows already added to 
		//the solution grid : must then backtrack in the grid creation process.
		return false;
	}	
}
