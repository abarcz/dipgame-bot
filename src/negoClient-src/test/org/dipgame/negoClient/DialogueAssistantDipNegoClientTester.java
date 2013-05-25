package org.dipgame.negoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;

import org.dipgame.dialogueAssistant.DialogueAssistant;
import org.dipgame.dialogueAssistant.MessageInbox;
import org.dipgame.dialogueAssistant.dictionary.Dictionary;
import org.dipgame.dialogueAssistant.dictionary.DictionaryLoader;
import org.dipgame.dialogueAssistant.exceptions.DictionaryFileLoadingException;
import org.dipgame.dialogueAssistant.exceptions.TranslationException;
import org.dipgame.dipNego.language.Message;
import org.dipgame.dipNego.language.TimeLine;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.negoUtilities.BotNegoUtility;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.comm.Parser;
import es.csic.iiia.fabregues.utilities.Interface;

/**
 * Runs a console based client that negotiates during a Diplomacy game using L language. The sentences are read from STDIN and translated to L using Dialogue Assistant.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class DialogueAssistantDipNegoClientTester {
	
	private Dictionary d;
	private HashMap<String, MessageInbox> messageInboxes;
	private DipNegoClient client;
	private String name;
	private static BufferedReader in;
	
	public DialogueAssistantDipNegoClientTester(InetAddress ip, int port, String name) throws DictionaryFileLoadingException, IOException, JSONException{
		this.name = name;
		d = new Dictionary();
		(new DictionaryLoader()).loadWords("files/dictionary.txt", d);
		messageInboxes = new HashMap<String, MessageInbox>();
		createClient(ip, port, name);
		
		client.init();
	}

	public static void main(String[] args) {
		InetAddress ip;
		int port;
		String name;
		
		//Reading parameters
		try{
			if(args.length == 3){
				ip = InetAddress.getByName(args[0]);
				port = Integer.valueOf(args[1]);
				name = args[2];
			}else{
				System.err.println("Ussage: chatClient <ip> <port> <name>");
				return;
			}
		} catch (UnknownHostException e1) {
			System.err.println("Problems with the ip of the server.");
			e1.printStackTrace();
			return;
		}
		
		DialogueAssistantDipNegoClientTester tester = null;
		try {
			tester = new DialogueAssistantDipNegoClientTester(ip, port, name);

			System.out.println("You should enter message like \"ENG:I propose you peace\"\n");
			in = new BufferedReader(new InputStreamReader(System.in));
		
			for(String line = in.readLine(); !tester.isClientDisconnecting() && line!=null && line.length()!=0; line = in.readLine()){
				try{
					String toStr = line.substring(0, line.indexOf(":"));
					String lineStr = line.substring(line.indexOf(":")+1, line.length());
					if(toStr.equals("") || lineStr.equals("")){
						System.err.println("<to_user_name>:<pseudo_message_text>");
						continue;
					}
				
					tester.send(toStr, lineStr);
				}catch (StringIndexOutOfBoundsException e){
					//Problems parsing the message. It is not <to>:<message>
					System.err.println("<to_user_name>:<pseudo_message_text>");
				}catch (TranslationException e) {
					System.err.println("<to_user_name>:<pseudo_message_text>");
				} catch (JSONException e) {
					//It will never happen
					e.printStackTrace();
				}
			}
			tester.disconnectClient();
		} catch (DictionaryFileLoadingException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} catch (JSONException e) {
			//It will never happen
			e.printStackTrace();
		}
		
	}
	 
	 private void send(String toStr, String lineStr) throws TranslationException, JSONException, IOException {
		if(client.game==null){
			System.err.println("Wait until the game starts.");
			return;
		}
		MessageInbox m = getMessageInbox(toStr);
		DialogueAssistant interpreter = new DialogueAssistant(client.game, name, toStr, d, m);
		Illocution illoc = interpreter.translate(lineStr);
		m.keepMessagesSend(new Message(client.game, illoc, new TimeLine()));
		client.send(BotNegoUtility.toStringList(toStr), illoc);
	}

	private MessageInbox getMessageInbox(String toStr) {
		if(!messageInboxes.containsKey(toStr)){
			messageInboxes.put(toStr, new MessageInbox());
		}
		return messageInboxes.get(toStr);
	}

	private void disconnectClient() {
		client.disconnect();
	}

	private boolean isClientDisconnecting() {
		return client.isDisconnecting();
	}

	/**
	  * Console based chat client
	  * 
	  * @param ip
	  * @param port
	  * @param name
	  * @return
	  */
	private void createClient(InetAddress ip, int port, String name) {
		client = new DipNegoClient(ip, port, name){

			@Override
			protected void handleErrorMessage(String line) {
				System.err.println(line);
			}

			@Override
			protected void handleServerOff() {
				super.log.close();
				System.exit(0);
			}

			@Override
			protected void handleNegotiationMessage(Power from, List<Power> tos, Illocution illocution) {
				MessageInbox m = getMessageInbox(from.getName());
				DialogueAssistant interpreter = new DialogueAssistant(game, name, tos.get(0).getName(), d, m);
				m.keepMessagesReceived(new Message(game, illocution, new TimeLine()));
				String msg;
				try {
					msg = interpreter.translate(illocution);
					System.out.println(from +"--->"+me+" "+msg);
				} catch (TranslationException e) {
					System.err.println(e.getMessage());
				}
			}
			
			@Override
			protected void handleFirstGamePhase() {
				System.out.println("Game starts");
			}

			@Override
			protected void handleNewGamePhase() {
				System.out.println(Interface.arrToString(Parser.getSCO(client.getGame())));
				System.out.println(Interface.arrToString(Parser.getNOW(client.getGame())));
			}
		};
	}

}
