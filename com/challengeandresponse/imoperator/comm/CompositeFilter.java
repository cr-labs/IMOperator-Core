package com.challengeandresponse.imoperator.comm;

import java.util.Iterator;
import java.util.Vector;

import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.packet.Packet;

/**
 * A CompositeFilter is a set of INCLUDE and EXCLUDE filters.
 * For a "TRUE" response, at least one "INCLUDE" filter must match
 * and no "EXCLUDE" filters may match.
 * If no filters are set, "true" is returned. This is consistent
 * with the default behavior in Smack's PacketFilter, which returns
 * true if the filter is null.
 * Similarly, if no include filters are set, but at least one exclude filter is
 * set, accept will return true provided there are no matching exclude filters,
 * and false if any exclude filter matches.
 * 
 * @author jim
 *
 */
public class CompositeFilter implements PacketFilter {

	private Vector <PacketFilter> includeFilters;
	private Vector <PacketFilter> excludeFilters;

	private boolean verbose = false;

	public CompositeFilter() {
		includeFilters = new Vector <PacketFilter> ();
		excludeFilters = new Vector <PacketFilter> ();
	}

	public void setVerbose(boolean setting) {
		verbose = setting;
	}

	public boolean accept(Packet packet) {
		if (verbose)
			System.out.println("CompositeFilter.accept() examining packet: "+packet+" "+packet.getPacketID());

		boolean result;
		
		// if no include filter is set, the packet gets a default "accept"
		// otherwise, we search the include filters for an accepting filter
		if (includeFilters.size() > 0)
			result = false;
		else
			result = true;

		// first process the include filters
		Iterator <PacketFilter> it = includeFilters.iterator();
		while (it.hasNext() && (! result)) {
			PacketFilter pf = it.next();
			if (verbose)
				System.out.println("testing for include: "+pf);
			result = pf.accept(packet);
		}

		// otherwise, check all the EXCLUDE filters, at least until one matches
		it = excludeFilters.iterator();
		while (it.hasNext() && result) {
			PacketFilter pf = it.next();
			if (verbose)
				System.out.println("testing for exclude: "+pf);
			result = (! pf.accept(packet));
		}
		if (verbose)
			System.out.println("accept() returning "+result);
		return result;
	}


	public void addExcludeFilter(PacketFilter pf) {
		excludeFilters.add(pf);
	}

	public void addIncludeFilter(PacketFilter pf) {
		includeFilters.add(pf);
	}

	public void removeExcludeFilter(PacketFilter pf) {
		excludeFilters.remove(pf);
	}

	public void removeIncludeFilter(PacketFilter pf) {
		includeFilters.remove(pf);
	}

	public String toString() {
		return "include filters:\n"+includeFilters+"\nexclude filters:\n"+excludeFilters;
	}

}
