import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

import es.csic.iiia.fabregues.dip.Player;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.comm.CommException;
import es.csic.iiia.fabregues.dip.comm.IComm;
import es.csic.iiia.fabregues.dip.comm.daide.DaideComm;
import es.csic.iiia.fabregues.dip.orders.DSBOrder;
import es.csic.iiia.fabregues.dip.orders.HLDOrder;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.dip.orders.REMOrder;
import es.csic.iiia.fabregues.dip.orders.WVEOrder;

/**
 * SagBot is a skeleton to support the development of a bot capable to play DipGame negotiating about peace, alliances and/or orders.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class SagBot extends Player {

	private InetAddress negoServerIp;
	private int negoServerPort;
	private SagNegotiator negotiator;
	 
	public SagBot(InetAddress negoServerIp, int negoServerPort) {
		super();
		this.negoServerIp = negoServerIp;
		this.negoServerPort = negoServerPort;
	}

	/**
	 * Handling being accepted in a game
	 */
	@Override
	public void init() {
		negotiator = new SagNegotiator(negoServerIp, negoServerPort, this);
	}

	/**
	 * Handling the game starting
	 */
	@Override
	public void start() {
	  negotiator.init();
	}
	
	/**
	 * Decide what orders to send to your units
	 */
	@Override
	public List<Order> play() {
		negotiator.negotiate();	//TODO negotiations should probably be made after the tactic planning phase

		/* HoldBot code */
		List<Order> orders = new Vector<Order>();
		switch (game.getPhase()) {
		case SPR:
		case FAL:
			//Holding all controlled units
			for (Region unit: me.getControlledRegions()) {
				HLDOrder hldOrder = new HLDOrder(me, unit);
				orders.add(hldOrder);
			}
			break;
		case SUM:
		case AUT:
			//Disbanding all dislodged units
			for (Region dislodgedUnit: game.getDislodgedRegions(me)) {
				DSBOrder dsbOrder = new DSBOrder(dislodgedUnit, me);
				orders.add(dsbOrder);
			}
			break;
		default:
			//That's WIN
			int nBuilds = me.getOwnedSCs().size() - me.getControlledRegions().size ();
			if (nBuilds > 0) {
				//Waiving nBuilds times
				for (int i = 0; i < nBuilds; i++) {
					WVEOrder wveOrder = new WVEOrder(me);
					orders.add(wveOrder);
				}
			} else if (nBuilds < 0) {
				//Removing nBuilds units
				int nRemovals = -nBuilds;
				for (int i = 0; i < nRemovals; i++) {
					Region remUnit = me.getControlledRegions().get (i);
					REMOrder remOrder = new REMOrder(me, remUnit);
					orders.add(remOrder);
				}
			}
			break;
		}
		return orders;
	}
	
	/**
	 * Handling the reception of previous phase performed orders
	 */
	@Override
	public void receivedOrder(Order arg0) {
		
	}
	
	/**
	 * Program that runs SagBot connected to a gameManager (gameServer + negoServer)
	 * @param args
	 */
	public static void main(String[] args) {
		InetAddress negoServerIp;
		int negoServerPort;
		InetAddress gameServerIp;
		int gameServerPort;
		String name;
		try {
			if (args.length == 0) {
				gameServerIp = InetAddress.getLocalHost();
				gameServerPort = 16713;
				negoServerIp = InetAddress.getLocalHost();
				negoServerPort = 16714;
				name = "SagBot";
			} else if (args.length == 5) {
				gameServerIp = InetAddress.getByName(args[0]);
				gameServerPort = Integer.valueOf(args[1]);
				negoServerIp = InetAddress.getByName(args[2]);
				negoServerPort = Integer.valueOf(args[3]);
				name = args[4];
			} else {
				System.err.println("Usage:\n SagBot [<gameServerIp> <gameServerPort> <negoServerIp> <negoServerPort> <name>]");
				return;
			}
		} catch (UnknownHostException e) {
			System.err.println("Usage:\n SagBot [<gameServerIp> <gameServerPort> <negoServerIp> <negoServerPort> <name>]");
			return;
		} catch (NumberFormatException e) {
			System.err.println("Usage:\n SagBot [<gameServerIp> <gameServerPort> <negoServerIp> <negoServerPort> <name>]");
			return;
		}
		
	  try {
		  	System.out.println("Connecting to: " + gameServerIp + ":" + gameServerPort);
			IComm comm = new DaideComm(gameServerIp, gameServerPort, name);
			SagBot sagBot = new SagBot(negoServerIp, negoServerPort);
			sagBot.start(comm);
	  } catch (CommException e) {
	  	System.err.println("Cannot connect to the server.");
	  }
	}
}
