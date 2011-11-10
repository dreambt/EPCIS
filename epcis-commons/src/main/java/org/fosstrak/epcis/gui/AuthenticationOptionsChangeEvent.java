package org.fosstrak.epcis.gui;

import java.util.EventObject;

/**
 * Notification that authentication options have changed.
 */
public class AuthenticationOptionsChangeEvent extends EventObject {

	static final long serialVersionUID = 7641439802544240559L;
	
	private boolean complete;
	
	public AuthenticationOptionsChangeEvent(Object source, boolean complete) {
		super(source);
		this.complete = complete;
	}
	
	/**
	 * Indicates whether the supplied options are complete (i.e. sufficient filled out
	 * to allow the desired type of authentication to proceed), and the GUI buttons
	 * can be reactivated.
	 * @return true, if the options are sufficiently filled out to allow the
	 * desired type of authentication to proceed, false otherwise.
	 */
	public boolean isComplete() {
		return complete;
	}
	
}