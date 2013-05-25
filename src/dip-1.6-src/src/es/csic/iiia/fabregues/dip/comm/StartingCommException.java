package es.csic.iiia.fabregues.dip.comm;

/**
 * The client cannot connect to the game manager. It does not respond to 
 * @author angels
 *
 */
public class StartingCommException extends CommException {

	public StartingCommException(String msg) {
		super(msg);
	}

}
