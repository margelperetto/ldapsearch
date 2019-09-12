package org.test.ldapsearch.argsreader;


public class ArgumentProperties {

	private String address;
	private String user;
	private String pass;
	private String filter;
	private String path;
	private String attrs;
	private int limit;
	private boolean forceSSL;
	
    public String getUser() {
        return this.user;
    }
    public String getPass() {
        return this.pass;
    }
    public String getFilter() {
        return this.filter;
    }
    public String getPath() {
        return this.path;
    }
    public String getAttrs() {
        return this.attrs;
    }
    public int getLimit() {
        return this.limit;
    }
    public void setUser(String user) {
        this.user = user;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }
    public void setFilter(String filter) {
        this.filter = filter;
    }
    public void setPath(String path) {
        this.path = path;
    }
    public void setAttrs(String attrs) {
        this.attrs = attrs;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public boolean isForceSSL() {
        return forceSSL;
    }
    public void setForceSSL(boolean forceSSL) {
        this.forceSSL = forceSSL;
    }
    
    public String[] getAttributes() {
        return attrs.split("\\|");
    }
	
}
