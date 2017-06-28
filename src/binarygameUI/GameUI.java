package binarygameUI;

import binarygamelogic.*;

import java.util.Locale;
import java.util.PropertyResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLayeredPane;
import javax.swing.JButton;
import javax.swing.JToggleButton;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.AbstractAction;
import javax.swing.KeyStroke;
import javax.swing.border.BevelBorder;

import binarygamelogic.GameGrid;
import binarygamelogic.GameGridCell;
import binarygamelogic.GameGridController;
import binarygamelogic.GameGridControllerInterface;
import binarygamelogic.LocalesResourcesControllerInterface;

import java.awt.Window;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.*;



public class GameUI implements GameUIInterface, LocalesResourcesUIInterface {
	private byte gridSize;
	private boolean visualHints;
	private boolean inLocaleLanguage;
	
	private GameGridControllerInterface gameGridController;
	private GameGridGameUIControllerInterface gameGridGameUIController;
	private LocalesResourcesControllerInterface resourcesController;
	
	private JFrame binairoGame;
	private JPanel binairoGameUIPanel;
	
	private GameGridUI gameGridUI;
	
	
	
	private OKCancelWindow lsUIWindow;
	private LanguageSelectionUI lsUI;
	private Color lsUIColor;
	
	private OKCancelWindow newGameUIWindow;
	private NewGameUI newGameUI;
	private Color newGameUIColor;
	
	private MainUIBar mainUIBar;
	private Color mainUIColor;
	
	
	
	
	
	
	public GameUI(byte gridSize, GameGridControllerInterface gameGridController, GameGridGameUIControllerInterface gameGridGameUIController, LocalesResourcesControllerInterface resourcesController) {		
		this.gridSize = gridSize;
		this.visualHints = true;
		this.inLocaleLanguage = true;
		
		this.gameGridController = gameGridController;
		this.setGameGridGameUIController(gameGridGameUIController);
		this.setResourcesController(resourcesController);
		
		this.lsUIColor = Color.WHITE;
		this.mainUIColor = Color.WHITE;
		this.newGameUIColor = Color.WHITE;
		
		createBinairoGameUI();
	}

	public void setGameGridGameUIController(GameGridGameUIControllerInterface gameGridGameUIController) {
		this.gameGridGameUIController = gameGridGameUIController;
		this.gameGridGameUIController.addGameUI(this);
	}
	
	public GameGridGameUIControllerInterface getGameGridGameUIController() {
		return this.gameGridGameUIController;
	}
	
	public void updateGridCompleted(boolean solutionCorrect) {
		PropertyResourceBundle gameMessagesBundle = this.resourcesController.getResource("UIMessages");
		
		JOptionPane.setDefaultLocale(this.resourcesController.getSelectedLocale());
		
		if (solutionCorrect)
			JOptionPane.showConfirmDialog(this.binairoGame, gameMessagesBundle.getString("correct_solution"), "", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this.binairoGame, gameMessagesBundle.getString("incorrect_solution"), "", JOptionPane.INFORMATION_MESSAGE);
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
	
	
	
	
	
	
	
	
	private void createBinairoGameUI() {
		this.binairoGame = new JFrame(this.resourcesController.getResource("UILabels").getString("binairoGameUI_Title"));
		this.binairoGameUIPanel = new JPanel();
		this.gameGridUI = new GameGridUI(this.gridSize, this.visualHints, this.gameGridController, this.resourcesController);
		this.mainUIBar = new MainUIBar(this.visualHints, this.resourcesController, this.mainUIColor);
		
		this.binairoGame.add(this.binairoGameUIPanel);
		
		GridBagConstraints binairoGameConstraints = new GridBagConstraints();
		this.binairoGameUIPanel.setLayout(new GridBagLayout());
		JPanel mainUIBarPanel = new JPanel(new GridBagLayout());
		
		binairoGameConstraints.fill = GridBagConstraints.BOTH;
		binairoGameConstraints.anchor = GridBagConstraints.CENTER;
		binairoGameConstraints.gridx = 0;
		binairoGameConstraints.gridy = 0;
		binairoGameConstraints.insets = new Insets(0, 15, 0, 15);
		binairoGameConstraints.weightx = 1.0;
		binairoGameConstraints.weighty = 1.0;
		
		mainUIBarPanel.add(this.mainUIBar.getMainUIPanel(), binairoGameConstraints);
		mainUIBarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLACK));
		mainUIBarPanel.setBackground(this.mainUIColor);
		
		binairoGameConstraints.fill = GridBagConstraints.BOTH;
		binairoGameConstraints.anchor = GridBagConstraints.CENTER;
		binairoGameConstraints.gridx = 0;
		binairoGameConstraints.gridy = 0;
		binairoGameConstraints.insets = new Insets(0, 0, 0, 0);
		binairoGameConstraints.weightx = 0.0;
		binairoGameConstraints.weighty = 1.0;
		this.binairoGameUIPanel.add(mainUIBarPanel, binairoGameConstraints);
		
		binairoGameConstraints.fill = GridBagConstraints.BOTH;
		binairoGameConstraints.anchor = GridBagConstraints.CENTER;
		binairoGameConstraints.gridx = 1;
		binairoGameConstraints.gridy = 0;
		binairoGameConstraints.insets = new Insets(0, 0, 0, 0);
		binairoGameConstraints.weightx = 0.8;
		binairoGameConstraints.weighty = 1.0;
		this.binairoGameUIPanel.add(this.gameGridUI.getGamePanel(), binairoGameConstraints);
		
		this.binairoGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.binairoGame.setPreferredSize(new Dimension(700, 500));
		this.binairoGame.pack();
		this.binairoGame.setLocationRelativeTo(null);
		this.binairoGame.setEnabled(true);
		this.binairoGame.setVisible(true);
		
