package binarygameUI;

import binarygamelogic.*;

public interface LocalesResourcesUIInterface {
	public void setResourcesController(LocalesResourcesControllerInterface resourcesController);
	
	public LocalesResourcesControllerInterface getResourcesController();
	
	public void updateLocalesResources();
}
