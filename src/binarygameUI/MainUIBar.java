package binarygameUI;

import binarygamelogic.LocalesResourcesControllerInterface;

import java.util.PropertyResourceBundle;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;


public class MainUIBar implements LocalesResourcesUIInterface {
	private LocalesResourcesControllerInterface resourcesController;
	private PropertyResourceBundle mainUILabels;
	private Color panelColor;
	
	private BufferedImage checkMarkImage;
	private BufferedImage xMarkImage;
	
	private JPanel UIPanel;
	private JButton newGrid;
	private JButton saveGrid;
	private JButton loadGrid;
	private JButton validateGrid;
	private JCheckBox visualHints;
	private JToggleButton cellSolution;
	private JPanel cellTryButtonsPanel;
	private JToggleButton cellTry;
	private JButton confirmCellTry;
	private JButton cancelCellTry;
	private JButton changeLocale;
	
	
	
	public MainUIBar(boolean visualHints, LocalesResourcesControllerInterface resourcesController, Color panelColor) {
		this.resourcesController = resourcesController;
		this.resourcesController.addUI(this);
		this.mainUILabels = this.resourcesController.getResource("UILabels");
		this.panelColor = panelColor;
		
		try {
			this.checkMarkImage = ImageIO.read(new File("Images/Program Icons/CheckMark.png"));
			this.xMarkImage = ImageIO.read(new File("Images/Program Icons/XMark.png"));
		}
		catch (IOException ioe) {
		}
		
		createMainUIPanel();
		
		this.visualHints.setSelected(visualHints);
	}
	
	public JPanel getMainUIPanel() {
		return this.UIPanel;
	}
	
	public void setResourcesController(LocalesResourcesControllerInterface resourcesController) {
		this.resourcesController = resourcesController;
	}
	
	public LocalesResourcesControllerInterface getResourcesController() {
		return this.resourcesController;
	}
	
	public void toggleCellSolution(boolean cellSolution) {
		this.cellSolution.setSelected(cellSolution);
	}
	
	public boolean getCellSolution() {
		return this.cellSolution.isSelected();
	}
	
	public boolean getCellTry() {
		return this.cellTry.isSelected();
	}
	
	public void updateLocalesResources() {
		this.mainUILabels = resourcesController.getResource("UILabels");
		
		this.newGrid.setText(this.mainUILabels.getString("mainUI_NewGridLabel"));
		this.saveGrid.setText(this.mainUILabels.getString("mainUI_SaveGridLabel"));
		this.loadGrid.setText(this.mainUILabels.getString("mainUI_LoadGridLabel"));
		this.validateGrid.setText(this.mainUILabels.getString("mainUI_ValidateGridLabel"));
		this.visualHints.setText(this.mainUILabels.getString("mainUI_VisualHintLabel"));
		this.cellSolution.setText(this.mainUILabels.getString("mainUI_CellSolutionLabel"));
		this.cellTry.setText(this.mainUILabels.getString("mainUI_CellTryLabel"));
		this.changeLocale.setText(this.mainUILabels.getString("mainUI_ChangeLocaleLabel"));
	}
	
	public void addNewGridActionListener(ActionListener newGrid_AL) {
		this.newGrid.addActionListener(newGrid_AL);
	}
	
	public void addSaveGridActionListener(ActionListener saveGrid_AL) {
		this.saveGrid.addActionListener(saveGrid_AL);
	}
	
	public void addLoadGridActionListener(ActionListener loadGrid_AL) {
		this.loadGrid.addActionListener(loadGrid_AL);
	}
	
	public void addValidateGridActionListener(ActionListener validateGrid_AL) {
		this.validateGrid.addActionListener(validateGrid_AL);
	}
	
	public void addVisualHintsListener(ActionListener visualHints_AL) {
		this.visualHints.addActionListener(visualHints_AL);
	}
	
	public void addCellSolutionListener(ActionListener cellSolution_AL) {
		this.cellSolution.addActionListener(cellSolution_AL);
	}
	
	public void addCellTryListener(ActionListener cellTry_AL) {
		this.cellTry.addActionListener(cellTry_AL);
	}
	
	public void addConfirmCellTryListener(ActionListener confirmCellTry_AL) {
		this.confirmCellTry.addActionListener(confirmCellTry_AL);
	}
	
	public void addCancelCellTryListener(ActionListener cancelCellTry_AL) {
		this.cancelCellTry.addActionListener(cancelCellTry_AL);
	}
	
	public void addChangeLocaleListener(ActionListener changeLocale_AL) {
		this.changeLocale.addActionListener(changeLocale_AL);
	}
	
