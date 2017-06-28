package binarygameUI;

import binarygamelogic.LocalesResourcesControllerInterface;

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.Vector;
import java.util.HashMap;
import java.util.LinkedList;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Font;
import java.awt.Dimension;
import java.awt.event.*;

import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JComboBox;



public class LanguageSelectionUI extends JPanel implements LocalesResourcesUIInterface {
	private LocalesResourcesControllerInterface resourcesController;
	private PropertyResourceBundle languageSelectUILabels;
	
	private HashMap<String, String> localesDisplayName;
	private boolean inLocaleLanguage;
	private Color panelColor;
	
	private JLabel ls_Title;
	private JComboBox<String> ls_CB;
	
	
	public LanguageSelectionUI(LocalesResourcesControllerInterface resourcesController, boolean inLocaleLanguage, Color panelColor) {
		super(new GridBagLayout());
		
		setResourcesController(resourcesController);
		this.languageSelectUILabels = this.resourcesController.getResource("UILabels");
		this.inLocaleLanguage = inLocaleLanguage;
		this.panelColor = panelColor;
		
		createLanguageSelectPanel();
	}

	public String getSelectedLocaleInfos() {
		return this.localesDisplayName.get(this.ls_CB.getSelectedItem().toString());
	}
	
	public String getSelectedLocaleDisplayName() {
		return this.ls_CB.getSelectedItem().toString();
	}
	
	public void addLSActionListener(ActionListener ls_AL) {
		this.ls_CB.addActionListener(ls_AL);
	}
	
	public void removeLSActionListener(ActionListener ls_AL) {
		this.ls_CB.removeActionListener(ls_AL);
	}
	
	public void changePanelColor(Color newPanelColor) {
		this.panelColor = newPanelColor;
		this.setBackground(this.panelColor);
		this.ls_Title.setBackground(this.panelColor);
		this.ls_CB.setBackground(this.panelColor);
	}
	
	public Color getPanelColor() {
		return this.panelColor;
	}
	
	public void setResourcesController(LocalesResourcesControllerInterface resourcesController) {
		this.resourcesController = resourcesController;
		this.resourcesController.addUI(this);
	}
	
	public LocalesResourcesControllerInterface getResourcesController() {
		return this.resourcesController;
	}
	
	public void updateLocalesResources() {
		this.languageSelectUILabels = this.resourcesController.getResource("UILabels");
		this.ls_Title.setText(this.languageSelectUILabels.getString("languageUI_Label"));
		updateComboBoxItems();
	}
	
	
	
	
	
	private void createLanguageSelectPanel() {		
		GridBagConstraints panelConstraints = new GridBagConstraints();
		Font componentFont;
		
		this.setBackground(this.panelColor);
		this.setPreferredSize(new Dimension(350, 75));
		this.addFocusListener(new LSPanel_FocusListener());
		
		this.ls_Title = new JLabel(this.languageSelectUILabels.getString("languageUI_Label"));
		componentFont = this.ls_Title.getFont();
		componentFont = componentFont.deriveFont(18.0f);
		this.ls_Title.setFont(componentFont);
		this.ls_Title.setBackground(this.panelColor);
		
		
		this.ls_CB = new JComboBox<String>();
		updateComboBoxItems();
		componentFont = this.ls_CB.getFont();
		componentFont = componentFont.deriveFont(14.0f);
		this.ls_CB.setFont(componentFont);
		this.ls_CB.setBackground(this.panelColor);
		
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.anchor = GridBagConstraints.PAGE_END;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 0;
		panelConstraints.weighty = 1;
		panelConstraints.insets = new Insets(5, 0, 5, 0);
		this.add(this.ls_Title, panelConstraints);
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.anchor = GridBagConstraints.PAGE_START;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 1;
		panelConstraints.weighty = 1;
		panelConstraints.insets = new Insets(5, 0, 5, 0);
		this.add(this.ls_CB, panelConstraints);
	}
	
	private void updateComboBoxItems() {
		this.localesDisplayName = getLocalesDisplayName();
		
		Vector<String> ls_CB_Items = new Vector<String>(this.localesDisplayName.keySet());
		Locale currLocale = this.resourcesController.getSelectedLocale();
		for (byte i = 0 ; i < ls_CB_Items.size() ; i++) {
			ls_CB_Items.set(i, adjustLocaleDisplayName(ls_CB_Items.get(i), currLocale));
		}
		ls_CB_Items.sort(null);
		
		this.ls_CB.removeAllItems();
		for (String item : ls_CB_Items) {
			this.ls_CB.addItem(item);
		}
		
		this.ls_CB.setSelectedItem(adjustLocaleDisplayName(this.resourcesController.getSelectedLocale().getDisplayName(currLocale), currLocale));
	}
	
	private HashMap<String, String> getLocalesDisplayName() {
		LinkedList<Locale> localesList = this.resourcesController.getLocalesList();
		HashMap<String, String> localesDisplayName = new HashMap<String, String>();
		
		if (localesList != null) {
			String currLocaleName;
			
			for (Locale currLocale : localesList) {
				if (this.inLocaleLanguage)
					currLocaleName = currLocale.getDisplayName(currLocale);
				else
					currLocaleName = currLocale.getDisplayName();
				
				localesDisplayName.put(currLocaleName, currLocale.toString());
			}
		}
		
		return localesDisplayName;
	}
	
	private String adjustLocaleDisplayName(String localeDisplayName, Locale localeToUse) {
		return localeDisplayName = localeDisplayName.substring(0, 1).toUpperCase(localeToUse) + localeDisplayName.substring(1, localeDisplayName.length());
	}
	
	
	
	
	
	
	
	class LSPanel_FocusListener extends FocusAdapter {
		public void focusGained(FocusEvent e) {
			LanguageSelectionUI.this.ls_CB.requestFocus();
		}
	}
}
