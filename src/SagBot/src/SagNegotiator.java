

import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.dipgame.dipNego.language.illocs.*;
import org.dipgame.dipNego.language.infos.*;
import org.dipgame.dipNego.language.offers.*;
import org.dipgame.negoClient.DipNegoClient;
import org.dipgame.negoClient.Negotiator;
import org.dipgame.negoClient.simple.DipNegoClientHandler;
import org.dipgame.negoClient.simple.DipNegoClientImpl;
import org.json.JSONException;

import es.csic.iiia.fabregues.bot.options.OptionBoard;
import es.csic.iiia.fabregues.dip.Player;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;
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
	private KnowledgeBase knowledgeBase;
	private BotObserver observer;
	private Random rand;
	
	private boolean occupied;
	
	/**
	 * Bot will wait no longer than MAX_NEGOTIATION_TIME_TOTAL ms for negotiation replies.
	 */
	private static final long MAX_NEGOTIATION_TIME_TOTAL /* in millis */ = 60 * 1000; 
	
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
		
/*===========================================================================*/
/*==                            DipNegoHandler:                            ==*/
/*===========================================================================*/
		
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

			/**
			 * L1 message: we have received a deal proposal
			 */
			public void handleProposal(Propose propose) {
				pendingProposalsMutex.lock();
				pendingProposals.push(new PendingDeal(propose.getDeal(), propose.getSender()));
				pendingProposalsMutex.unlock();
			}

			/**
			 * L1 message: our deal has been accepted
			 */
			public void handleAccept(Accept accept) {
				final String deal_ident = accept.getDeal().toString();
				defferedDealsMutex.lock();
				final List<Order> orders = ordersByDealIdent.get(deal_ident);
				if (orders != null) {
					for (Order o : orders) {
						dealsByOrder.get(o).setAccepted();
					}
					defferedDealsCondition.notifyAll();
					log ("Our deal was accepted: " + deal_ident);
					// TODO: we are counting on other bot to do as he declared; store this info
					//       and adjust his reputation accordingly when orders are known.
				} else {
					// ???:
					log ("Received response for unknown deal: " + deal_ident);
				}
				defferedDealsMutex.unlock();
			}

			/**
			 * L1 message: our deal has been rejected
			 */
			public void handleReject(Reject reject) {
				final String deal_ident = reject.getDeal().toString();
				defferedDealsMutex.lock();
				final List<Order> orders = ordersByDealIdent.get(deal_ident);
				if (orders != null) {
					for (Order o : orders) {
						dealsByOrder.get(o).setRejected();
					}
					defferedDealsCondition.notifyAll();
					log ("Our deal was rejected: " + deal_ident + ". Let's pity the fool who dare to ignore our will, as he shall find himself lost to our overwhelming might.");
				} else {
					// ???:
					log ("Received response for unknown deal: " + deal_ident);
				}
				defferedDealsMutex.unlock();
			}

			/**
			 * L2 message: we have received a piece of information
			 */
			public void handleInform(Inform inform) {
				handleInform(inform.getInformation(), inform.getSender());
			}

			@SuppressWarnings("incomplete-switch") // complete for stage L4
			public void handleInform(Information i, Power p) {
				switch (i.getType()) {
				case AND: // L2 info
					org.dipgame.dipNego.language.infos.And i_and = (org.dipgame.dipNego.language.infos.And) i;
					// TODO: implement
					break;
				case BELIEF: // L2 info
					org.dipgame.dipNego.language.infos.Belief i_belief = (org.dipgame.dipNego.language.infos.Belief) i;
					// TODO: implement
					switch (i_belief.getOffer().getType()) {
					case ALLIANCE:
						break;
					case AND:
						break;
					case CHOOSE:
						break;
					case DEMILITARISED_ZONE:
						break;
					case DO:
						break;
					case DRAW:
						break;
					case FOR_INTERVAL:
						break;
					case FOR_POINT:
						break;
					case IF:
						break;
					case IF_ELSE:
						break;
					case NOT:
						break;
					case OCCUPY:
						break;
					case OWES:
						break;
					case PEACE:
						break;
					case SEQUENCE:
						break;
					case SOLO:
						break;
					case SUPPORT_CENTER_DISTRIBUTION:
						break;
					case YOU_DO:
						break;
					default:
						break;
					
					}
					break;
				case DESIRE: // L2 info
					org.dipgame.dipNego.language.infos.Desire i_desire = (org.dipgame.dipNego.language.infos.Desire) i;
					// TODO: implement
					break;
				case NOT: // L2 info
					org.dipgame.dipNego.language.infos.Not i_not = (org.dipgame.dipNego.language.infos.Not) i;
					// TODO: implement
					break;
				case OBSERV: // L2 info
					org.dipgame.dipNego.language.infos.Observ i_observ = (org.dipgame.dipNego.language.infos.Observ) i;
					// TODO: implement
					break;
				case UNKNOWN: // L3 info
					org.dipgame.dipNego.language.infos.Unknown i_unk = (org.dipgame.dipNego.language.infos.Unknown) i;
					// TODO: implement
					break;
				}
			}
			
			/**
			 * L>=3 message: we have been queried
			 */
			public void handleQuery(Query query) {
				handleQuery(query.getInformation(), query.getSender());				
			}

			@SuppressWarnings("incomplete-switch") // complete for stage L4
			void handleQuery(Information i, Power p) {
				switch (i.getType()) {
				case AND: // L2 info
					org.dipgame.dipNego.language.infos.And i_and = (org.dipgame.dipNego.language.infos.And) i;
					// TODO: implement
					break;
				case BELIEF: // L2 info
					org.dipgame.dipNego.language.infos.Belief i_belief = (org.dipgame.dipNego.language.infos.Belief) i;
					// TODO: implement
					break;
				case DESIRE: // L2 info
					org.dipgame.dipNego.language.infos.Desire i_desire = (org.dipgame.dipNego.language.infos.Desire) i;
					// TODO: implement
					break;
				case NOT: // L2 info
					org.dipgame.dipNego.language.infos.Not i_not = (org.dipgame.dipNego.language.infos.Not) i;
					// TODO: implement
					break;
				case OBSERV: // L2 info
					org.dipgame.dipNego.language.infos.Observ i_observ = (org.dipgame.dipNego.language.infos.Observ) i;
					// TODO: implement
					break;
				case UNKNOWN: // L3 info
					org.dipgame.dipNego.language.infos.Unknown i_unk = (org.dipgame.dipNego.language.infos.Unknown) i;
					// TODO: implement
					break;
				}
			}
			
			/**
			 * L>=3 message: our deal has been accepted
			 */
			private void handleAnswer (Answer answer) {
				handleAnswer(answer.getInformation(), answer.getSender());
			}
			
			@SuppressWarnings("incomplete-switch") // complete for stage L4
			private void handleAnswer (Information i, Power p) {
				switch (i.getType()) {
				case AND: // L2 info
					org.dipgame.dipNego.language.infos.And i_and = (org.dipgame.dipNego.language.infos.And) i;
					// TODO: implement
					break;
				case BELIEF: // L2 info
					org.dipgame.dipNego.language.infos.Belief i_belief = (org.dipgame.dipNego.language.infos.Belief) i;
					// TODO: implement
					break;
				case DESIRE: // L2 info
					org.dipgame.dipNego.language.infos.Desire i_desire = (org.dipgame.dipNego.language.infos.Desire) i;
					// TODO: implement
					break;
				case NOT: // L2 info
					org.dipgame.dipNego.language.infos.Not i_not = (org.dipgame.dipNego.language.infos.Not) i;
					// TODO: implement
					break;
				case OBSERV: // L2 info
					org.dipgame.dipNego.language.infos.Observ i_observ = (org.dipgame.dipNego.language.infos.Observ) i;
					// TODO: implement
					break;
				case UNKNOWN: // L3 info
					org.dipgame.dipNego.language.infos.Unknown i_unk = (org.dipgame.dipNego.language.infos.Unknown) i;
					// TODO: implement
					break;
				}
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
		}; /* DipNegoHandler */

