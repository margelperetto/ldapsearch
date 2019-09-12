package org.test.ldapsearch.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.Control;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import javax.naming.ldap.PagedResultsControl;

import org.test.ldapsearch.ssl.LdapSSLSocketFactory;

public final class LDAPSearchUtils {
    
    private LDAPSearchUtils() {}

    public static String[] breakPaths(String path) {
        path = path.trim();
        if (path.length() == 0) {
            return new String[]{};
        }
        if (path.charAt(0) != '(') {
            return new String[]{path};
        }
        int start = path.indexOf("(");
        List<String> l = new ArrayList<>();
        while (start > -1) {
            final int end = path.indexOf("),(", start);
            if (end == -1) {
                if (path.charAt(path.length() - 1) == ')') {
                    l.add(path.substring(start + 1, path.length() - 1));
                    break;
                } else {
                    throw new RuntimeException("Invalid path! End ')' not found!");
                }
            }
            l.add(path.substring(start + 1, end));
            start = path.indexOf("(", end);
        }
        return l.toArray(new String[]{});
    }

    public static boolean usingSSL(String ldapURL) {
        return ldapURL.toLowerCase().trim().startsWith("ldaps:");
    }

    public static List<SearchResult> getSearchResults(LdapContext ctx, String pathStr, String filterStr, String[] attributes, int limit) {
        try {
            System.out.println("Searching...");
            System.out.println("Path: "+pathStr);
            System.out.println("Filter: "+filterStr);
            System.out.println("Attrs: "+Arrays.asList(attributes));
            System.out.println("Limit: "+limit);
            
            List<SearchResult> results = new ArrayList<>();
            String[] paths = LDAPSearchUtils.breakPaths(pathStr);
            for (String path : paths) {
                String filter = filterStr.replaceAll("\n", "");
                
                SearchControls searchCtls = new SearchControls();
                searchCtls.setSearchScope(SearchControls.SUBTREE_SCOPE);
                searchCtls.setReturningAttributes(attributes);
                Control[] ctls = new Control[]{new PagedResultsControl(limit, true)};
                ctx.setRequestControls(ctls);
                NamingEnumeration<SearchResult> enumerations = ctx.search(path, filter, searchCtls);
                while (enumerations.hasMoreElements()) {
                    results.add(enumerations.nextElement());
                }
            }
            System.out.println("Search done! Results-> "+results.size()+"\n");
            return results;
        }catch (Exception e) {
            System.err.println("Search fail!\n");
            throw new RuntimeException("Search error! "+e.getMessage(), e);
        }
    }

    public static LdapContext connect(String ldapURL, String user, String pass, boolean forceSSL) {
        try {
            System.out.println("Connecting...");
            System.out.println("URL: "+ldapURL);
            System.out.println("USER: "+user);
            System.out.println("PASS: "+new String(new char[pass.length()]).replace("\0", "*"));
            
            Map<Object, Object> map = new HashMap<>();
            map.put(Context.SECURITY_PRINCIPAL, user);
            map.put(Context.SECURITY_CREDENTIALS, pass);
            map.put(Context.PROVIDER_URL, ldapURL);
            map.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
            map.put(Context.SECURITY_AUTHENTICATION, "simple");
            map.put("java.naming.ldap.attributes.binary", "objectGUID");
            if(forceSSL || LDAPSearchUtils.usingSSL(ldapURL)) {
                map.put(Context.SECURITY_PROTOCOL, "ssl");
                map.put("java.naming.ldap.factory.socket", LdapSSLSocketFactory.class.getName());
            }
            InitialLdapContext ctx = new InitialLdapContext(new Hashtable<>(map), null);
            System.out.println("Connected!\n");
            return ctx;
        } catch (Exception e) {
            System.err.println("Connection fail!\n");
            throw new RuntimeException("Error LDAP connection! "+ldapURL+" - "+user, e);
        }
    }
    
    public static Object getValue(List<SearchResult> results, String[] attributes, int r, int c) {
        try {
            return results.get(r).getAttributes().get(attributes[c]).get();
        } catch (Exception e) {
            return null;
        }
    }

    public static String[][] getData(List<SearchResult> results, String[] attributes) {
        String[][] data = new String[results.size()][attributes.length];
        for (int r = 0; r < results.size(); r++) {
            for (int c = 0; c < attributes.length; c++) {
                Object obj = LDAPSearchUtils.getValue(results, attributes, r, c);
                data[r][c] = obj == null ? "" : obj.toString();
            }
        }
        return data;
    }
}
