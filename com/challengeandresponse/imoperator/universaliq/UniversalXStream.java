package com.challengeandresponse.imoperator.universaliq;

import java.lang.reflect.Field;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.Packet;

import com.thoughtworks.xstream.XStream;



/**
 * An implementation of XStream tuned for use with the UniversalIQ
 * design of the IMOperator M2M base classes (UniversalIQ and its descendants).
 * This merely sets features - primarily omitField() params - on the XStream
 * objects, so that non-user-originated packet materials are not propagated 
 * into the getChildElementXML() call... those outer fields are handled by Smack
 * and must not appear in the child XML.
 * 
 * @author jim
 * @deprecated Use the UniversalXStream class of com.challengeandresponse.universalxstream now
 *
 */
public final class UniversalXStream extends XStream {

	private static final String[] packetFields;
	private static final String[] iqFields;

	static {
		Field f[] = new Field[0];
		try {
			f = Class.forName("org.jivesoftware.smack.packet.Packet").getDeclaredFields();
		}
		catch (ClassNotFoundException cnfe) { } // static initializers can't just stop...
		packetFields = new String[f.length];
		for (int i = 0; i < f.length; i++)
			packetFields[i] = f[i].getName();
		
		f = new Field[0];
		try {
			f = Class.forName("org.jivesoftware.smack.packet.IQ").getDeclaredFields();
		}
		catch (ClassNotFoundException cnfe) { } // static initializers can't stop...

		iqFields = new String[f.length];
		for (int i = 0; i < f.length; i++)
			iqFields[i] = f[i].getName();
	}



	
	public UniversalXStream() {
		super();
		// omit all fields declared in Packet and IQ classes...
		// these are not to be serialized by our serializer... just our declared classes are
		for (int i = 0; i < packetFields.length; i++)
				this.omitField(Packet.class,packetFields[i]);
		for (int i = 0; i < iqFields.length; i++)
			this.omitField(IQ.class,iqFields[i]);
	}

}