/*===========================================================================*/
/*==                        End of DipNegoHandler!                         ==*/
/*===========================================================================*/
		
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
	
	static final class PendingDeal {
		
		private final Deal deal;
		
		private final Power power;
		
		PendingDeal (Deal deal, Power power) {
			this.deal = deal;
			this.power = power;
		}

		public Deal getDeal() {
			return deal;
		}

		public Power getPower() {
			return power;
		}
		
	}; /* class PendingDeal */
	
	final LinkedList<PendingDeal> pendingProposals = new LinkedList<PendingDeal>();
	
	final Lock pendingProposalsMutex = new ReentrantLock();
	
	final HashMap <Order, DeferredDealResult> dealsByOrder = new HashMap <Order, DeferredDealResult>();
	
	final HashMap <String, List<Order> > ordersByDealIdent = new HashMap <String, List<Order> >();
	
	final Lock defferedDealsMutex = new ReentrantLock();
	
	final Condition defferedDealsCondition = defferedDealsMutex.newCondition();
	
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
		final Illocution illoc = new Propose(player.getMe(), player.getGame().getPower(power), deal);
		final String deal_ident = deal.toString();
		
		defferedDealsMutex.lock();
		if (!ordersByDealIdent.containsKey(deal_ident)) {
			ordersByDealIdent.put(deal_ident, new LinkedList<Order>());
		}
		ordersByDealIdent.get(deal_ident).add(order);
		dealsByOrder.put(order, new DeferredDealResult(order, optimistic ? 0 : -value, optimistic ? -value : 0));
		defferedDealsMutex.unlock();
		
		sendDialecticalAction(illoc);
	}

	/**
	 * Given best scenarios chosen by evaluators resolve pending negotiation requests.
	 */
	public void resolveNegotiations(OptionBoard scenarios) {
		
		pendingProposalsMutex.lock();
		while (!pendingProposals.isEmpty()) {
			Deal deal = pendingProposals.pollLast();
			pendingProposalsMutex.unlock();
			
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
			pendingProposalsMutex.lock();
		} /* while not pending proposals is empty */
		pendingProposalsMutex.unlock();
		
	} /* void resolveNegotiations () */

	/**
	 * Update orders basing upon deferred deal results.
	 */
	public void updateOrders() {
		defferedDealsMutex.lock();
		try {
			long wait_start = System.currentTimeMillis();
			for (DeferredDealResult ddr : dealsByOrder.values()) {
				while (!ddr.answered()) {
					long wait_for = MAX_NEGOTIATION_TIME_TOTAL - System.currentTimeMillis() + wait_start;
					if (wait_for > 0)
							defferedDealsCondition.wait(wait_for);
					else break;
				}
				if (!ddr.answered()) {
					ddr.setRejected();
				}
				ddr.updateOrder();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		defferedDealsMutex.unlock();
	}

	public void clear() {
		defferedDealsMutex.lock();
		dealsByOrder.clear();
		ordersByDealIdent.clear();
		defferedDealsMutex.unlock();
	}

/*===========================================================================*/
/*= Parallel negotiations handling:                                         =*/
/*===========================================================================*/

	private final ExecutorService threadPool = Executors.newFixedThreadPool(2);
	
	private int workersWorking = 0;

	private final Lock workersWorkingMutex = new ReentrantLock();
	
	private final Condition workersWorkingCondition = workersWorkingMutex.newCondition();
	
	public void negotiate(OptionBoard scenarios) {
		threadPool.execute(new Runnable () {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				workersWorkingMutex.lock();
				workersWorking--;
				workersWorkingCondition.signal();
				workersWorkingMutex.unlock();
			}
			
		});
		threadPool.execute(new Runnable () {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				workersWorkingMutex.lock();
				workersWorking--;
				workersWorkingCondition.signal();
				workersWorkingMutex.unlock();
			}
			
		});
		workersWorkingMutex.lock();
		workersWorking += 2;
		try {
			while (workersWorking != 0) {
				workersWorkingCondition.await();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();	
		}
		workersWorkingMutex.unlock();
	}
	
	/*
	 * 	/**
	 * Sends negotiation proposals randomly
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
*/
	
}
