package org.dipgame.negoClient.simple;

import java.net.InetAddress;
import java.util.List;

import org.dipgame.negoClient.BotNegoClient;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.utilities.Log;

/**
 * ChatClient implementation that allows the use of {@code org.dipgame.chatClient.ChatClient} avoiding class inheritance.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class BotNegoClientImpl extends BotNegoClient{

	protected BotNegoClientHandler handler;
	
	public BotNegoClientImpl(InetAddress serverIp, int serverPort, String name, BotNegoClientHandler handler){
		super(serverIp, serverPort, name);
		this.handler = handler;
	}
	
	public BotNegoClientImpl(InetAddress serverIp, int serverPort, String name, BotNegoClientHandler handler, Log log){
		super(serverIp, serverPort, name, log);
		this.handler = handler;
	}

	@Override
	protected void handleReceivedProposal(Power from, List<Order> myOrders, List<Order> itsOrders) {
		handler.handleReceivedProposal(from, myOrders, itsOrders);
	}

	@Override
	protected void handleReceivedAccept(Power from, List<Order> myOrders, List<Order> itsOrders) {
		handler.handleReceivedAccept(from, myOrders, itsOrders);
	}

	@Override
	protected void handleReceivedReject(Power from, List<Order> myOrders, List<Order> itsOrders) {
		handler.handleReceivedReject(from, myOrders, itsOrders);
	}

	@Override
	protected void handleReceivedWithdraw(Power itsName) {
		handler.handleReceivedWithdraw(itsName);
	}
	
	/**
	 * Deals with error messages
	 * @param line
	 */
	@Override
	protected void handleErrorMessage(String line){
		handler.handleErrorMessage(line);
	}

	/**
	 * Deals with server disconnection
	 */
	@Override
	protected void handleServerOff(){
		handler.handleServerOff();
	}

	@Override
	protected void handleFirstGamePhase() {
		handler.handleFirstGamePhase();
	}

	@Override
	protected void handleNewGamePhase() {
		handler.handleNewGamePhase();
	}
	

}
