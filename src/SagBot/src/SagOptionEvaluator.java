import java.util.Random;

import es.csic.iiia.fabregues.bot.OptionEvaluator;
import es.csic.iiia.fabregues.bot.options.Option;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;
import es.csic.iiia.fabregues.dip.board.Province;
import es.csic.iiia.fabregues.dip.orders.MTOOrder;
import es.csic.iiia.fabregues.dip.orders.Order;


public class SagOptionEvaluator implements OptionEvaluator{

	private Random rand;
	private KnowledgeBase knowledgeBase;
	private SagNegotiator negotiator;
	
	public SagOptionEvaluator(){
		rand = new Random(System.currentTimeMillis());
	}

	public void setNegotiator(SagNegotiator negotiator) {
		this.negotiator = negotiator;
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}

	@Override
	public void evaluate(Option option, Game game, Power power) {
		if (validate(option)) {
			float sum = 0.0f;
			for (Order o : option.getOrders())
				sum += o.getValue();
			option.setValue(sum);
		}
	}
	
	private boolean validate(Option option) {
		for (Order a : option.getOrders()) {	
			for (Order b : option.getOrders()) {
				if (!a.equals(b) && 
						a instanceof MTOOrder && 
						b instanceof MTOOrder) {
					Province destination_a = ((MTOOrder) a).getDestination().getProvince();
					Province destination_b = ((MTOOrder) b).getDestination().getProvince();
					if (destination_a.equals(destination_b)) {
						return false;
					}
				}
			}
		}
		return true;
	}

}
