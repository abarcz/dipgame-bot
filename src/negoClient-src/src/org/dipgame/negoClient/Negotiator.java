package org.dipgame.negoClient;

/**
 * Deals with the negotiation
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public interface Negotiator {
	public void init();
	public void negotiate();
	public void disconnect();
	public boolean isOccupied();

}
