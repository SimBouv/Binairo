package binarygamelogic;

import binarygameUI.*;

import java.util.LinkedList;

public class GameGridGameUIController extends GameGridGameUIControllerInterface {
	public GameGridGameUIController() {
		super();
	}
	
	public GameGridGameUIController(GameGridInterface gameGrid) {
		super();
		this.setGameGrid(gameGrid);
	}
	
	public GameGridGameUIController(GameGridInterface gameGrid, LinkedList<GameUIInterface> gameUIs) {
		super();
		this.setGameGrid(gameGrid);
		this.addGameUIs(gameUIs);
	}
}
