package com.challengeandresponse.imoperator.universaliq;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import com.challengeandresponse.universalxstream.UniversalXStream;

/**
 * Provides an implementation of IQ with automatic Object-to-XML provided by
 * using XStream to handle the conversions for any class presented (most classes
 * can be encoded - see XStream documentation for specific limitations).
 * This class provides a concrete getChildElementXML() so child classes don't 
 * need to include anything but their own fields and methods. Just extend this class and go.
 * <p>This class works in concert with the UniversalIQProvider class which handles registration
 * of the processor with the ProcessorManager over the xmpp connection and
 * the XML-to-object reconstitution.</p>
 * <p>The IQ type is defaulted to IQ.Type.SET in the constructor but may be changed
 * by a child class calling setType().
 * 
 * @author jim
 * @version 0.11 2007-06-11
 *
 */
/*
 * REVISION HISTORY
 * 2007-03-23	Created
 * 2007-06.11	v0.11	Sets default type 'IQ.Type.SET' since the deprecated UniversalIQRPC used to do this
 */
public abstract class UniversalIQ 
extends IQ {
	
	private static transient UniversalXStream xstream = null;
	
	static {
		UniversalXStream.excludeClasses(Packet.class, IQ.class);
		xstream = new UniversalXStream();
	}
	

	/**
	 * Instantiate a new UniversalIQ object
	 */
	public UniversalIQ() {
		super();
		setType(IQ.Type.SET);
	}

	
	public static UniversalXStream getXStreamInstance() {
		return xstream;
	}
	
	
	/**
	 * Set the Type of this IQ packet. The ordinary type for RPC IQ's is SET
	 */
//	public void setType(IQ.Type t) {
//		super.setType(t);
//	}
	
	/**
	 * Sets an error stanza on the RPC packet
	 */
//	public void setError(XMPPError xmppe) {
//		super.setError(xmppe);
//	}
	

	/**
	 * Method required by the IQ interface... this outputs the object as XML
	 */
	public  String getChildElementXML() {
		return "<query xmlns=\""+this.getClass().getName()+"\">\n"+xstream.toXML(this)+"\n</query>";
	}
	
	
	
}
