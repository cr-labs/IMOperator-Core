package com.challengeandresponse.imoperator.comm;

import java.util.Iterator;
import java.util.Vector;

import org.jivesoftware.smack.*;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketIDFilter;
import org.jivesoftware.smack.packet.*;

import com.challengeandresponse.imoperator.test.ProcessorEcho;

/**
 * <p>Services instantiate an IMOperatorConnection object and use it to communicate with the 
 * IMOperator server's interface for services. This class encapsulates the basics of
 * XMPP communication:<br />
 * - connecting to a server<br />
 * - logging in, and binding packet filter and processor to the connection<br />
 * - logging out<br />
 * - sending messages<br />
 * </p>
 * 
 * TODO Get nodes on one another's rosters the first time they try to communicate. Otherwise 2 messages have to fail if "onlyIfOnline= true" because the nodes can't see each other's status on initial communications. can set onlyIfOnline = false, but it's preferable to be able to know status!
 * 
 * @author jim
 *
 */

/*
 * REVISION HISTORY
 * 2007-03-05 Created - Jim
 * 2008-03-20 Patched up a bit, trimmed the documentation
 * 2008-04-06 Now uses the package's TimeoutException rather than one from java.concurrent (doh)
 * 
 * KNOWN ISSUES
 * beware of memory usage issues - the connection adds ALL correspondents to its roster (to
 * prevent attempting to send when the addressee would be offline) - this list grows without
 * bound, maybe just an issue for the server (?)  - where is the roster kept?
 */

public class SimpleXMPPConnection {
	private XMPPConfig xmppConfig = null; // xmppconfig object is the source of connection settings, password and username, if non-null

	private boolean verbose;
	private boolean allowSelfSigned;

	// connection state
	private XMPPConnection xmppConnection;
	private Presence currentPresence;
	private Vector <PacketListener> packetListeners;
	private Roster roster;

	// public constants
	public static final int XMPP_CLIENT_DEFAULT_PORT = 5222;


	/**
	 * Constructor for connections that use only the Xmpp Service Name - via DNS SVR lookup. Preferred.
	 * @param xmppServiceName
	 */
	public SimpleXMPPConnection(String xmppServiceName, boolean allowSelfSigned) {
		this (null,-1,xmppServiceName,allowSelfSigned);
	}

	/**
	 * Full constructor that forces the host and port settings, rather than performing an SVR lookup...
	 * ... or perhaps there is no SVR record to look up.
	 * @param xmppHost
	 * @param xmppHostPort
	 * @param xmppServiceName
	 * @param allowSelfSigned
	 */
	public SimpleXMPPConnection(String xmppHost, int xmppHostPort, String xmppServiceName, boolean allowSelfSigned) {
		this(new XMPPConfig(xmppHost, xmppHostPort, null, xmppServiceName, null, null),allowSelfSigned);
	}

	/**
	 * This constructor uses the XMPPConfig object... easiest to manage if your config is coming from a file...
	 * see XMPPConfig for details on that...
	 * @param config
	 * @param allowSelfSigned
	 */
	public SimpleXMPPConnection(XMPPConfig config, boolean allowSelfSigned) {
		this.xmppConfig = config;
		this.allowSelfSigned = allowSelfSigned;
		this.xmppConnection = null;
		this.verbose = false;
		this.currentPresence = new Presence(Presence.Type.unavailable);
		this.packetListeners = new Vector <PacketListener> ();
	}




	/**
	 * Set the packet listener and filter on a SimpleXMPPConnection
	 * @param packetListener an initialized PacketListener
	 * @param packetFilter an initialized PacketFilter
	 * @throws SimpleXMPPException if the connection is not open or otherwise cannot be 
	 */
	public void addPacketListenerAndFilter(PacketListener packetListener,PacketFilter packetFilter)
	throws SimpleXMPPException {
		if ( (xmppConnection == null) || (! xmppConnection.isConnected()) )
			throw new SimpleXMPPException("XMPP connection is not open. Cannot set listener and filter.");
		xmppConnection.addPacketListener(packetListener,packetFilter);
		packetListeners.add(packetListener);
	}

