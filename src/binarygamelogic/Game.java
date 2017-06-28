package binarygamelogic;

import javax.swing.*;
import binarygameUI.GameGridUI;
import binarygameUI.GameUI;

import java.util.Locale;

/*
 * Main class of the application, containing the application main(...) method.
 * 
 */


public class Game {
	/* Constant representing the different possible values in a cell :
	 * ZERO : the cell contain a 0
	 * ONE : the cell contain a 1
	 * EMPTY : the cell is empty
	*/
	public static final byte CELLVALUE_ZERO = 0;
	public static final byte CELLVALUE_ONE = 1;
	public static final byte CELLVALUE_EMPTY = -1;
	
	
	
	private final static byte GRID_SIZE = 10;
	
	
	
	
	public static void main(String[] args) {
		GameGrid gameGrid = new GameGrid(GRID_SIZE, StartingGameGridGenerator.BINAIRO_LEVEL_TWO);
		GameGridController gridController = new GameGridController(gameGrid);
		GameGridGameUIController gameGridGameUIController = new GameGridGameUIController(gameGrid);
		
		String[] resourcesName = new String[2];
		resourcesName[0] = "UILabels";
		
		resourcesName[1] = "UIMessages";
		
		Locale.setDefault(new Locale("en"));
		Locale.setDefault(Locale.Category.DISPLAY, new Locale("en"));
		LocalesResources resources = new LocalesResources("Language files", resourcesName, "en");
		LocalesResourcesController resourcesController = new LocalesResourcesController(resources);
		
		/*GameUI gameUI = */
		new GameUI(GRID_SIZE, gridController, gameGridGameUIController, resourcesController);
		
		/*
		binairoGame.setLayout(null);
		binairoGame.setSize(500, 500);
		gameGrid.setBounds(0, 0, binairoGame.getWidth(), binairoGame.getHeight());
		*/
	}
}
