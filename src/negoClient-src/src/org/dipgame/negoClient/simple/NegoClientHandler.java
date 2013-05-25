package org.dipgame.negoClient.simple;

import java.util.List;

import org.dipgame.dipNego.language.illocs.Illocution;

/**
 * Interface used by {@code org.dipgame.chatClient.short.ChatClientImpl} to handle server messages.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public interface NegoClientHandler {

	/**
	 * Deals with error messages
	 * @param line
	 */
	public void handleErrorMessage(String line);
	
	/**
	 * Deals with server disconnection
	 */
	public void handleServerOff();

	/**
	 * Allows to do something when the client is accepted
	 */
	public void handleAccepted();

	/**
	 * Deals with negotiating messages
	 * @param from
	 * @param tos
	 * @param illocution
	 */
	public void handleNegotiationMessage(String from, List<String> tos, Illocution illocution);
}
