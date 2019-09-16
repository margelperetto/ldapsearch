package org.test.ldapsearch.argsreader;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ArgumentReader {
	
	private String[] args;
	
	public ArgumentReader(String[] args) {
		this.args = args;
	}

	public boolean parseArguments(ArgumentProperties properties) throws ParseException {
		Options options = new Options();
		options.addOption(new Option("a", "address", true, "ldap://contoso.local"));
		options.addOption(new Option("u", "user", true, "administrator"));
		options.addOption(new Option("w", "password", true, "con"));
		options.addOption(new Option("f", "filter", true, "(&(displayName=*)(objectClass=User))"));
		options.addOption(new Option("p", "path", true, "DC=contoso,DC=local"));
		options.addOption(new Option("t", "attributes", true, "displayName|mail|samAccountName|department|title"));
		options.addOption(new Option("g", "pageSize", true, "1000"));
		options.addOption(new Option("s", "forceSSL", true, "false"));
		CommandLineParser parser = new DefaultParser();
		HelpFormatter formatter = new HelpFormatter();
		try {
			CommandLine cmd = parser.parse(options, args);
			populateProperties(cmd, properties);
			return true;
		} catch (ParseException e) {
			formatter.printHelp("LDAP Search", options);
			return false;
		} catch (Exception e) {
		    throw new RuntimeException("Error parse arguments!");
        }
	}

	private void  populateProperties(CommandLine cmd, ArgumentProperties properties) {
		String address = cmd.getOptionValue("address");
		String user = cmd.getOptionValue("user");
		String pass = cmd.getOptionValue("password");
		String filter = cmd.getOptionValue("filter");
		String path = cmd.getOptionValue("path");
		String attrs = cmd.getOptionValue("attributes");
		String pageSize = cmd.getOptionValue("pageSize");
		String forceSSL = cmd.getOptionValue("forceSSL");
		
		if(address!=null) properties.setAddress(address);
		if(user!=null) properties.setUser(user);
		if(pass!=null) properties.setPass(pass);
		if(filter!=null) properties.setFilter(filter);
		if(path!=null) properties.setPath(path);
		if(attrs!=null) properties.setAttrs(attrs);
		if(pageSize!=null) properties.setPageSize(Integer.parseInt(pageSize));
		if(forceSSL!=null) properties.setForceSSL(Boolean.parseBoolean(forceSSL));
	}
}
