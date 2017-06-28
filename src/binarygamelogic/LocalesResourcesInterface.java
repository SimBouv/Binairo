package binarygamelogic;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.Locale;
import java.util.LinkedList;

public abstract class LocalesResourcesInterface  {
	protected PropertyChangeSupport propertyChangeSupport;
	
	public LocalesResourcesInterface() {
		propertyChangeSupport = new PropertyChangeSupport(this);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pcs) {
		propertyChangeSupport.addPropertyChangeListener(pcs);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener pcs) {
		propertyChangeSupport.removePropertyChangeListener(pcs);
	}
	
	protected void firePropertyEvent(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}
	
	public abstract Locale getSelectedLocale();
	
	public abstract LinkedList<Locale> getLocalesList();
	
	public abstract String[] getResourcesName();
	
	public abstract PropertyResourceBundle getResource(String resourceName);
	
	public abstract boolean loadLocalesAndResources(String resourcesRootDirectory, String[] resourcesName, String startingLocaleInfo);
	
	public abstract boolean changeLocaleResources(String localeInfos);
}
