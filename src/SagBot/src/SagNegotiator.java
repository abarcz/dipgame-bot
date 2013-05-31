

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
	private Log dipLog;
	
	//Negotiation variables
	private int negotiationPort;
	private InetAddress negotiationServer;
	private DipNegoClient chat;
	private Random rand;
	private KnowledgeBase knowledgeBase;
	private BotObserver observer;
	
	private boolean occupied;
	
	public SagNegotiator(InetAddress negotiationIp, int negotiationPort, Player player){
		this.negotiationServer = negotiationIp;
		this.negotiationPort = negotiationPort;
		this.player = player;
		this.dipLog = player.log.getLog();
		//this.log = new Interface(name+"_"+System.currentTimeMillis());
		this.rand = new Random(System.currentTimeMillis());
		occupied = false;
	}

	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}
	
	public void setGuiObserver(BotObserver observer) {
		this.observer = observer;
	}
	
	protected void log(String string) {
		observer.log(string);
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

			public void handleProposal(Propose propose) {
				Deal deal = propose.getDeal();
				if (deal instanceof Agree) {
					Offer offer = ((Agree)deal).getOffer();
					Vector<Power> too = null;
					Illocution response = null;
					switch (offer.getType()) {
					case PEACE:
						too = new Vector<Power>(1);
						too.add(propose.getSender());
						String powerName = too.get(0).getName();
						if(knowledgeBase.getStrength(propose.getSender().getName()) > knowledgeBase.getStrength(knowledgeBase.getPowerName())){
							response = new Accept(player.getMe(), too, deal);
							knowledgeBase.addPeace(powerName);
							log("accepted peace with " + powerName);
						}else{
							response = new Reject(player.getMe(), too, deal);
							log("rejected peace with " + powerName);
						}
						break;
					case ALLIANCE:
						too = new Vector<Power>(1);
						too.add(propose.getSender());
						Alliance alliance = (Alliance) ((Agree) deal).getOffer();
						String ally = too.get(0).getName();
						if(rand.nextBoolean()){
							response = new Accept(player.getMe(), too, deal);
							String logString = "accepted alliance with " + ally + " against ";
							for (Power power : alliance.getEnemyPowers()) {
								String enemy = power.getName();
								knowledgeBase.addAlliance(ally, enemy);
								logString += enemy + ", ";
							}
							log(logString);
						}else{
							response = new Reject(player.getMe(), too, deal);
							String logString = "rejected alliance with " + ally + " against ";
							for (Power power : alliance.getEnemyPowers()) {
								String enemy = power.getName();
								logString += enemy + ", ";
							}
							log(logString);
						}
						break;
					}
					
					// We have a new diplomaticAction to be performed
					if (response != null) {
						sendDialecticalAction(response);
					}
				}
				// TODO: implement handler
			}
				
			public void handleAccept(Accept accept) {
				// TODO: implement handler
			}
				
			public void handleReject(Reject reject) {
				// TODO: implement handler
			
			}
				
			public void handleQuery(Query query) {
				// TODO: implement handler
				
			}
				
			public void handleAnswer (Answer answer) {
				// TODO: implement handler

				
			}
				
			public void handleInform(Inform inform) {
				// TODO: implement handler
				
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
					handleProposal((Propose) illocution);
				} else if (illocution instanceof Accept) {
					handleAccept((Accept) illocution);
				} else if (illocution instanceof Reject) {
					handleReject((Reject) illocution);
				} else if (illocution instanceof Query) {
					handleQuery((Query) illocution);
				} else if (illocution instanceof Answer) {
					handleAnswer((Answer) illocution);
				} else if (illocution instanceof Inform) {
					handleInform((Inform) illocution);
				}
				occupied = false;
			}

			@Override
			public void handleNewGamePhase() {

				
			}
		};
		chat = new DipNegoClientImpl(negotiationServer, negotiationPort, player.getMe().getName(), handler, dipLog);
		try {
			chat.init();
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		dipLog.print("Negotiation module initiated.");
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
	
	/**
	 * Make negotiator object negotiate this deal with other power. Assumption should be made that
	 * this deal is or isn't successful (depending on optimistic option), as result of negotiation
	 * is not available immediately. If setting a deal fails or succeeds, weight of the connected
	 * order will be adjusted during option selection.
	 * @param order This deal will be connected with a given order.
	 * @param power Power the deal will be made with.
	 * @param value Deal influence on the value of the connected order.
	 * @param optimistic Optimistic deals are assumed to be successful by default, and if they fail,
	 * 		their value is decreased during option selection. Pessimistic options are assumed to fail
	 * 		by default, and if they succeed, their value is increased during option selection.
	 */
	void offerDeal (Order order, Deal deal, String power, Float value, boolean optimistic) {
		// this is a  method stub, it will be implemented later! :P
	}
	
}
