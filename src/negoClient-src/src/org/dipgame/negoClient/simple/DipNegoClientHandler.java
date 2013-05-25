package org.dipgame.negoClient.simple;

import java.util.List;

import org.dipgame.dipNego.language.illocs.Illocution;

import es.csic.iiia.fabregues.dip.board.Power;

/**
 * Interface used by {@code org.dipgame.chatClient.short.ChatClientImpl} to handle server messages.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public interface DipNegoClientHandler{

	/**
	 * Handles the acceptance of the client by the server 
	 */
	public void handleClientAccepted();
	
	/**
	 * Deals with error messages
	 * @param line
	 */
	public void handleErrorMessage(String line);
	
	/**
	 * Deals with server disconnection
	 */
	public void handleServerOff();


	public void handleNegotiationMessage(Power from, List<Power> tos, Illocution illocution);

	public void handleFirstGamePhase();

	public void handleNewGamePhase();
}
