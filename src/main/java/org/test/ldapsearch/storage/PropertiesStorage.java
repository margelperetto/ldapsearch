package org.test.ldapsearch.storage;

public class PropertiesStorage extends AbstractPropertiesStorage<PropKey>{

	private static final String DEFAULT_PROP_FILE = INSTALL_FOLDER+"conf_ldapsearch.properties";
	
	private static PropertiesStorage instance;
	
	public static synchronized PropertiesStorage getInstance() {
	    if(instance == null)
	        throw new RuntimeException("Properties storage not defined!");
	    return instance;
	}
	
	public synchronized static void setInstance(String file) throws Exception {
	    instance = new PropertiesStorage(file == null ? DEFAULT_PROP_FILE : file);
	}
	
	private final String file;
	
	private PropertiesStorage(String file) throws Exception {
	   super(file);
	   this.file = file;
	}
	
	public String getFile() {
		return file;
	}
}
