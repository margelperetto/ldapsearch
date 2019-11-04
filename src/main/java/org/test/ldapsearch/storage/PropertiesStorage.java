package org.test.ldapsearch.storage;

import java.io.File;

public class PropertiesStorage extends AbstractPropertiesStorage<PropKey>{

	public static final String DEFAULT_PROP_FILE = System.getProperty("user.home")+File.separator+".ldapsearch"+File.separator+"conf_ldapsearch.properties";
	
	private static PropertiesStorage instance;
	
	public synchronized static PropertiesStorage getInstance() {
	    if(instance == null)
	        throw new RuntimeException("Properties storage not defined!");
	    return instance;
	}
	
	public synchronized static void setInstance(String file) throws Exception {
	    instance = new PropertiesStorage(file == null ? DEFAULT_PROP_FILE : file);
	}
	
	private PropertiesStorage(String file) throws Exception {
	   super(file);
	}
}
