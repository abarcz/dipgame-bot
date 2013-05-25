package org.dipgame.negoClient.simple;

import java.net.InetAddress;
import java.util.List;

import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.negoClient.DipNegoClient;

import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.utilities.Log;

/**
 * ChatClient implementation that allows the use of {@code org.dipgame.chatClient.ChatClient} avoiding class inheritance.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class DipNegoClientImpl extends DipNegoClient{

	protected DipNegoClientHandler handler;
	
	public DipNegoClientImpl(InetAddress serverIp, int serverPort, String name, DipNegoClientHandler handler){
		super(serverIp, serverPort, name);
		this.handler = handler;
	}
	
	public DipNegoClientImpl(InetAddress serverIp, int serverPort, String name, DipNegoClientHandler handler, Log log){
		super(serverIp, serverPort, name, log);
		this.handler = handler;
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

	/**
	 * Handles the acceptance of the client by the server 
	 */
	@Override
	protected void handleClientAccepted() {
		super.handleClientAccepted();
		handler.handleClientAccepted();
	}

	@Override
	protected void handleNegotiationMessage(Power from, List<Power> tos, Illocution illocution) {
		handler.handleNegotiationMessage(from, tos, illocution);
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
