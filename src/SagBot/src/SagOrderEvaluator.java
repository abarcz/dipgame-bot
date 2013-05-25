


import java.util.Random;

import es.csic.iiia.fabregues.bot.OrderEvaluator;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.dip.orders.SUPOrder;

/**
 * Class that evaluates orders assigning to them random values.
 * This class is required for the correct execution of RandomBot. Check {@link http
 * ://www.dipgame.org} for more information.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class SagOrderEvaluator implements OrderEvaluator{

	private Random rand;
	private KnowledgeBase knowledgeBase;
	
	public SagOrderEvaluator(){
		rand = new Random(System.currentTimeMillis());
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}

	@Override
	/**
	 * Sets a random value to the orders that evaluates
	 */
	public void evaluate(Order order, Game game, Power power) {
		order.setOrderValue(rand.nextFloat());
	}
	
}
