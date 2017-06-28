package binarygamelogic;

import binarygameUI.GameGridUIInterface;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.LinkedList;

public abstract class GameGridControllerInterface implements PropertyChangeListener {
	protected GameGridInterface registeredGameGrid;
	protected LinkedList<GameGridUIInterface> registeredGridUIs; 
	
	public GameGridControllerInterface() {
		this.registeredGridUIs = new LinkedList<GameGridUIInterface>();
	}
	
	public void setGameGrid(GameGridInterface gameGrid) {
		this.registeredGameGrid = gameGrid;
		this.registeredGameGrid.addPropertyChangeListener(this);
	}
	
	public void addGridUI(GameGridUIInterface gridUI) {
		if (gridUI != null)
			this.registeredGridUIs.add(gridUI);
	}
	
	public void addGridUIs(LinkedList<GameGridUIInterface> gridUIs) {
		this.registeredGridUIs.addAll(gridUIs);
	}
	
	public void removeGridUI(GameGridUIInterface gridUI) {
		this.registeredGridUIs.remove(gridUI);
	}
	
	public void removeGridUIs(LinkedList<GameGridUIInterface> gridUIs) {
		this.registeredGridUIs.removeAll(gridUIs);
	}
	
	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().equals("CellChange")) {
			for (GameGridUIInterface gridUI : this.registeredGridUIs) {
				gridUI.updateCellInfo((GameGridCell)pce.getNewValue());
			}
		}
	}
		
	public abstract int getGridSize();
	
	public abstract byte getGameLevel();
	
	public abstract byte getCellValue(byte rowInd, byte colInd);
	
	public abstract void setCellValue(byte rowInd, byte colInd, byte value);
	
	public abstract byte getCellSolutionValue(byte rowInd, byte colInd);
	
	public abstract boolean isCellFixed(byte rowInd, byte colInd);
	
	public abstract boolean isCellInASerie(byte rowInd, byte colInd);
	
	public abstract boolean isRowValid(byte rowInd);
	
	public abstract boolean isColumnValid(byte colInd);
	
	public abstract boolean isRowIdentical(byte rowInd);
	
	public abstract boolean isColumnIdentical(byte colInd);
	
	public abstract boolean isGridComplete();
	
	public abstract boolean isCurrSolutionCorrect();
}
