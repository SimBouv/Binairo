package binarygamelogic;

import java.util.*;





public class GameGrid extends GameGridInterface {
	private int gridSize;
	private byte gameLevel;
	private byte[][] solutionGrid;
	private boolean[][] fixedCells;
	private byte[][] cellsValue;
	private boolean[][] cellsInASerie;
	private boolean[] validRows;
	private boolean[] validColumns;
	private boolean[][] identicalRows;
	private boolean[][] identicalColumns;
	
	
	public static void printGrid(byte[][] grid) {
		for (int i = 0 ; i < grid.length ; i++) {
			for (int j = 0 ; j < grid.length ; j++)
				if (grid[i][j] != Game.CELLVALUE_EMPTY)
					System.out.print(grid[i][j] + " ; ");
				else
					System.out.print(" " + " ; ");
			
			System.out.println();
		}
	}
	
	
	public GameGrid(int gridSize, byte gameLevel) throws IllegalArgumentException {		
		initializePlayGrid(gridSize, gameLevel);
	}
	
	public void initializePlayGrid(int gridSize, byte gameLevel) {
		//Set the size of the grid
		setGridSize(gridSize);
		
		//Set the difficulty of the game
		setGameLevel(gameLevel);
		
		//Get a randomly generated game grid
		this.solutionGrid = SolutionGridGenerator.generateSolutionGrid(this.gridSize);
		
		//Get a randomly generated starting game grid
		this.fixedCells = StartingGameGridGenerator.getStartingGrid(solutionGrid, gameLevel);
		
		this.cellsValue = new byte[this.gridSize][this.gridSize];
		for (byte i = 0 ; i < this.gridSize ; i++)
			for (byte j = 0 ; j < this.gridSize ; j++)
				if (this.fixedCells[i][j])
					this.cellsValue[i][j] = this.solutionGrid[i][j];
				else
					this.cellsValue[i][j] = Game.CELLVALUE_EMPTY;

		
		System.out.println("Number of solutions: " + GameGridVerifier.getNbrOfSolutions(cellsValue));
		
		this.cellsInASerie = new boolean[this.gridSize][this.gridSize];
		
		
		this.validRows = new boolean[this.gridSize];
		this.validColumns = new boolean[this.gridSize];
		for (byte i = 0 ; i < this.gridSize ; i++) {
			this.validRows[i] = true;
			this.validColumns[i] = true;
		}
		
		
		this.identicalRows = new boolean[this.gridSize][this.gridSize];
		this.identicalColumns = new boolean[this.gridSize][this.gridSize];
	}
	
	
	public int getGridSize() {
		return gridSize;
	}
	
	public void setGridSize(int gridSize) {
		//Check if the size of the grid is valid (must be a positive even number)
		if (gridSize <= 1 || (gridSize & 1) != 0)
			throw new IllegalArgumentException("Invalid size for the grid : must be greater than 1 and be an even number.");
		else
			this.gridSize = gridSize;
	}
	
	public byte getGameLevel() {
		return gameLevel;
	}
	
	public byte getCellValue(int rowInd, int colInd) {
		return cellsValue[rowInd][colInd];
	}
	
	public void setCellValue(int rowInd, int colInd, byte value) {
		if (validCellValue(value) && !fixedCells[rowInd][colInd] && value != cellsValue[rowInd][colInd]) {
			LinkedList<ModifiedCellData> modifiedCells = new LinkedList<ModifiedCellData>();
			
			addModifiedCell(rowInd, colInd, modifiedCells);
			cellsValue[rowInd][colInd] = value;
			
			updateIdenticalCellsSeries(rowInd, colInd, modifiedCells);
			updateRowValidity(rowInd, modifiedCells);
			updateColumnValidity(colInd, modifiedCells);
			updateIdenticalRows(rowInd, modifiedCells);
			updateIdenticalColumns(colInd, modifiedCells);
			
			for (ModifiedCellData currModifiedCell : modifiedCells) {
				firePropertyEvent("CellChange", currModifiedCell.getOldCellValues(), getCellInfos(currModifiedCell.getRowInd(), currModifiedCell.getColInd()));
			}
			
			if (isGridComplete()) {
				firePropertyEvent("GridCompleted", null, isCurrSolutionCorrect());
			}
		}
	}
	
	public byte getCellSolutionValue(int rowInd, int colInd) {
		return this.solutionGrid[rowInd][colInd];
	}
	
	public boolean isCellFixed(int rowInd, int colInd) {
		return fixedCells[rowInd][colInd];
	}
	
