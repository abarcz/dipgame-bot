package org.dipgame.negoClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import org.dipgame.dipNego.Translator;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.dipNego.parser.ParseException;
import org.json.JSONException;

/**
 * Runs a console based client
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class NegoClientTester {

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
		
		NegoClient client = createClient(ip, port, name);
		
		try {
			client.init();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		} catch (JSONException e) {
			e.printStackTrace();
			return;
		}

		final BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		
		try {
			for(String line = in.readLine(); !client.isDisconnecting() && line!=null && line.length()!=0; line = in.readLine()){
				try{
					String toStr = line.substring(0, line.indexOf(":"));
					String lineStr = line.substring(line.indexOf(":")+1, line.length());
					List<String> to = new Vector<String>(1);
					to.add(toStr);
					if(toStr.equals("") || lineStr.equals("")){
						System.err.println("<to_user_name>:<message_text>");
						continue;
					}
					try {
						client.send(to, translator.parseMessage(lineStr).getIllocution());
					} catch (IOException e) {
						e.printStackTrace();
						break;
					} catch (JSONException e) {
						e.printStackTrace();
						break;
					}
				}catch (ParseException e) {
					System.err.println("Parser error. "+e.getMessage());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		client.disconnect();
	}
	 
	 /**
	  * Console based chat client
	  * 
	  * @param ip
	  * @param port
	  * @param name
	  * @return
	  */
	private static NegoClient createClient(InetAddress ip, int port, String name) {
		NegoClient client = new NegoClient(ip, port, name){

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
			protected void handleClientAccepted() {
				
			}

			@Override
			protected void handleNegotiationMessage(String from, List<String> tos, Illocution illocution) {
				System.out.println(from +"--->"+tos.get(0)+" "+illocution.toString());
			}
		};
		return client;
	}

}
