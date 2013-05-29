

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import es.csic.iiia.fabregues.bot.Bot;
import es.csic.iiia.fabregues.bot.options.Option;
import es.csic.iiia.fabregues.bot.options.OptionBoard;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.board.Region;
import es.csic.iiia.fabregues.dip.comm.IComm;
import es.csic.iiia.fabregues.dip.comm.daide.DaideComm;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;

/**
 * Bot based on a merge of RandomBot and RandomNegotiatorRandomBot
 */
public class SagBot extends Bot {

	//Negotiation variables
	private int negotiationPort;
	private InetAddress negotiationServer;
	private SagNegotiator negotiator;
	
	private static String name = "SagBot";
	protected Random rand;
	private BotObserver botObserver;
	private String powerName;
	private KnowledgeBase knowledgeBase;
	
	/**
	 *
	 */
	public SagBot(InetAddress negotiationIp, int negotiationPort) {
		super(new SagProvinceEvaluator(), new SagOrderEvaluator(), new SagOptionEvaluator());
		this.negotiationServer = negotiationIp;
		this.negotiationPort = negotiationPort;
	}

	@Override
	/**
	 * Sets the number of options to preselect during the search of best options
	 */
	protected int getNumberOfBestOptions() {
		return 2;
	}

	@Override
	/**
	 * Sets the number of orders per unit to preselect during the search of best orders
	 */
	protected int getNumberOfBestOrdersPerUnit() {
		return 2;
	}

	@Override
	public void init() {
		System.out.println("Initializing..");
		rand = new Random(System.currentTimeMillis());
		System.out.println("Map " + mapName);
	}
	
	@Override
	public void start() {
		System.out.println("Starting game..");
		powerName = getMe().getName();
		System.out.println("We are " + powerName);
		
		knowledgeBase = new KnowledgeBase(powerName, game);
		
		negotiator = new SagNegotiator(negotiationServer, negotiationPort, this);
		negotiator.init();
		negotiator.setKnowledgeBase(knowledgeBase);
		
		((SagOrderEvaluator) this.orderEvaluator).setKnowledgeBase(knowledgeBase);
		((SagOptionEvaluator) this.optionEvaluator).setKnowledgeBase(knowledgeBase);
		((SagProvinceEvaluator) this.provinceEvaluator).setKnowledgeBase(knowledgeBase);
		
		botObserver = new BotObserver(knowledgeBase);
		knowledgeBase.addObserver(botObserver);
	}
	
	public HashMap<String, String> getRegionControllers() {
		HashMap<String, String> controllers = new HashMap<String, String>();
		for (Power power : game.getPowers()) {
			for (Region region : power.getControlledRegions()) {
				controllers.put(region.getName(), power.getName());
			}
		}
		return controllers;
	}
	
	public HashMap<String, String> getScOwners() {
		HashMap<String, String> owners = new HashMap<String, String>();
		for (Power power : game.getPowers()) {
			for (Province province : power.getOwnedSCs()) {
				owners.put(province.getName(), power.getName());
			}
		}
		return owners;
	}

	@Override
	/**
	 * Receive all orders made by all players, including myself
	 */
	public void receivedOrder(Order order) {
		//System.out.println("receivedOrder: " + order.toString());
		HashMap<String, String> controllers = getRegionControllers();
		HashMap<String, String> owners = getScOwners();
		
		// check if an attack on an army or SC is made
		if (order instanceof MTOOrder) {
			MTOOrder mtoOrder = (MTOOrder) order;
			String orderMaker = order.getPower().getName();
			String regionName = mtoOrder.getDestination().getName();
			String provinceName = mtoOrder.getDestination().getProvince().getName();
			if (controllers.containsKey(regionName)) {
				String ownerName = controllers.get(regionName);
				if (!ownerName.equals(orderMaker)) {
					addAggression(orderMaker, ownerName);
				}
			} else if (owners.containsKey(provinceName)) {
				String ownerName = owners.get(provinceName);
				if (!ownerName.equals(orderMaker)) {
					addAggression(orderMaker, ownerName);
				}
			}
		}
		negotiator.negotiate();
	}
	
	public void addAggression(String aggressor, String victim) {
		System.out.println(victim + " is under attack of " + aggressor);
		this.knowledgeBase.addAggression(aggressor, victim);
	}

	@Override
	/**
	 * Selects the orders to send from the preselected ones that are stored in optionBoard
	 */
	protected List<Order> selectOption(OptionBoard optionBoard) {
		if (this.botObserver.getAutoMode()) {
			System.out.println("starting round. press Enter to continue");
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			String command;
			try {
				command = reader.readLine();
			} catch (IOException ioe) {
				System.out.println("IO error waiting for enter!");
			}
		}

		if(optionBoard.getOptions().size()>0){
			Option option = optionBoard.getOptions().get(rand.nextInt(optionBoard.getOptions().size()));
			optionBoard.selectOption(option);
			return optionBoard.getSelectedOrders();
		}
		return new Vector<Order>(0);
	}
	
	public void exit(){
		super.exit();
		if(negotiator!=null){
			negotiator.disconnect();
		}
	}
	
	/**
	 * Main method. It allows to connect and reconnect to a game.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SagBot sagBot = null;
		String usageString = "Usage:\n  " + name + " <ip> <port> <name>  <negotiation ip> <negotiation port>";
		try {
			if (args.length == 0) {
				InetAddress negoServerIp;
				int negoServerPort;
				InetAddress gameServerIp;
				int gameServerPort;
				String name = "SagBot";
				gameServerIp = InetAddress.getLocalHost();
				gameServerPort = 16713;
				negoServerIp = InetAddress.getLocalHost();
				negoServerPort = 16714;
				System.out.println(name + " connecting to: " + gameServerIp + ":" + gameServerPort);
				sagBot = new SagBot(negoServerIp, negoServerPort);
				IComm comm = new DaideComm(gameServerIp, gameServerPort, name);
				sagBot.start(comm);
			}  else if (args.length >= 5) {
				sagBot = new SagBot(InetAddress.getByName(args[3]), Integer.parseInt(args[4]));
				IComm comm = new DaideComm(InetAddress.getByName(args[0]), Integer.parseInt(args[1]), name + "(" + args[2] + ")");
				sagBot.start(comm);
			} else {
				System.err.println(usageString);
			}
		} catch (final ArrayIndexOutOfBoundsException be) {
			System.err.println(usageString);
		} catch (final UnknownHostException uhe) {
			System.err.println("Unknown host: " + uhe.getMessage());
		} catch (final NumberFormatException nfe) {
			System.err.println(usageString);
		} catch (Exception e) {
			System.err.println(usageString);
		}
	}
}
