package binarygamelogic;

import binarygameUI.GameGridUIInterface;

import java.util.LinkedList;


public class GameGridController extends GameGridControllerInterface {		
	public GameGridController() {
		super();
	}
	
	public GameGridController(GameGridInterface gameGrid) {
		super();
		this.setGameGrid(gameGrid);
	}
	
	public GameGridController(GameGridInterface gameGrid, LinkedList<GameGridUIInterface> gridUIs) {
		super();
		this.setGameGrid(gameGrid);
		this.addGridUIs(gridUIs);
	} 
		
	public int getGridSize() {
		return this.registeredGameGrid.getGridSize();
	}
	
	public byte getGameLevel() {
		return this.registeredGameGrid.getGameLevel();
	}
	
	public byte getCellValue(byte rowInd, byte colInd) {
		return this.registeredGameGrid.getCellValue(rowInd, colInd);
	}
	
	public void setCellValue(byte rowInd, byte colInd, byte value) {
		this.registeredGameGrid.setCellValue(rowInd, colInd, value);
	}
	
	public byte getCellSolutionValue(byte rowInd, byte colInd) {
		return this.registeredGameGrid.getCellSolutionValue(rowInd, colInd);
	}
	
	public boolean isCellFixed(byte rowInd, byte colInd) {
		return this.registeredGameGrid.isCellFixed(rowInd, colInd);
	}
	
	public void fixCell(byte rowInd, byte colInd) {
		this.registeredGameGrid.fixCell(rowInd, colInd);
	}
	
	public void unfixCell(byte rowInd, byte colInd) {
		this.registeredGameGrid.unfixCell(rowInd, colInd);
	}
	
	public boolean isCellInASerie(byte rowInd, byte colInd) {
		return this.registeredGameGrid.isCellInASerie(rowInd, colInd);
	}
	
	public boolean isRowValid(byte rowInd) {
		return this.registeredGameGrid.isRowValid(rowInd);
	}
	
	public boolean isColumnValid(byte colInd) {
		return this.registeredGameGrid.isColumnValid(colInd);
	}
	
	public boolean isRowIdentical(byte rowInd) {
		return this.registeredGameGrid.isRowIdentical(rowInd);
	}
	
	public boolean isColumnIdentical(byte colInd) {
		return this.registeredGameGrid.isColumnIdentical(colInd);
	}
	
	public boolean isGridComplete() {
		return this.registeredGameGrid.isGridComplete();
	}
	
	public boolean isCurrSolutionCorrect() {
		return this.registeredGameGrid.isCurrSolutionCorrect();
	}
}
