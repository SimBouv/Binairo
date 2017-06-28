package binarygameUI;

import binarygamelogic.*;

import java.util.PropertyResourceBundle;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Color;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;



public class NewGameUI extends JPanel implements LocalesResourcesUIInterface {
	private LocalesResourcesControllerInterface resourcesController;
	private PropertyResourceBundle newGameUILabels;
	
	private Color panelColor;
	
	private JLabel newGameText;
	private JPanel levelsButtonsPanel;
	private ButtonGroup levelsButtonsGroup;
	private JRadioButton level1_RB;
	private JRadioButton level2_RB;
	private JRadioButton level3_RB;
	
	
	
	public NewGameUI(LocalesResourcesControllerInterface resourcesController, Color panelColor) {
		super(new GridBagLayout());
		
		setResourcesController(resourcesController);
		this.newGameUILabels = this.resourcesController.getResource("UILabels");
		this.panelColor = panelColor;
		
		createNewGamePanel();
	}
	
	public byte getSelectedLevel() {
		if (this.level1_RB.isSelected())
			return StartingGameGridGenerator.BINAIRO_LEVEL_ONE;
		else if (this.level2_RB.isSelected())
			return StartingGameGridGenerator.BINAIRO_LEVEL_TWO;
		else
			return StartingGameGridGenerator.BINAIRO_LEVEL_THREE;
	}
	
	public void addLevelRBActionListener(ActionListener levelRadioButtonAL) {
		this.level1_RB.addActionListener(levelRadioButtonAL);
		this.level2_RB.addActionListener(levelRadioButtonAL);
		this.level3_RB.addActionListener(levelRadioButtonAL);
	}
	
	public void removeLevelRBActionListener(ActionListener levelRadioButtonAL) {
		this.level1_RB.removeActionListener(levelRadioButtonAL);
		this.level2_RB.removeActionListener(levelRadioButtonAL);
		this.level3_RB.removeActionListener(levelRadioButtonAL);
	}
	
	public void setResourcesController(LocalesResourcesControllerInterface resourcesController) {
		this.resourcesController = resourcesController;
		this.resourcesController.addUI(this);
	}
	
	public LocalesResourcesControllerInterface getResourcesController() {
		return this.resourcesController;
	}
	
	public void updateLocalesResources() {
		this.newGameUILabels = this.resourcesController.getResource("UILabels");
		this.newGameText.setText(this.newGameUILabels.getString("newGameUI_Label"));
		this.level1_RB.setText(this.newGameUILabels.getString("newGameUI_Level1"));
		this.level2_RB.setText(this.newGameUILabels.getString("newGameUI_Level2"));
		this.level3_RB.setText(this.newGameUILabels.getString("newGameUI_Level3"));
	}
	
	
	
	
	private void createNewGamePanel() {
		GridBagConstraints panelConstraints = new GridBagConstraints();
		Font componentFont;

		this.setPreferredSize(new Dimension(350, 100));
		this.setBackground(panelColor);
		
		this.newGameText = new JLabel(this.newGameUILabels.getString("newGameUI_Label"));
		this.newGameText.setBackground(this.panelColor);
		componentFont = this.newGameText.getFont();
		componentFont = componentFont.deriveFont(18.0f);
		this.newGameText.setFont(componentFont);
		
		this.level1_RB = new JRadioButton(this.newGameUILabels.getString("newGameUI_Level1"), true);
		this.level1_RB.setBackground(this.panelColor);
		
		this.level2_RB = new JRadioButton(this.newGameUILabels.getString("newGameUI_Level2"), false);
		this.level2_RB.setBackground(this.panelColor);
		
		this.level3_RB = new JRadioButton(this.newGameUILabels.getString("newGameUI_Level3"), false);
		this.level3_RB.setBackground(this.panelColor);
		
		componentFont = this.level1_RB.getFont();
		componentFont = componentFont.deriveFont(14.0f);
		this.level1_RB.setFont(componentFont);
		this.level2_RB.setFont(componentFont);
		this.level3_RB.setFont(componentFont);
		
		
		this.levelsButtonsGroup = new ButtonGroup();
		this.levelsButtonsGroup.add(level1_RB);
		this.levelsButtonsGroup.add(level2_RB);
		this.levelsButtonsGroup.add(level3_RB);
		
		this.levelsButtonsPanel = new JPanel();
		this.levelsButtonsPanel.setBackground(this.panelColor);
		this.levelsButtonsPanel.add(this.level1_RB);
		this.levelsButtonsPanel.add(this.level2_RB);
		this.levelsButtonsPanel.add(this.level3_RB);
		
		
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.anchor = GridBagConstraints.PAGE_END;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 0;
		panelConstraints.weighty = 1;
		panelConstraints.insets = new Insets(5, 0, 5, 0);
		this.add(this.newGameText, panelConstraints);
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.anchor = GridBagConstraints.PAGE_START;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 1;
		panelConstraints.weighty = 1;
		panelConstraints.insets = new Insets(5, 0, 5, 0);
		this.add(this.levelsButtonsPanel, panelConstraints);
	}
}
