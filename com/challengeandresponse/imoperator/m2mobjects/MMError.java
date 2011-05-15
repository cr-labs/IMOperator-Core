package com.challengeandresponse.imoperator.m2mobjects;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.XMPPError;

import com.challengeandresponse.imoperator.universaliq.UniversalIQ;

/**
 * A simple object that ONLY carries back an error status...
 * e.g. if a passed-in request is null, or the object is not recognized
 * by the receiver, the receiver can send back this MMError object,
 * which is just a simple UniversalIQRPC with type set to "Error"
 * 
 * @author jim
 *
 */
public class MMError extends UniversalIQ {
	
	/**
	 * Instantiate a new MMError, setting condition, error message and packetID
	 * @param condition the XMPPError.Condition to report
	 * @param errorMessage additional textual error message to report in the error
	 * @param packetID the packetID so the caller can pair off this response with its request
	 */
	public MMError(XMPPError.Condition condition, String errorMessage, String packetID) {
		super();
		setType(IQ.Type.ERROR);
		XMPPError.Condition xc = condition;
		setError(new XMPPError(xc,errorMessage));
		setPacketID(packetID);
	}

	/**
	 * Instantiate a new MMError, setting condition and error message. packetID will be ""
	 * @param condition the XMPPError.Condition to report
	 * @param errorMessage additional textual error message to report in the error
	 */
	public MMError(XMPPError.Condition condition, String errorMessage) {
		this(condition, errorMessage, "");
	}
	
	
	// for testing
	public static void main(String[] args) {
		MMError er = new MMError(XMPPError.Condition.bad_request,"Test - this is a bad request","666");
		System.out.println("Child element xml:\n"+er.getChildElementXML());
		System.out.println("\nError:\n"+er.getError());
		System.out.println("\nPacketID:\n"+er.getPacketID());
	}
	
}
