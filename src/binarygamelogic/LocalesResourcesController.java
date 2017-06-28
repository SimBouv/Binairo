package binarygamelogic;

import binarygameUI.LocalesResourcesUIInterface;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PropertyResourceBundle;
import java.util.Locale;
import java.util.LinkedList;


public class LocalesResourcesController extends LocalesResourcesControllerInterface {
	public LocalesResourcesController() {
		super();
	}
	
	public LocalesResourcesController(LocalesResourcesInterface localesResources) {
		super();
		setLocalesResources(localesResources);
	}
	
	public LocalesResourcesController(LocalesResourcesInterface localesResources, LinkedList<LocalesResourcesUIInterface> game_UIs) {
		super();
		this.setLocalesResources(localesResources);
		this.addUIs(game_UIs);
	}
	
	public Locale getSelectedLocale() {
		return this.localesResources.getSelectedLocale();
	}
	
	public LinkedList<Locale> getLocalesList() {
		return this.localesResources.getLocalesList();
	}
	
	public String[] getResourcesName() {
		return this.localesResources.getResourcesName();
	}
	
	public PropertyResourceBundle getResource(String resourceName) {
		return this.localesResources.getResource(resourceName);
	}
	
	public boolean loadLocalesAndResources(String resourcesRootDirectory, String[] resourcesName, String startingLocaleInfo) {
		return this.localesResources.loadLocalesAndResources(resourcesRootDirectory, resourcesName, startingLocaleInfo);
	}
	
	public boolean changeLocaleResources(String localeInfos) {
		return this.localesResources.changeLocaleResources(localeInfos);
	}
}
