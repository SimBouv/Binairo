package binarygamelogic;

import binarygameUI.*;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Locale;

public abstract class LocalesResourcesControllerInterface implements PropertyChangeListener {
	protected LocalesResourcesInterface localesResources;
	protected LinkedList<LocalesResourcesUIInterface> game_UIs;

	public LocalesResourcesControllerInterface() {
		game_UIs = new LinkedList<LocalesResourcesUIInterface>();
	}
	
	public void setLocalesResources(LocalesResourcesInterface localesResources) {
		this.localesResources = localesResources;
		this.localesResources.addPropertyChangeListener(this);
	}
	
	public void addUI(LocalesResourcesUIInterface newUI) {
		if (newUI != null)
			this.game_UIs.add(newUI);
	}
	
	public void addUIs(LinkedList<LocalesResourcesUIInterface> newUIs) {
		this.game_UIs.addAll(newUIs);
	}
	
	public void removeUI(LocalesResourcesUIInterface removedUI) {
		this.game_UIs.remove(removedUI);		
	}
	
	public void removeUIs(LinkedList<LocalesResourcesUIInterface> removedUIs) {
		this.game_UIs.removeAll(removedUIs);
	}
	
	public void propertyChange(PropertyChangeEvent pce) {
		if (pce.getPropertyName().equals("ResourcesReloaded")) {
			for (LocalesResourcesUIInterface ui : game_UIs) {
				ui.updateLocalesResources();
			}
		}
	}
	
	public abstract Locale getSelectedLocale();
	
	public abstract LinkedList<Locale> getLocalesList();
	
	public abstract String[] getResourcesName();
	
	public abstract PropertyResourceBundle getResource(String resourceName);
	
	public abstract boolean loadLocalesAndResources(String resourcesRootDirectory, String[] resourcesName, String startingLocaleInfo);
	
	public abstract boolean changeLocaleResources(String localeInfos);
}
