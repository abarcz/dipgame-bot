import es.csic.iiia.fabregues.bot.OrderEvaluator;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;
import es.csic.iiia.fabregues.dip.orders.SUPMTOOrder;

public class SagOrderEvaluator implements OrderEvaluator{
	private KnowledgeBase knowledgeBase;
	
	public SagOrderEvaluator() {
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}

	@Override
	/**
	 * Sets a random value to the orders that evaluates
	 */
	public void evaluate(Order order, Game game, Power power) {
		if (order instanceof MTOOrder) {
			order.setOrderValue(((MTOOrder) order).getDestination().getProvince().getValue());
		}
		else if (order instanceof SUPMTOOrder) {
			order.setOrderValue(((SUPMTOOrder) order).getDestination().getValue());
		}
		else {
			order.setOrderValue(order.getLocation().getProvince().getValue());
		}
		System.out.println(order.toString() + " => " + order.getValue());
	}
	
}
