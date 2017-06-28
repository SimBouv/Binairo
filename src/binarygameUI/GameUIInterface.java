package binarygameUI;

import binarygamelogic.*;

public interface GameUIInterface {
	public void setGameGridGameUIController(GameGridGameUIControllerInterface gameGrid);
	
	public GameGridGameUIControllerInterface getGameGridGameUIController();
	
	public void updateGridCompleted(boolean solutionCorrect);
}
