package binarygamelogic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Random;

public class StartingGameGridGenerator {
	//Constant representing the different levels of difficulty of the game
	public static final byte BINAIRO_LEVEL_ONE = 1;
	public static final byte BINAIRO_LEVEL_TWO = 2;
	public static final byte BINAIRO_LEVEL_THREE = 3;
	
	
	/*Constants representing the different possibles states of a cell :
	 *FREE : value used to represent a cell that is empty in the game grid and must be filled by the player.
	 *FIXED : value used to represent a cell that display the solution number and can't be modified by the player (these cells form the starting game grid).
	 *BOTH : value used by different methods to indicate that we accept either free cells or fixed cells. 
	 */
	public static final byte BINAIRO_FREE_CELL = 0;
	public static final byte BINAIRO_FIXED_CELL = 1;
	public static final byte BINAIRO_BOTH = 2;
	
	
	
	/*
	 * Method that return a randomly generated starting grid, based on the solution grid and game level given as arguments.
	 * 
	 * @param solutionGrid	
	 * @param gameLevel		
	 * @return				
	 */
	public static boolean[][] getStartingGrid(byte[][] solutionGrid, byte gameLevel) {
		int nbrOfCellsToFix, nbrOfIdenticalPairsToFix, nbrOfPairsToFix, nbrOfHolesToFix;
		switch (gameLevel) {
			case BINAIRO_LEVEL_ONE :	nbrOfCellsToFix = 65;
										nbrOfIdenticalPairsToFix = 35; 
					 					nbrOfPairsToFix = 40;
					 					nbrOfHolesToFix = 5;
					 					break;
					 
			case BINAIRO_LEVEL_TWO :	nbrOfCellsToFix = 40;
										nbrOfIdenticalPairsToFix = 15;		
										nbrOfPairsToFix = 17;
										nbrOfHolesToFix = 5;
					 					break;
					 
			case BINAIRO_LEVEL_THREE :	nbrOfCellsToFix = 30;
										nbrOfIdenticalPairsToFix = 5;
										nbrOfPairsToFix = 15;
										nbrOfHolesToFix = 5;
					 					break;
					 	
			
			default : 	throw new IllegalArgumentException("The level for the grid is invalid.");
		}
		
		boolean[][] fixedCells = new boolean[solutionGrid.length][solutionGrid.length];
		
		int nbrOfFixedPairs = fixSeparateCellPairs(fixedCells, solutionGrid, nbrOfIdenticalPairsToFix, true);
		completeCellPairs(fixedCells, solutionGrid, nbrOfPairsToFix - nbrOfFixedPairs, true);
		int nbrOfFixedHoles = fixCellsHole(fixedCells, solutionGrid, nbrOfHolesToFix, true);
		completeCellHoles(fixedCells, solutionGrid, nbrOfHolesToFix - nbrOfFixedHoles, true);

		/*
		int nbrOfFixedCells = getNbrOfFixedCells(fixedCells);
		if (nbrOfFixedCells < nbrOfCellsToFix)
			fixCells(fixedCells, solutionGrid, nbrOfCellsToFix-nbrOfFixedCells, false, false, false);
		*/
		
		makeStartingGridSingleSolution(fixedCells, solutionGrid);
				
		return fixedCells;
	}
	
	
	/*
	 * Method that return the number of fixed cells in the game grid.
	 * 
	 * @param fixedCells	the matrix indicating which cells has been fixed in the game grid
	 * @return				the number of fixed cells in the game grid
	 */
	private static int getNbrOfFixedCells(boolean[][] fixedCells) {
		int nbrFixedCells = 0;
		
		for (int i = 0 ; i <= fixedCells.length-1 ; i++)
			for (int j = 0 ; j <= fixedCells[i].length-1 ; j++)
				if (fixedCells[i][j])
					nbrFixedCells++;
		
		return nbrFixedCells;
	}
	
	
	