	public void fixCell(int rowInd, int colInd) {
		if (!fixedCells[rowInd][colInd]) {
			setCellValue(rowInd, colInd, solutionGrid[rowInd][colInd]);
						
			GameGridCell oldCell = getCellInfos(rowInd, colInd);
			fixedCells[rowInd][colInd] = true;
			GameGridCell newCell = getCellInfos(rowInd, colInd);
			firePropertyEvent("CellChange", oldCell, newCell);
		}
	}
	
	public void unfixCell(int rowInd, int colInd) {
		if (fixedCells[rowInd][colInd]) {
			GameGridCell oldCell = getCellInfos(rowInd, colInd);
			fixedCells[rowInd][colInd] = false;
			GameGridCell newCell = getCellInfos(rowInd, colInd);
			firePropertyEvent("CellChange", oldCell, newCell);
		}
	}
	
	public boolean isCellInASerie(int rowInd, int colInd) {
		return cellsInASerie[rowInd][colInd];
	}
	
	public boolean isRowValid(int rowInd) {
		return validRows[rowInd];
	}
	
	public boolean isColumnValid(int colInd) {
		return validColumns[colInd];
	}
	
	public boolean isRowIdentical(int rowInd) {
		for (byte i = 0 ; i < identicalRows[rowInd].length ; i++) {
			if (identicalRows[rowInd][i])
				return true;
		}
		
		return false;
	}
	
	public boolean isColumnIdentical(int colInd) {
		for (byte i = 0 ; i < identicalColumns[colInd].length ; i++) {
			if (identicalColumns[colInd][i])
				return true;
		}
		
		return false;
	}
	
	public boolean isGridComplete() {
		for (byte i = 0 ; i < cellsValue.length ; i++) {
			for (byte j = 0 ; j < cellsValue[i].length ; j++) {
				if (cellsValue[i][j] == Game.CELLVALUE_EMPTY)
					return false;
			}
		}
		
		return true;
	}
	
	public boolean isCurrSolutionCorrect() {
		for (byte i = 0 ; i < cellsValue.length ; i++) {
			for (byte j = 0 ; j < cellsValue[i].length ; j++) {
				if (cellsValue[i][j] == Game.CELLVALUE_EMPTY || cellsValue[i][j] != solutionGrid[i][j])
					return false;
			}
		}
		
		return true;
	}
	
	
	
	
	
	
	
	private boolean validCellValue(byte cellValue) {
		switch (cellValue) {
			case Game.CELLVALUE_EMPTY : 	;
			case Game.CELLVALUE_ZERO : 	;
			case Game.CELLVALUE_ONE : 	;
											 	return true;
			default :							return false;
		}
	}
	
	private void setGameLevel(byte gameLevel) {
		switch (gameLevel) {
			case StartingGameGridGenerator.BINAIRO_LEVEL_ONE :		; 
			case StartingGameGridGenerator.BINAIRO_LEVEL_TWO : 		;
			case StartingGameGridGenerator.BINAIRO_LEVEL_THREE :	this.gameLevel = gameLevel;
																	break;
												
			
			default :	throw new IllegalArgumentException("Invalid difficulty level.");
		}
	}
	
	
	/**********************************
	private boolean isValidGridLevel(byte gridLevel) {
		switch (gridLevel) {
			case StartingGameGridGenerator.BINAIRO_LEVEL_ONE : 		;
			case StartingGameGridGenerator.BINAIRO_LEVEL_TWO : 		;
			case StartingGameGridGenerator.BINAIRO_LEVEL_THREE :	return true;
										
			default :	return false;
		}
	}
	***********************************/
	
	private GameGridCell getCellInfos(int rowInd, int colInd) {
		boolean isInIdenticalRow = isCellInIdenticalRow(rowInd);
		boolean isInIdenticalColumn = isCellInIdenticalColumn(colInd);
		return new GameGridCell(cellsValue[rowInd][colInd], rowInd, colInd, fixedCells[rowInd][colInd], cellsInASerie[rowInd][colInd], validRows[rowInd], validColumns[colInd], isInIdenticalRow, isInIdenticalColumn);
	}
	
	private void addModifiedCell(int rowInd, int colInd, LinkedList<ModifiedCellData> modifiedCells) {
		ModifiedCellData newModifiedCell = new ModifiedCellData(rowInd, colInd, null);
		
		if (!modifiedCells.contains(newModifiedCell)) {
			newModifiedCell.setOldCellValues(getCellInfos(rowInd, colInd));
			modifiedCells.add(newModifiedCell);
		}
	}

