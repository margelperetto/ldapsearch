package org.test.ldapsearch;

import java.io.File;

import javax.swing.UIManager;

import org.test.ldapsearch.argsreader.ArgumentProperties;
import org.test.ldapsearch.argsreader.ArgumentReader;
import org.test.ldapsearch.view.MainFrame;

public class App  {
	
    private static final String KEY_STORE = "javax.net.ssl.keyStore";
    private static final String TRUST_STORE = "javax.net.ssl.trustStore";
    private static final String JAVA_HOME = "java.home";

    public static void main( String[] args ) {
        setSSLProperties();
        
        if(args==null || args.length==0) {
            openGraphicInterface();
            return;
        }
        
        runCommandLine(args);
    }
    
    private static void runCommandLine(String[] args) {
        try {
            ArgumentProperties prop = createDefaultProperties();
            if(!new ArgumentReader(args).parseArguments(prop)) {
                return;
            }
            new RuntimeSearch(prop).connectAndSearch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static ArgumentProperties createDefaultProperties() {
        ArgumentProperties prop = new ArgumentProperties();
        prop.setAddress("ldap://contoso.local");
        prop.setUser("administrator");
        prop.setPass("con");
        prop.setPath("DC=contoso,DC=local");
        prop.setFilter("(&(displayName=*)(objectClass=User))");
        prop.setAttrs("displayName|mail|samAccountName|department|title");
        prop.setPageSize(1000);
        return prop;
    }

    private static void openGraphicInterface() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            new MainFrame().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void setSSLProperties() {
        String javaHome = System.getProperty(JAVA_HOME);
        String cacertsFile = javaHome+File.separator+"lib"+File.separator+"security"+File.separator+"cacerts";
        if(new File(cacertsFile).exists()) {
            if(System.getProperty(KEY_STORE)==null) {
                System.setProperty(KEY_STORE, cacertsFile);
            }
            if(System.getProperty(TRUST_STORE)==null) {
                System.setProperty(TRUST_STORE, cacertsFile);
            }
        }else {
            System.err.println("'"+cacertsFile+"' not found!");
        }
        System.setProperty("com.sun.jndi.ldap.object.disableEndpointIdentification", "true");
        
        System.out.println("-----------------------------------------------------------------------------------");
        System.out.println(JAVA_HOME+":                "+System.getProperty(JAVA_HOME));
        System.out.println(KEY_STORE+":   "+System.getProperty(KEY_STORE));
        System.out.println(TRUST_STORE+": "+System.getProperty(TRUST_STORE));
        System.out.println("-----------------------------------------------------------------------------------\n");
    }
}