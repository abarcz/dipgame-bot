package org.dipgame.negoClient;

import java.io.IOException;
import java.net.InetAddress;
import java.util.List;
import java.util.Vector;

import org.dipgame.dipNego.language.illocs.Accept;
import org.dipgame.dipNego.language.illocs.DealIllocution;
import org.dipgame.dipNego.language.illocs.Illocution;
import org.dipgame.dipNego.language.illocs.Propose;
import org.dipgame.dipNego.language.illocs.Reject;
import org.dipgame.dipNego.language.illocs.Withdraw;
import org.dipgame.dipNego.language.infos.CommitSequence;
import org.dipgame.negoUtilities.BotNegoUtility;
import org.json.JSONException;

import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.utilities.Log;

/**
 * Specific nego client to play with negotiation messages assuming the following:
 *  - messages exchanged only during movements phase
 *  - messages concern only actions to be done at the end of the current phase (only movement actions: hold, move, support hold and support move)
 *  - messages concern only actions to be done by the communicating agents
 *  - withdraws are global for an agent. They are not about a specific proposal.
 * @author angels
 *
 */
public abstract class BotNegoClient extends DipNegoClient{
	
	public BotNegoClient(InetAddress serverIp, int serverPort,
			String clientName, Log log) {
		super(serverIp, serverPort, clientName, log);
	}
	
	public BotNegoClient(InetAddress serverIp, int serverPort,
			String clientName) {
		super(serverIp, serverPort, clientName);
	}

	/**
	 * Delegates handling negotiation messages to four specialized methods.
	 * @param msg
	 */
	public void handleNegotiationMessage(Power from, List<Power> tos, Illocution illoc){
		List<Order> myOrders = new Vector<Order>();
		List<Order> itsOrders = new Vector<Order>();
		DealIllocution deal;
		try {
			deal = (DealIllocution) illoc;
		
			CommitSequence commits = (CommitSequence) deal.getDeal();
			BotNegoUtility.extractOrders(me, commits, myOrders, itsOrders);
			
			if(deal instanceof Propose){
				handleReceivedProposal(from, myOrders, itsOrders);
			}else if(deal instanceof Accept){
				handleReceivedAccept(from, myOrders, itsOrders);
			}else if(deal instanceof Reject){
				handleReceivedReject(from, myOrders, itsOrders);
			}
		} catch (ClassCastException e) {
			handleReceivedWithdraw(from);
		}
	}
	
	/**
	 * Formats a proposal and sends it
	 * @param myOrders
	 * @param itsOrders
	 */
	public void sendProposal(Power to, List<Order> myOrders, List<Order> itsOrders){
		CommitSequence commits = BotNegoUtility.formatCommitments(me, to, myOrders, itsOrders);
		Propose propose = new Propose(me, BotNegoUtility.toPowerList(to), commits);
		send(to, propose);
	}

	private void send(Power to, Illocution illoc) {
		List<String> tos = BotNegoUtility.toStringList(to);
		try {
			super.send(tos, illoc);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Formats an accept and sends it
	 * @param myOrders
	 * @param itsOrders
	 */
	public void sendAccept(Power to, List<Order> myOrders, List<Order> itsOrders){
		CommitSequence commits = BotNegoUtility.formatCommitments(me, to, myOrders, itsOrders);
		Accept accept = new Accept(me, BotNegoUtility.toPowerList(to), commits);
		send(to, accept);
	}

	/**
	 * Formats a reject and sends it
	 * @param myOrders
	 * @param itsOrders
	 */
	public void sendReject(Power to, List<Order> myOrders, List<Order> itsOrders){
		CommitSequence commits = BotNegoUtility.formatCommitments(me, to, myOrders, itsOrders);
		Reject reject = new Reject(me, BotNegoUtility.toPowerList(to), commits);
		send(to, reject);
	}
	
	/**
	 * Formats a withdraw and sends it
	 * Note: it is sending global withdraws. TODO send specific withdraws (for the given list of orders)
	 * @param myOrders
	 * @param itsOrders
	 */
	public void sendWithdraw(Power to, List<Order> myOrders, List<Order> itsOrders){
		Withdraw withdraw = new Withdraw(me, BotNegoUtility.toPowerList(to));
		send(to, withdraw);
	}

	/**
	 * Handles a received proposal consisting of a list of orders to be performed by us and a list of orders to be performed by the other power
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	protected abstract void handleReceivedProposal(Power from, List<Order> myOrders, List<Order> itsOrders);
	
	/**
	 * Handles a received accept consisting of a list of orders to be performed by us and a list of orders to be performed by the other power
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	protected abstract void handleReceivedAccept(Power from, List<Order> myOrders, List<Order> itsOrders);
	
	/**
	 * Handles a received reject consisting of a list of orders to be performed by us and a list of orders to be performed by the other power
	 * @param from 
	 * @param myOrders
	 * @param itsOrders
	 */
	protected abstract void handleReceivedReject(Power from, List<Order> myOrders, List<Order> itsOrders);

	/**
	 * Handles a received withdraw consisting of a list of orders to be performed by us and a list of orders to be performed by the other power
	 * @param myOrders
	 * @param itsOrders
	 */
	protected abstract void handleReceivedWithdraw(Power from);


}
