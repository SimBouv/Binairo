package binarygamelogic;

import binarygameUI.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.LinkedList;

public abstract class GameGridGameUIControllerInterface implements PropertyChangeListener {
	protected GameGridInterface registeredGameGrid;
	protected LinkedList<GameUIInterface> registeredGameUIs;
	
	public GameGridGameUIControllerInterface() {
		this.registeredGameUIs = new LinkedList<GameUIInterface>();
	}
	
	public void setGameGrid(GameGridInterface gameGrid) {
		this.registeredGameGrid = gameGrid;
		this.registeredGameGrid.addPropertyChangeListener(this);
	}
	
	public void addGameUI(GameUIInterface gameUI) {
		if (gameUI != null)
			this.registeredGameUIs.add(gameUI);
	}
	
	public void addGameUIs(LinkedList<GameUIInterface> gameUIs) {
		this.registeredGameUIs.addAll(gameUIs);
	}
	
	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().equals("GridCompleted")) {
			for (GameUIInterface gameUI : registeredGameUIs) {
				gameUI.updateGridCompleted((Boolean)pce.getNewValue());
			}
		}
	}
	
	
}
