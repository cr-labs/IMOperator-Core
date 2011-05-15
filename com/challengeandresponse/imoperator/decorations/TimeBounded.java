package com.challengeandresponse.imoperator.decorations;

import org.joda.time.*;

/**
 * MM Objects with a TimeBounded object in them have
 * valid-from and valid-to timestamps on them, allowing them to exist in,
 * or report about, intervals of time. Usage of these fields is up to the
 * implementing agent and its peers.
 * 
 * <p>The class also provides the constants MIN_TIME and MAX_TIME which 
 * represent the earliest and latest times allowed in the system.<br />
 * MIN_TIME = 01/01/0000 at 0:0:0.0<br />
 * MAX_TIME = 12/31/2999 at 23:59:59.0<br />
 * MAX_INTERVAL = MIN_TIME .. MAX_TIME<br />
 * 
 * <p>Behind the scenes: the time boudinng should be kept in a MutableInterval, really.
 * The methods of this interface anticipate that the time bounds will be a JODA Time
 * MutableInterval, and the methods below allow ReadableInstant for setters because
 * that's an accepted argument set for the MutableInterval class constructor.</p>
 * 
 * <p>See JODA TIME for the methods that can operate on the MutableInterval that is 
 * represented by this class, and the behavior of comparators, period measurements
 * (e.g. Joda Intervals are NOT inclusive of start and end times) and the other methods that
 * operate on Intervals.</p>
 * 
 * <p>Classes using this decorator should 'implement TimeBoundedI' to guarantee 
 * that the TimeBounded object is accessible to the class and other methods in the rest of the system.</p>

 * 
 * @author jim
 * @version 2007-06-10 v0.30
 *
 */

/*
 * REVISION HISTORY
 * 2007-06-01	v0.10	Created as an interface
 * 2006-06-03	v0.20	Turned into a concrete Class for implementation consistency inside other M2M objects and avoidance of naming issues
 * 2006-06-10 	v0.30	Revised the class to just extend MutableInterval for friendliness with target code -- mainly, to provide consistent MIN and MAX instants and a default "infinite duration" constructor so that all comparisons are consistent in every usage
 * 
 */
public class TimeBounded
extends MutableInterval {
	private static final transient long serialVersionUID = 1L;

	private static final MutableDateTime MIN_TIME = new MutableDateTime(0,1,1,0,0,0,0);
	private static final MutableDateTime MAX_TIME = new MutableDateTime(2999,12,31,23,59,59,0);
	private static final MutableInterval MAX_INTERVAL = new MutableInterval(MIN_TIME,MAX_TIME);

	/**
	 * Create a new interval, having a timespan of MAX_INTERVAL. 
	 * Note that this is different from the default constructor for Interval, which sets
	 * a zero-duration interval from and to 1/1/1970. We instead default to the max-duration
	 * interval... that is, "forever" (per our MAX/MIN instants) rather than "never"
	 *
	 */
	public TimeBounded() {
		super();
		setInterval(MAX_INTERVAL);
	}
	
	/**
	 * Create a new interval representing the span from startInstant .. endInstant
	 * @param startInstant the beginning instant of the interval
	 * @param endInstant the ending instant of the interval
	 */
	public TimeBounded(ReadableDateTime startInstant, ReadableDateTime endInstant) {
		super();
		setInterval(startInstant,endInstant);
	}
	
	/**
	 * @return a new instance of MIN_TIME, our version of minus infinity for the timeline
	 */
	public MutableDateTime getMinTime() {
		return MIN_TIME.copy();
	}

	/**
	 * @return a new instance of MAX_TIME, our version of minus infinity for the timeline
	 */
	public MutableDateTime getMaxTime() {
		return MAX_TIME.copy();
	}
	
	/**
	 * @return a new instance of MAX_INTERVAL, an interval spanning MIN_TIME .. MAX_TIME
	 */
	public MutableInterval getMaxInterval() {
		return MAX_INTERVAL.copy();
	}
	
	public String toString() {
		return "interval from: "+getStart()+" to: "+getEnd();
	}
	
}
