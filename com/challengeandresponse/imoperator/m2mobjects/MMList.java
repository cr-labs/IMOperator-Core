package com.challengeandresponse.imoperator.m2mobjects;

import java.util.List;

import com.challengeandresponse.imoperator.universaliq.UniversalIQ;

/**
 * This wrapper provides for delivery of a collection of other objects
 * in a list... so that lists of objects can be easily made M2M-compatible 
 * for XML streaming (provided the object can be decomposed into other objects that
 * XStream understands - which is most Java objects). Without this class, it would
 * be necessary to create the singular and plural (list of...) versions of MM classes.
 * This should solve the issue...
 * 
 * <p>This now implements RPCI so that there will be a containing RPC call
 * that describes what's to be done to the contents of the list (if list elements
 * also happen to implement RPCI, the individual objects' RPC content, if any, should be ignored).
 * </p>
 * 
 * @author jim
 *
 */
public class MMList <T>
extends UniversalIQ {
	
	private List <T> list;
	
	/**
	 * Instantiate a new MMList object, setting its list to a provided list
	 */
	public MMList(List <T> l) {
		super();
		this.list = l;
	}

	
	public List <T> getList() {
		return this.list;
	}
	
	public void setList(List <T> l) {
		this.list = l;
	}
	
}
