package binarygameUI;

import binarygamelogic.*;

public interface GameGridUIInterface {
	public void setGameGridController(GameGridControllerInterface gameGridController);
	
	public GameGridControllerInterface getGameGridController();
	
	public abstract void updateCellInfo(GameGridCell cell);
}