		this.binairoGame.addWindowListener(new GameUI_WindowListener());
		
		this.mainUIBar.addNewGridActionListener(new MainUI_NG_ButtonListener());
		this.mainUIBar.addChangeLocaleListener(new MainUI_LS_ButtonListener());
		this.mainUIBar.addVisualHintsListener(new MainUI_VH_CheckBoxListener());
		this.mainUIBar.addCellSolutionListener(new MainUI_CS_ToggleButtonListener());
		this.mainUIBar.addCellTryListener(new MainUI_CellTry_ActionListener());
		this.mainUIBar.addConfirmCellTryListener(new MainUI_ConfirmCellTry_ActionListener());
		this.mainUIBar.addCancelCellTryListener(new MainUI_CancelCellTry_ActionListener());
		
		this.binairoGameUIPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("shift pressed SHIFT"), "ToggleON_CellSolution");
		this.binairoGameUIPanel.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(KeyStroke.getKeyStroke("released SHIFT"), "ToggleOFF_CellSolution");
		this.binairoGameUIPanel.getActionMap().put("ToggleON_CellSolution", new CellSolutionPressedShiftAction());
		this.binairoGameUIPanel.getActionMap().put("ToggleOFF_CellSolution", new CellSolutionReleasedShiftAction());
	}
	
	private void openLanguageSelectionWindow() {
		this.lsUI = new LanguageSelectionUI(this.resourcesController, this.inLocaleLanguage, this.lsUIColor);
		this.lsUIWindow = new OKCancelWindow(this.binairoGame, this.lsUI, this.resourcesController, this.lsUIColor);
		this.lsUIWindow.pack();
		this.lsUIWindow.setLocationRelativeTo(this.binairoGame);
		this.lsUIWindow.setVisible(true);
		
		this.lsUIWindow.addWindowListener(new LS_WindowListener());
		this.lsUIWindow.addOKButtonActionListener(new LS_OKButtonListener());
		this.lsUIWindow.addCancelButtonActionListener(new LS_CancelButtonListener());
		
		this.binairoGame.setEnabled(false);
	}
	
	private void closeLanguageSelectionWindow() {
		this.lsUI = null;
		this.lsUIWindow = null;
		this.binairoGame.setEnabled(true);
		this.gameGridUI.getGamePanel().requestFocus();
	}
	
	private void openNewGameWindow() {
		this.newGameUI = new NewGameUI(this.resourcesController, this.newGameUIColor);
		this.newGameUIWindow = new OKCancelWindow(this.binairoGame, this.newGameUI, this.resourcesController, this.newGameUIColor);
		this.newGameUIWindow.pack();
		this.newGameUIWindow.setLocationRelativeTo(this.binairoGame);
		this.newGameUIWindow.setVisible(true);
		
		this.newGameUIWindow.addWindowListener(new NG_WindowListener());
		this.newGameUIWindow.addOKButtonActionListener(new NG_OKButtonListener());
		this.newGameUIWindow.addCancelButtonActionListener(new NG_CancelButtonListener());
		
		this.binairoGame.setEnabled(false);
	}
	
	private void closeNewGameWindow() {
		this.newGameUI = null;
		this.newGameUIWindow = null;
		this.binairoGame.setEnabled(true);
		this.gameGridUI.getGamePanel().requestFocus();
	}
	
	
	
	
	
	
	class CellSolutionPressedShiftAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.mainUIBar.toggleCellSolution(true);
			gameUI.gameGridUI.setCellSolutionState(true);
		}
	}
	
	class CellSolutionReleasedShiftAction extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.mainUIBar.toggleCellSolution(false);
			gameUI.gameGridUI.setCellSolutionState(false);
		}
	}
	
	

	class GameUI_WindowListener extends WindowAdapter {
		public void windowActivated(WindowEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridUI.getGamePanel().requestFocus();
		}
	}
	
	
	
	class LS_WindowListener extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.closeLanguageSelectionWindow();
		}
	}
	
	class LS_OKButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.resourcesController.changeLocaleResources(gameUI.lsUI.getSelectedLocaleInfos());
			gameUI.lsUIWindow.dispose();
		}
	}
	
	class LS_CancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.lsUIWindow.dispose();
		}
	}

	
	
	class NG_WindowListener extends WindowAdapter {
		public void windowClosed(WindowEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.closeNewGameWindow();
		}
	}
	
	class NG_OKButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridController.setGameGrid(new GameGrid(gameUI.gridSize, gameUI.newGameUI.getSelectedLevel()));
			gameUI.gameGridUI.updateGridCells();
			gameUI.newGameUIWindow.dispose();
		}
	}
	
	class NG_CancelButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.newGameUIWindow.dispose();
		}
	}
	
	
	
	class MainUI_NG_ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.openNewGameWindow();
		}
	}
	
	class MainUI_VH_CheckBoxListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridUI.setVisualHints(((JCheckBox)e.getSource()).isSelected());
		}
	}
	
	class MainUI_CS_ToggleButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridUI.setCellSolutionState(gameUI.mainUIBar.getCellSolution());
		}
	}
	
	class MainUI_CellTry_ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			
			if (gameUI.mainUIBar.getCellTry()) {
				gameUI.gameGridUI.setCellTryState(true, false);
			}
		}
	}
	
	class MainUI_ConfirmCellTry_ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridUI.setCellTryState(false, true);
		}
	}
	
	class MainUI_CancelCellTry_ActionListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI gameUI = GameUI.this;
			gameUI.gameGridUI.setCellTryState(false, false);
		}
	}
	
	class MainUI_LS_ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			GameUI.this.openLanguageSelectionWindow();
		}
	}
}
