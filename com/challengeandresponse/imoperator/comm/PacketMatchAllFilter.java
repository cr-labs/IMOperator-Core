package com.challengeandresponse.imoperator.comm;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import com.challengeandresponse.imoperator.m2mobjects.MMObject;


/**
 * Filter always returns TRUE -- for agents based on BaseAgent
 * that don't want to use the built-in PacketCollector....
 * or that want to see all traffic, or none.
 * (If you set up no filters at all, then ALL incoming packets
 * are received and your agent will try to process them, which is
 * sort of the opposite of what you probably want to happen)
 * </p>
 * <p>So:<br />
 * addRecognizedClass(MMPersonalGeocodedLocation.class);<br />
 * addPCIgnoreFilter(new PacketMatchAllFilter());<br />
 * </p>
 * 
 * @author jim
 *
 */
public class PacketMatchAllFilter
implements PacketFilter {

	public PacketMatchAllFilter() {
	}

	public synchronized boolean accept(Packet packet) {
		return true;
	}

	// for testing
	public static void main(String[] args) {
		PacketMatchAllFilter f = new PacketMatchAllFilter();

		MMObject <Integer> obj1 = new MMObject <Integer> (new Integer(1));
		Message obj2 = new Message();

		System.out.println("Obj1, MMObject, should be false::"+f.accept(obj1));
		System.out.println("Obj2, String, should be false:"+f.accept(obj2));
	}

}