	//=======================================================================================================================
	//=======================================================================================================================
	//=======================================================================================================================
	
	
	
	
	/*
	 * Method that randomly fix pairs of values in the game grid (values that are given at the start of the game).
	 * The method fix only pairs that are separated from each other, meaning that for a given pair there won't be any neighbouring
	 * cells (horizontally or vertically) that has been fixed.
	 * 
	 * @param fixedCells			the matrix indicating which cells has been fixed in the game grid
	 * @param cellsValue			the matrix containing the solution grid
	 * @param nbrOfPairsToFix		the number of separated pairs that the method will try to fix in the grid
	 * @param identicalPairsOnly	true if the method must only create pairs of identical values, false otherwise 
	 * @return						the number of separate cell pairs that the method has successfully fixed in the game grid
	 */
	private static int fixSeparateCellPairs(boolean[][] fixedCells, byte[][] solutionGrid, int nbrOfPairsToFix, boolean identicalPairsOnly) {
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
		
		int nbrOfPairsFixed = 0;
		//Loop until we run out of cells that can be fixed by the method or until the method has successfully fixed 
		//the desired number of separate pairs (which respect all the given constraints).
		while (fixableCellsPool.size() > 0 && nbrOfPairsFixed < nbrOfPairsToFix) {
			Random randomInd = new Random();
			
			//Randomly choose a cell to try to fixed (amongst the pool of cell that can be considered to form a separate pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];

			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, true, false, false)) {
				ArrayList<int[]> cellNeighbours = getCellNeighbours(randomRowInd, randomColInd, fixedCells, 1, BINAIRO_FREE_CELL);
				
				//Loop that remove every neighbour cell that can't be considered to form a pair with the randomly chosen cell.
				for (int neighbourInd = cellNeighbours.size() - 1 ; neighbourInd >= 0 ; neighbourInd--) {
					//Remove the neighbour cell from the list if it is not a valid cell to fix or if its value is different and the method
					//must fix pairs of identical value.
					int[] currNeighbour = cellNeighbours.get(neighbourInd);
					if (!isValidCellToFix(currNeighbour[0], currNeighbour[1], fixedCells, solutionGrid, true, false, false) || 
						(identicalPairsOnly && solutionGrid[randomRowInd][randomColInd] != solutionGrid[currNeighbour[0]][currNeighbour[1]])) 
					{
						cellNeighbours.remove(neighbourInd);
					}
				}
				
				//If the randomly chosen cell has neighbours with which it can form a separate pair,
				//randomly choose one of this neighbour to form a pair.
				if (cellNeighbours.size() > 0) {
					int randomNeighbourInd = randomInd.nextInt(cellNeighbours.size());
					int randomNeighbourRowInd = cellNeighbours.get(randomNeighbourInd)[0];
					int randomNeighbourColInd = cellNeighbours.get(randomNeighbourInd)[1];
					
					fixedCells[randomRowInd][randomColInd] = true;
					fixedCells[randomNeighbourRowInd][randomNeighbourColInd] = true;			
					
					nbrOfPairsFixed++;
	
					//The neighbour cell has been fixed : remove it from the pool of free cells
					fixableCellsPool.get(randomNeighbourRowInd).remove(randomNeighbourColInd);
					if (fixableCellsPool.get(randomNeighbourRowInd).size() == 0)
						fixableCellsPool.remove(randomNeighbourRowInd);
				}
			}
			
