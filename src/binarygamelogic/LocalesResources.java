package binarygamelogic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.HashMap;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.LinkedList;

public class LocalesResources extends LocalesResourcesInterface {
	String resourcesFilesRootDirectory;
	
	HashMap<String, Locale> gameLocales;
	private Locale currSelectedLocale;
	
	String[] resourcesName;
	HashMap<String, PropertyResourceBundle> gameResourcesBundles;
	
	public LocalesResources(String resourcesRootDirectory, String[] resourcesName, String startingLocaleInfo) {
		Locale.setDefault(Locale.FRENCH);
		loadLocalesAndResources(resourcesRootDirectory, resourcesName, startingLocaleInfo);
	}
	
	public Locale getSelectedLocale() {
		return this.currSelectedLocale;
	}
	
	public LinkedList<Locale> getLocalesList() {
		if (this.gameLocales != null) {
			return new LinkedList<Locale>(gameLocales.values());
		}
		else
			return null;
	}
	
	public String getLanguageFilesRootDirectory() {
		return this.resourcesFilesRootDirectory;
	}
	
	public String[] getResourcesName() {
		return this.resourcesName.clone();
	}
	
	public PropertyResourceBundle getResource(String resourceName) {
		if (this.gameResourcesBundles != null)
			return this.gameResourcesBundles.get(resourceName);
		else
			return null;
	}
	
	public boolean loadLocalesAndResources(String resourcesRootDirectory, String[] resourcesName, String startingLocaleInfo) {
		this.resourcesFilesRootDirectory = resourcesRootDirectory;
		this.resourcesName = resourcesName.clone();
		
		if (!loadLocales())
			return false;
		
		if (!loadLocaleResources(startingLocaleInfo))
			return false;
		
		return true;
	}
	
	private boolean loadLocales() {
		FileInputStream localesFile;
		InputStreamReader localesISR;
		PropertyResourceBundle localesRB = null;
		String[] supportedLocales;
		String[] localeInfos;
		Locale currLocale;

		this.gameLocales = null;
		
		try {
			localesFile = new FileInputStream(new File(this.resourcesFilesRootDirectory + "\\Locales.properties"));
			
			try {
				localesISR = new InputStreamReader(localesFile, "UNICODE");
				localesRB = new PropertyResourceBundle(localesISR);
				
				supportedLocales = localesRB.getString("supported_locales").split(";");
				
				if (supportedLocales.length > 0) {
					this.gameLocales = new HashMap<String, Locale>(supportedLocales.length, 1);
					
					for (byte i = 0 ; i < supportedLocales.length ; i++) {
						localeInfos = supportedLocales[i].split("_");
	
						switch (localeInfos.length) {
							case 1: 	currLocale = new Locale(localeInfos[0]);
										break;
										
							case 2: 	currLocale = new Locale(localeInfos[0], localeInfos[1]);
										break;
										
							case 3: 	String localeVariant = "";
										for (byte variantInd = 2 ; variantInd < localeInfos.length ; variantInd++) {
											localeVariant += localeInfos[variantInd];
										}
										currLocale = new Locale(localeInfos[0], localeInfos[1], localeVariant);
										break;
										
							default : 	currLocale = null;
									  	break;
						}
	
						if (currLocale != null) {
							this.gameLocales.put(currLocale.toString(), currLocale);
						}	
					}
				}
			}
			catch (IOException ioe) {
				return false;
			}
		}
		catch (FileNotFoundException e) {
			return false;
		}
		
		if (this.gameLocales.size() == 0) {
			this.gameLocales = null;
			return false;
		}
		else
			return true;
	}
	
	public boolean changeLocaleResources(String localeInfos) {
		if (loadLocaleResources(localeInfos)) {
			this.firePropertyEvent("ResourcesReloaded", null, null);
			return true;
		} else {
			this.currSelectedLocale = null;
			this.gameResourcesBundles = null;
			return false;
		}
	}
	
	private boolean loadLocaleResources(String localeInfos) {
		Locale localeToLoad;
		String resourceFilePath;
		FileInputStream resourceFile;
		InputStreamReader resourceISR;
		PropertyResourceBundle newResourceBundle;

		this.gameResourcesBundles = null;
		
		if (this.gameLocales != null) {
			localeToLoad = this.gameLocales.get(localeInfos);
			
			if (localeToLoad == null) {
				localeToLoad = Locale.getDefault();
			}
			
			if (localeToLoad != null) {
				this.gameResourcesBundles = new HashMap<String, PropertyResourceBundle>(this.resourcesName.length, 1);
				
				for (byte i = 0 ; i < this.resourcesName.length ; i++) {
					resourceFilePath = this.resourcesFilesRootDirectory + "\\" + localeToLoad.getLanguage() + "\\" + this.resourcesName[i] + "_" + localeToLoad.toString() + ".properties";	
					
					try {
						resourceFile = new FileInputStream(new File(resourceFilePath));
						
						try {
							resourceISR = new InputStreamReader(resourceFile, "UNICODE");
							newResourceBundle = new PropertyResourceBundle(resourceISR);
							this.gameResourcesBundles.put(this.resourcesName[i], newResourceBundle);
						}
						catch (IOException ioe) {
							System.out.println("Erreur loading properties " + this.resourcesName[i]);
						}
					}
					catch (FileNotFoundException fnfe) {
						System.out.println("File for " + localeToLoad.getDisplayLanguage() + " language not found.");
					}
				}
				
				if (this.gameResourcesBundles.size() == 0) {
					return false;
				}
				else {
					this.currSelectedLocale = localeToLoad;
					Locale.setDefault(Locale.Category.DISPLAY, localeToLoad);
					return true;
				}
					
			}
		}

		return false;
	}
}
