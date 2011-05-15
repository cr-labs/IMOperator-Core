package com.challengeandresponse.imoperator.decorations;


/**
 * MM Objects with a member of this class can be personalized, and store records
 * for individual people or other entities. The fields are a "subject" which
 * is "the subject of this record" -- e.g. the person whose data are in this record;
 * and "label" which is a human/agent-readable naming label for the record... implementers
 * can make their own policies regarding labels (e.g. whether they must be unique or not)
 * </p>
 * <p>Classes using this decorator should 'implement PersonalizedI' to guarantee 
 * that the Personalized object is accessible to the class and other methods in the rest of the system.</p>
 * 
 * @author jim
 * @version 2007-06-03 v0.20
 *
 */

/*
 * REVISION HISTORY
 * 2007-06-01	v0.10	Created as an interface
 * 2007-06-03 	v0.20	Made into a concrete class for implementation consistency inside other M2M objects and avoidance of naming issues
 * 
 */

public class Personalized {
	private static final transient long serialVersionUID = 1L;

	
	/**
	 * This record concerns /whom/ ? The subject would be the internal ID of the entity that
	 * the record is reporting on, for example, a location report for the username "jim@xyz.com".
	 */
	private String subject; 

	/**
	 * This record is also known as "label" - a human or otherwise readable tag.
	 */
	private String label;


	public Personalized() {
		setSubject("");
		setLabel("");
	}

	public Personalized(String subject, String label) {
		setSubject(subject);
		setLabel(label);
	}
	
	
	
	/**
	 * The Subject of a record is the entity that it's "about", for example, "where is JIM"
	 * @return the Subject of this record -- any reference that the record creator and its peers understand is acceptable
	 */
	public String getSubject() {
		return subject;
	}

	/**
	 * The Subject of a record is the entity that it's "about", for example, "where is JIM"
	 * @param subject the "subject" of this record, any reference to the "subject" that the record creator and its peers will understand
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	
	/**
	 * the Label of a record is the subject's or system's meta label to ID the record, for example, to name a "saved place" in a Whereis agent's location record
	 * Implementers must decide in each case whether a label must be unique or may occur multiple times
	 * The label is intended to be human-readable or otherwise the private business of the
	 * implementing method and its peers that may also want to read it, so no policies
	 * are globally defined for labels.
	 * @return the label on this record
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * the Label of a record is the subject's or system's meta label to ID the record, for example, to name a "saved place" in a Whereis agent's location record
	 * Implementers must decide in each case whether a label must be unique or may occur multiple times
	 * @param label the label to set on this record.
	 */
	public void setLabel(String label) {
		this.label = label;
	}


	
	public String toString() {
		return "subj:"+getSubject()+" label:"+label;
	}
	


	
	
}
