package org.test.ldapsearch.view;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

public class PropertiesUtils {

	private static final String PROP_FILE = System.getProperty("user.home")+File.separator+"conf_ldapsearch.properties";
	private static final Properties PROPERTIES = new Properties();
	
	public static void loadProperties() throws Exception{
		if(new File(PROP_FILE).exists()){
			PROPERTIES.load(new FileInputStream(PROP_FILE));
		}
	}
	
	public static void saveProperties() throws Exception{
		PROPERTIES.store(new FileOutputStream(PROP_FILE), "");
	}
	
	public static void setProp(Prop p, String value){
		PROPERTIES.setProperty(p.name(), value);
	}
	
	public static String getProp(Prop p){
		return PROPERTIES.getProperty(p.name());
	}
	
	public static enum Prop{
		URL,USER,PASS,PATH,FILTER,ATTRIBUTES,LIMIT;
	}

    public static void setPropArray(Prop p, String[] values) {
        setProp(p, convertArrayToString(values));
    }
    
    private static String convertArrayToString(String[] values) {
        StringBuilder sb = new StringBuilder();
        for(String v : values) {
            sb.append(v).append(";");
        }
        String value = sb.toString();
        return value.isEmpty()?"":value.substring(0, value.length()-1);
    }

    public static String[] getPropArray(Prop p) {
        String value = getProp(p);
        if(value!=null && !value.trim().isEmpty()) {
            return value.split(";");
        }
        return new String[0];
    }

    public static int getPropInt(Prop p, int defaultValue) {
        try {
            return Integer.parseInt(getProp(p));
        }catch (Exception e) {
            return defaultValue;
        }
    }
}
