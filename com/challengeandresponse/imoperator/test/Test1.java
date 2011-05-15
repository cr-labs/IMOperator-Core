package com.challengeandresponse.imoperator.test;

import com.challengeandresponse.imoperator.m2mobjects.MMObject;

public class Test1 {
	
	public static void main(String[] args) {
		MMObject <Integer> mmo = new MMObject <Integer> ();
		mmo.setObject(new Integer(15));
		System.out.println(mmo.getChildElementXML());
	}
	

}
