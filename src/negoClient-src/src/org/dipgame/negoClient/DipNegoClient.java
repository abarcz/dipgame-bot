package org.dipgame.negoClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import org.dipgame.chatUtilities.ClientMsg;
import org.dipgame.chatUtilities.MsgProcessor;
import org.dipgame.dipNego.Translator;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.negoUtilities.NegoMsg;
import org.dipgame.negoUtilities.NegoMsgProcessor;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.comm.GameBuilder;
import es.csic.iiia.fabregues.dip.comm.Parser;
import es.csic.iiia.fabregues.utilities.Interface;
import es.csic.iiia.fabregues.utilities.Log;

public abstract class DipNegoClient extends NegoClient{
	
	protected Power me;
	protected String[] lastSCO;
	protected String[] lastNOW;
	protected Game game;
	private boolean isFirstUpdate = true;

	public DipNegoClient(InetAddress serverIp, int serverPort, String clientName) {
		super(serverIp, serverPort, clientName);
	}
	
	public DipNegoClient(InetAddress serverIp, int serverPort, String clientName, Log log) {
		super(serverIp, serverPort, clientName, log);
	}
	
	/**
	 * Deals with the messages received from the server
	 * @param line
	 * @throws JSONException
	 * @throws IOException
	 */
	// It is necessary to override ChatClient's handleMessage. Otherwise an exception is thrown when processing game state messages
	public void handleMessage(String line) throws JSONException, IOException {
		try{
			switch(super.state){
				case CHATING:
					try{
						ClientMsg message = MsgProcessor.getMessage(line);
						log.print(message.getFrom() +"--->"+message.getTo()+" "+message.getContent());
						handleNegotiationMessage(message.getFrom(), message.getTo(), translator.parseMessage(message.getContent()).getIllocution());
					}catch (JSONException e) {
						//Diplomacy messages
						NegoMsg msg = NegoMsgProcessor.getMessage(line);
						log.print(Interface.arrToString(msg.getMessage()));
						updateGameState(msg);
					}
					break;
				default:
					super.handleMessage(line);
			}
		}catch (Exception e) {
			super.handleMessage(line);
		}
	}
	
	protected void updateGameState(NegoMsg msg){
		if(msg.getType().equals("SCO")){
			lastSCO = msg.getMessage();
		}else if(msg.getType().equals("NOW")){
			lastNOW = msg.getMessage();
			if(game==null){
				game = GameBuilder.createGame(lastSCO, lastNOW);
			}else{
				Parser.updateOwnedSCs(lastSCO, game);
				Parser.updateControlledRegions(lastNOW, game);
			}
			if(isFirstUpdate){
				isFirstUpdate = false;
				translator = new Translator(game);
				me = game.getPower(clientName);
				handleFirstGamePhase();
			}else{
				handleNewGamePhase();
			}
		}
	}
	
	protected abstract void handleFirstGamePhase();
	protected abstract void handleNewGamePhase();

	/**
	 * Requestes game info
	 */
	protected void handleClientAccepted(){
		try {
			super.send(NegoMsgProcessor.formatDAIDEMsgRequest("SCO"));
			super.send(NegoMsgProcessor.formatDAIDEMsgRequest("NOW"));
		} catch (JSONException e) {
			//It's not going to be executed
			e.printStackTrace();
		}
	}

	protected void handleNegotiationMessage(String from, List<String> tos, Illocution illocution){
		List<Power> powers = new Vector<Power>(tos.size());
		for(String to: tos){
			powers.add(game.getPower(to));
		}
		
		handleNegotiationMessage(game.getPower(from), powers, illocution);
	}
	
	protected abstract void handleNegotiationMessage(Power from, List<Power> tos, Illocution illocution);
	
	public Game getGame() {
		return game;
	}
}
