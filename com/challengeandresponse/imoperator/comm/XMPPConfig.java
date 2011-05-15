package com.challengeandresponse.imoperator.comm;

import java.io.File;

import com.challengeandresponse.configfilereader.ConfigFileReader;

/**
 * This class holds and can load from a file (or programmatically) the settings for an XMPP connection...
 * host, port, username, password -- everything needed to open an XMPP connection.
 * 
 * <p>The config file looks like this:<br />
 * <pre>
 * &lt;config&gt;
 *   &lt;host&gt;host.name.tld&lt;/xhost&gt;
 *   &lt;port&gt;portnum&lt;/port&gt;
 *   &lt;service&gt;servicename&lt;/service&gt;
 *   etcetera...
 * &lt;/config&gt;
 * </pre>
 *
 * <p>The values for the elements are string constants of this class... *_ELEMENT:<br />
 * host, port, service, username, password, resource<br />
 * </p>
 *
 * 
 * @author jim
 *
 */
public class XMPPConfig {

	//////////////////////////////////////////////
	//////////////// DEFAULTS
	//////////////////////////////////////////////
	/**
	 * Default port number for XMPP
	 */
	public static final int    	PORTNUM_DEFAULT	=		5222;


	//////////////////////////////////////////////
	//////////////// ELEMENTS IN THE CONFIG FILE
	//////////////////////////////////////////////

	public static final String	CONFIG_ROOT_ELEMENT = 	"com.challengeandresponse.imoperator.comm";

	public static final String 	HOST_ELEMENT = 		"host";
	public static final String 	PORT_ELEMENT = 		"port";
	public static final String 	SERVICE_ELEMENT = 	"service";
	public static final String 	USERNAME_ELEMENT = 	"username";
	public static final String 	PASSWORD_ELEMENT = 	"password";
	public static final String 	RESOURCE_ELEMENT = 	"resource";

	// XMPP server connection info
	private String host;
	private int port;
	private String service;
	private String username;
	private String password;
	private String resource;
	

	
	public XMPPConfig(String host, int port, String resource, String service, String username, String password) {
		this.host = host;
		this.port = port;
		this.resource = resource;
		this.service = service;
		this.username = username;
		this.password = password;
	}


	/**
	 * Construct an XMPPConfig object, loading its configuration from an XML config file's config section
	 * whose label is the same as 'configRootElement'
	 * All defaulting is "true" so that it will not stop with an exception if an element is missing.
	 * Default value for the XMPP port number is provided... all other values default to the empty string, except
	 * xmppResource, which defaults to null.
	 * The application will have to test values on its own to make sure all mandatory elements were provided in the config file
	 * 
	 * @param filePath full path to the XML config file
	 * @param configRootElement use the section inside 'configRootElement' instead of the default section name for this config (the constant CONFIG_ROOT_ELEMENT)
	 */
	public XMPPConfig(String filePath, String configRootElement)
	throws SimpleXMPPException {
		File cfile = new File(filePath);
		try {
			ConfigFileReader cfr = new ConfigFileReader(cfile,configRootElement);
			host 	= cfr.getString("", true, HOST_ELEMENT);
			port 	= cfr.getInt(PORTNUM_DEFAULT, true, PORT_ELEMENT);
			service	= cfr.getString(null, true, SERVICE_ELEMENT);
			username	= cfr.getString("", true, USERNAME_ELEMENT);
			password	= cfr.getString("", true, PASSWORD_ELEMENT);
			resource	= cfr.getString(null, true, RESOURCE_ELEMENT);
		}
		catch (Exception e) {
			throw new SimpleXMPPException(e.getMessage());
		}
	}
	
	
	/**
	 * Construct an XMPPConfig object, loading its configuration from an XML config file's config section
	 * whose label is the same as CONFIG_ROOT_ELEMENT.
	 * All defaulting is "true" so that it will not stop with an exception if an element is missing.
	 * Default value for the XMPP port number is provided... all other values default to the empty string, except
	 * xmppResource, which defaults to null.
	 * The application will have to test values on its own to make sure all mandatory elements were provided in the config file
	 * 
	 * @param filePath path to the XML config file
	 */
	public XMPPConfig(String filePath)
	throws SimpleXMPPException {
		this(filePath,CONFIG_ROOT_ELEMENT);
	}


	/**
	 * Bulk configuration of all XMPP parameters at once.
	 * Either provide: (a) service  OR (b) host, port and resource
	 */
	public void setXMPP(String host, int port, String resource, String service, String username, String password) {
		this.host = host;
		this.port = port;
		this.resource = resource;
		this.service = service;
		this.username = username;
		this.password = password;
	}


	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int portnum) {
		this.port = portnum;
	}

	public String getResource() {
		return resource;
	}

	public void setResource(String resource) {
		this.resource = resource;
	}

	public String getService() {
		return service;
	}

	public void setService(String servicename) {
		this.service = servicename;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	
	public String getXmppAddress() {
		StringBuilder sb = new StringBuilder();
		sb.append(username+"@"+host);
		if (service != null)
			sb.append("/"+service);
		return sb.toString();
	}
	

}