	public void removeNewGridActionListener(ActionListener newGrid_AL) {
		this.newGrid.removeActionListener(newGrid_AL);
	}
	
	public void removeSaveGridActionListener(ActionListener saveGrid_AL) {
		this.saveGrid.removeActionListener(saveGrid_AL);
	}
	
	public void removeLoadGridActionListener(ActionListener loadGrid_AL) {
		this.loadGrid.removeActionListener(loadGrid_AL);
	}
	
	public void removeValidateGridActionListener(ActionListener validateGrid_AL) {
		this.validateGrid.removeActionListener(validateGrid_AL);
	}
	
	public void removeVisualHintsListener(ActionListener visualHints_AL) {
		this.visualHints.removeActionListener(visualHints_AL);
	}
	
	public void removeCellSolutionListener(ActionListener cellSolution_AL) {
		this.cellSolution.removeActionListener(cellSolution_AL);
	}
	
	public void removeCellTryListener(ActionListener cellTry_AL) {
		this.cellTry.removeActionListener(cellTry_AL);
	}
	
	public void removeConfirmCellTryListener(ActionListener confirmCellTry_AL) {
		this.confirmCellTry.removeActionListener(confirmCellTry_AL);
	}
	
	public void removeCancelCellTryListener(ActionListener cancelCellTry_AL) {
		this.cancelCellTry.removeActionListener(cancelCellTry_AL);
	}
	
	public void removeChangeLocaleListener(ActionListener changeLocale_AL) {
		this.changeLocale.removeActionListener(changeLocale_AL);
	}
	
	private void createMainUIPanel() {
		this.UIPanel = new JPanel(new GridBagLayout());
		this.newGrid = new JButton(this.mainUILabels.getString("mainUI_NewGridLabel"));
		this.saveGrid = new JButton(this.mainUILabels.getString("mainUI_SaveGridLabel"));
		this.loadGrid = new JButton(this.mainUILabels.getString("mainUI_LoadGridLabel"));
		this.validateGrid = new JButton(this.mainUILabels.getString("mainUI_ValidateGridLabel"));
		this.visualHints = new JCheckBox(this.mainUILabels.getString("mainUI_VisualHintLabel"));
		this.cellSolution = new JToggleButton(this.mainUILabels.getString("mainUI_CellSolutionLabel"));
		
		this.cellTryButtonsPanel = new JPanel(new GridBagLayout());	
		this.cellTry = new JToggleButton(this.mainUILabels.getString("mainUI_CellTryLabel"));
		this.confirmCellTry = new JButton();
		this.cancelCellTry = new JButton();
		this.confirmCellTry.setEnabled(false);
		this.cancelCellTry.setEnabled(false);
		
		this.changeLocale = new JButton(this.mainUILabels.getString("mainUI_ChangeLocaleLabel"));
		
		this.UIPanel.setBackground(this.panelColor);
		this.cellTryButtonsPanel.setBackground(this.panelColor);
		
		this.newGrid.setFocusable(false);
		this.saveGrid.setFocusable(false);
		this.loadGrid.setFocusable(false);
		this.validateGrid.setFocusable(false);
		this.visualHints.setFocusable(false);
		this.cellSolution.setFocusable(false);
		this.cellTryButtonsPanel.setFocusable(false);
		this.cellTry.setFocusable(false);
		this.confirmCellTry.setFocusable(false);
		this.cancelCellTry.setFocusable(false);
		this.changeLocale.setFocusable(false);
		
		Font buttonFont = this.newGrid.getFont();
		buttonFont = buttonFont.deriveFont(16.0f);
		this.newGrid.setFont(buttonFont);
		this.saveGrid.setFont(buttonFont);
		this.loadGrid.setFont(buttonFont);
		this.validateGrid.setFont(buttonFont);
		this.visualHints.setFont(buttonFont);
		this.cellSolution.setFont(buttonFont);
		this.cellTry.setFont(buttonFont);
		this.changeLocale.setFont(buttonFont);
		
		
		this.visualHints.setBackground(this.panelColor);
		this.visualHints.setHorizontalTextPosition(SwingConstants.LEFT);
		this.visualHints.setHorizontalAlignment(SwingConstants.CENTER);
		
		
		/*
		this.newGrid.setBackground(this.panelColor);
		this.saveGrid.setBackground(this.panelColor);
		this.loadGrid.setBackground(this.panelColor);
		this.validateGrid.setBackground(this.panelColor);
		
		this.cellSolution.setBackground(this.panelColor);
		this.cellTry.setBackground(this.panelColor);
		this.changeLocale.setBackground(this.panelColor);
		*/
		
		GridBagConstraints panelConstraints = new GridBagConstraints();
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.anchor = GridBagConstraints.CENTER;
		panelConstraints.insets = new Insets(5, 5, 5, 5);
		panelConstraints.weightx = 1.0;
		panelConstraints.weighty = 1.0;
		panelConstraints.gridx = 0;
		
		panelConstraints.gridy = 0;
		this.UIPanel.add(this.newGrid, panelConstraints);
		
		
		/*
		panelConstraints.gridy = 1;
		this.UIPanel.add(this.saveGrid, panelConstraints);
		
		panelConstraints.gridy = 2;
		this.UIPanel.add(this.loadGrid, panelConstraints);
		
		panelConstraints.gridy = 3;
		this.UIPanel.add(this.validateGrid, panelConstraints);
		*/
		
		
		panelConstraints.gridy = 4;
		this.UIPanel.add(this.visualHints, panelConstraints);
		
		panelConstraints.gridy = 5;
		this.UIPanel.add(this.cellSolution, panelConstraints);	
		
		panelConstraints.gridy = 6;
		this.UIPanel.add(this.cellTryButtonsPanel, panelConstraints);
		
		panelConstraints.gridy = 7;
		this.UIPanel.add(this.changeLocale, panelConstraints);
		
		
		GridBagConstraints cellTryButtonsConstraints = new GridBagConstraints();
		cellTryButtonsConstraints.fill = GridBagConstraints.BOTH;
		cellTryButtonsConstraints.weightx = 1;
		cellTryButtonsConstraints.weighty = 1;
		
		cellTryButtonsConstraints.gridwidth = 2;
		cellTryButtonsConstraints.gridx = 0;
		cellTryButtonsConstraints.gridy = 0;
		this.cellTryButtonsPanel.add(this.cellTry, cellTryButtonsConstraints);
		
		cellTryButtonsConstraints.gridwidth = 1;
		cellTryButtonsConstraints.gridx = 0;
		cellTryButtonsConstraints.gridy = 1;
		this.cellTryButtonsPanel.add(this.confirmCellTry, cellTryButtonsConstraints);
		
		cellTryButtonsConstraints.gridwidth = 1;
		cellTryButtonsConstraints.gridx = 1;
		cellTryButtonsConstraints.gridy = 1;
		this.cellTryButtonsPanel.add(this.cancelCellTry, cellTryButtonsConstraints);
		
		this.cellTry.addActionListener(new CellTry_ActionListener());
		this.confirmCellTry.addActionListener(new ConfirmCancelCellTry_ActionListener());
		this.cancelCellTry.addActionListener(new ConfirmCancelCellTry_ActionListener());
		this.cellTry.addComponentListener(new CellTry_ComponentListener());
	}
	