	/**
	 * Removes a packet listener from a SimpleXMPPConnection
	 * @param packetListener the initialized PacketListener to remove. Should have been previously added.
	 * @throws SimpleXMPPException if the connection is not open or otherwise cannot be 
	 */
	public void removePacketListenerAndFilter(PacketListener packetListener)
	throws SimpleXMPPException {
		if ( (xmppConnection == null) || (! xmppConnection.isConnected()) )
			throw new SimpleXMPPException("XMPP connection is not open. Cannot remove packetListener.");
		xmppConnection.removePacketListener(packetListener);
		packetListeners.remove(packetListener);
	}





	/**
	 * Set VERBOSE status for progress log 
	 * @param setTo true for verbose, false otherwise. False is the default.
	 */
	public void setVerbose(boolean setTo) {
		this.verbose = setTo;
	}


	/**
	 * Connect securely to the server named in the constructor, log in using username and password, and 
	 * NOT setting a resource name.
	 * then bind the packetListener and packetFilter to the connection
	 * Connection must be secure and using TLS, or an exception will be thrown. If the connection is not secure, login will not be attempted.
	 * Self-signed certificates are explicitly permitted by this method.
	 * @param username the username to log in with
	 * @param password and the matching password for that username
	 */
	public void secureConnect(String username, String password)
	throws SimpleXMPPException {
		xmppConfig.setUsername(username);
		xmppConfig.setPassword(password);
		xmppConfig.setResource(null);
		secureConnect();
	}

	/**
	 * Connect securely to the server named in the constructor, log in using username and password,
	 * and forcing a resource name...
	 * then bind the packetListener and packetFilter to the connection
	 * Connection must be secure and using TLS, or an exception will be thrown. If the connection is not secure, login will not be attempted.
	 * Self-signed certificates are explicitly permitted by this method.
	 * <p>THIS DOES NOT SEND PRESENCE -- so clients MUST explicitly set "available" presence.
	 * The sendPresence() method is provided to make this easy.</p>
	 * 
	 */
	public void secureConnect()
	throws SimpleXMPPException {
		try {
			if (verbose)
				System.out.println("Opening XMPP communications");
			// if an xmpp host is set, use that host and port, otherwise use the domain and do a SVR lookup
			if (xmppConfig.getHost() == null) {
				if (verbose)
					System.out.println("Connecting via SVR discovery to service:"+xmppConfig.getService());
				xmppConnection = new XMPPConnection(xmppConfig.getService());
			}
			else {
				if (verbose)
					System.out.println("Connecting directly to host:port:service "+xmppConfig.getHost()+":"+xmppConfig.getPort()+":"+xmppConfig.getService());
				ConnectionConfiguration cc = new ConnectionConfiguration(xmppConfig.getHost(),xmppConfig.getPort(),xmppConfig.getService());
				cc.setSecurityMode(ConnectionConfiguration.SecurityMode.required);
				cc.setSelfSignedCertificateEnabled(allowSelfSigned);
				xmppConnection = new XMPPConnection(cc);
				xmppConnection.connect();
			}
			// sleep briefly to let connection get going (recommended by someone in a newsgroup ha ha ha why not)
			try { Thread.sleep(1000); } catch (InterruptedException ie) { }

			if (! xmppConnection.isSecureConnection())
				throw new SimpleXMPPException("Connection established, but it is not secure.");
			if (! xmppConnection.isUsingTLS())
				throw new SimpleXMPPException("Connection established, but it is not using TLS.");

			if (verbose)
				System.out.println("Logging in with username:"+xmppConfig.getUsername()+" and resource:"+xmppConfig.getResource());
			xmppConnection.login(xmppConfig.getUsername(), xmppConfig.getPassword(),xmppConfig.getResource(),false);

			if (verbose)
				System.out.println("Logged in.");

			// sleep briefly to let connection get going (recommended by someone in a newsgroup ha ha ha why not)
			try { Thread.sleep(1000); } catch (InterruptedException ie) { }

			if (verbose)
				System.out.println("Getting roster.");
			roster = xmppConnection.getRoster();
			if (verbose)
				System.out.println("Got roster:"+roster.getEntries());
		}
		catch (XMPPException xmppe) {
			String msg = xmppe.getMessage();
			if (xmppe.getMessage().indexOf("401") > -1)
				msg = "Authorization failure "+msg;
			else if (xmppe.getMessage().toLowerCase().indexOf("sasl authentication failed") > -1)
				msg = "Authorization failure "+msg;
			else if (xmppe.getMessage().indexOf("504") > -1)
				msg = "Unknown host "+msg;
			else if (xmppe.getMessage().indexOf("502") > -1)
				msg = "IOException "+msg;
			throw new SimpleXMPPException("XMPPException: "+msg);
		}
	}






