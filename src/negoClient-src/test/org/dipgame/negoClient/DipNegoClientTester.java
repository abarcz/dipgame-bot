package org.dipgame.negoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.dipgame.dipNego.Translator;
import org.dipgame.dipNego.language.Message;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.dipNego.parser.ParseException;
import org.dipgame.dipNego.parser.TokenMgrError;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.comm.Parser;
import es.csic.iiia.fabregues.utilities.Interface;

/**
 * Runs a console based client
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class DipNegoClientTester {

	private static DipNegoClient client;
	
	public static void main(String[] args){
		InetAddress ip;
		int port;
		String name;
		Translator translator = new Translator();
		
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
			createClient(ip, port, name);
			
			client.init();

			System.out.println("You should enter message like \"ENG:propose(agree(peace([eng ger ]),[eng ger ]),ger,[eng ])\"\n");
			final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
			for(String line = in.readLine(); !client.isDisconnecting() && line!=null && line.length()!=0; line = in.readLine()){
				try{
					String toStr = line.substring(0, line.indexOf(":"));
					String lineStr = line.substring(line.indexOf(":")+1, line.length());
					if(toStr.equals("") || lineStr.equals("")){
						System.err.println("<to_user_name>:<L message>");
						continue;
					}
					Message message = translator.parseMessage(lineStr);
					List<String> to = new Vector<String>(1);
					to.add(toStr);
					client.send(to, message.getIllocution());
				}catch (TokenMgrError e) {
					//Problems parsing the message. It is not L
					System.err.println("<to_user_name>:<L message>");
				}catch (StringIndexOutOfBoundsException e){
					//Problems parsing the message. It is not <to>:<message>
					System.err.println("<to_user_name>:<L message>");
				}catch (ParseException e) {
					//Problems parsing the message.
					System.err.println("<to_user_name>:<L message>.");
				} catch (JSONException e) {
					//It will never happen
					e.printStackTrace();
				}
			}
			client.disconnect();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		} catch (JSONException e) {
			//It will never happen
			e.printStackTrace();
		}
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
			protected void handleNegotiationMessage(Power from,
					List<Power> tos, Illocution illocution) {
				System.out.println(from +"--->"+tos.get(0)+" "+illocution.toString());
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
