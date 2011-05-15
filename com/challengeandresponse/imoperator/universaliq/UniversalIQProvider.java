package com.challengeandresponse.imoperator.universaliq;

import java.util.Vector;

import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.provider.IQProvider;
import org.xmlpull.v1.XmlPullParser;

import com.thoughtworks.xstream.XStream;

/**
 * This UniversalIQProvider supports "query" and "result" IQ packets.
 * Bind an instance of this class to the ProviderManager to
 * enable support for any classes that extend UniversalIQ. 
 * This works for many classes, but not all... 
 * Specifically, all types supported by XStream will work, as XStream is the 
 * inner core that makes this work.
 * </p>
 * <p>TODO There SHOULD BE a separate UniversalIQErrorProvider specifically for handling Error responses.</p>
 * 
 * @author jim
 *
 */
public class UniversalIQProvider implements IQProvider {

	private XStream xstream;
	
	private static final Vector <String> iqTypes;
	static {
		iqTypes = new Vector <String> ();
		iqTypes.add("query");
		iqTypes.add("result");
	}

	public UniversalIQProvider() {
		super();
		this.xstream = new XStream();
	}

	
	public void setXStream(XStream xs) {
		this.xstream = xs;
	}
	
	/**
	 * This rebuilds the original XML then calls XStream to reconstitute the 
	 * original object. Skips over inital "query" tags.
	 */
	/*
	 * NOTE: in the loop below, the parser handed it is already positioned on 
	 * the first event... which is the START_DOCUMENT event. The loop at the TOP
	 * (rather than the bottom) calls parser.next() so this event is blown out
	 * and never seen. Also it may seem disturbing that parser.next() is called
	 * at the top when the while() is testing for something that it may turn up,
	 * and that would normally stop BEFORE that next thing is processed...
	 * however this is ok as there are no conditions within the while that 
	 * do anything to the END_DOCUMENT tag... so the END_DOCUMENT event is 
	 * encountered by next()... the if's below all fail, and we come back around
	 * to while() where the loop terminates... if it even gets that far.
	 * Note that this is will terminate when a match for the 
	 * opening tag is encountered at the bottom.
	 * The outer "query" tag is just plainly ignored, because XSTREAM demands that
	 * the first tag name be its clue to the kind of object to reconstitute. I'm cool with that.
	 * Also, the XStream API requires that the pointer be on the END_DOCUMENT event
	 * when parseIQ terminates.
	 */
	public IQ parseIQ(XmlPullParser parser) throws Exception {
		StringBuilder sb = new StringBuilder();
		int eventType = parser.getEventType();
		String firstTag = "";
		String currentNamespace = "";
		while (eventType != XmlPullParser.END_DOCUMENT) {
			eventType = parser.next(); // this blows out the first event, which is START_DOCUMENT and we don't care
			if (eventType == XmlPullParser.START_TAG) {
				if (iqTypes.contains(parser.getName())) // the type tag at start or end is not processed
					continue;
				if (firstTag.length() < 1)
					firstTag = parser.getName();
				// attach the opening tag but leave it open
				sb.append("<"+parser.getName());
				// then read in and add all its attributes
				int attCount = parser.getAttributeCount();
				for (int i = 0; i < attCount; i++) {
					sb.append(" "+parser.getAttributeName(i));
					sb.append("=\""+parser.getAttributeValue(i)+"\"");
				}
				// if it's in a different namespace, copy that into the opening tag too
				if (parser.getNamespace() != currentNamespace) {
					currentNamespace = parser.getNamespace();
					sb.append(" xmlns=\""+currentNamespace+"\"");
				}
				// finally close the opening tag
				sb.append(">");
			} else if(eventType == XmlPullParser.END_TAG) {
				if (iqTypes.contains(parser.getName()))
					continue;
				sb.append("</"+parser.getName()+">");
				if (parser.getName().equals(firstTag))
					break;
			} else if(eventType == XmlPullParser.TEXT) {
				sb.append(parser.getText());
			}
        }
		

		return(IQ) xstream.fromXML(sb.toString());
	}

}