	/**
	 * Set the presence type in this class to 'pt' and send presence status to the server
	 * @param pt
	 */
	public void sendPresence(Presence.Type pt,Presence.Mode pm) {
		currentPresence.setType(pt);
		currentPresence.setMode(pm);
		xmppConnection.sendPacket(currentPresence);
	}


	/**
	 * Send a message via the server, failing with an exception if the recipient is offline (not in this agent's roster)
	 * @param to
	 * @param message
	 * @throws SimpleXMPPException if the message sending failed (e.g. user is offline or server not available)
	 */
	public void sendMessage(String to,String message)
	throws SimpleXMPPException {
		sendMessage(to,message,true);
	}


	/**
	 * Send a message via the server, with override to allow sending even if the recipient is offline
	 * @param to
	 * @param message
	 * @param onlyIfOnline if true, check recipient's online status before sending. If recipient is not online, don't send the packet, but throw a SimpleXMPPException instead
	 * @throws SimpleXMPPException if the message sending failed (e.g. user is offline or server not available)
	 */
	public void sendMessage(String to,String message,boolean onlyIfOnline)
	throws SimpleXMPPException {
		String newTo = to;
		// patch to: addresses and other things escaped by MSN
		if (to.contains("@msn.")) {
			newTo = to.replace("\40","@");
		}

		try {
			// only send message if user is online
			if (onlyIfOnline) {
				if (! roster.contains(to))
					roster.createEntry(to,to,null);
				Presence pres = roster.getPresence(to);
				if (pres.getType() != Presence.Type.available) {
					throw new SimpleXMPPException("Not available: "+to);
				}
			}
			// send the message
			Message m = new Message();
			m.setTo(newTo);
			m.setBody(message);
			xmppConnection.sendPacket(m);
		}
		catch (Exception e) {
			throw new SimpleXMPPException(e.getLocalizedMessage());
		}
	}

	/**
	 * Send a message via the server. If an exception is thrown, discard it.
	 * This convenience method is intended for advisory messages that might be
	 * sent inside some application-level exception handler, for example, or
	 * other cases where the caller can't deal with an unsent message and so  
	 * has no interest in exceptions.
	 * @param to
	 * @param message
	 */
	public void sendNoExceptionMessage(String to,String message) {
		try {
			sendMessage(to,message);
		}
		catch (SimpleXMPPException e) {
		}
	}



	/**
	 * Send an IQ packet via the server
	 * @param to	addressee
	 * @param iq	the IQ packet to send
	 * @param onlyIfOnline if true, check recipient's online status before sending. If recipient is not online, don't send the packet, but throw a SimpleXMPPException instead
	 * @throws SimpleXMPPException if the message sending failed (e.g. user is offline or server not available)
	 */
	public void sendIQ(String to,IQ iq,boolean onlyIfOnline)
	throws SimpleXMPPException {
		try {
			if (onlyIfOnline) {
				// only send message if user is online
				if (! roster.contains(to))
					roster.createEntry(to,to,null);
				Presence pres = roster.getPresence(to);
				if (verbose)
					System.out.println("Presence of "+to+" is "+pres.getStatus());
				if (pres.getType() != Presence.Type.available) {
					throw new SimpleXMPPException("Not available: "+to);
				}
			}
			iq.setTo(to);
			xmppConnection.sendPacket(iq);
		}
		catch (Exception e) {
			throw new SimpleXMPPException(e.getLocalizedMessage());
		}
	}


