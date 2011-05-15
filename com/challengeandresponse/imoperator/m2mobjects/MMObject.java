package com.challengeandresponse.imoperator.m2mobjects;

import com.challengeandresponse.imoperator.universaliq.UniversalIQ;

/**
 * A wrapper to make any object IMO-compatible for XML streaming
 * (provided the object can be decomposed into other objects that
 * XStream understands - which is most Java objects).
 * 
 * Revised 2009-11-04 Now uses generics to type the wrapped object
 * 
 * @author jim
 * @version 2009-11-04
 *
 */
public class MMObject <T> 
extends UniversalIQ {
	
	private T o;

	/**
	 * Instantiate a new MMObject, leaving the wrapped object unset
	 */
	public MMObject() {
		super();
	}

	/**
	 * Instantiate a new MMObject, setting the object
	 */
	public MMObject(T o) {
		super();
		this.o = o;
	}
	
	public T getObject() {
		return this.o;
	}
	
	public void setObject(T o) {
		this.o = o;
	}


}
