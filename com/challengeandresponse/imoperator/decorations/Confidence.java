package com.challengeandresponse.imoperator.decorations;


/**
 * Allows the message to express its provider's confidence measure in the validity of what is asserted in it.
 * <p>Classes using this decorator should 'implement ConfidenceI' to guarantee 
 * that the Confidence object is accessible to the class and other methods in the rest of the system.</p>
 * 
 * @author jim
 * @version 2008-03-19 v0.10
 *
 */

/*
 * REVISION HISTORY
 * 2007-08-19	v0.10	Spun out from RPC
 * 
 */

public class Confidence {
	private static final transient long serialVersionUID = 1L;

	/**
	 * The creator's confidence in the validity of the information asserted in this 
	 * record. Valid range is -1.0F .. 1.0F. 0.0F is neutral, meaning no assertion is made about confidence.
	 */
	private float level;


	public Confidence() {
		setLevel(0.0F);
	}

	public Confidence(float level)
	throws IllegalArgumentException {
		setLevel(level);
	}
	
	
	
	/**
	 * UniversalIQRPC messages can have a confidence measure from -1.0 (assertion that this is definitely not true)
	 * to +1.0 (assertion that this is definitely true). A measure of 0.0 asserts no belief about truth or falsity.
	 * 
	 * @return the confidence measure for this object
	 */
	public float getLevel() {
		return level;
	}
	
	/**
	 * UniversalIQRPC messages can have a confidence measure from -1.0 (assertion that this is definitely not true)
	 * to +1.0 (assertion that this is definitely true). A measure of 0.0 asserts no belief about truth or falsity.
	 * 
	 * @param level the new confidence level for this object
	 * @throws IllegalArgumentException if the new confidence measure is outside the range -1.0 .. 1.0
	 */
	public void setLevel(float level)
	throws IllegalArgumentException {
		if ( (level < -1.0) || (level > 1.0))
			throw new IllegalArgumentException("Confidence value "+level+" is out of bounds. Confidence values must be in the interval -1.0 .. 1.0 inclusive");
		this.level = level;		
	}


	
	public String toString() {
		return ""+level;
	}
	


	
	
}
