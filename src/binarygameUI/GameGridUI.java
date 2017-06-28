package binarygameUI;

import binarygamelogic.*;

import java.awt.*;
import java.awt.event.*;
//import java.awt.Robot;

import java.text.ParseException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.MaskFormatter;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.awt.Toolkit;

public class GameGridUI implements GameGridUIInterface, LocalesResourcesUIInterface {
	private final static Color FIXED_CELL_BGCOLOR = new Color(0.9f, 0.9f, 0.9f);
	private final static Color FREE_CELL_BGCOLOR = Color.WHITE;
	private final static Color FIXED_CELL_IN_A_SERIE_BGCOLOR = FIXED_CELL_BGCOLOR;
	private final static Color FREE_CELL_IN_A_SERIE_BGCOLOR = new Color(1.0f, 0.2f, 0.2f);
	private final static Color FIXED_CELL_INVALID_ROW_BGCOLOR = new Color(0.75f, 0.55f, 0.55f);
	private final static Color FREE_CELL_INVALID_ROW_BGCOLOR = new Color (1.0f, 0.8f, 0.8f);
	private final static Color FIXED_CELL_IDENTICAL_ROW_BGCOLOR = new Color(0.55f, 0.55f, 0.75f);
	private final static Color FREE_CELL_IDENTICAL_ROW_BGCOLOR = new Color(0.8f, 0.8f, 1.0f);
	private final static Color GOT_SOLUTION_CELL_BGCOLOR = new Color(1.0f, 1.0f, 0.0f);
	private final static Color FREE_CELL_FGCOLOR = Color.BLACK;
	private final static Color CELL_TRY_FGCOLOR = new Color(0.0f, 0.75f, 1.0f);
	
	private GameGridControllerInterface gameGridController;
	private LocalesResourcesControllerInterface resourcesController;
	
	private JLayeredPane gamePanel;
	private JPanel gameGrid;
	private GridCell[][] gridCells;
	private GridCell selectedCell;
	private JButton choiceZero;
	private JButton choiceOne;
	
	private byte gridSize;
	private boolean visualHints;
	private boolean cellSolution;
	private boolean cellTry;
	
	private LineBorder unselectedCellBorder;
	private LineBorder selectedCellBorder;
	
	private MaskFormatter gridCellMask;
	private GridCellMouseListener cellMouseListener;
	private GridCellFocusListener cellFocusListener;
	private CellPropertyChangeListener cellPropertyChangeListener;
	
	
	class GridCell extends JFormattedTextField {
		private byte posX;
		private byte posY;
		private byte cellValue;
		private boolean isFixed;
		private boolean gotSolution;
		private boolean cellTry;
		private boolean isInASerie;
		private boolean isInValidRow;
		private boolean isInValidColumn;
		private boolean isInIdenticalRow;
		private boolean isInIdenticalColumn;
		