	/*
	private boolean cellAlreadyAdded(byte rowInd, byte colInd, LinkedList<byte[]> modifiedCells) {
		Iterator<byte[]> modifiedCellsItr;
		byte[] currModifiedCell;
		
		modifiedCellsItr = modifiedCells.iterator();
		while (modifiedCellsItr.hasNext()) {
			currModifiedCell = modifiedCellsItr.next();
			if (currModifiedCell[0] == rowInd && currModifiedCell[1] == colInd)
				return true;
		}
		
		return false;
	}
	*/
	
	

	
	
	
	
	private void updateIdenticalCellsSeries(int rowInd, int colInd, LinkedList<ModifiedCellData> modifiedCells) {
		int nextRowInd;
		int nextColInd;
		boolean currCellInIdenticalCellsSerie;
		
		for (int i = -2 ; i <= 2 ; i++) {
			nextRowInd = rowInd+i;
			nextColInd = colInd+i;
			
			if (nextRowInd >= 0 && nextRowInd <= cellsValue.length -1 && nextColInd >= 0 && nextColInd <= cellsValue[rowInd].length-1) {
				currCellInIdenticalCellsSerie = !GameGridVerifier.checkLocalCellConstraints(nextRowInd, nextColInd, this.cellsValue);
				
				if (currCellInIdenticalCellsSerie && !cellsInASerie[nextRowInd][nextColInd]) {
					addModifiedCell(nextRowInd, nextColInd, modifiedCells);
					cellsInASerie[nextRowInd][nextColInd] = true;
				}
				else if (!currCellInIdenticalCellsSerie && cellsInASerie[nextRowInd][nextColInd]) {
					addModifiedCell(nextRowInd, nextColInd, modifiedCells);
					cellsInASerie[nextRowInd][nextColInd] = false;
				}
			}
				
			
			
			//if (nextRowInd >= 0 && nextRowInd <= cellsValue.length-1) {
				
				/*
				if (currCellInIdenticalCellsSerie && !cellsInASerie[nextRowInd][colInd]) {
					addModifiedCell(nextRowInd, colInd, modifiedCells);
					cellsInASerie[nextRowInd][colInd] = true;
				}
				else if (!currCellInIdenticalCellsSerie && cellsInASerie[nextRowInd][colInd]) {
					addModifiedCell(nextRowInd, colInd, modifiedCells);
					cellsInASerie[nextRowInd][colInd] = false;
				}
				*/
						
			//}

			/*
			if (nextColInd >= 0 && nextColInd <= cellsValue[rowInd].length-1) {
				currCellInIdenticalCellsSerie = GameGridVerifier.checkLocalCellConstraints(rowInd, nextColInd, this.cellsValue);
			
				if (currCellInIdenticalCellsSerie && !cellsInASerie[rowInd][nextColInd]) {
					addModifiedCell(rowInd, nextColInd, modifiedCells);
					cellsInASerie[rowInd][nextColInd] = true;
				}
				else if (!currCellInIdenticalCellsSerie && cellsInASerie[rowInd][nextColInd]) {
					addModifiedCell(rowInd, nextColInd, modifiedCells);
					cellsInASerie[rowInd][nextColInd] = false;
				}
			}
			*/
		}
	}
	
	
	
	
	
	private void updateRowValidity(int rowInd, LinkedList<ModifiedCellData> modifiedCells) {
		boolean isRowValid = isNbrValuesValid(cellsValue[rowInd]);
		
		if (isRowValid != validRows[rowInd]) {
			for (int i = 0 ; i < gridSize ; i++) {
				addModifiedCell(rowInd, i, modifiedCells);
			}
			
			validRows[rowInd] = isRowValid;
		}
	}
	
	private void updateColumnValidity(int colInd, LinkedList<ModifiedCellData> modifiedCells) {
		byte[] columnValues = new byte[gridSize];
		for (int i = 0 ; i < cellsValue.length ; i++) {
			columnValues[i] = cellsValue[i][colInd];
		}
		
		boolean isColumnValid = isNbrValuesValid(columnValues);
		
		if (isColumnValid != validColumns[colInd]) {
			for (int i = 0 ; i < gridSize ; i++) {
				addModifiedCell(i, colInd, modifiedCells);
			}
			
			validColumns[colInd] = isColumnValid;
		}
	}
	
