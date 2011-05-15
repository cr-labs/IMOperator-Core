package com.challengeandresponse.imoperator.decorations;



/**
 * An RPC decorator for the UniversalIQ packet... this contains the extra methodCall and 
 * the optional priority fields (and maybe other things that make sense
 * for sending data along with a request to run a remote action)
 * </p>.
 * <p>The current design is NOT compliant with XEP-0009. In particular,
 * the RPC packet contains no distinct methodCall element directly under the 
 * query element.... and responses will have no methodResponse element
 * at all... right now. This should be corrected eventually. The behaviour is an
 * artifact of the way XStream creates and interprets XML data, particularly with
 * regard to figuring out which class to instantiate when presented with some XML
 * to parse. XEP-0009 also makes no mention of "priority" - it might be best
 * to fold it into a parameter of <methodName> rather than as a distinct field</p>
 * <P>Per XEP-009, these are always IQ packets of type "SET", never anything else.
 * Also the setError() method of this class overrides the inherited method
 * and sets NOTHING even if called with an XMPPError object... because RPC
 * messages wouldn't have error stanzas.</p>
 * 
 * <p>Classes using this decorator should 'implement RPCI' to guarantee 
 * that the RPC object is accessible to the class and other methods in the rest of the system.</p>
 * 
 * @author jim
 *
 */
/*
 * OPEN ISSUE:
 * Packet looks like this:
 * 	<query xmlns=classname>
 * 		<classname>
 * 			<methodName>blah</methodName>
 * 			<priority>n</priority>
 * 			<owner>blah2</owner>
 * 			<oid>blah3</oid>
 * 			class members
 * 		</classname>
 * 	</query>
 * 
 * Packet SHOULD look like this:
 * 	<query xmlns=classname>
 * 	<methodCall>
 * 	<methodName>blah</methodName>
 * 	<priority>n</priority>
 * 	<owner>blah2</owner>
 * 	<oid>blah3</oid>
 * 		<classname>
 * 			class members
 * 		</classname>
 * </methodCall>
 * 	</query>
 * 
 */

/**
 * 2008-03-19 	JEY	removed 'confidence' stuff to its own decorator class and interface
 */

public class RPC {
	private static final transient long serialVersionUID = 1L;

	/**
	 * The name of the method to run against this object, in the receiving RPC processor (agent)
	 */
	private String methodName;
	/**
	 * Relative priority of this request... only meaningful if all object creators, as well as
	 * the processor (agent) recognize the field and are coordinated in setting/interpreting it.
	 */
	private int priority;
	/**
	 * What agent created/maintains this datum? This field is used in agents that
	 * store data from other agents, where those agents are able to request
	 * and maintain (update, remove, add) their own data without impact on info
	 * stashed there by others. Probably most agents won't use this. When the agent does,
	 * owner identification must be coordinated between the agent and its clients that
	 * create these objects. Honor system and external coordination are needed so that 
	 * agents don't lie, pull records that aren't theirs in error, or cross up their
	 * records with one another. Multiple agents of one type (e.g. several geocoders) would
	 * probably uset he same creator ID so that they can recognize and handle one another's work.
	 * </p>
	 */
	private String creator;

	/**
	 * An optional object id.<br />
	 * If the object is used in a collection, an explicit 'oid' field is provided, to retrieve
	 * the objects for deletion/alteration... as their live OIDs will change from run to run
	 * and when they move across the wire.
	 */
	private String oid;
	
	
	
	public RPC() {
		this("","");
	}
	
	/**
	 * Convenience constructor that sets the method name... because many RPC implementations
	 * will not use the other RPC fields, just the method name.
	 * 
	 * @param methodName the name of the method being called
	 * @param oidPrefix the prefix to prepend to numeric OIDs generated here. If this is null, the prefix is set to an empty string.
	 */
	public RPC(String methodName, String oidPrefix) {
		setMethodName(methodName);
		setPriority(0);
		setCreator("");
		setOID();
		setOIDPrefix("");
	}
	
	
	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
	public void setPriority(int priority) {
		this.priority = priority;
	}


	public String getCreator() {
		return creator;
	}

	public void setCreator(String owner) {
		this.creator = owner;
	}



	// these prevent any repeating of the oid value itself, then the agent JID is tacked on to make a GUID
	private transient static long newOID;
	private transient static long lastOID;
	private transient static String oidPrefix = ""; // universally unique string to prepend to the generated, system time-based OID
	/**
	 * Set the OID to a unique value.
	 * <p>If the oidPrefix has been set using the 
	 * static setOIDPrefix() method, then "prefix:value" will be the form of the OID,
	 * otherwise, just the value will be placed in the OID field (no leading ":").</p>
	 * <p>Value is a long, and it is merely set to the current system time when the OID is generated.
	 * The generator is a synchronized method (1 call at a time) and sleeps 1 msec if necessary, 
	 * during execution, to guarantee that the serial value is unique for each call.</p>
	 *
	 */
	private synchronized void setOID() {
		newOID = System.currentTimeMillis();
		while (newOID == lastOID) {
			try {
				Thread.sleep(1); // 1msec should guarantee unique values, but just to be safe...
				newOID = System.currentTimeMillis();
			}
			catch (InterruptedException ie) { }
		}
		lastOID = newOID;
		this.oid = (oidPrefix.length() > 0) ? (oidPrefix+":"+newOID) : ("" + newOID);
	}

	
	/**
	 * Call this before using the constructor to create new instances of this class...
	 * then your prefix will be prepended to all generated OIDs, making them
	 * globally unique (provided you are careful setting the prefix).
	 * @param prefix the prefix to prepend to numeric OIDs generated here. If this is null, the prefix is set to an empty string.
	 */
	public static void setOIDPrefix(String prefix) {
		if (prefix != null)
			RPC.oidPrefix = prefix;
		else
			RPC.oidPrefix = "";
	}
	
	public String getOID() {
		return oid;
	}
	
// not sure why this was like this! seems wrong. the OID should not change once set
//	public String getOID() {
//		return this.oid+ " subj:"+getSubject()+" label:"+label+" from:"+startValidTime.getMillis()+" to:"+endValidTime.getMillis();
//	}

	
	
}
