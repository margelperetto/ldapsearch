package org.test.ldapsearch.storage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class AbstractPropertiesStorage<P extends PropKey> {

	private final Properties properties;
    private String file;
	
	AbstractPropertiesStorage(String file) throws Exception {
	    this.file = file;
        this.properties = new Properties();
	    loadProperties(file);
	}
	
	private void loadProperties(String file) throws Exception{
		File confFile = new File(file);
        if(confFile.exists()){
			properties.load(new FileInputStream(file));
		} else if(!confFile.getParentFile().exists()){
		    confFile.getParentFile().mkdir();
		}
	}
	
	public void saveProperties() throws Exception{
		properties.store(new FileOutputStream(file), "");
	}
	
	public void setProp(P p, String value){
		properties.setProperty(p.name(), value);
	}
	
	public String getProp(P p){
		return properties.getProperty(p.name());
	}
	
    public void setPropArray(P p, String[] values) {
        setProp(p, convertArrayToString(values));
    }
    
    private String convertArrayToString(String[] values) {
        StringBuilder sb = new StringBuilder();
        for(String v : values) {
            sb.append(v).append(";");
        }
        String value = sb.toString();
        return value.isEmpty()?"":value.substring(0, value.length()-1);
    }

    public String[] getPropArray(P p) {
        String value = getProp(p);
        if(value!=null && !value.trim().isEmpty()) {
            return value.split(";");
        }
        return new String[0];
    }

    public int getPropInt(P p, int defaultValue) {
        try {
            return Integer.parseInt(getProp(p));
        }catch (Exception e) {
            return defaultValue;
        }
    }
    
}