		public GridCell(byte posX, byte posY, byte cellValue, boolean isFixed, boolean isInASerie, boolean isInValidRow, boolean isInValidColumn, boolean isInIdenticalRow, boolean isInIdenticalColumn, MaskFormatter cellFormatter) {
			super(cellFormatter);
			
			this.posX = posX;
			this.posY = posY;
			this.setCellValue(cellValue);
			this.isFixed = isFixed;
			this.gotSolution = false;
			this.cellTry = false;
			this.isInASerie = isInASerie;
			this.isInValidRow = isInValidRow;
			this.isInValidColumn = isInValidColumn;
			this.isInIdenticalRow = isInIdenticalRow;
			this.isInIdenticalColumn = isInIdenticalColumn;
			
			this.setHorizontalAlignment(JTextField.CENTER);
			this.setColumns(1);
			this.setBorder(unselectedCellBorder);
			this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			this.getCaret().setVisible(false);
			this.setHighlighter(null);
			
			this.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "none");
			this.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "none");
		}

		public void setPosX(byte posX) {
			this.posX = posX;
		}
		
		public byte getPosX() {
			return posX;
		}
		
		public void setPosY(byte posY) {
			this.posY = posY;
		}
		
		public byte getPosY() {
			return posY;
		}
		
		public void setCellValue(byte value) {
			if (this.cellValue != value) {
				this.cellValue = value;
				
				GameGridUI gameGridUI = GameGridUI.this;
				gameGridUI.gameGridController.setCellValue(this.posX, this.posY, this.cellValue);
				
				if (value == Game.CELLVALUE_EMPTY) {
					super.setValue(null);
				}
				else if (value == Game.CELLVALUE_ZERO || value == Game.CELLVALUE_ONE) {
					super.setValue(value);
				}
			}
			
			
		}
		
		public byte getCellValue() {
			return this.cellValue;
		}
		
		public void setIsFixed(boolean isFixed) {
			this.isFixed = isFixed;
		}
		
		public boolean getIsFixed() {
			return this.isFixed;
		}
		
		public void setGotSolution(boolean gotSolution) {
			this.gotSolution = gotSolution;
		}
		
		public boolean gotSolution() {
			return this.gotSolution;
		}
		
		public void setCellTry(boolean cellTry) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			this.cellTry = cellTry;
			
			if (this.cellTry)
				this.setForeground(CELL_TRY_FGCOLOR);
			else
				this.setForeground(FREE_CELL_FGCOLOR);
		}
		
		public boolean isCellTry() {
			return this.cellTry;
		}
		
		public void setIsInASerie(boolean isInASerie) {
			this.isInASerie = isInASerie;
		}
		
		public boolean isInASerie() {
			return isInASerie;
		}
		
		public void setIsInValidRow(boolean isInValidRow) {
			this.isInValidRow = isInValidRow;
		}
		
		public boolean isInValidRow() {
			return isInValidRow;
		}
		
		public void setIsInValidColumn(boolean isInValidColumn) {
			this.isInValidColumn = isInValidColumn;
		}
		
		public boolean isInValidColumn() {
			return isInValidColumn;
		}
		
		public void setIsInIdenticalRow(boolean isInIdenticalRow) {
			this.isInIdenticalRow = isInIdenticalRow;
		}
		
		public boolean isInIdenticalRow() {
			return isInIdenticalRow;
		}
		
		public void setIsInIdenticalColumn(boolean isInIdenticalColumn) {
			this.isInIdenticalColumn = isInIdenticalColumn;
		}
		
		public boolean isInIdenticalColumn() {
			return isInIdenticalColumn;
		}
	}
	
	
	
	
	
	
	public GameGridUI(byte gridSize, boolean visualHints, GameGridControllerInterface gameGridController, LocalesResourcesControllerInterface resourcesController) {
		this.gridSize = gridSize;
		this.visualHints = visualHints;
		this.cellSolution = false;
		this.cellTry = false;
		
		this.setGameGridController(gameGridController);
		this.setResourcesController(resourcesController);
		
		this.unselectedCellBorder = new LineBorder(Color.BLACK, 1, false);
		this.selectedCellBorder = new LineBorder(Color.BLUE, 4, false);
		
		this.gridCellMask = null;
		try {
			this.gridCellMask = new MaskFormatter("#");
			this.gridCellMask.setValidCharacters("01");
			this.gridCellMask.setCommitsOnValidEdit(true);
			this.gridCellMask.setValueClass(Byte.class);
		}
		catch (ParseException e) {
		}
		
		gamePanel = new JLayeredPane();
		gamePanel.setOpaque(true);
		gamePanel.setBackground(Color.WHITE);
		gamePanel.setLayout(null);	
		gamePanel.setBorder(null);
		gamePanel.addComponentListener(new GamePanelComponentListener());
		gamePanel.addFocusListener(new GridPanelFocusListener());
		gamePanel.setPreferredSize(new Dimension(500, 500));

		gamePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed UP"), "UP_ArrowKey");
		gamePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed DOWN"), "DOWN_ArrowKey");
		gamePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed LEFT"), "LEFT_ArrowKey");
		gamePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed RIGHT"), "RIGHT_ArrowKey");
		gamePanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("pressed ESCAPE"), "ESCAPE_Key");

		gamePanel.getActionMap().put("UP_ArrowKey", new PanelUPArrowKeyAction());
		gamePanel.getActionMap().put("DOWN_ArrowKey", new PanelDOWNArrowKeyAction());
		gamePanel.getActionMap().put("LEFT_ArrowKey", new PanelLEFTArrowKeyAction());
		gamePanel.getActionMap().put("RIGHT_ArrowKey", new PanelRIGHTArrowKeyAction());
		gamePanel.getActionMap().put("ESCAPE_Key", new PanelESCAPEKeyAction());
		
		createGameGridUI(gridSize);
		
		gamePanel.add(gameGrid);
		gamePanel.setLayer(gameGrid, 0);	
	}
	
	public JLayeredPane getGamePanel() {
		return gamePanel;
	}
	
	public boolean isUsingVisualHints() {
		return this.visualHints;
	}
	
	public void setVisualHints(boolean visualHints) {
		this.visualHints = visualHints;
		updateAllCellsDisplay();
	}
	
	public void setCellSolutionState(boolean cellSolution) {
		this.cellSolution = cellSolution;
	}
	
	public boolean getCellSolutionState() {
		return this.cellSolution;
	}
	
	public void setCellTryState(boolean cellTry, boolean confirmCellTry) {
		this.cellTry = cellTry;
		
		if (!this.cellTry) {
			for (byte i = 0 ; i < this.gridSize ; i++) {
				for (byte j = 0 ; j < this.gridSize ; j++) {
					if (gridCells[i][j].isCellTry()) {
						gridCells[i][j].setCellTry(false);
						
						if (!confirmCellTry) {
							gridCells[i][j].setCellValue(Game.CELLVALUE_EMPTY);
						}
					}
				}
			}
			
			this.gridCells[0][0].requestFocus();
			this.selectedCell.requestFocus();
		}
	}
	
	public boolean getCellTryState() {
		return this.cellTry;
	}
	
	public void setGameGridController(GameGridControllerInterface gameGridController) {
		this.gameGridController = gameGridController;
		this.gameGridController.addGridUI(this);
	}
	
	public GameGridControllerInterface getGameGridController() {
		return this.gameGridController;
	}
	
	public void updateGridCells() {
		GridCell currCell;
		boolean foundSelectedCell = false;
		
		for (byte i = 0 ; i < this.gridSize ; i++) {
			for (byte j = 0 ; j < this.gridSize ; j++) {
				currCell = this.gridCells[i][j];
				
				currCell.setIsFixed(this.gameGridController.isCellFixed(i, j));
				
				if (currCell.getIsFixed()) {
					currCell.setCellValue(this.gameGridController.getCellValue(i, j));
					currCell.setEditable(false);
				}
				else {
					currCell.setCellValue(Game.CELLVALUE_EMPTY);
					currCell.setEditable(true);
					
					if (!foundSelectedCell) {
						foundSelectedCell = true;
						updateSelectedCell(currCell.getPosX(), currCell.getPosY());
					}
				}
				
				currCell.setIsInASerie(this.gameGridController.isCellInASerie(i, j));
				currCell.setIsInValidRow(this.gameGridController.isRowValid(i));
				currCell.setIsInValidColumn(this.gameGridController.isColumnValid(j));
				currCell.setIsInIdenticalRow(this.gameGridController.isRowIdentical(i));
				currCell.setIsInIdenticalColumn(this.gameGridController.isColumnIdentical(j));
				currCell.setGotSolution(false);
			}
		}
		
		updateAllCellsDisplay();
	}
	
	public void updateCellInfo(GameGridCell cell) {
		GridCell cellToUpdate = gridCells[cell.getRowInd()][cell.getColInd()];
		boolean cellHasChanged = false;
		boolean cellInSerieHasChanged = false;

		if (cellToUpdate.getCellValue() != cell.getCellValue()) {
			cellToUpdate.setCellValue(cell.getCellValue());
			cellHasChanged = true;
		}

		if (cellToUpdate.getIsFixed() != cell.isCellFixed()) {
			cellToUpdate.setIsFixed(cell.isCellFixed());
			cellHasChanged = true;
		}
		
		if (cellToUpdate.isInASerie() != cell.isCellInASerie()) {
			cellToUpdate.setIsInASerie(cell.isCellInASerie());
			cellHasChanged = true;
			cellInSerieHasChanged = true;
		}
		
		if (cellToUpdate.isInValidRow() != cell.isInValidRow()) {
			cellToUpdate.setIsInValidRow(cell.isInValidRow());
			cellHasChanged = true;
		}
		
		if (cellToUpdate.isInValidColumn() != cell.isInValidColumn()) {
			cellToUpdate.setIsInValidColumn(cell.isInValidColumn());
			cellHasChanged = true;
		}
		
		if (cellToUpdate.isInIdenticalRow() != cell.isInIdenticalRow()) {
			cellToUpdate.setIsInIdenticalRow(cell.isInIdenticalRow());
			cellHasChanged = true;
		}
		
		if (cellToUpdate.isInIdenticalColumn() != cell.isInIdenticalColumn()) {
			cellToUpdate.setIsInIdenticalColumn(cell.isInIdenticalColumn());
			cellHasChanged = true;
		}
		
		if (cellHasChanged) {
			updateCellDisplay(cellToUpdate);
		
			if (cellInSerieHasChanged) {
				for (byte i = 0 ; i < gridCells[cellToUpdate.getPosX()].length ; i++) {
					updateCellDisplay(gridCells[cellToUpdate.getPosX()][i]);
				}
				
				for (byte i = 0 ; i < gridCells.length ; i++) {
					updateCellDisplay(gridCells[i][cellToUpdate.getPosY()]);
				}
			}
		}
	}

	public void setResourcesController(LocalesResourcesControllerInterface resourcesController) {
		this.resourcesController = resourcesController;
		this.resourcesController.addUI(this);
	}
	
	public LocalesResourcesControllerInterface getResourcesController() {
		return this.resourcesController;
	}
	
	public void updateLocalesResources() {
	}
	
	
	
	
	
	private void createGameGridUI(byte gridSize) {
		gameGrid = new JPanel();
		gameGrid.setOpaque(true);
		gameGrid.setBackground(Color.WHITE);
		gameGrid.setLayout(null);
		gameGrid.setBorder(null);
		gameGrid.addComponentListener(new GridPanelComponentListener());
		gridCells = new GridCell[gridSize][gridSize];
		
		GridCell cell;
		
		cellMouseListener = new GridCellMouseListener();
		cellFocusListener = new GridCellFocusListener();
		cellPropertyChangeListener = new CellPropertyChangeListener();
				
		for (byte i=0 ; i<gridSize ; i++) {
			for (byte j=0 ; j<gridSize ; j++) {
				cell = new GridCell(i, j, Game.CELLVALUE_EMPTY, false, false, true, true, false, false, this.gridCellMask);
				gameGrid.add(cell);
				gridCells[i][j] = cell;
				
				cell.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("pressed 0"), "CellChangeValueKeyPressed");
				cell.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("pressed 1"), "CellChangeValueKeyPressed");
				cell.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("pressed DELETE"), "CellEraseKeyPressed");
				cell.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke("pressed BACK_SPACE"), "CellEraseKeyPressed");
				cell.getActionMap().put("CellChangeValueKeyPressed", new CellChangeValueKeyAction());
				cell.getActionMap().put("CellEraseKeyPressed", new CellEraseKeyAction());
				
				cell.addFocusListener(cellFocusListener);
				cell.addMouseListener(cellMouseListener);
				cell.addPropertyChangeListener("value", cellPropertyChangeListener);
			}
		}
		
		updateGridCells();
	}
	
	private void resizeGridCells() {
		int gridWidth = gameGrid.getWidth();
		int gridHeight = gameGrid.getHeight();
		int gridNbrRows = gridCells.length;
		int gridNbrColumns = gridCells[0].length;
		
		int cellWidth = Math.floorDiv(gridWidth, gridNbrColumns);
		int cellHeight = Math.floorDiv(gridHeight, gridNbrRows);
		
		int gridWidthRemainder = gridWidth - (cellWidth * gridNbrColumns);
		int gridHeightRemainder = gridHeight - (cellHeight * gridNbrRows);
		
		int currCellPosX;
		int currCellPosY;
		for (int i = 0 ; i < gridCells.length ; i++) {
			for (int j = 0 ; j < gridCells.length ; j++) {
				currCellPosX = j * cellWidth + Math.floorDiv(gridWidthRemainder, 2);
				currCellPosY = i * cellHeight + Math.floorDiv(gridHeightRemainder, 2);
				gridCells[i][j].setBounds(currCellPosX, currCellPosY, cellWidth, cellHeight);
			}
		}
	}
	
	private void updateSelectedCell(byte posX, byte posY) {
		if (posX >= 0 && posX < gridSize && posY >= 0 && posY < gridSize) {
			this.clearChoiceButton();
			
			if (this.selectedCell != null)
				this.selectedCell.setBorder(unselectedCellBorder);
			
			this.selectedCell = this.gridCells[posX][posY];
			this.selectedCell.setBorder(selectedCellBorder);
		}
	}
	
	private void updateAllCellsDisplay() {
		for (byte i = 0 ; i < gridSize ; i++) {
			for (byte j = 0 ; j < gridSize ; j++) {
				updateCellDisplay(this.gridCells[i][j]);
			}
		}
	}
	
	private void updateCellDisplay(GridCell cell) {
		if (cell.isEditable()) {
			if (this.visualHints) {
				if (cell.isInASerie())
					cell.setBackground(FREE_CELL_IN_A_SERIE_BGCOLOR);
				else if (!cell.isInValidRow() || !cell.isInValidColumn())
					cell.setBackground(FREE_CELL_INVALID_ROW_BGCOLOR);
				else if ((cell.isInIdenticalRow() && !rowContainsCellsSerie(cell.getPosX())) || (cell.isInIdenticalColumn() && !columnContainsCellsSerie(cell.getPosY())))
					cell.setBackground(FREE_CELL_IDENTICAL_ROW_BGCOLOR);
				else
					cell.setBackground(FREE_CELL_BGCOLOR);
			}
			else
				cell.setBackground(FREE_CELL_BGCOLOR);
		}
		else {
			if (cell.gotSolution()) {
				cell.setBackground(GOT_SOLUTION_CELL_BGCOLOR);
			}
			else if (this.visualHints) {
				if (!cell.isInValidRow() || !cell.isInValidColumn())
					cell.setBackground(FIXED_CELL_INVALID_ROW_BGCOLOR);
				else if ((cell.isInIdenticalRow() && !rowContainsCellsSerie(cell.getPosX())) || (cell.isInIdenticalColumn() && !columnContainsCellsSerie(cell.getPosY())))
					cell.setBackground(FIXED_CELL_IDENTICAL_ROW_BGCOLOR);
				else
					cell.setBackground(FIXED_CELL_BGCOLOR);
			}
			else
				cell.setBackground(FIXED_CELL_BGCOLOR);
		}
	}
	
	private boolean rowContainsCellsSerie(byte rowInd) {
		for (byte i = 0 ; i < gridCells[rowInd].length ; i++) {
			if (gridCells[rowInd][i].isInASerie())
				return true;
		}
		
		return false;
	}
	
	private boolean columnContainsCellsSerie(byte colInd) {
		for (byte i = 0 ; i < gridCells.length ; i++) {
			if (gridCells[i][colInd].isInASerie())
				return true;
		}
		
		return false;
	}	
	
	private void createChoiceButton(GridCell currCell) {
		Rectangle gridCoord = gameGrid.getBounds();
		Rectangle currCellCoord = currCell.getBounds();

		int buttonWidth;
		int buttonHeight;
		int choiceZeroPosX = 0;
		int choiceZeroPosY = 0;
		int choiceOnePosX = 0;
		int choiceOnePosY = 0;
		int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
		Font buttonFont;
		float fontSize;
		Color buttonColor = Color.LIGHT_GRAY;
		GameChoiceButtonFocusListener choiceButtonFocusListener = new GameChoiceButtonFocusListener();

		clearChoiceButton();
		
		choiceZero = new JButton("0");
		choiceOne = new JButton("1");
		
		buttonWidth = (int)(currCellCoord.getWidth() * 0.6);
		buttonHeight = (int)(currCellCoord.getHeight() * 0.6);
		choiceZero.setSize(buttonWidth, buttonHeight);
		choiceOne.setSize(buttonWidth, buttonHeight);

		choiceZero.setBorder(null);
		choiceOne.setBorder(null);
		choiceZero.setBackground(buttonColor);
		choiceOne.setBackground(buttonColor);
		
		
		fontSize = ((float)choiceZero.getHeight()/(float)dpi)*72;
		buttonFont = choiceZero.getFont().deriveFont(fontSize);
		choiceZero.setFont(buttonFont);
		choiceOne.setFont(buttonFont);

		choiceZeroPosX = (int)(gridCoord.getX() + currCellCoord.getX() - choiceZero.getWidth()/2);
		choiceZeroPosY = (int)(gridCoord.getY() + currCellCoord.getY() + currCellCoord.getHeight() - choiceZero.getHeight()/2);
			
		choiceOnePosX = (int)(gridCoord.getX() + currCellCoord.getX() + currCellCoord.getWidth() - choiceOne.getWidth()/2);
		choiceOnePosY = (int)(gridCoord.getY() + currCellCoord.getY() + currCellCoord.getHeight() - choiceOne.getHeight()/2);	
		
		
		gamePanel.add(choiceZero);
		gamePanel.add(choiceOne);
		gamePanel.setLayer(choiceZero, 1);
		gamePanel.setLayer(choiceOne, 1);
		choiceZero.setBounds(choiceZeroPosX, choiceZeroPosY, choiceZero.getWidth(), choiceZero.getHeight());
		choiceOne.setBounds(choiceOnePosX, choiceOnePosY, choiceOne.getWidth(), choiceOne.getHeight());
		choiceZero.addFocusListener(choiceButtonFocusListener);
		choiceOne.addFocusListener(choiceButtonFocusListener);
		
		gamePanel.repaint();
	}
	
	private void clearChoiceButton() {
		if (choiceZero != null) {
			gamePanel.remove(choiceZero);
			choiceZero = null;
		}
		
		if (choiceOne != null) {	
			gamePanel.remove(choiceOne);
			choiceOne = null;
		}
		
		gamePanel.repaint();
	}
	
	
	
	
	
	
	
	class PanelUPArrowKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			if (gameGridUI.selectedCell.getPosX() > 0) {
				gameGridUI.gridCells[gameGridUI.selectedCell.getPosX()-1][gameGridUI.selectedCell.getPosY()].requestFocus();
			}
		}
	}
	
	class PanelDOWNArrowKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			if (gameGridUI.selectedCell.getPosX() < gameGridUI.gridSize-1) {
				gameGridUI.gridCells[gameGridUI.selectedCell.getPosX()+1][gameGridUI.selectedCell.getPosY()].requestFocus();
			}
		}
	}
	
	class PanelLEFTArrowKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			if (gameGridUI.selectedCell.getPosY() > 0) {
				gameGridUI.gridCells[gameGridUI.selectedCell.getPosX()][gameGridUI.selectedCell.getPosY()-1].requestFocus();
			}
		}
	}
	
	class PanelRIGHTArrowKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			if (gameGridUI.selectedCell.getPosY() < gameGridUI.gridSize-1) {
				gameGridUI.gridCells[gameGridUI.selectedCell.getPosX()][gameGridUI.selectedCell.getPosY()+1].requestFocus();
			}
		}
	}
	
	class PanelESCAPEKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI.this.clearChoiceButton();
		}
	}
	
	class GamePanelComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			int gamePanelWidthBorder = 0;
			int gamePanelHeightBorder = 0;
			int gamePanelSize;
			int gamePanelWidth = gameGridUI.gamePanel.getWidth();
			int gamePanelHeight = gameGridUI.gamePanel.getHeight();
			
			if (gamePanelWidth > gamePanelHeight) {
				gamePanelSize = gamePanelHeight;
				gamePanelWidthBorder = (gamePanelWidth - gamePanelHeight)/2;
			}
			else {
				gamePanelSize = gamePanelWidth;
				gamePanelHeightBorder = (gamePanelHeight - gamePanelWidth)/2;
			}
			
			int gridBorder = Math.floorDiv(Math.floorDiv(gamePanelSize, gameGridUI.gridSize+1), 2);
			
			gameGridUI.gameGrid.setBounds(gamePanelWidthBorder + gridBorder, gamePanelHeightBorder + gridBorder, gamePanelSize-(2*gridBorder), gamePanelSize - (2*gridBorder));
		}
	}
	
	class GridPanelComponentListener extends ComponentAdapter {
		public void componentResized(ComponentEvent e) {	
			GameGridUI gameGridUI = GameGridUI.this;
			int dpi = Toolkit.getDefaultToolkit().getScreenResolution();
			GridCell currCell;
			Font cellFont;
			int cellHeight;
			float fontSize;
			
			gameGridUI.resizeGridCells();
			
			if (gameGridUI.gridCells != null) {
				cellFont = gameGridUI.gridCells[0][0].getFont();
				cellHeight = gameGridUI.gridCells[0][0].getHeight();
				fontSize = ((float)cellHeight/(float)dpi)*72;
						
				for (int i = 0 ; i < gameGridUI.gridSize ; i++) {
					for (int j = 0 ; j < gameGridUI.gridSize ; j++) {
						currCell = gameGridUI.gridCells[i][j];
						currCell.setFont(cellFont.deriveFont(fontSize));
					}
				}
			}
			
			if (gameGridUI.choiceZero != null && gameGridUI.choiceOne != null) {
				gameGridUI.createChoiceButton(gameGridUI.selectedCell);
			}
		}
	}
	
	class GridPanelFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			if (gameGridUI.selectedCell != null)
				gameGridUI.selectedCell.requestFocus();
		}
	}
	
	class GameChoiceButtonFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			
			gameGridUI.selectedCell.setCellValue(Byte.valueOf(((JButton)e.getSource()).getText()));	
			gameGridUI.selectedCell.requestFocus();
			clearChoiceButton();
		}
	}
	
	class CellChangeValueKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			GridCell currCell = (GridCell)e.getSource();

			gameGridUI.clearChoiceButton();

			if (currCell.isEditable() && currCell.getValue() != null) {
				currCell.removePropertyChangeListener("value", gameGridUI.cellPropertyChangeListener);
				currCell.setCellValue(Game.CELLVALUE_EMPTY);
				currCell.addPropertyChangeListener("value", gameGridUI.cellPropertyChangeListener);
			}
		}
	}
	
	class CellEraseKeyAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			GridCell currCell = (GridCell)e.getSource();

			gameGridUI.clearChoiceButton();

			if (currCell.isEditable())
				currCell.setCellValue(Game.CELLVALUE_EMPTY);
		}
	}
	
	class GridCellMouseListener extends MouseAdapter {
		public void mousePressed(MouseEvent e) {
			if (SwingUtilities.isRightMouseButton(e)) {
				GridCell currCell = (GridCell)e.getSource();
				if (currCell.isEditable())
					currCell.setCellValue(Game.CELLVALUE_EMPTY);
			}
		}
		
		public void mouseClicked(MouseEvent e) {
			if (SwingUtilities.isLeftMouseButton(e)) {
				GameGridUI gameGridUI = GameGridUI.this;
				GridCell currCell = (GridCell)e.getSource();
				
				if (!gameGridUI.getCellSolutionState()) {
					if (gameGridUI.choiceZero != null || gameGridUI.choiceOne != null) {
						gameGridUI.clearChoiceButton();
					}
					else {	
						if (currCell.isEditable())
							gameGridUI.createChoiceButton(currCell);
					}
				}
				else if (currCell.isEditable()) {
					gameGridUI.clearChoiceButton();
					currCell.setCellValue(gameGridUI.gameGridController.getCellSolutionValue(currCell.getPosX(), currCell.getPosY()));
					currCell.setGotSolution(true);
					currCell.setEditable(false);
					gameGridUI.updateCellDisplay(currCell);
				}
			}
		}
	}
	
	class GridCellFocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			GridCell newSelectedCell = (GridCell)e.getSource();
			
			if (gameGridUI.selectedCell != newSelectedCell)
				gameGridUI.updateSelectedCell(newSelectedCell.getPosX(), newSelectedCell.getPosY());
			
			newSelectedCell.getCaret().setVisible(false);
		}
	}
	
	class CellPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent e) {
			GameGridUI gameGridUI = GameGridUI.this;
			GridCell currCell = (GridCell)e.getSource();
			
			if (e.getNewValue() != e.getOldValue()) {
				byte newCellValue;
				
				if (e.getNewValue() == null)
					newCellValue = Game.CELLVALUE_EMPTY;
				else
					newCellValue = ((Byte)e.getNewValue()).byteValue();
				
				currCell.setCellValue(newCellValue);
				currCell.setCellTry(gameGridUI.getCellTryState());
			}
		}
	}
}
