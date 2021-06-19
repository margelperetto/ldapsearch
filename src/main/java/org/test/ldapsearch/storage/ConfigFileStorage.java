package org.test.ldapsearch.storage;

public class ConfigFileStorage extends AbstractPropertiesStorage<PropFileConfig>{
	private static final String PROP_FILE = INSTALL_FOLDER+"system.bd";
	
	private static ConfigFileStorage instance;
	
	public static synchronized ConfigFileStorage getInstance() throws Exception {
	    if(instance == null)
	        instance = new ConfigFileStorage(PROP_FILE);
	    return instance;
	}
	
	private ConfigFileStorage(String file) throws Exception {
	    super(file);
	}
}
