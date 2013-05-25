


import java.util.Random;

import es.csic.iiia.fabregues.bot.OptionEvaluator;
import es.csic.iiia.fabregues.bot.options.Option;
import es.csic.iiia.fabregues.dip.board.Game;
import es.csic.iiia.fabregues.dip.board.Power;

/**
 * Class that evaluates combinations of orders, here called options, assigning to them random values.
 * This class is required for the correct execution of RandomBot. Check {@link http
 * ://www.dipgame.org} for more information.
 * 
 * @author Angela Fabregues, IIIA-CSIC, fabregues@iiia.csic.es
 */
public class SagOptionEvaluator implements OptionEvaluator{

	private Random rand;
	private KnowledgeBase knowledgeBase;
	
	public SagOptionEvaluator(){
		rand = new Random(System.currentTimeMillis());
	}
	
	public void setKnowledgeBase(KnowledgeBase base) {
		this.knowledgeBase = base;
	}

	@Override
	/**
	 * Sets a random value to the options that evaluates
	 */
	public void evaluate(Option option, Game game, Power power) {
		option.setValue(rand.nextFloat());
	}

}