package com.challengeandresponse.imoperator.test;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.challengeandresponse.imoperator.comm.SimpleXMPPConnection;
import com.challengeandresponse.imoperator.comm.SimpleXMPPException;


/**
 * Example implementation of a Jabber message processing class that implements
 * the PacketFilter and PacketListener interfaces, as required by the
 * SimpleXMPPConnection class. This processor just echoes messages back to their senders
 * 
 * @author jim
 *
 */
public class ProcessorEcho 
implements PacketFilter, PacketListener
{
	private SimpleXMPPConnection xmppc;

	/**
	 * @param xmppc a live XMPPCommunicator that this class can use for talking-back to clients
	 */
	public ProcessorEcho(SimpleXMPPConnection xmppc) {
		this.xmppc = xmppc;
	}

	// FILTER -- this one accepts all packets
	public boolean accept(Packet packet) {
		return true;
	}

	// PROCESSOR -- echo the packet
	public void processPacket(Packet packet) {
		String from = packet.getFrom();
		String message = ((Message) packet).getBody();
		try { // echo
			xmppc.sendMessage(from,message);
		}
		catch (SimpleXMPPException sxe2) {
			System.out.println("ProcessorEcho exception: "+sxe2.getMessage());
		}
	}


}
