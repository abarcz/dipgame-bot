package org.dipgame.negoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.dipgame.dialogueAssistant.DialogueAssistant;
import org.dipgame.dialogueAssistant.dictionary.Dictionary;
import org.dipgame.dialogueAssistant.dictionary.DictionaryLoader;
import org.dipgame.dialogueAssistant.exceptions.DictionaryFileLoadingException;
import org.dipgame.dialogueAssistant.exceptions.TranslationException;
import org.dipgame.dialogueAssistant.orders.TranslateOrder;
import org.dipgame.dipNego.language.infos.CommitSequence;
import org.dipgame.negoUtilities.BotNegoUtility;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.comm.Parser;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.utilities.Interface;

/**
 * Runs a console based client to negotiate during movement phase about orders.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class BotNegoClientTester {
	
	private Dictionary d;
	private static BotNegoClient client;
	private String name;
	
	public BotNegoClientTester(InetAddress ip, int port, String name) throws DictionaryFileLoadingException, IOException{
		this.name = name;
		d = new Dictionary();
		(new DictionaryLoader()).loadWords("files/dictionary.txt", d);
		
		createClient(ip, port, name);
		
		try {
			client.init();
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}
	}

	public static void main(String[] args) throws DictionaryFileLoadingException{
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
		
		try {
			BotNegoClientTester tester = new BotNegoClientTester(ip, port, name);
			
			System.out.println("You should enter a message like \"ENG:EDI->NTH LON[EDI->NTH] LVP->WAL\"\n");
			final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			
			for(String line = in.readLine(); !tester.isClientDisconnecting() && line!=null && line.length()!=0; line = in.readLine()){
				try{
					String toStr = line.substring(0, line.indexOf(":"));
					String lineStr = line.substring(line.indexOf(":")+1, line.length());
					if(toStr.equals("") || lineStr.equals("")){
						System.err.println("<to_user_name>:<human_like_order_messages>");
						continue;
					}
				
					tester.send(toStr, lineStr);
					
				}catch (TranslationException e) {
					//Problems parsing orders.
					System.err.println("Translation error: "+e.getMessage());
				}
			}
			tester.disconnectClient();
		} catch (IOException e) {
			System.err.println(e.getMessage());
		}
	}
	 
	 private void send(String toStr, String lineStr) throws TranslationException {
		List<Power> to = BotNegoUtility.toPowerList(client.game.getPower(toStr));
		TranslateOrder orderInterpreter = new TranslateOrder(client.game);
		
		List<Order> myOrders = new Vector<Order>();
		List<Order> itsOrders = new Vector<Order>();
		CommitSequence commits = orderInterpreter.translate(lineStr, client.game.getPower(name), to);
		BotNegoUtility.extractOrders(client.game.getPower(name), commits, myOrders, itsOrders);
		
		client.sendProposal(to.get(0), myOrders, itsOrders);
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
	private static void createClient(InetAddress ip, int port, String name) {
		client = new BotNegoClient(ip, port, name){

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
			protected void handleReceivedProposal(Power from, List<Order> myOrders,
					List<Order> itsOrders) {
				System.out.print(from +"--->"+me+" proposal: \""+DialogueAssistant.humanize(myOrders)+" "+DialogueAssistant.humanize(itsOrders));
			}

			@Override
			protected void handleReceivedAccept(Power from, List<Order> myOrders,
					List<Order> itsOrders) {
				System.out.print(from +"--->"+me+" accept: \""+DialogueAssistant.humanize(myOrders)+" "+DialogueAssistant.humanize(itsOrders));
			}

			@Override
			protected void handleReceivedReject(Power from, List<Order> myOrders,
					List<Order> itsOrders) {
				System.out.print(from +"--->"+me+" reject: \""+DialogueAssistant.humanize(myOrders)+" "+DialogueAssistant.humanize(itsOrders));
			}

			@Override
			protected void handleReceivedWithdraw(Power from) {
				System.out.println(from +"--->"+me+" withdraw: "+from);
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