			//The randomly chosen cell has been fixed or can't be considered to form a pair : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);
		
		}
		
		return nbrOfPairsFixed;
	}
	
	
	/*
	 * Method that form pair of cells by fixing cells that are next to an already fixed one, following the different criteria given as parameters. 
	 * This allow to fix more pairs of cells in the grid (with identical value or not), without having make them separated from each other.
	 * 
	 * @param fixedCells			the matrix indicating which cells has been fixed in the game grid
	 * @param cellsValue			the matrix containing the solution grid
	 * @param nbrOfPairsToFix		the number of pairs that the method will try to fix in the grid (fixing a cell can create more than one pair, depending on the number of fixed neighbours)
	 * @param identicalPairsOnly	indicate to the method if it must only fix cells that are next to an already fixed cell of identical value
	 * @return						the number of pairs that the method was able to successfully fix
	 */
	private static int completeCellPairs(boolean[][] fixedCells, byte[][] solutionGrid, int nbrOfPairsToFix, boolean identicalPairsOnly) {	
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
		
		int nbrOfPairsFixed = 0;
		//Loop until we run out of cells that can be fixed by the method or until the method has successfully fixed 
		//the desired number of pairs (that respect all the given constraints).
		while (fixableCellsPool.size() > 0 && nbrOfPairsFixed < nbrOfPairsToFix) {
			Random randomInd = new Random();
				
			//Randomly choose a cell to try to fix (amongst the pool of cells that can be considered to form a pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];

			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, false, false, false)) {
				ArrayList<int[]> cellNeighbours = getCellNeighbours(randomRowInd, randomColInd, fixedCells, 1, BINAIRO_FIXED_CELL);
				
				//If the method must form identical pairs only, then remove from the list of neighbours those that
				//have a different value than the randomly chosen cell
				for (int neighbourInd = cellNeighbours.size() - 1 ; neighbourInd >= 0 ; neighbourInd--) {
					int[] currNeighbour = cellNeighbours.get(neighbourInd);
					
					if (identicalPairsOnly && solutionGrid[currNeighbour[0]][currNeighbour[1]] != solutionGrid[randomRowInd][randomColInd])
						cellNeighbours.remove(neighbourInd);
				}
				
				//If there are neighbours left with which the method can form a pair 
				if (cellNeighbours.size() > 0) {
					fixedCells[randomRowInd][randomColInd] = true;
					nbrOfPairsFixed += cellNeighbours.size();
				}
				
				//Add all valid neighbour cells to the pool of cells that can be considered to be fixed
				//(if they were isolated before, fixing a neighbouring cell could make them eligible to form a pair).
				for (int[] neighbour : getCellNeighbours(randomRowInd, randomColInd, fixedCells, 1, BINAIRO_FREE_CELL)) {
					if (isValidCellToFix(neighbour[0], neighbour[1], fixedCells, solutionGrid, false, false, false)) {
						if (!fixableCellsPool.containsKey(randomRowInd))
							fixableCellsPool.put(randomRowInd, new HashSet<>());
						
						fixableCellsPool.get(randomRowInd).add(randomColInd);
					}
				}
			}
			
			//The randomly chosen cell has been fixed or can't be considered to form a pair : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);
		}

		return nbrOfPairsFixed;
	}
	
	
	
	/*
	 * Method that put "holes" in the game grid : these are empty cells that have two fixed cells on either side 
	 * of them (horizontally or vertically). This method create these holes by fixing two free cells in the grid
	 * without using an already fixed cell to surround the hole cell.
	 * 
	 * @param fixedCells			the matrix indicating which cells has been fixed in the game grid
	 * @param cellsValue			the matrix containing the solution grid
	 * @param nbrOfHolesToCreate	the number of hole to create in the game grid
	 * @param identicalPairsOnly	indicate if the method must create hole that are surrounded by two identical values
	 * @return						the number of holes that the method was able to successfully create in the game grid
	 */
	private static int fixCellsHole(boolean[][] fixedCells, byte[][] solutionGrid, int nbrOfHolesToCreate, boolean identicalPairsOnly) {		
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
		
		int nbrOfCreatedHoles = 0;
		while (fixableCellsPool.size() > 0 && nbrOfCreatedHoles < nbrOfHolesToCreate) {
			Random randomInd = new Random();
			
			//Randomly choose a cell to try to fix (amongst the pool of cell that can be considered to form a separate pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];
  
			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, false, false, false)) {
				//Get all the free cells that the randomly chosen cell could be paired with to have a hole between them
				//(the candidate cells are those that are free and at a distance of two cells from the randomly chosen cell).
				ArrayList<int[]> cellsToPairWith = getCellNeighbours(randomRowInd, randomColInd, fixedCells, 2, BINAIRO_FREE_CELL);
	
				for (int cellToPairInd = cellsToPairWith.size() - 1 ; cellToPairInd >= 0 ; cellToPairInd--) {
					int[] currCellToPair = cellsToPairWith.get(cellToPairInd);
					
					//Indexes of the cell that lie between the two cells.
					int middleCellRowInd = randomRowInd + (currCellToPair[0] - randomRowInd)/2;
					int middleCellColInd = randomColInd + (currCellToPair[1] - randomColInd)/2;
					
					/*Remove all the cells from the list that don't respect one or more of these conditions :
					 *	- The cell is not a valid cell to be fixed.
					 *	- The method must pair cell of identical value and the cell is of a different value.
					 *	- The cell in the middle has already been fixed.
					 */
					if (!isValidCellToFix(currCellToPair[0], currCellToPair[1], fixedCells, solutionGrid, false, false, false) ||
						(identicalPairsOnly && solutionGrid[randomRowInd][randomColInd] != solutionGrid[currCellToPair[0]][currCellToPair[1]]) ||
						fixedCells[middleCellRowInd][middleCellColInd])
					{
						cellsToPairWith.remove(cellToPairInd);
					}
				}
				
				//If the randomly chosen cell has neighbours with which it can form a hole,
				//randomly choose one of these neighbours to form a hole.
				if (cellsToPairWith.size() > 0) {
					int randomCellInd = randomInd.nextInt(cellsToPairWith.size());
					int randomCellRowInd = cellsToPairWith.get(randomCellInd)[0];
					int randomCellColInd = cellsToPairWith.get(randomCellInd)[1];
					
					fixedCells[randomRowInd][randomColInd] = true;
					fixedCells[randomCellRowInd][randomCellColInd] = true;			
					
					nbrOfCreatedHoles++;
	
					//The cell used to form a hole has been fixed : remove it from the pool of free cells
					fixableCellsPool.get(randomCellRowInd).remove(randomCellColInd);
					if (fixableCellsPool.get(randomCellRowInd).size() == 0)
						fixableCellsPool.remove(randomCellRowInd);
				}
			}
			
			//The randomly chosen cell has been fixed or can't be considered to form a hole : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);		
			}
		
		return nbrOfCreatedHoles;
	}
	
	
	
	/*
	 * Method that put "holes" in the game grid : these are empty cells that have two fixed cells on either side 
	 * of them (horizontally or vertically). This method create these holes by fixing one free cell in the grid
	 * and using an already fixed cell to surround the hole cell.
	 * 
	 * @param fixedCells			the matrix indicating which cells has been fixed in the game grid
	 * @param cellsValue			the matrix containing the solution grid
	 * @param nbrOfHolesToCreate	the number of hole to create in the game grid
	 * @param identicalPairsOnly	indicate if the method must create hole that are surrounded by two identical values
	 * @return						the number of holes that the method was able to successfully create in the game grid
	 */
	private static int completeCellHoles(boolean[][] fixedCells, byte[][] solutionGrid, int nbrOfHolesToCreate, boolean identicalPairsOnly) {	
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
		
		int nbrOfCreatedHoles = 0;
		while (fixableCellsPool.size() > 0 && nbrOfCreatedHoles < nbrOfHolesToCreate) {
			Random randomInd = new Random();
				
			//Randomly choose a cell to try to fix (amongst the pool of cell that can be considered to form a separate pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];
			
			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, false, false, false)) {
				//Get all the fixed cells that the randomly chosen cell could be paired with to have a hole between them
				//(the candidate cells are those that are fixed and at a distance of two cells from the randomly chosen cell).
				ArrayList<int[]> cellsToPairWith = getCellNeighbours(randomRowInd, randomColInd, fixedCells, 2, BINAIRO_FIXED_CELL);
				
				
				for (int cellToPairInd = cellsToPairWith.size() - 1 ; cellToPairInd >= 0 ; cellToPairInd--) {
					int[] currCellToPair = cellsToPairWith.get(cellToPairInd);
					
					int middleCellRowInd = randomRowInd + (currCellToPair[0] - randomRowInd)/2;
					int middleCellColInd = randomColInd + (currCellToPair[1] - randomColInd)/2;
					
					/*Remove all the cells from the list that don't respect one or more of these conditions :
					 *	- The method must pair cell of identical value and the cell is of a different value.
					 *	- The cell in the middle has already been fixed.
					 */
					if ((identicalPairsOnly && solutionGrid[randomRowInd][randomColInd] != solutionGrid[currCellToPair[0]][currCellToPair[1]]) ||
						fixedCells[middleCellRowInd][middleCellColInd])
					{
						cellsToPairWith.remove(cellToPairInd);
					}
				}
				
				//If the randomly chosen cell has at least one fixed cell with which it can form a hole,
				//then the method can fix this randomly chosen cell.
				if (cellsToPairWith.size() > 0) {
					fixedCells[randomRowInd][randomColInd] = true;			
					nbrOfCreatedHoles += cellsToPairWith.size();
					
					//Add all valid neighbour cells to the pool of cells that can be considered to be fixed
					//(if they didn't had any fixed cell with which to form a hole, fixing a new cell 
					//could make them eligible to form a hole).
					for (int[] neighbour : getCellNeighbours(randomRowInd, randomColInd, fixedCells, 2, BINAIRO_FREE_CELL)) {
						if (isValidCellToFix(neighbour[0], neighbour[1], fixedCells, solutionGrid, false, false, false)) {
							if (!fixableCellsPool.containsKey(randomRowInd))
								fixableCellsPool.put(randomRowInd, new HashSet<>());
							
							fixableCellsPool.get(randomRowInd).add(randomColInd);
						}
					}
				}
			}
		
			//The randomly chosen cell has been fixed or can't be considered to form a hole : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);
		}
		
		return nbrOfCreatedHoles;
	}
	
	
	private static void makeStartingGridSingleSolution(boolean[][] fixedCells, byte[][] solutionGrid) {
		byte[][] startingGameGrid = new byte[solutionGrid.length][solutionGrid.length];
		for (int rowInd = 0 ; rowInd < startingGameGrid.length ; rowInd++)
			for (int colInd = 0 ; colInd < startingGameGrid.length ; colInd++)
				if (fixedCells[rowInd][colInd])
					startingGameGrid[rowInd][colInd] = solutionGrid[rowInd][colInd];
				else
					startingGameGrid[rowInd][colInd] = Game.CELLVALUE_EMPTY;
		
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
		int nbrOfCellFixedToSingle = 0;
		while (GameGridVerifier.getNbrOfSolutions(startingGameGrid) == 2) {
			Random randomInd = new Random();
			
			//Randomly choose a cell to try to fix (amongst the pool of cell that can be considered to form a separate pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];
			
			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, false, true, false)) {
				nbrOfCellFixedToSingle++;
				
				fixedCells[randomRowInd][randomColInd] = true;
				startingGameGrid[randomRowInd][randomColInd] = solutionGrid[randomRowInd][randomColInd];
			}
			
			//The randomly chosen cell has been fixed or can't be considered to form a hole : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);
		}
		
		
		System.out.println("Cells fixed to single: " + nbrOfCellFixedToSingle);
	}
	
	
	
	/*
	 * Method that randomly fix lone cells in the game grid that respect all the criteria given as parameters.
	 *
	 * @param fixedCells		the matrix indicating which cells has been fixed in the game grid
	 * @param nbrOfCellsToFix	the number of cells that the method will try to fix in the game grid
	 * @param isolatedCellsOnly	indicate the method is only allowed to fix cells that don't have any fixed neighbour cells
	 * @param fixHoleCells		indicate if the method is allowed to fix a cell that is a hole cell (that is surrounded by two fixed neighbour cell)
	 * @param fixDeducibleCells	indicate if the method is allowed to fix a cell that could be deduced based on the value of the neighbouring cells
	 * @return					the number of cells that the method was able to successfully fix in the game grid
	 */
	private static int fixCells(boolean[][] fixedCells, byte[][] solutionGrid, int nbrOfCellsToFix, boolean isolatedCellsOnly, boolean fixHoleCells, boolean fixDeducibleCells) {
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = getFixableCellsPool(fixedCells);
	
		int nbrOfFixedCells = 0;
		while (fixableCellsPool.size() > 0 && nbrOfFixedCells < nbrOfCellsToFix) {
			Random randomInd = new Random();
			
			//Randomly choose a cell to try to fix (amongst the pool of cell that can be considered to form a separate pair)
			Integer randomRowInd = (Integer)fixableCellsPool.keySet().toArray()[randomInd.nextInt(fixableCellsPool.keySet().size())];
			Integer randomColInd = (Integer)fixableCellsPool.get(randomRowInd).toArray()[randomInd.nextInt(fixableCellsPool.get(randomRowInd).size())];
			
			if (isValidCellToFix(randomRowInd, randomColInd, fixedCells, solutionGrid, isolatedCellsOnly, fixHoleCells, fixDeducibleCells)) {
				fixedCells[randomRowInd][randomColInd] = true;
				nbrOfFixedCells++;
			}
			
			//The randomly chosen cell has been fixed or can't be considered to form a hole : 
			//in either cases, must remove it from the pool of fixable cells.
			fixableCellsPool.get(randomRowInd).remove(randomColInd);
			if (fixableCellsPool.get(randomRowInd).size() == 0)
				fixableCellsPool.remove(randomRowInd);
		}
		
		return nbrOfFixedCells;
	}
	
	
	//=======================================================================================================================
	//=======================================================================================================================
	//=======================================================================================================================
	
	

	
	
	/*
	 * Method that return all the cell in the grid that haven't been fixed yet (all the cells that are free) and thus 
	 * that can potentially be fixed by one of the method used to generate a starting game grid.
	 * 
	 * @param fixedCells	the matrix indicating which cells has been fixed in the game grid
	 * @return				the list of cells that are still free in the game grid
	 */
	private static Hashtable<Integer, HashSet<Integer>> getFixableCellsPool(boolean[][] fixedCells) {
		Hashtable<Integer, HashSet<Integer>> fixableCellsPool = new Hashtable<>();
		for (int rowInd = 0 ; rowInd < fixedCells.length ; rowInd++)
			for (int colInd = 0 ; colInd < fixedCells[rowInd].length ; colInd++)
				if (!fixedCells[rowInd][colInd]) {
					if (!fixableCellsPool.containsKey(rowInd))
						fixableCellsPool.put(rowInd, new HashSet<Integer>());
					
					fixableCellsPool.get(rowInd).add(colInd);
				}
		
		return fixableCellsPool;
	}
			
	/*
	 * Method that verify is a cell could be fixed in the starting game grid by one of the method,
	 * according to the criteria given as parameters.
	 * 
	 * @param rowInd			the row index of the given cell
	 * @param colInd			the column index of the given cell
	 * @param fixedCells		the matrix indicating which cells has been fixed in the game grid
	 * @param isolatedCellsOnly	indicate if a method is only allowed to fix cells that don't have any fixed neighbour cells
	 * @param fixHoleCells		indicate if a method is allowed to fix a cell that is a hole cell (that is surrounded by two fixed neighbour cell)
	 * @param fixDeducibleCells	indicate if a method is allowed to fix a cell that could be deduced based on the value of the neighbouring cells
	 * @return					true if the cell is valid to be fix, false otherwise
	 */
	private static boolean isValidCellToFix(int rowInd, int colInd, boolean[][] fixedCells, byte[][] solutionGrid, boolean isolatedCellsOnly, boolean fixHoleCells, boolean fixDeducibleCells) {
		return	(!isolatedCellsOnly || getCellNeighbours(rowInd, colInd, fixedCells, 1, BINAIRO_FIXED_CELL).size() == 0) &&
				(fixDeducibleCells || !cellValueIsDeducible(rowInd, colInd, fixedCells, solutionGrid)) &&
				(fixHoleCells || !cellIsAHole(rowInd, colInd, fixedCells));
	}
	
	
	/*
	 * Method that return the horizontal and vertical neighbours of a given cell that are of a certain type and at a certain
	 * distance from the cell.
	 * 
	 * @param rowInd					the row index of the given cell
	 * @param colInd					the column index of the given cell
	 * @param fixedCells				the matrix indicating which cells has been fixed in the game grid
	 * @param distance					the distance from the given cell of to cells to return
	 * @param typeOfNeighboursToReturn	the type of neighbouring cells to return (fixed cells only, free cells only or both)
	 * @return							the list of horizontal and vertical neighbouring cells around the given cell that satisfies all criteria and that do not
	 * 									exceed the bounds of the grid
	 */
	private static ArrayList<int[]> getCellNeighbours(int rowInd, int colInd, boolean[][] fixedCells, int distance, byte typeOfNeighboursToReturn) {
		ArrayList<int[]> neighbours = new ArrayList<>();
		
		if (rowInd >= distance)
			if (isValidNeighbour(fixedCells[rowInd-distance][colInd], typeOfNeighboursToReturn))
				neighbours.add(new int[]{rowInd-distance, colInd});
		
		if (rowInd < fixedCells.length-distance)
			if (isValidNeighbour(fixedCells[rowInd+distance][colInd], typeOfNeighboursToReturn))
				neighbours.add(new int[]{rowInd+distance, colInd});
		
		if (colInd >= distance)
			if (isValidNeighbour(fixedCells[rowInd][colInd-distance], typeOfNeighboursToReturn))
				neighbours.add(new int[]{rowInd, colInd-distance});

		if (colInd < fixedCells[rowInd].length-distance)
			if (isValidNeighbour(fixedCells[rowInd][colInd+distance], typeOfNeighboursToReturn))
				neighbours.add(new int[]{rowInd, colInd+distance});
		
		return neighbours;
	}
	
	/*
	 * Method that count the number of neighbouring cells (horizontally or vertically) with the same value has the given cell,
	 * that are at a certain distance from the cell and of a certain type.
	 * 
	 * @param rowInd					the row index of the given cell
	 * @param colInd					the column index of the given cell
	 * @param fixedCells				the matrix indicating which cells has been fixed in the game grid
	 * @param cellsValue				the matrix containing the values of the cells
	 * @param distance					the distance from the given cell of to cells to return
	 * @param typeofNeighboursToTest	the type of neighbouring cells to return (fixed cells only, free cells only or both)
	 * @return							the list of horizontal and vertical neighbouring cells around the given cell that satisfies all criteria and that do not
	 * 									exceed the bounds of the grid
	 */
	/*
	private static int getNbrOfIdenticalNeighbours(int rowInd, int colInd, boolean[][] fixedCells, byte[][] solutionGrid, int distance, byte typeOfNeighboursToTest) {
		ArrayList<int[]> cellNeighbours = getCellNeighbours(rowInd, colInd, fixedCells, distance, typeOfNeighboursToTest);		
		byte cellValue = solutionGrid[rowInd][colInd];
		
		int nbrIdenticalNeighbours = 0;
		for (int[] currNeighbour : cellNeighbours) {
			if (cellValue == solutionGrid[currNeighbour[0]][currNeighbour[1]])
				nbrIdenticalNeighbours++;
		}
		
		
		return nbrIdenticalNeighbours;
	}
	*/
	
	/*
	 * Method to verify if a given neighbouring cell is valid and should be added to the neighbours list
	 * 
	 * @param neighbourCellValue		the value of the neighbouring cell to examine
	 * @param typeOfNeighboursToReturn	the type of neighbouring cells to return
	 * @return							true if the neighbouring cell is valid and should be added to the list, false otherwise
	 */
	private static boolean isValidNeighbour(boolean neighbourCellValue, byte typeOfNeighboursToReturn) {
		if (typeOfNeighboursToReturn == BINAIRO_BOTH)
			return true;
		else if (typeOfNeighboursToReturn == BINAIRO_FIXED_CELL && neighbourCellValue)
			return true;
		else if (typeOfNeighboursToReturn == BINAIRO_FREE_CELL && !neighbourCellValue)
			return true;
		else
			return false;
	}
	
	
	/*
	 * Method to check if a given cell could be deduced by the value of the neighbouring cells
	 * 
	 * @param rowInd		row index of the cell to check
	 * @param colInd		column index of the cell to check
	 * @param fixedCells	the matrix indicating which cells of the game grid are fixed
	 * @param cellsvalue	the matrix containing the values of the cells
	 * @return				true if the given cell can be deduced, false otherwise
	 */
	private static boolean cellValueIsDeducible(int rowInd, int colInd, boolean[][] fixedCells, byte[][] solutionGrid) {
		return (isNextToFixedIdenticalPair(rowInd, colInd, fixedCells, solutionGrid) || isBetweenIdenticalCells(rowInd, colInd, fixedCells, solutionGrid));
	}
	
	
	/*
	 * Method to check if a given cell is next to a fixed pair of identical value (horizontally or vertically) and thus can be deduced.
	 * 
	 * @param rowInd		row index of the cell to check
	 * @param colInd		column index of the cell to check
	 * @param fixedCells	the matrix indicating which cells of the game grid are fixed
	 * @param cellsvalue	the matrix containing the values of the cells
	 * @return				true if the given cell is next to a fixed pair of identical cells, false otherwise
	 */
	private static boolean isNextToFixedIdenticalPair(int rowInd, int colInd, boolean[][] fixedCells, byte[][] solutionGrid) {
		if (rowInd > 1 && fixedCells[rowInd-1][colInd] && fixedCells[rowInd-2][colInd] && solutionGrid[rowInd-1][colInd] == solutionGrid[rowInd-2][colInd])
			return true;
		
		if (rowInd < fixedCells.length-2 && fixedCells[rowInd+1][colInd] && fixedCells[rowInd+2][colInd] && solutionGrid[rowInd+1][colInd] == solutionGrid[rowInd+2][colInd])
			return true;
		
		if (colInd > 1 && fixedCells[rowInd][colInd-1] && fixedCells[rowInd][colInd-2] && solutionGrid[rowInd][colInd-1] == solutionGrid[rowInd][colInd-2])
			return true;
		
		if (colInd < fixedCells[rowInd].length-2 && fixedCells[rowInd][colInd+1] && fixedCells[rowInd][colInd+2] && solutionGrid[rowInd][colInd+1] == solutionGrid[rowInd][colInd+2])
			return true;
		
	
		return false;
	}
	
	
	/*
	 * Method to check if a given cell is between two identical cells (horizontally of vertically) and thus can be deduced.
	 * 
	 * @param rowInd		row index of the cell to check
	 * @param colInd		column index of the cell to check
	 * @param fixedCells	the matrix indicating which cells of the game grid are fixed
	 * @param cellsvalue	the matrix containing the values of the cells
	 * @return				true if the given cell is between two identical cells, false otherwise
	 */
	private static boolean isBetweenIdenticalCells(int rowInd, int colInd, boolean[][] fixedCells, byte[][] solutionGrid) {
		if (rowInd > 0 && rowInd < fixedCells.length-1 && fixedCells[rowInd-1][colInd] && fixedCells[rowInd+1][colInd] && solutionGrid[rowInd-1][colInd] == solutionGrid[rowInd+1][colInd])
			return true;
		
		if (colInd > 0 && colInd < fixedCells[rowInd].length-1 && fixedCells[rowInd][colInd-1] && fixedCells[rowInd][colInd+1] && solutionGrid[rowInd][colInd-1] == solutionGrid[rowInd][colInd+1])
			return true;
		
		
		return false;
	}
	
	
	/*
	 * Method to check if a given cell is a "hole cell" (that is surrounded by two cells that has been fixed).
	 * 
	 * @param rowInd		row index of the cell to check
	 * @param colInd		column index of the cell to check
	 * @param fixedCells	the matrix indicating which cells has been fixed
	 * @return				true if the given cell is a "hole cell", false otherwise
	 */
	private static boolean cellIsAHole(int rowInd, int colInd, boolean[][] fixedCells) {
		if (rowInd > 0 && rowInd < fixedCells.length-1 && fixedCells[rowInd-1][colInd] && fixedCells[rowInd][colInd])
			return true;
		else if (colInd > 0 && colInd < fixedCells[rowInd].length-1 && fixedCells[rowInd][colInd-1] && fixedCells[rowInd][colInd+1])
			return true;
		else
			return false;
	}
}