	private boolean isCellInIdenticalRow(int rowInd) {
		for (int i = 0 ; i < identicalRows[rowInd].length ; i++) {
			if (identicalRows[rowInd][i])
				return true;
		}
		
		return false;
	}
	
	private boolean isCellInIdenticalColumn(int colInd) {
		for (int i = 0 ; i < identicalColumns[colInd].length ; i++) {
			if (identicalColumns[colInd][i])
				return true;
		}
		
		return false;
	}
	
	private void updateIdenticalRows(int rowInd, LinkedList<ModifiedCellData> modifiedCells) {
		boolean rowsIdentical;
		
		for (int i = 0 ; i < gridSize ; i++) {
			if (i != rowInd) {
				rowsIdentical = areRowsIdentical(rowInd, i);
				if (rowsIdentical != identicalRows[rowInd][i]) {
					for (byte j = 0 ; j < gridSize ; j++) {
						addModifiedCell(rowInd, j, modifiedCells);
						addModifiedCell(i, j, modifiedCells);
					}
					
					identicalRows[rowInd][i] = rowsIdentical;
					identicalRows[i][rowInd] = rowsIdentical;
				}
			}
		}
	}

	private boolean areRowsIdentical(int firstRowInd, int secondRowInd) {
		for (int i = 0 ; i < gridSize ; i++) {
			if (cellsValue[firstRowInd][i] == Game.CELLVALUE_EMPTY || cellsValue[secondRowInd][i] == Game.CELLVALUE_EMPTY)
				return false;
			else if (cellsValue[firstRowInd][i] != cellsValue[secondRowInd][i])
				return false;
		}
		
		return true;
	}
	
	private void updateIdenticalColumns(int colInd, LinkedList<ModifiedCellData> modifiedCells) {
		boolean columnsIdentical;
		
		for (int i = 0 ; i < gridSize ; i++) {
			if (i != colInd) {
				columnsIdentical = areColumnsIdentical(colInd, i);
				if (columnsIdentical != identicalColumns[colInd][i]) {
					for (int j = 0 ; j < gridSize ; j++) {
						addModifiedCell(j, colInd, modifiedCells);
						addModifiedCell(j, i, modifiedCells);
					}
					
					identicalColumns[colInd][i] = columnsIdentical;
					identicalColumns[i][colInd] = columnsIdentical;
				}
			}
		}
	}

	private boolean areColumnsIdentical(int firstColInd, int secondColInd) {
		for (int i = 0 ; i < gridSize ; i++) {
			if (cellsValue[i][firstColInd] == Game.CELLVALUE_EMPTY || cellsValue[i][secondColInd] == Game.CELLVALUE_EMPTY)
				return false;
			else if (cellsValue[i][firstColInd] != cellsValue[i][secondColInd])
				return false;
		}
		
		return true;
	}
	
	
	
	
	/*
	public boolean rowContainsCellsSerie(int rowInd) {
		for (byte i = 0 ; i < cellsInASerie[rowInd].length ; i++) {
			if (cellsInASerie[rowInd][i])
				return true;
		}
		
		return false;
	}
	
	public boolean columnContainsCellsSerie(int colInd) {
		for (byte i = 0 ; i < cellsInASerie.length ; i++) {
			if (cellsInASerie[i][colInd])
				return true;
		}
		
		return false;
	}
	*/
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	


	
	
	
	
	
	
	
	
	

	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Method that return, for a given cell, the number of direct neighbours that are fixed in the grid.
	 * 
	 * @param rowInd			row index of the given cell
	 * @param colInd			column index of the given cell
	 * @param fixedCells		the matrix indicating which cells has been fixed
	 * @return					the number of direct neighbours that have been fixed
	 */
	private int nbrFixedNeighbours(int rowInd, int colInd, boolean[][] fixedCells) {
		int nbrFixedNeighbours = 0;
		
		if (rowInd > 0 && fixedCells[rowInd-1][colInd])
			nbrFixedNeighbours++;
		
		if (rowInd < fixedCells.length-1 && fixedCells[rowInd+1][colInd])
			nbrFixedNeighbours++;
		
		if (colInd > 0 && fixedCells[rowInd][colInd-1])
			nbrFixedNeighbours++;
		
		if (colInd < fixedCells[rowInd].length-1 && fixedCells[rowInd][colInd+1])
			nbrFixedNeighbours++;
		
		return nbrFixedNeighbours;
	}
	
	
	
	
	private int nbrFreeNeighbours(byte[] cell, boolean[][] cellsGrid) {
		int nbrFreeNeighbours = 0;
		
		if (cell[0] > 0 && !cellsGrid[cell[0]-1][cell[1]])
			nbrFreeNeighbours++;
		
		if (cell[0] < cellsGrid.length-1 && !cellsGrid[cell[0]+1][cell[1]])
			nbrFreeNeighbours++;
		
		if (cell[1] > 0 && !cellsGrid[cell[0]][cell[1]-1])
			nbrFreeNeighbours++;
		
		if (cell[1] < cellsGrid[cell[0]].length-1 && !cellsGrid[cell[0]][cell[1]+1])
			nbrFreeNeighbours++;
		
		return nbrFreeNeighbours;
	}

