

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import org.dipgame.dipNego.language.illocs.Accept;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.dipNego.language.illocs.Propose;
import org.dipgame.dipNego.language.illocs.Reject;
import org.dipgame.dipNego.language.infos.Agree;
import org.dipgame.dipNego.language.infos.Deal;
import org.dipgame.dipNego.language.offers.Alliance;
import org.dipgame.dipNego.language.offers.Offer;
import org.dipgame.dipNego.language.offers.Peace;
import org.dipgame.negoClient.DipNegoClient;
import org.dipgame.negoClient.Negotiator;
import org.dipgame.negoClient.simple.DipNegoClientHandler;
import org.dipgame.negoClient.simple.DipNegoClientImpl;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.Player;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.utilities.Log;

/**
 * Based on RandomNegotiator
 */
public class SagNegotiator implements Negotiator{
	
	private Player player;
	private Log log;
	
	//Negotiation variables
	private int negotiationPort;
	private InetAddress negotiationServer;
	private DipNegoClient chat;
	private Random rand;
	private KnowledgeBase knowledgeBase;
	
	private boolean occupied;
	
	public SagNegotiator(InetAddress negotiationIp, int negotiationPort, Player player){
		this.negotiationServer = negotiationIp;
		this.negotiationPort = negotiationPort;
		this.player = player;
		this.log = player.log.getLog();
		//this.log = new Interface(name+"_"+System.currentTimeMillis());
		this.rand = new Random(System.currentTimeMillis());
		occupied = false;
	}

	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}
	
	/**
	 * Inits the negotiation
	 */
	public void init() {
		DipNegoClientHandler handler = new DipNegoClientHandler() {
			
			@Override
			public void handleServerOff() {
				
			}
			
			@Override
			public void handleErrorMessage(String arg0) {
				
			}

			@Override
			public void handleClientAccepted() {

				
			}

			@Override
			public void handleFirstGamePhase() {

				
			}

			/** Receives only messages sent directly to me - no other recipients */
			@Override
			public void handleNegotiationMessage(Power from, List<Power> to, Illocution illocution) {
				String recipients = "";
				for (Power power : to) {
					recipients = recipients + ", " + power.getName();
				}
				//System.out.println("received msg from: " + from.getName()
				//		+ " to: " + recipients
				//		+ " text: " + illocution.getString());
				occupied = true;
				if (illocution instanceof Propose) {
					Deal deal = ((Propose)illocution).getDeal();
					if (deal instanceof Agree) {
						String dealMakerName = ((Agree) deal).getPowers().get(0).getName();
						Offer offer = ((Agree)deal).getOffer();
						Vector<Power> too = null;
						Illocution response = null;
						switch (offer.getType()) {
						case PEACE:
							too = new Vector<Power>(1);
							too.add(illocution.getSender());
							if(rand.nextBoolean()){
								response = new Accept(player.getMe(), too, deal);
								knowledgeBase.addPeace(too.get(0).getName());
							}else{
								response = new Reject(player.getMe(), too, deal);
							}
							break;
						case ALLIANCE:
							too = new Vector<Power>(1);
							too.add(illocution.getSender());
							Alliance alliance = (Alliance) ((Agree) deal).getOffer();
							alliance.getEnemyPowers();
							if(rand.nextBoolean()){
								response = new Accept(player.getMe(), too, deal);
								for (Power power : alliance.getEnemyPowers()) {
									String enemy = power.getName();
									knowledgeBase.addAlliance(too.get(0).getName(), enemy);
								}
							}else{
								response = new Reject(player.getMe(), too, deal);
							}
							break;
						}
						
						// We have a new diplomaticAction to be performed
						if (response != null) {
							sendDialecticalAction(response);
						}
					}
				}
				occupied = false;
				
			}

			@Override
			public void handleNewGamePhase() {

				
			}
		};
		chat = new DipNegoClientImpl(negotiationServer, negotiationPort, player.getMe().getName(), handler, log);
		try {
			chat.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		log.print("Negotiation module initiated.");
	}
	
	/**
	 * Sends negotiation proposals randomly
	 */
	public void negotiate(){
		if( rand.nextInt(70) == 0){
			List<Power> available = new Vector<Power>(7);
			for(Power power: player.getGame().getPowers()){
				if(power.getControlledRegions().size()>0){
					available.add(power);
				}
			}
			Power receiver = available.get(rand.nextInt(available.size()));
			if(receiver.equals(player.getMe())){
				return;
			}
			Deal deal = null;
			if(rand.nextBoolean()){
				List<Power> peace = new Vector<Power>(2);
				peace.add(player.getMe());
				peace.add(receiver);
				deal = new Agree(peace, new Peace(peace));
			}else{
				List<Power> peace = new Vector<Power>(2);
				peace.add(player.getMe());
				peace.add(receiver);
				
				Power againstPower = available.get(rand.nextInt(available.size()));
				if(againstPower.equals(receiver) || againstPower.equals(player.getMe())){
					return;
				}
				List<Power> against = new Vector<Power>(1);
				against.add(againstPower);
				deal = new Agree(peace, new Alliance(peace, against));
			}
			Illocution illoc = new Propose(player.getMe(), receiver, deal);
			sendDialecticalAction(illoc);
			//TODO update knowledgeBase

			//System.out.println("sending msg to: " + receiver.getName()
			//		+ " text: " + illoc.getString());
		}
	}
	
	/**
	 * Sends a negotiation message
	 * @param illoc
	 */
	private void sendDialecticalAction(Illocution illoc) {
		try {
			Vector<String> recvs = new Vector<String>();
			for(Power rec : illoc.getReceivers()){
				recvs.add(rec.getName());
			}
			chat.send(recvs, illoc);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void disconnect() {
		chat.disconnect();
	}

	@Override
	public boolean isOccupied() {
		return occupied;
	}
}
