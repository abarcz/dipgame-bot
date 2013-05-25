package org.dipgame.negoClient.simple;

import java.net.InetAddress;
import java.util.List;

import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.negoClient.NegoClient;

import es.csic.iiia.fabregues.utilities.Log;

/**
 * ChatClient implementation that allows the use of {@code org.dipgame.chatClient.ChatClient} avoiding class inheritance.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class NegoClientImpl extends NegoClient{

	protected NegoClientHandler handler;
	
	public NegoClientImpl(InetAddress serverIp, int serverPort, String name, NegoClientHandler handler){
		super(serverIp, serverPort, name);
		this.handler = handler;
	}
	
	public NegoClientImpl(InetAddress serverIp, int serverPort, String name, NegoClientHandler handler, Log log){
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

	@Override
	protected void handleClientAccepted() {
		handler.handleAccepted();
	}

	/**
	 * Deals with negotiating messages
	 * @param from
	 * @param tos
	 * @param illocution
	 */
	@Override
	protected void handleNegotiationMessage(String from, List<String> tos, Illocution illocution) {
		handler.handleNegotiationMessage(from, tos, illocution);
	}
}
