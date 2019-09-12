package org.test.ldapsearch;

import java.util.List;

import javax.naming.directory.SearchResult;
import javax.naming.ldap.LdapContext;

import org.test.ldapsearch.argsreader.ArgumentProperties;
import org.test.ldapsearch.utils.LDAPSearchUtils;

public class RuntimeSearch {

    private ArgumentProperties prop;

    public RuntimeSearch(ArgumentProperties prop) {
        this.prop = prop;
    }

    public void connectAndSearch() {
        LdapContext ctx = connect();
        searchAndShowResults(ctx);
    }

    private void searchAndShowResults(LdapContext ctx) {
        List<SearchResult> results = searchUsers(ctx);
        showResults(results);
    }
    
    private void showResults(List<SearchResult> results) {
        try {
            String[][] data = LDAPSearchUtils.getData(results, prop.getAttributes());
            
            CommandLineTable st = new CommandLineTable();
            st.setHeaders(prop.getAttributes());
            for (int i = 0; i < data.length; i++) {
                st.addRow(data[i]);
            }
            st.print();
        }catch (Exception e) {
            throw new RuntimeException("Error show results! "+e.getMessage(), e);
        }
    }

    private LdapContext connect() {
        String ldapURL = prop.getAddress();
        String userName = prop.getUser();
        String userPassword = prop.getPass();
        boolean forceSSL = prop.isForceSSL();
        return LDAPSearchUtils.connect(ldapURL, userName, userPassword, forceSSL);
    }

    public List<SearchResult> searchUsers(LdapContext ctx) {
        String pathStr = prop.getPath();
        String filterStr = prop.getFilter();
        String[] attrs = prop.getAttributes();
        int limit = prop.getLimit();
        return LDAPSearchUtils.getSearchResults(ctx, pathStr, filterStr, attrs, limit);
    }

}