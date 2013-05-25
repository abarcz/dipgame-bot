package org.dipgame.negoClient.simple;

import java.util.List;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;

/**
 * Interface used by {@code org.dipgame.chatClient.short.ChatClientImpl} to handle server messages.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public interface BotNegoClientHandler{

	
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
	 * Handles the reception of a proposal
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	public void handleReceivedProposal(Power from, List<Order> myOrders, List<Order> itsOrders);

	/**
	 * Handles the reception of an accept
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	public void handleReceivedAccept(Power from, List<Order> myOrders, List<Order> itsOrders);

	/**
	 * Handles the reception of a reject
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	public void handleReceivedReject(Power from, List<Order> myOrders, List<Order> itsOrders);

	/**
	 * Handles the reception of a withdraw
	 * @param itsName
	 */
	public void handleReceivedWithdraw(Power itsName);
	
	
	public void handleGameStateMessage(String[] msg);

	public void handleFirstGamePhase();

	public void handleNewGamePhase();
}
