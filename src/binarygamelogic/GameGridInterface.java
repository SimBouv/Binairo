package binarygamelogic;

import java.beans.*;


public abstract class GameGridInterface {
	protected PropertyChangeSupport propertyChangeSupport;
	
	public GameGridInterface() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcs) {
		propertyChangeSupport.addPropertyChangeListener(pcs);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener pcs) {
		propertyChangeSupport.removePropertyChangeListener(pcs);
	}
	
	protected void firePropertyEvent(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public abstract int getGridSize();
	
	public abstract byte getGameLevel();
	
	public abstract byte getCellValue(int rowInd, int colInd);
	
	public abstract void setCellValue(int rowInd, int colInd, byte value); 
	
	public abstract byte getCellSolutionValue(int rowInd, int colInd);
	
	public abstract void fixCell(int rowInd, int colInd);
	
	public abstract void unfixCell(int rowInd, int colInd);
	
	public abstract boolean isCellFixed(int rowInd, int colInd);
	
	public abstract boolean isCellInASerie(int rowInd, int colInd);
	
	public abstract boolean isRowValid(int rowInd);
	
	public abstract boolean isColumnValid(int colInd);
	
	public abstract boolean isRowIdentical(int rowInd);
	
	public abstract boolean isColumnIdentical(int colInd);
	
	public abstract boolean isGridComplete();
	
	public abstract boolean isCurrSolutionCorrect();
}