	private byte[] getCellInBetween(byte[] firstCell, byte[] secondCell) {
		byte[] cellInBetween;
		int distanceX = Math.abs(secondCell[0] - firstCell[0]);
		int distanceY = Math.abs(secondCell[1] - firstCell[1]);
		
		if (firstCell[0] != secondCell[0] || firstCell[1] != secondCell[1]) {
			if ((distanceX == 2 || distanceX == 0) && (distanceY == 2 || distanceY == 0)) {
				cellInBetween = new byte[2];
				cellInBetween[0] = (byte)(firstCell[0] + (secondCell[0] - firstCell[0])/2);
				cellInBetween[1] = (byte)(firstCell[1] + (secondCell[1] - firstCell[1])/2);
				return cellInBetween;
			}
		}
		
		return null;
	}
	
	private int getNbrPairs(boolean[][] cellsGrid) {
		int nbrPairs = 0;
		
		for (int i = 0 ; i <= cellsGrid.length-1 ; i++) {
			for (int j = 0 ; j <= cellsGrid[i].length-1 ; j++) {
				if (j <= cellsGrid[i].length-2) {
					if (cellsGrid[i][j] && cellsGrid[i][j+1])
						nbrPairs++;
				}
				
				if (i <= cellsGrid.length-2) {
					if (cellsGrid[i][j] && cellsGrid[i+1][j])
						nbrPairs++;
				}	
			}
		}
		
		return nbrPairs;
	}
		
	private boolean isNbrValuesValid(byte[] values) {
		byte nbrZeros = 0;
		byte nbrOnes = 0;
		
		for (byte i = 0 ; i < values.length ; i++) {
			if (values[i] == Game.CELLVALUE_ZERO)
				nbrZeros++;
			else if (values[i] == Game.CELLVALUE_ONE)
				nbrOnes++;
		}
		
		if (nbrZeros > gridSize/2  || nbrOnes > gridSize/2)
			return false;
		else
			return true;
	}
	

	
	private byte nbrIdenticalRows(byte rowInd) {
		byte nbrIdenticalRows = 0;
		
		for (byte i = 0 ; i < identicalRows.length ; i++) {
			if (identicalRows[rowInd][i])
				nbrIdenticalRows++;
		}
		
		return nbrIdenticalRows;
	}
	
	private byte nbrIdenticalColumns(byte colInd) {
		byte nbrIdenticalColumns = 0;
		
		for (byte i = 0 ; i < identicalColumns.length ; i++) {
			if (identicalColumns[colInd][i])
				nbrIdenticalColumns++;
		}
		
		return nbrIdenticalColumns;
	}
	
	
	private class ModifiedCellData {
		private int rowInd;
		private int colInd;
		GameGridCell oldCellValues;
		
		public ModifiedCellData(int rowInd, int colInd, GameGridCell oldCellValues) {
			this.rowInd = rowInd;
			this.colInd = colInd;
			this.oldCellValues = oldCellValues;
		}
		
		public void setRowInd(byte rowInd) {
			this.rowInd = rowInd;
		}
		
		public int getRowInd() {
			return rowInd;
		}
		
		public void setColInd(byte colInd) {
			this.colInd = colInd;
		}
		
		public int getColInd() {
			return colInd;
		}
		
		public void setOldCellValues(GameGridCell oldCellValues) {
			this.oldCellValues = oldCellValues;
		}
		
		public GameGridCell getOldCellValues() {
			return oldCellValues;
		}
		
		public boolean equals(Object cellData) {
			if (cellData instanceof ModifiedCellData) {
				if (this.getRowInd() == ((ModifiedCellData)cellData).getRowInd() && this.getColInd() == ((ModifiedCellData)cellData).getColInd())
					return true;
				else
					return false;
			}
			else
				return super.equals(cellData);
		}
	}
}
