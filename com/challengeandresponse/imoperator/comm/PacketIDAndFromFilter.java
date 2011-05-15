package com.challengeandresponse.imoperator.comm;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;


/**
 * Filter incoming XMPP packets based on the packet ID and/or sender's JID.
 * This filter is used when making a PacketCollector that's 
 * e.g. blocking in anticipation of a particular packet.
 * The filter accepts IQ packets only... text and presence packets are just bounced.
 * This is intended to filter for IQRPC packets, really.
 * 
 * @author jim
 */
public class PacketIDAndFromFilter
implements PacketFilter {

	private String recognizedPacketID;
	private String fromJID;
	private boolean verbose = false;

	/**
	 * Instantiate a filter but don't set any recognized packet IDs
	 * The creator must call addRecognizedID() to add at least one
	 * packet ID to the collection, or the filter will reject all packets.
	 */
	public PacketIDAndFromFilter() {
	}

	public void setVerbose(boolean setting) {
		this.verbose = setting;
	}

	/**
	 * Instantiate a filter that accepts packets with the packetID 'recognizedID'
	 * @param recognizedPacketID a packet ID that this filter should accept or null if any packet ID is ok
	 * @param fromJID the JID that the packet must come from, or null if any sender is ok
	 */
	public PacketIDAndFromFilter(String recognizedPacketID, String fromJID) {
		this.recognizedPacketID = recognizedPacketID;
		this.fromJID = fromJID;
	}

	/**
	 * Set the packetID that this filter accepts.
	 * @param recognizedPacketID a packet ID that this filter should accept
	 */
	public void setRecognizedID(String recognizedPacketID) {
		this.recognizedPacketID = recognizedPacketID;
	}

	/**
	 * Set the from JID that this filter accepts.
	 * @param fromJID the JID that the packet must come from, or null if any sender is ok
	 */
	public void setFromJID(String fromJID) {
		this.fromJID = fromJID;
	}

	public boolean accept(Packet packet) {
		if (! (packet instanceof IQ))
			return false;
		if (packet.getFrom() == null)
			return false;

		if (verbose) {
			System.out.println("PacketIDAndFromFilter... Accept is checking packet: "+packet);
			System.out.println("recognizedPacketID:" +recognizedPacketID);
			System.out.println("packet.getfrom: "+packet.getFrom());
			System.out.println("packetID: "+packet.getPacketID());
			System.out.println("fromJID:" +fromJID);
		}

		boolean result = ( (recognizedPacketID == null) || (packet.getPacketID().equals(recognizedPacketID)) );
		result = result && ( (fromJID == null) || (packet.getFrom().equals(fromJID)) );
		if (verbose)
			System.out.println("PacketIDANDFromFilter... Accept is returning: "+result);
		return result;
	}

}
