package com.challengeandresponse.imoperator.comm;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.challengeandresponse.imoperator.m2mobjects.MMObject;
import com.challengeandresponse.imoperator.universaliq.UniversalIQ;


/**
 * Filter incoming XMPP packets based on the class of the packet.
 * This allows creating, for example, agents that handle a particular  
 * kind of object. Note that the PacketFilter interface specfies that
 * the object passed in will be a Packet. Therefore all
 * classes provided as "recognizedClass" arguments must be subclasses
 * of Packet. No object that's not based on Packet would
 * ever be presented to the filter.
 * </p>
 * <p>If no recognizedClass is set, the filter will always return
 * false (rejecting all packets).</p>
 * 
 * @author jim
 *
 */
public class PacketClassFilter
implements PacketFilter {

	@SuppressWarnings("unchecked")
	private Class recognizedClass = null;
	private boolean verbose = false;

	/**
	 * Instantiate a filter but don't set the recognized class.
	 * The creator must call addRecognizedClass() to set a class for this 
	 * filter to recognize.
	 */
	public PacketClassFilter() {
	}

	/**
	 * Instantiate a filter that accepts packets of the class of whatever recognizedClass is
	 * @param recognizedClass the class that this filter should accept
	 */
	@SuppressWarnings("unchecked")
	public PacketClassFilter(Class recognizedClass) {
		this.recognizedClass = recognizedClass;
	}

	public void setVerbose(boolean setting) {
		this.verbose = setting;
	}

	/**
	 * Add an additional class that this filter accepts. Any number of 
	 * classes may be added. 
	 * 
	 * @param recognizedClass a class that this filter should accept
	 */
	@SuppressWarnings("unchecked")
	public synchronized void addRecognizedClass(Class recognizedClass) {
		this.recognizedClass = recognizedClass;
	}


	public synchronized boolean accept(Packet packet) {
		if (verbose) {
			System.out.println("PacketClassFilter... Accept is checking packet: "+packet);
			System.out.println("recognizedPacketID:" +packet.getPacketID());
			System.out.println("packet.getfrom: "+packet.getFrom());
			System.out.println("packetID: "+packet.getPacketID());
			System.out.println("fromJID:" +packet.getFrom());
			System.out.println("PacketClassFilter... Accept is returning: "+((recognizedClass != null) && (recognizedClass.isInstance(packet))));
		}

		return ((recognizedClass != null) && (recognizedClass.isInstance(packet)));
	}

	public String toString() {
		return "recognizedClass: "+recognizedClass.getName();
	}




	public static void main(String[] args) {
		PacketClassFilter uf = new PacketClassFilter(UniversalIQ.class);

		MMObject <Integer> obj1 = new MMObject<Integer>(new Integer(1));
		Message obj2 = new Message();

		System.out.println("Obj1, MMObject, should be true::"+uf.accept(obj1));
		System.out.println("Obj2, String, should be false:"+uf.accept(obj2));
	}

}