	/**
	 * Send an IQ packet via the server. If an exception is thrown, discard it.
	 * This convenience method is intended for advisory messages that might be
	 * sent inside some application-level exception handler, for example, or
	 * other cases where the caller can't deal with an unsent message and so  
	 * has no interest in exceptions.
	 * @param to	addressee
	 * @param iq	the IQ packet to send
	 * @param onlyIfOnline	if true, only attempt sending if the addressee is online
	 */
	public void sendNoExceptionIQ(String to,IQ iq, boolean onlyIfOnline) {
		try {
			sendIQ(to,iq,onlyIfOnline);
		}
		catch (SimpleXMPPException e) {
		}
	}


	/**
	 * Send an IP packet via the server, then wait for and return the reply packet.
	 * The "reply" is recognized as a packet having the same packetID as the sent packet, coming from the JID to which the first packet was sent.
	 * @param to	addressee
	 * @param iq	the IQ packet to send
	 * @param onlyIfOnline if true, check recipient's online status before sending. If recipient is not online, don't send the packet, but throw a SimpleXMPPException instead
	 * @param timeout msec to wait for the packet, timing out if it doesn't arrive.
	 * @return the reply packet from the address, if one is received before the timeout
	 * @throws SimpleXMPPException if the message sending failed (e.g. user is offline or server not available)
	 * @throws TimeoutException if the timeout expired before a reply was received
	 */
	public IQ sendIQgetIQ(String to, IQ iq, boolean onlyIfOnline, long timeout)
	throws SimpleXMPPException, TimeoutException {
		// set up a PacketCollector to receive only the response desired
		PacketIDFilter pf = new PacketIDFilter(iq.getPacketID()); 
		PacketCollector pc = xmppConnection.createPacketCollector(pf);
		// send the packet
		sendIQ(to,iq,onlyIfOnline);
		// block for the response. Return it if received, or throw exception if not
		Packet p = pc.nextResult(timeout);
		// if timeout, nextResult() returns null
		if (p == null) {
			throw new TimeoutException("sendIQgetIQ reply packet not received within "+timeout+" msec");
		}
		if (! (p instanceof IQ)) {
			throw new SimpleXMPPException("sendIQgetIQ reply packet received was not an IQ packet");
		}
		pc.cancel();
		return (IQ) p;
	}






	/**
	 * @return the XMPPConnection underlying this SimpleXMPPConnection, so that its
	 * methods can be called directly, for manipulations not supported in this class
	 */
	public XMPPConnection getXMPPConnection() {
		return this.xmppConnection;
	}


	/**
	 * Disconnect from the server, close the connection and null out the connection state
	 */
	public void disconnect() {
		if (verbose)
			System.out.println("Disconnecting");
		if ((xmppConnection != null) && (xmppConnection.isConnected())) {
			this.currentPresence.setType(Presence.Type.unavailable);
			this.currentPresence.setMode(Presence.Mode.away);
			Iterator <PacketListener> it = packetListeners.iterator();
			while (it.hasNext()) 
				xmppConnection.removePacketListener(it.next());
			xmppConnection.disconnect(this.currentPresence);
			xmppConnection = null;
		}
	}		

	




	// for testing
	public static void main(String[] args)
	throws SimpleXMPPException {
		
		System.out.println("SimpleXMPPConnection test running");

		XMPPConfig config = new XMPPConfig("/Users/jim/Projects/RandD_Projects/IMOperator/config/imoperator.xml",XMPPConfig.CONFIG_ROOT_ELEMENT);

		SimpleXMPPConnection xmppc = new SimpleXMPPConnection(config,true);
		xmppc.setVerbose(true);
		xmppc.secureConnect();

		ProcessorEcho pe = new ProcessorEcho(xmppc);
		xmppc.addPacketListenerAndFilter(pe,pe);

		try {
			Thread.sleep(4000);
		}
		catch (InterruptedException ie) { }

		xmppc.disconnect();

	}



}
