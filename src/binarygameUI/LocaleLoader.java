//System.out.println(System.getProperty("java.class.path")); 

package binarygameUI;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Locale;
import java.util.HashMap;
import java.util.PropertyResourceBundle;
import java.util.LinkedList;
import java.util.Iterator;

public class LocaleLoader {
	HashMap<String, Locale> binairoLocales;
	HashMap<String, PropertyResourceBundle> binairoResourceBundles;
	String languageFilesRootDirectory;
	LinkedList<String> resourcesName;
	
	public LocaleLoader(String languageFilesRootDirectory, String[] resourcesName, String startingLocale) {
		reloadLocales(languageFilesRootDirectory, resourcesName, startingLocale);
	}
	
	public String getLanguageFilesRootDirectory() {
		return this.languageFilesRootDirectory;
	}
	
	public String[] getResourcesName() {
		String[] resourcesNameArray = new String[this.resourcesName.size()];
		return this.resourcesName.toArray(resourcesNameArray);
	}
	
	public LinkedList<String> getLocalesInfosList() {
		if (this.binairoLocales != null) {
			LinkedList<String> localesInfos = new LinkedList<String>();
			localesInfos.addAll(this.binairoLocales.keySet());
			return localesInfos;
		}
		else
			return null;
	}
	
	public HashMap<String, String> getLocalesDisplayName() {
		if (this.binairoLocales != null) {
			HashMap<String, String> localesDisplayName = new HashMap<String, String>();
			Locale currLocale;
			String currLocaleName;
			
			for (String key : this.binairoLocales.keySet()) {
				currLocale = this.binairoLocales.get(key);
				
				currLocaleName = currLocale.getDisplayName(currLocale);
				currLocaleName = currLocaleName.substring(0, 1).toUpperCase(currLocale) + currLocaleName.substring(1, currLocaleName.length());
				
				localesDisplayName.put(currLocale.toString(), currLocaleName);
			}
			
			return localesDisplayName;
		}
		else
			return null;
	}
	
	public PropertyResourceBundle getBinairoResource(String resourceName) {
		if (this.binairoResourceBundles != null)
			return this.binairoResourceBundles.get(resourceName);
		else
			return null;
	}
	
	public boolean reloadLocales(String languageFilesRootDirectory, String[] resourcesName, String startingLocale) {
		this.binairoLocales = null;
		this.binairoResourceBundles = null;
		
		this.languageFilesRootDirectory = languageFilesRootDirectory;
		
		this.resourcesName = new LinkedList<String>();
		for (byte i = 0 ; i < resourcesName.length ; i++) {
			this.resourcesName.add(resourcesName[i]);
		}
		
		if (!loadLocales())
			return false;
		
		if (!loadLocaleResources(startingLocale))
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

		this.binairoLocales = null;
		
		try {
			localesFile = new FileInputStream(new File(languageFilesRootDirectory + "\\Locales.properties"));
			
			try {
				localesISR = new InputStreamReader(localesFile, "UNICODE");
				localesRB = new PropertyResourceBundle(localesISR);
				
				supportedLocales = localesRB.getString("supported_locales").split(";");
				
				if (supportedLocales.length > 0) {
					this.binairoLocales = new HashMap<String, Locale>(supportedLocales.length, 1);
					
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
							this.binairoLocales.put(currLocale.toString(), currLocale);
						}	
					}
				}
			}
			catch (IOException ioe) {
				this.binairoLocales = null;
				return false;
			}
		}
		catch (FileNotFoundException e) {
			this.binairoLocales = null;
			return false;
		}
		
		if (this.binairoLocales.size() == 0) {
			this.binairoLocales = null;
			return false;
		}
		else
			return true;
	}
	
	public boolean loadLocaleResources(String localeInfos) {
		Locale localeToLoad;
		String localeFilePath;
		FileInputStream localeFiles;
		InputStreamReader localeISR;
		PropertyResourceBundle localeRB = null;
		Iterator<String> resourcesNameItr;
		String currResourceName;

		if (this.binairoLocales != null) {
			localeToLoad = this.binairoLocales.get(localeInfos);
			
			if (localeToLoad != null) {
				this.binairoResourceBundles = new HashMap<String, PropertyResourceBundle>(resourcesName.size(), 1);
				
				resourcesNameItr = this.resourcesName.iterator();
				while (resourcesNameItr.hasNext()) {
					currResourceName = resourcesNameItr.next();
					localeFilePath = this.languageFilesRootDirectory + "\\" + localeToLoad.getLanguage() + "\\" + currResourceName + "_" + localeToLoad.toString() + ".properties";	
					
					try {
						localeFiles = new FileInputStream(new File(localeFilePath));
						
						try {
							localeISR = new InputStreamReader(localeFiles, "UNICODE");
							localeRB = new PropertyResourceBundle(localeISR);
							this.binairoResourceBundles.put(currResourceName, localeRB);
						}
						catch (IOException ioe) {
							System.out.println("Erreur loading properties.");
							this.binairoResourceBundles = null;
							return false;
						}
					}
					catch (FileNotFoundException fnfe) {
						System.out.println("File for " + localeToLoad.getDisplayLanguage() + " language not found.");
						this.binairoResourceBundles = null;
						return false;
					}
				}
					
				return true;
			}
		}
		
		this.binairoResourceBundles = null;
		return false;
	}
}

