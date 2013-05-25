package org.dipgame.negoClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;

import org.dipgame.chatClient.ChatClient;
import org.dipgame.chatUtilities.ClientMsg;
import org.dipgame.dipNego.Translator;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.json.JSONException;

import es.csic.iiia.fabregues.utilities.Log;

public abstract class NegoClient extends ChatClient{
	
	protected Translator translator;

	public NegoClient(InetAddress serverIp, int serverPort, String clientName) {
		super(serverIp, serverPort, clientName);
		translator = new Translator();
	}
	
	public NegoClient(InetAddress serverIp, int serverPort, String clientName, Log log) {
		super(serverIp, serverPort, clientName, log);
		translator = new Translator();
	}
	
	@Override
	protected void handleChattingMessage(ClientMsg message) {
		try {
			Illocution illocution = translator.parseMessage(message.getContent()).getIllocution();
			handleNegotiationMessage(message.getFrom(), message.getTo(), illocution);
		} catch (Exception e) {
			e.printStackTrace();
			log.print(e.getMessage());
		}
	}

	protected abstract void handleNegotiationMessage(String from, List<String> tos, Illocution illocution);
	
	/**
	 * Sends an illocution
	 * @param msg
	 * @throws IOException
	 */
	public void send(List<String> to, Illocution illoc) throws JSONException, IOException {
		super.send(to, illoc.toString());
	}
}