    private Image getScaledImage(Image srcImg, int w, int h){
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    
    
    
    
    
    class CellTry_ActionListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		MainUIBar mainUIBar = MainUIBar.this;

    		if (mainUIBar.cellTry.isSelected()) {
				mainUIBar.cellTry.setEnabled(false);
				mainUIBar.confirmCellTry.setEnabled(true);
				mainUIBar.cancelCellTry.setEnabled(true);
    		}
    		else {
    			mainUIBar.cellTry.setEnabled(true);
				mainUIBar.confirmCellTry.setEnabled(false);
				mainUIBar.cancelCellTry.setEnabled(false);
    		}
    	}
    }
    
    class ConfirmCancelCellTry_ActionListener implements ActionListener {
    	public void actionPerformed(ActionEvent e) {
    		MainUIBar mainUIBar = MainUIBar.this;
    		mainUIBar.cellTry.setEnabled(true);
    		mainUIBar.cellTry.setSelected(false);
			mainUIBar.confirmCellTry.setEnabled(false);
			mainUIBar.cancelCellTry.setEnabled(false);
    	}
    }
    
    class CellTry_ComponentListener extends ComponentAdapter {
    	public void componentResized(ComponentEvent e) {
    		MainUIBar mainUIBar = MainUIBar.this;
    		
    		int iconSize = Math.round(mainUIBar.cellTry.getHeight()*(2.0f/3.5f));
    		ImageIcon checkMarkIcon = new ImageIcon(mainUIBar.getScaledImage(mainUIBar.checkMarkImage, iconSize, iconSize));
    		ImageIcon xMarkIcon = new ImageIcon(getScaledImage(mainUIBar.xMarkImage, iconSize, iconSize));
    		mainUIBar.confirmCellTry.setIcon(checkMarkIcon);
    		mainUIBar.cancelCellTry.setIcon(xMarkIcon);
    	}
    }
}
