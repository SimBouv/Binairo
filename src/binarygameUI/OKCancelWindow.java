package binarygameUI;

import binarygamelogic.LocalesResourcesControllerInterface;

import java.util.PropertyResourceBundle;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Window;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;




public class OKCancelWindow extends Window implements LocalesResourcesUIInterface {
	private LocalesResourcesControllerInterface resourcesController;
	private PropertyResourceBundle windowLabels;
	private Color windowColor;
	
	private JPanel mainPanel;
	private JPanel uiPanel;
	private JPanel buttonsPanel;
	private JButton okButton;
	private JButton cancelButton;
	
	
	
	public OKCancelWindow(Window parent, JPanel uiPanel, LocalesResourcesControllerInterface resourcesController, Color windowColor) {
		super(parent);
		
		setResourcesController(resourcesController);
		this.windowLabels = this.resourcesController.getResource("UILabels");
		
		createLSWindow(uiPanel, resourcesController, windowColor);
	}
	
	public OKCancelWindow(Frame parent, JPanel uiPanel, LocalesResourcesControllerInterface resourcesController, Color windowColor) {
		super(parent);
		
		setResourcesController(resourcesController);
		this.windowLabels = this.resourcesController.getResource("UILabels");
		
		createLSWindow(uiPanel, resourcesController, windowColor);
	}
	
	public void addOKButtonActionListener(ActionListener okButtonAL) {
		this.okButton.addActionListener(okButtonAL);
	}
	
	public void removeOKButtonActionListener(ActionListener okButtonAL) {
		this.okButton.removeActionListener(okButtonAL);
	}

	public void addCancelButtonActionListener(ActionListener cancelButtonAL) {
		this.cancelButton.addActionListener(cancelButtonAL);
	}
	
	public void removeCancelButtonActionListener(ActionListener cancelButtonAL) {
		this.cancelButton.removeActionListener(cancelButtonAL);
	}
	
	public void setResourcesController(LocalesResourcesControllerInterface resourcesController) {
		this.resourcesController = resourcesController;
		this.resourcesController.addUI(this);
	}
	
	public LocalesResourcesControllerInterface getResourcesController() {
		return this.resourcesController;
	}
	
	public void updateLocalesResources() {
		this.windowLabels = this.resourcesController.getResource("UILabels");
		this.okButton.setText(this.windowLabels.getString("ok_button"));
		this.cancelButton.setText(this.windowLabels.getString("cancel_button"));
	}
	
	
	
	
	private void createLSWindow(JPanel uiPanel, LocalesResourcesControllerInterface resourcesController, Color windowColor) {
		this.uiPanel = uiPanel;
		this.windowColor = windowColor;
		
		this.setLayout(new GridBagLayout());
		this.setBackground(this.windowColor);
		this.setLayout(new GridBagLayout());
		
		this.mainPanel = new JPanel(new GridBagLayout());
		this.mainPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, Color.RED, Color.BLUE));
		this.mainPanel.setBackground(this.windowColor);
		
		this.okButton = new JButton(this.windowLabels.getString("ok_button"));
		this.okButton.setPreferredSize(new Dimension(100, 25));
		
		this.cancelButton = new JButton(this.windowLabels.getString("cancel_button"));
		this.cancelButton.setPreferredSize(new Dimension(100, 25));
		
		this.buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
		this.buttonsPanel.setBackground(this.windowColor);
		this.buttonsPanel.add(this.okButton);
		this.buttonsPanel.add(this.cancelButton);

		GridBagConstraints panelConstraints = new GridBagConstraints();
		
		panelConstraints.fill = GridBagConstraints.HORIZONTAL;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 0;
		panelConstraints.anchor = GridBagConstraints.PAGE_END;
		panelConstraints.insets = new Insets(25, 25, 0, 25);
		this.mainPanel.add(this.uiPanel, panelConstraints);
	
		panelConstraints.fill = GridBagConstraints.NONE;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 1;
		panelConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
		panelConstraints.insets = new Insets(0, 25, 25, 25);
		this.mainPanel.add(this.buttonsPanel, panelConstraints);
		

		panelConstraints.fill = GridBagConstraints.BOTH;
		panelConstraints.gridx = 0;
		panelConstraints.gridy = 0;
		panelConstraints.anchor = GridBagConstraints.CENTER;
		panelConstraints.insets = new Insets(0, 0, 0, 0);
		this.add(this.mainPanel, panelConstraints);
		
		
	}
}
